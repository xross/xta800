package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeSet;

import com.xmos.xta.engine.Status.BlockStatus;

@SuppressWarnings("serial")
class AbortedException extends Exception {}

public class Route {
	private final static boolean DEBUG = false;
	
	enum RouteMode {
		ABSLOUTE, FUNCTION
	};
	static private final int SR_IEBLE_SHIFT = 1;
	static private final int SR_IEBLE_SIZE = 1; 
	
	enum RouteStatus {
		COMPLETED, ABORTED, ERROR
	}
		
	private static int s_routeCount;
	private static int s_pathCount;
	private boolean m_firstStep;
	private int m_routeId;
	private ArrayList<BasicBlock> m_nodes;
	private int m_startPc;
	private TreeSet<Integer> m_endPcs; 
	private ArrayList<PathBasicBlock> m_completedPaths;
	private ArrayList<PathBasicBlock> m_allBasicBlocks;
	private String m_description;
	private ArrayList<Unresolved> m_unresolvedPaths;
	private InstructionDecodeCache m_idc;
	private InstructionMnemonics m_mnenonics;
	private Engine m_engine;	
	private RouteTiming m_routeTiming;
	private boolean m_constrained;
	
	public static void resetRouteCount() {
		s_routeCount = 0;
	}
	
	public Route(Config config, Engine engine, int startPc, TreeSet<Integer> endPcs, 
			ArrayList<BasicBlock> nodes, InstructionDecodeCache idc, 
			InstructionMnemonics mnenonics, String description){
		m_engine = engine;
		m_completedPaths = new ArrayList<PathBasicBlock>();
		m_allBasicBlocks = new ArrayList<PathBasicBlock>();
		m_unresolvedPaths = new ArrayList<Unresolved>();
		m_startPc = startPc;
		m_endPcs =endPcs;
		m_description = description;
		m_nodes = nodes;
		m_idc = idc;
		m_mnenonics = mnenonics;
		m_routeTiming = new RouteTiming(this, config, engine.getNodeId(), engine.getCoreId());
		m_routeId = s_routeCount;
		++s_routeCount;
		s_pathCount = 0;
	}
	
	public int getRouteId() {
		return m_routeId;
	}
	
	public int getStartPc() {
		return m_startPc;
	}
	
	public TreeSet<Integer> getEndPcs() {
		return m_endPcs;
	}
	
	public ArrayList<PathBasicBlock> getCompletedPaths(){
		return m_completedPaths;
	}
	
	public ArrayList<Unresolved> getUnresolvedPaths(){
		return m_unresolvedPaths;
	}
	
	public String getDescription() {
		return m_description;
	}
		
	public ArrayList<PathBasicBlock> getAllBasicBlocks(){
		return m_allBasicBlocks;
	}
	
	public void invalidateAllBlocks(){
		for(PathBasicBlock block : m_allBasicBlocks){
			block.getBlockStats().invalidateAll();
		}
	}
	
	public RouteTiming getRouteTiming() {
		return m_routeTiming;
	}
	
	public RouteStatus analyse(RouteMode mode){
		m_allBasicBlocks.clear();
		BasicBlock startNode = getNode(m_startPc);
		if (startNode == null){
			return RouteStatus.ERROR;
		}
		PathBasicBlock startBasicBlock;
		if (startNode.getInstructions().getFirst().getPc() == m_startPc){
			startBasicBlock = new PathBasicBlock(m_engine, startNode, null, m_idc);
		} else {
			startBasicBlock = new PathBasicBlock(m_engine,startNode, null, m_startPc, true, m_idc);
		}
		
		Stack<Integer> stack = new Stack<Integer>();
		Stack<LinkedList<BasicBlock>> nodeContext = new Stack<LinkedList<BasicBlock>>();
		m_firstStep = true;
		nodeContext.push(new LinkedList<BasicBlock>());
		
		try {
		    followNode(startBasicBlock, stack, nodeContext, mode);
		} catch (AbortedException e) {
			return RouteStatus.ABORTED;
		}
		
		sortPaths();
		displayWarnings();
		return RouteStatus.COMPLETED;
	}
	
