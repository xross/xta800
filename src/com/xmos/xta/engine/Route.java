package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeSet;

import com.xmos.xta.engine.Engine.RouteDescription;
import com.xmos.xta.engine.Status.BlockStatus;
import com.xmos.xta.engine.unknowns.InstructionUnknown;
import com.xmos.xta.engine.unknowns.LoopUnknown;
import com.xmos.xta.engine.unknowns.Unknowns;

@SuppressWarnings("serial")
class AbortedException extends Exception {}

@SuppressWarnings("unchecked")
public class Route {
	
    public static final int INVALID_ROUTE_ID = -1;

    enum RouteMode {
		ABSLOUTE, FUNCTION
	};
	private static final int SR_IEBLE_SHIFT = 1;
	private static final int SR_IEBLE_SIZE = 1; 
	
	enum RouteStatus {
		COMPLETED, ABORTED, ERROR
	}
		
	public static final boolean DEBUG = false;
	
	private static int s_routeCount;
	private static int s_pathCount;
	private boolean m_firstStep;
	private int m_routeId;
	private ArrayList<BasicBlock> m_nodes;
	private int m_startPc;
	private TreeSet<Integer> m_endPcs; 
	private ArrayList<PathBasicBlock> m_completedPaths;
	private ArrayList<PathBasicBlock> m_allBasicBlocks;
	private ArrayList<PathBasicBlock> m_loopBlocks;	//horrible
	private RouteDescription m_description;
	private ArrayList<Unresolved> m_unresolvedPaths;
	private InstructionDecodeCache m_idc;
	private InstructionMnemonics m_mnenonics;
	private Engine m_engine;	
	private RouteTiming m_routeTiming;
	private boolean m_constrained;
	private Unknowns m_unknowns;
	
	private HashSet<Integer> m_pcsHit;
	
	public static void resetRouteCount() {
		s_routeCount = 0;
	}
	
	public Route(Config config, Engine engine, int startPc, TreeSet<Integer> endPcs, 
			ArrayList<BasicBlock> nodes, InstructionDecodeCache idc, 
			InstructionMnemonics mnenonics, RouteDescription description){
		m_engine = engine;
		m_completedPaths = new ArrayList<PathBasicBlock>();
		m_allBasicBlocks = new ArrayList<PathBasicBlock>();
		m_loopBlocks = new ArrayList<PathBasicBlock>();
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
		m_pcsHit = new HashSet<Integer>();
		m_unknowns = new Unknowns();
	}
	
	/**
	 * this takes a pc and returns a list of the blocks that contain it
	 * @param pc
	 * @return
	 */
	private ArrayList<PathBasicBlock> pcToBlocks(int pc){
		ArrayList<PathBasicBlock> loopForming = new ArrayList<PathBasicBlock>();
		ArrayList<PathBasicBlock> nonLoopForming = new ArrayList<PathBasicBlock>();
		
		for(PathBasicBlock pbb : m_loopBlocks){
			for(XtaInstruction inst : pbb.getInstructionTrimmed()){
				if(inst.getPc() == pc){
					
					if(pbb.getParent().getPathId() != -1){
						loopForming.add(pbb);
					} else {
						nonLoopForming.add(pbb);
					}
					
					break;
				}
			}
		}
		
		ArrayList<PathBasicBlock> blocks = new ArrayList<PathBasicBlock>();
		blocks.addAll(loopForming);
		blocks.addAll(nonLoopForming);
		return blocks;
	}
	
	public boolean setLoopScope(PathBasicBlock path, boolean absolute) {
		PathBasicBlock parent = path;
		while(!parent.getloops().containsBlock(path)){
			parent = parent.getParent();
			if(parent == null) return false;
		}
		parent.getloops().setLoopScope(path, absolute);	
		return true;
	}
	
	/**
	 * This sets the total number of iterations on a set of loops all with a common header. It
	 * sets the count on the tightest loop around the pc.
	 * @param a pc within the loop
	 * @return return false if this failed to set any loop counts and true for success
	 */
	public boolean setLoopIterationCount(int pc, long count){
		//translate the pc into blocks
		ArrayList<PathBasicBlock> blocks = pcToBlocks(pc);
		
		HashSet<PathBasicBlock> loopHeaders = new HashSet<PathBasicBlock>();
		//crawl up the tree until a block with the loop attached is found
		for(PathBasicBlock block : blocks){
			PathBasicBlock parent = block;
			while(!parent.getloops().containsBlock(block)){
				parent = parent.getParent();
				if(parent == null) return false;
			}
			//parent should now be the block where the loop containing the pc is attached
			loopHeaders.add(parent);
		}
		if(loopHeaders.isEmpty()){
			return false;
		}
		//now we have a hash set of loop headers
		
		//for each loop header set the tightest loop containing the pc as count iterations
		for(PathBasicBlock loopHeader : loopHeaders){
			if(loopHeader.getloops().setLoopMax(pc,  count)){
				loopHeader.getloops().reAssignLoopIterations();
			}
		}
		
		return true;
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
		return m_description.getDescription();
	}
	