	public PathBasicBlock getBlock(int id) {
		for(PathBasicBlock pbb : m_allBasicBlocks){
			if(pbb.getBlockId() == id){
				return pbb;
			}
		}
		return null;
	}

	public PathBasicBlock getPath(int id) {
		for(PathBasicBlock pbb : m_allBasicBlocks){
			if(pbb.getPathId() == id){
				return pbb;
			}
		}
		return null;
	}
	
	public boolean setLoopIterations(int pathId, boolean absolute, int iterations) {
		for (PathBasicBlock block : m_completedPaths) {
			if (setLoopIterations(block, pathId, absolute, iterations))
				return true;
		}
		return false;
	}

	public boolean setLoopIterations(PathBasicBlock path, int pathId, boolean absolute, int iterations) {
		for (PathBasicBlock block : path.getPathBlocks()) {
			for (PathBasicBlock loopPath : block.getLoopPaths(path)) {
				if (loopPath.getPathId() == pathId) {
					PathAttributes attributes = block.getLoopPathAttributes(loopPath);
					attributes.setNumIterations(iterations, absolute);
					return true;
				}
					
				if (setLoopIterations(loopPath, pathId, absolute, iterations))
					return true;
			}
		}
		return false;		
	}

	
	public ArrayList<XtaInstruction> getUnknowns() {
        ArrayList<XtaInstruction> unknowns = new ArrayList<XtaInstruction>();
		for (PathBasicBlock path : m_completedPaths) {
			for (XtaInstruction instruction : path.getInstructions(true)) {
				if (instruction.isUnknown()) {
					unknowns.add(instruction);
				}
			}
		}
        return unknowns;
	}
	
	public String getEndpointsPcsString() {
		String pcString =  "(0x" + Integer.toHexString(m_startPc) + " => ";
		for (int end : m_endPcs) {
			pcString += "0x" + Integer.toHexString(end) + ", ";
		}
		pcString = pcString.substring(0, pcString.length() - 2);
		pcString += ")";
		return pcString;
	}
	
	@SuppressWarnings("unchecked")
	private void followNode(
		PathBasicBlock currentBasicBlock, 
		Stack<Integer> stack, 
		Stack<LinkedList<BasicBlock>> nodeContext, RouteMode mode) throws AbortedException {
		
		if(m_engine.getAbort()) {
			throw new AbortedException();
		}
		
		if(reachedEnd(currentBasicBlock, mode)){
			if(DEBUG) System.out.println("\treached end");

			int size = currentBasicBlock.getInstructionTrimmed().size()-1;
			PathBasicBlock endBlock = currentBasicBlock;
			while(size>=0){
				if(m_endPcs.contains(currentBasicBlock.getInstructionTrimmed().get(size).getPc())){
					int endPc = 0;
					if(mode == RouteMode.ABSLOUTE){
						//this shouldn't cause a problem but is.
						endPc = currentBasicBlock.getInstructionTrimmed().get(size-1).getPc();
					} else {
						endPc = currentBasicBlock.getInstructionTrimmed().get(size).getPc();
					}
					int startPc = currentBasicBlock.getInstructionStream().getFirst().getPc();
					endBlock = new PathBasicBlock(m_engine, currentBasicBlock.getBasicBlock(), currentBasicBlock.getParent(), 
							startPc, endPc, m_idc);
					currentBasicBlock.setStatus(BlockStatus.COMPLETE);
					break;
				}
				size--;
			}
			if(updateConstraints(endBlock)){
				endBlock.setStatus(BlockStatus.EXCLUDED);
				isConstrained(true);
				return;
			}

			checkSupportedInstructions(endBlock);
			
			if (endBlock.getConstraint().isTrue()){
				addCompletePath(endBlock);
			}  else {
				isConstrained(true);
				endBlock.setStatus(BlockStatus.EXCLUDED);
			}
			return;
		} else {
			
			m_firstStep = false;
			
			if(updateConstraints(currentBasicBlock)){
				isConstrained(true);
				currentBasicBlock.setStatus(BlockStatus.EXCLUDED);
				return;
			}
			
			checkSupportedInstructions(currentBasicBlock);

			if(hasResursed(currentBasicBlock, nodeContext)){
				if(DEBUG) System.out.println("\recursed");
				String reason = "Recursion with 0x" + Integer.toHexString(currentBasicBlock.getBasicBlock().getInstructions().getFirst().getPc());
				addUnresolvedPath(currentBasicBlock, reason);
				return;
			}
			
			PathBasicBlock loopDestinationBlock = formsLoop(currentBasicBlock, nodeContext);
			if(loopDestinationBlock!=null){
				handelLoop(currentBasicBlock.getParent(), loopDestinationBlock);
				return;
			}
			
			
			XtaInstruction lastInstruction = currentBasicBlock.getInstructionTrimmed().get(currentBasicBlock.getInstructionTrimmed().size()-1);
			if(lastInstruction.getReturn() && stack.size()>0){
				BasicBlock nextNode = getNode(stack.peek());
				Stack<LinkedList<BasicBlock>> newContext = new Stack<LinkedList<BasicBlock>>();
				copyStack(nodeContext, newContext);
				newContext.pop();
				Stack<Integer> newStack = (Stack<Integer>) stack.clone();
				newStack.pop();
				PathBasicBlock nextPathBasicBlock = new PathBasicBlock(m_engine, nextNode, currentBasicBlock, m_idc);
				
				if(m_endPcs.contains(nextPathBasicBlock.getFirstPc()) &&  mode == RouteMode.ABSLOUTE ){
					if(currentBasicBlock.getConstraint().isTrue()){
						addCompletePath(currentBasicBlock);
					}  else {
						isConstrained(true);
						currentBasicBlock.setStatus(BlockStatus.EXCLUDED);
					}
					return;
				}
				
				followNode(nextPathBasicBlock, newStack, newContext, mode);
			} else {
				if (currentBasicBlock.getBasicBlock().getNextNodes().size() == 0) {;
					addUnresolvedPath(currentBasicBlock, "no known next block");
					return;
				}
				for(BasicBlock nextNode : currentBasicBlock.getBasicBlock().getNextNodes()){
					Stack<Integer> newStack = (Stack<Integer>) stack.clone();
					Stack<LinkedList<BasicBlock>> newContext = new Stack<LinkedList<BasicBlock>>();
					copyStack(nodeContext, newContext);
					if(lastInstruction.getBranchLink()){
						newStack.add(lastInstruction.getPc() + lastInstruction.getNumBytes());
						newContext.push(new LinkedList<BasicBlock>());
					} else {
						newContext.peek().add(currentBasicBlock.getBasicBlock());
					}
					PathBasicBlock nextPathBasicBlock = new PathBasicBlock(m_engine, nextNode, currentBasicBlock, m_idc);
					
					if(m_endPcs.contains(nextPathBasicBlock.getFirstPc()) &&  mode == RouteMode.ABSLOUTE){
						if(currentBasicBlock.getConstraint().isTrue()){
							addCompletePath(currentBasicBlock);
						}  else {
							isConstrained(true);
							currentBasicBlock.setStatus(BlockStatus.EXCLUDED);
						}
						continue;
					}
					followNode(nextPathBasicBlock, newStack, newContext, mode);
				}
			}
		}	
	}

	private void addUnresolvedPath(PathBasicBlock block, String reason) {
		Unresolved unresolved = new Unresolved(block, block.getBasicBlock().getInstructions().getLast(), reason);
		m_unresolvedPaths.add(unresolved);
		block.setUnresolved(unresolved);
		block.setPath(s_pathCount++);
		block.setStatus(BlockStatus.ERROR);
	}

	private void addCompletePath(PathBasicBlock block) {
		m_completedPaths.add(block);
		block.setPath(s_pathCount++);
		block.setStatus(BlockStatus.COMPLETE);
	}

	private void handelLoop(PathBasicBlock pathFormingLoop, PathBasicBlock loopDestinationBlock) {
		if(DEBUG) System.out.println("\t" + pathFormingLoop +" wants to form a loop with : " + loopDestinationBlock);
		
		//this must be true by design of the algorithm
		assert(pathFormingLoop != null);
		
		pathFormingLoop.setPath(s_pathCount++);

		if(DEBUG) System.out.println("adding loop " + pathFormingLoop  + " to "+loopDestinationBlock);				
		
		loopDestinationBlock.addLoopPath(pathFormingLoop);
		loopDestinationBlock.getLoopPathAttributes().put(pathFormingLoop, new PathAttributes());
		pathFormingLoop.setStatus(BlockStatus.COMPLETE);
		return;
	}
	