	public String getFunction() {
		return m_description.getFunction();
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
		
		if(DEBUG) {
			System.out.println("analysing from 0x" + Integer.toHexString(m_startPc));
			for(int to : m_endPcs){
				System.out.println("analysing to 0x" + Integer.toHexString(to));
			}
		}
		m_loopBlocks.clear();
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
		
		
		
		for (PathBasicBlock completedBlock : this.m_completedPaths){
			addPathPcs(completedBlock);
		}
		
		sortPaths();
		displayWarnings();
		return RouteStatus.COMPLETED;
	}

	private void addPathPcs(PathBasicBlock completedBlock) {
		for (PathBasicBlock pathBlock : completedBlock.getPathBlocks()) {
			m_pcsHit.addAll(pathBlock.getPcsCoveredThis());
			for(LoopUnknown loopUnknown : pathBlock.getloops().getUnknowns()){
				m_unknowns.addUnknown(loopUnknown);
			}
			for (XtaInstruction instruction : pathBlock.getInstructionTrimmed()) {
				if (instruction.canPause()) {
					m_unknowns.addUnknown(new InstructionUnknown(instruction));
				}
			}
			
			for (PathBasicBlock loop : pathBlock.getLoopPaths(completedBlock)){
				addPathPcs(loop);
			}
		}
	}

	
	
	private void followNode(
		PathBasicBlock currentBasicBlock, Stack<Integer> stack, 
		Stack<LinkedList<BasicBlock>> nodeContext, RouteMode mode) throws AbortedException {
		
		boolean alreadyContains = false;
		for(PathBasicBlock b : this.m_allBasicBlocks){
			if(b.getBlockId() == currentBasicBlock.getBlockId()){
				alreadyContains = true;
				break;
			}
		}
		if(!alreadyContains)
			m_loopBlocks.add(currentBasicBlock);
		m_allBasicBlocks.add(currentBasicBlock);
		
		if (DEBUG) System.out.println("begin block " + currentBasicBlock);
		if (m_engine.getAbort()) {
			throw new AbortedException();
		}
		
		if (reachedEnd(currentBasicBlock)){
			handleFinalBlock(currentBasicBlock, mode);
		} else {
			
			m_firstStep = false;
			
			if (updateConstraints(currentBasicBlock)){
				isConstrained(true);
				currentBasicBlock.setStatus(BlockStatus.EXCLUDED);
				return;
			}
			
			checkSupportedInstructions(currentBasicBlock);

			if (hasResursed(currentBasicBlock, nodeContext)){
				String reason = "Recursion with 0x" + Integer.toHexString(currentBasicBlock.getBasicBlock().getInstructions().getFirst().getPc());
				addUnresolvedPath(currentBasicBlock, reason);
				return;
			}
			
			PathBasicBlock loopDestinationBlock = formsLoop(currentBasicBlock, nodeContext);
			if (loopDestinationBlock!=null){
				handelLoop(currentBasicBlock.getParent(), loopDestinationBlock);
				return;
			}
			
			XtaInstruction lastInstruction = currentBasicBlock.getInstructionTrimmed().getLast();
			
			if (lastInstruction.getReturn() && stack.size()>0){
				handleReturn(currentBasicBlock, stack, nodeContext, mode);
				return;
			} else {
				if (currentBasicBlock.getBasicBlock().getNextNodes().size() == 0) {;
					addUnresolvedPath(currentBasicBlock, "no known next block");
				} else {
					for (BasicBlock nextNode : currentBasicBlock.getBasicBlock().getNextNodes()){
						handleNextBlock(currentBasicBlock, stack, nodeContext, mode, lastInstruction, nextNode);
					}
				}
			}
		}	
	}

	private void handleNextBlock(PathBasicBlock currentBasicBlock, Stack<Integer> stack,
			Stack<LinkedList<BasicBlock>> nodeContext, RouteMode mode, XtaInstruction lastInstruction,
			BasicBlock nextNode) throws AbortedException {
		
		
		PathBasicBlock nextPathBasicBlock = new PathBasicBlock(m_engine, nextNode, currentBasicBlock, m_idc);
		if (DEBUG) System.out.println("from block" + currentBasicBlock+" to block: " + nextPathBasicBlock);
		
		Stack<Integer> newStack = (Stack<Integer>) stack.clone();
		Stack<LinkedList<BasicBlock>> newContext = new Stack<LinkedList<BasicBlock>>();
		copyStack(nodeContext, newContext);
		if (lastInstruction.getBranchLink()){
			newStack.add(lastInstruction.getPc() + lastInstruction.getNumBytes());
			newContext.push(new LinkedList<BasicBlock>());
		} else {
			newContext.peek().add(currentBasicBlock.getBasicBlock());
		}
		followNode(nextPathBasicBlock, newStack, newContext, mode);
	}