	private boolean updateConstraints(PathBasicBlock block) {
		m_allBasicBlocks.add(block);					
		for(XtaInstruction inst : block.getInstructionTrimmed()){
			if(block.getConstraint().isFalse(inst.getPc())){
				return true;
			}
		}		
		return false;
	}
	
	private void checkSupportedInstructions(PathBasicBlock block) {
		for(XtaInstruction inst : block.getInstructionTrimmed()){
			
			// Checks for threading instructions...
			if (m_mnenonics.isSsync(inst))
				block.addWarning(String.format("Ssync (multiple threads) not supported. Pc: 0x%08x", inst.getPc()));					
			if (m_mnenonics.isMsync(inst))
				block.addWarning(String.format("Msync (multiple threads) not supported. Pc: 0x%08x", inst.getPc()));				
			if (m_mnenonics.isMjoin(inst)) {
				block.addWarning(String.format("Mjoin (multiple threads) not supported. Pc: 0x%08x", inst.getPc()));
			}
			
			// Checks for interrupt enable..
			if (m_mnenonics.isSetSr(inst)) {	
				int op1Field = inst.getOperandField("op1");
				int shift = SR_IEBLE_SHIFT;
				int mask = (1 << SR_IEBLE_SIZE) - 1;
				if (((op1Field >> shift) & mask) == 1) 
    				block.addWarning(String.format("Interrupt enable detected. Pc: 0x%08x", inst.getPc()));					
			}
		}		
	}

	private boolean reachedEnd(PathBasicBlock currentBasicBlock, RouteMode mode) {
		for (XtaInstruction inst : currentBasicBlock.getInstructionTrimmed()){
			if (m_endPcs.contains(inst.getPc())) {
				if (inst.getPc() == m_startPc && m_firstStep) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean hasResursed(PathBasicBlock currentBasicBlock, Stack<LinkedList<BasicBlock>> nodeContext) {
		for (int i = 0; i < nodeContext.size() - 1; i++) {
			for (BasicBlock bb : nodeContext.get(i)) {
				if (currentBasicBlock.getBasicBlock() == bb) {
					return true;
				}
			}
		}
		return false;
	}

	private void copyStack(Stack<LinkedList<BasicBlock>> nodeContext, Stack<LinkedList<BasicBlock>> temp) {
		Iterator<LinkedList<BasicBlock>> iter = nodeContext.iterator();
		while (iter.hasNext()) {
			LinkedList<BasicBlock> pcs = iter.next();
			LinkedList<BasicBlock> copy = new LinkedList<BasicBlock>(pcs);
			temp.add(copy);
		}
	}

	private PathBasicBlock formsLoop(PathBasicBlock currentNode, Stack<LinkedList<BasicBlock>> nodeContext) {
		if (!nodeContext.peek().contains(currentNode.getBasicBlock())) return null;
		PathBasicBlock parent = currentNode.getParent();
		while (parent != null) {
			if (parent.getBasicBlock() ==  currentNode.getBasicBlock()) {
				return parent;
			}
			parent = parent.getParent();
		}
		return null;
	}

	private BasicBlock getNode(int pc) {
		for(BasicBlock node : m_nodes){
			for(XtaInstruction inst : node.getInstructions()){
				if(inst.getPc() == pc) return node;
			}
		}
		return null;
	}
		
	private void displayWarnings() {
		for (PathBasicBlock block : m_completedPaths) {
			for (String message : block.getWarnings()) {
				m_engine.notify(Notifier.NOTIFY_WARNING, message);
			}
		}
	}
	
	private void sortPaths() {
		Collections.sort(m_completedPaths);
		for(PathBasicBlock block : m_allBasicBlocks){
			Collections.sort(block.getLoopPaths(block));
		}
	}

	/**
	 * @param m_constrained the m_constrained to set
	 */
	public void isConstrained(boolean m_constrained) {
		this.m_constrained = m_constrained;
	}

	/**
	 * @return the m_constrained
	 */
	public boolean wasConstrained() {
		return m_constrained;
	}
}