	private void handleCompleted(PathBasicBlock currentBasicBlock) {
		if (currentBasicBlock.getConstraint().isTrue()){
			if (DEBUG) System.out.println("block " + currentBasicBlock + " completed");
			addCompletePath(currentBasicBlock);
		} else {
			if (DEBUG) System.out.println("block " + currentBasicBlock + " constrained");
			isConstrained(true);
			currentBasicBlock.setStatus(BlockStatus.EXCLUDED);
		}
	}


	private void handleReturn(PathBasicBlock currentBasicBlock, Stack<Integer> stack,
			Stack<LinkedList<BasicBlock>> nodeContext, RouteMode mode) throws AbortedException {
		BasicBlock nextNode = getNode(stack.peek());
		PathBasicBlock nextPathBasicBlock = new PathBasicBlock(m_engine, nextNode, currentBasicBlock, m_idc);
		if(DEBUG) System.out.println("following block: " + nextPathBasicBlock);
		Stack<LinkedList<BasicBlock>> newContext = new Stack<LinkedList<BasicBlock>>();
		copyStack(nodeContext, newContext);
		newContext.pop();
		Stack<Integer> newStack = (Stack<Integer>) stack.clone();
		newStack.pop();
		followNode(nextPathBasicBlock, newStack, newContext, mode);
	}

	private void handleFinalBlock(PathBasicBlock currentBasicBlock, RouteMode mode) {
		for(XtaInstruction inst : currentBasicBlock.getInstructionTrimmed()){
			if(m_endPcs.contains(inst.getPc())){
				
				currentBasicBlock.setLoseFinalInstruction(mode == RouteMode.ABSLOUTE);
				currentBasicBlock.setEndPc(inst.getPc());
				if(updateConstraints(currentBasicBlock)){
					currentBasicBlock.setStatus(BlockStatus.EXCLUDED);
					isConstrained(true);
				} else {
					checkSupportedInstructions(currentBasicBlock);
					handleCompleted(currentBasicBlock);
				}
			}
		}
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

	 

	public boolean setLoopIterations(int pathId, long iterations) {
		for (PathBasicBlock block : m_completedPaths) {
			if (setLoopIterations(block, pathId, iterations))
				return true;
		}
		return false;
	}

	//FIXME
	public boolean setLoopIterations(PathBasicBlock path, int pathId, long iterations) {
		for (PathBasicBlock block : path.getPathBlocks()) {
			for (PathBasicBlock loopPath : block.getLoopPaths(path)) {
				
				if (loopPath.getPathId() == pathId) {
					if(block.setLoopIterations(loopPath, iterations)){
						return true;
					}
				}
				
				if (setLoopIterations(loopPath, pathId, iterations))
					return true;
					
			}
		}
		return false;		
	}

	public Unknowns getUnknowns() {
		return m_unknowns;
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
		if(DEBUG) System.out.println(loopDestinationBlock + " forms a loop with " + pathFormingLoop);
		//this must be true by design of the algorithm
		assert(pathFormingLoop != null);
		
		pathFormingLoop.setPath(s_pathCount++);
		
		loopDestinationBlock.addLoopPath(pathFormingLoop);
		pathFormingLoop.setStatus(BlockStatus.COMPLETE);
		return;
	}
	
	private boolean updateConstraints(PathBasicBlock block) {					
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

	private boolean reachedEnd(PathBasicBlock currentBasicBlock) {
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
		for (BasicBlock node : m_nodes){
			if(node.getPcs().contains(pc)) return node;
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
	}

	/**
	 * @param m_constrained the m_constrained to set
	 */
	public void isConstrained(boolean m_constrained) {
		this.m_constrained = m_constrained;
	}

	/**
	 * return a hash set of all the pcs hit by the currently analysed route
	 * @return HashSet<Integer> 
	 */
	public HashSet<Integer> getPcsHit(){
		return m_pcsHit;
	}
	
	/**
	 * @return the m_constrained
	 */
	public boolean wasConstrained() {
		return m_constrained;
	}

	public String getFullDescription() {
		String fullDescription = String.format("Route(%d) %s\n", getRouteId(), getDescription());
		fullDescription += m_routeTiming.getDescription();
		fullDescription += String.format("    Complete paths: %d, Unresolved paths: %d\n\n", 
    			getCompletedPaths().size(), getUnresolvedPaths().size());
		
		return fullDescription;
	}


}
