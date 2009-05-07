package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import com.xmos.xta.engine.RouteDescription;
import com.xmos.xta.engine.Status.BlockStatus;
import com.xmos.xta.engine.constraint.Constraint;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.unknowns.PausableInstructionUnknown;
import com.xmos.xta.engine.unknowns.LoopIterationsUnknown;
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
	private RouteDescription m_description;
	private ArrayList<Unresolved> m_unresolvedPaths;
	private InstructionDecodeCache m_idc;
	private InstructionMnemonics m_mnenonics;
	private Engine m_engine;	
	private RouteTiming m_routeTiming;
	private boolean m_constrained;
	private Unknowns m_unknowns;
	private Constraint m_constraint;
	private Map<PathBasicBlock, HashSet<Integer>> m_uniqueLoopPathPcs;
	
	private HashSet<Integer> m_pcsHit;
	
	public static void resetRouteCount() {
		s_routeCount = 0;
	}
	
	public Route(Config config, Engine engine, int startPc, TreeSet<Integer> endPcs, 
			ArrayList<BasicBlock> nodes, InstructionDecodeCache idc, 
			InstructionMnemonics mnenonics, RouteDescription description){
		m_engine = engine;
		m_constraint = engine.getConstraint().clone();
		m_completedPaths = new ArrayList<PathBasicBlock>();
		m_allBasicBlocks = new ArrayList<PathBasicBlock>();
		m_unresolvedPaths = new ArrayList<Unresolved>();
		m_startPc = startPc;
		m_endPcs = endPcs;
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
		m_uniqueLoopPathPcs = new HashMap<PathBasicBlock, HashSet<Integer>>();
	}
			
	/**
	 * This sets the total number of iterations on a particular paths through 
	 * set of loops all with a common header. It sets the count on the tightest loop around the pc.
	 * @param a pc within the loop
	 * @return return false if this failed to set any loop counts or the pc was on more than 
	 * one path, and true for success
	 */	
	public boolean setLoopIterations(int pc, Long count){
		HashSet<PathBasicBlock> loopHeaders = getLoopHeaders(pc);
		if (loopHeaders == null)
			return false;
		
		//for each loop header 
		for(PathBasicBlock loopHeader : loopHeaders){
			List<PathBasicBlock> loopList = loopHeader.getLoops().getLoopList();
			int numPathsHit = 0;
			PathBasicBlock loopFound = null;
			
			for (PathBasicBlock loop : loopList) {
				for (PathBasicBlock loopBlock : loop.getPathBlocks()) {
					
					if (loopBlock.getPcsCoveredThis().contains(pc)) {
						++numPathsHit;
						loopFound = loop;
					}
				}
			}
			
			// If this pc is only on one of the paths through this loop, then 
			// we can set the loop iterations on it.
			if (numPathsHit == 1) {
				if (!loopHeader.setLoopIterations(loopFound, count)) {
					return false;
			    }
			} else {
				return false;
			}
		}		
		return true;
	}
	
	/**
	 * This sets the total number of iterations on a set of loops all with a common header. It
	 * sets the count on the tightest loop around the pc.
	 * @param a pc within the loop
	 * @return return false if this failed to set any loop counts and true for success
	 */
	public boolean setLoopMaxIterations(int pc, Long count){
		HashSet<PathBasicBlock> loopHeaders = getLoopHeaders(pc);
		if (loopHeaders == null)
			return false;
		
		//for each loop header set the tightest loop containing the pc as count iterations
		for(PathBasicBlock loopHeader : loopHeaders){
			if (loopHeader.getLoops().setLoopMax(pc,  count)){
				loopHeader.getLoops().reAssignLoopIterations();
			} else {
				return false;
			}
		}		
		return true;
	}
	
	/**
	 * This sets the scope on a set of loops all with a common header. It
	 * sets the scope on the tightest loop around the pc.
	 * @param a pc within the loop
	 * @return returns false if this failed to set the scope or the pc was on more than 
	 * one path, and true for success
	 */	
	public boolean setLoopScope(int pc, boolean absolute) {
		HashSet<PathBasicBlock> loopHeaders = getLoopHeaders(pc);
		if (loopHeaders == null)
			return false;
		
		//for each loop header set the scope on the tightest loop containing the pc
		for(PathBasicBlock loopHeader : loopHeaders){
			loopHeader.getLoops().setLoopScope(pc, absolute);
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
	
	public Set<Integer> getUniqueLoopPathPcs(PathBasicBlock loopPath) {
		return m_uniqueLoopPathPcs.get(loopPath);
	}
	
	public RouteStatus analyse(RouteMode mode){		
		if (DEBUG) {
			System.out.println("Analysing from 0x" + Integer.toHexString(m_startPc));
			for(int to : m_endPcs){
				System.out.println("Analysing to 0x" + Integer.toHexString(to));
			}
		}
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

		for (PathBasicBlock completedBlock : m_completedPaths){
			if (!addPathPcs(completedBlock)) {			
				return RouteStatus.ABORTED;
			}			
		}
		for (Unresolved unresolvedBlock : m_unresolvedPaths){
			if (!addPathPcs(unresolvedBlock.getBlock())) {
				return RouteStatus.ABORTED;
			}			
		}
		
		createUniquePathPcs(); 
		sortPaths();
		displayWarnings();
		
		if (DEBUG) 
			printCompletedPaths();
		
		return RouteStatus.COMPLETED;
	}

	private boolean addPathPcs(PathBasicBlock completedBlock) {
		if (m_engine.getAbort())
			return false;
		
		for (PathBasicBlock pathBlock : completedBlock.getPathBlocks()) {
			m_pcsHit.addAll(pathBlock.getPcsCoveredThis());
			for (LoopIterationsUnknown loopUnknown : pathBlock.getLoops().getUnknowns()){
				m_unknowns.addUnknown(loopUnknown);
			}
			for (XtaInstruction instruction : pathBlock.getInstructionTrimmed()) {
				if (instruction.canPause()) {
					m_unknowns.addUnknown(new PausableInstructionUnknown(instruction));
				}
			}
			
			for (PathBasicBlock loop : pathBlock.getLoopPaths(completedBlock)){
				if (!addPathPcs(loop))
					return false;
			}			
		}
		return true;
	}
	
	private void followNode(
		PathBasicBlock currentBasicBlock, Stack<Integer> stack, 
		Stack<LinkedList<BasicBlock>> nodeContext, RouteMode mode) throws AbortedException {		
		m_allBasicBlocks.add(currentBasicBlock);
		
		if (DEBUG) System.out.println("FollowNode: " + currentBasicBlock);	
		
		if (m_engine.getAbort()) {
			throw new AbortedException();
		}
		
		if (reachedEnd(currentBasicBlock)){
			handleFinalBlock(currentBasicBlock, mode);
		} else {
			
			m_firstStep = false;
			
			if (updateConstraints(currentBasicBlock)){
				setConstrained(true);
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
				handleLoop(currentBasicBlock.getParent(), loopDestinationBlock);
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
		if (DEBUG) System.out.println("HandleNext: " + currentBasicBlock + " -> " + nextPathBasicBlock);
		
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
			if (DEBUG) System.out.println("HandleCompleted " + currentBasicBlock);
			addCompletePath(currentBasicBlock);
		} else {
			if (DEBUG) System.out.println("HandleConstrained " + currentBasicBlock);
			setConstrained(true);
			currentBasicBlock.setStatus(BlockStatus.EXCLUDED);
		}
	}


	private void handleReturn(PathBasicBlock currentBasicBlock, Stack<Integer> stack,
			Stack<LinkedList<BasicBlock>> nodeContext, RouteMode mode) throws AbortedException {
		BasicBlock nextNode = getNode(stack.peek());
		PathBasicBlock nextPathBasicBlock = new PathBasicBlock(m_engine, nextNode, currentBasicBlock, m_idc);
		if(DEBUG) System.out.println("HandleReturn: " + nextPathBasicBlock);
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
				if (updateConstraints(currentBasicBlock)){
					currentBasicBlock.setStatus(BlockStatus.EXCLUDED);
					setConstrained(true);
				} else {
					checkSupportedInstructions(currentBasicBlock);
					handleCompleted(currentBasicBlock);
				}
			}
		}
	}
	public PathBasicBlock getBlock(int id) {
		for (PathBasicBlock pbb : m_allBasicBlocks){
			if (pbb.getBlockId() == id){
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
	
	public void setConstrained(boolean constrained) {
		m_constrained = constrained;
	}

	/**
	 * return a hash set of all the pcs hit by the currently analysed route
	 * @return HashSet<Integer> 
	 */
	public HashSet<Integer> getPcsHit(){
		return m_pcsHit;
	}
	
	public boolean wasConstrained() {
		return m_constrained;
	}

	public Constraint getConstraint() {
		return m_constraint;
	}
	
	public String getFullDescription() {
		String fullDescription = String.format("Route(%d) %s\n", getRouteId(), getDescription());
		fullDescription += m_routeTiming.getDescription(true);
		fullDescription += String.format("    Complete paths: %d, Unresolved paths: %d\n\n", 
    			getCompletedPaths().size(), getUnresolvedPaths().size());
		
		return fullDescription;
	}

	public Reference getStartPoint() {
		return m_description.getStartPoint();
	}

	public Reference getEndPoint() {
		return m_description.getEndPoint();
	}
	
	public void sortPaths() {
		Collections.sort(m_completedPaths);
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

	private void handleLoop(PathBasicBlock pathFormingLoop, PathBasicBlock loopDestinationBlock) {
		if(DEBUG) System.out.println("HandleLoop: " + loopDestinationBlock + " -> " + pathFormingLoop);
		//this must be true by design of the algorithm
		assert(pathFormingLoop != null);
		
		pathFormingLoop.setPath(s_pathCount++);
		
		loopDestinationBlock.addLoopPath(pathFormingLoop);
		pathFormingLoop.setStatus(BlockStatus.COMPLETE);
		return;
	}
	
	private boolean updateConstraints(PathBasicBlock block) {					
		for (XtaInstruction inst : block.getInstructionTrimmed()){
			if (block.getConstraint().isFalse(inst.getPc())){
				return true;
			}
		}		
		return false;
	}
	
	private void checkSupportedInstructions(PathBasicBlock block) {
		for (XtaInstruction inst : block.getInstructionTrimmed()){
			
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
	
	private HashSet<PathBasicBlock> getLoopHeaders(int pc) {
		//translate the pc into blocks
		ArrayList<PathBasicBlock> blocks = pcToBlocks(pc);
		
		HashSet<PathBasicBlock> loopHeaders = new HashSet<PathBasicBlock>();
		//crawl up the tree until a block with the loop attached is found
		for(PathBasicBlock block : blocks){
			PathBasicBlock parent = block;
			while(!parent.getLoops().containsBlock(block)){
				parent = parent.getParent();
				
				// Check if we have hit the root of this path. If so, then 
				// this path block is not within a loop.
				if (parent == null) 
					break;
			}
			
			if (parent != null) {
     			// Parent should now be the block where the 
				// loop containing the pc is attached.
	    		loopHeaders.add(parent);
			}
		}
		if (loopHeaders.isEmpty()){
			return null;
		}
		//now we have a hash set of loop headers				
		return loopHeaders;		
	}		
	
	/**
	 * this takes a pc and returns a list of the blocks that contain it
	 * @param pc
	 * @return
	 */
	private ArrayList<PathBasicBlock> pcToBlocks(int pc){
		ArrayList<PathBasicBlock> blocks = new ArrayList<PathBasicBlock>();
		
		for(PathBasicBlock pbb : m_allBasicBlocks){
			for(XtaInstruction inst : pbb.getInstructionTrimmed()){
				if(inst.getPc() == pc){
					blocks.add(pbb);
					break;
				}
			}
		}
		return blocks;
	}
	
	/**
	 * This method builds up a hash of all the loop paths on this 
	 * route containing the pc's that are unique to that particular path
	 * through the loop.
	 */
	private void createUniquePathPcs() {
		for (PathBasicBlock block : m_allBasicBlocks){        	
			processLoopAttachment(block.getLoops().getSubLoops());
		}
	}

	private void processLoopAttachment(List<LoopAttachment> loopAttachments) {	
		if (loopAttachments.size() == 0)
			return;
		
		Map<PathBasicBlock, HashSet<Integer>> uniqueLoopPathPcs = 
			new HashMap<PathBasicBlock, HashSet<Integer>>();
		
		// Creates a hash map of loop paths to all pc's on the loop path.
		for (LoopAttachment loopAttachment : loopAttachments) {
			PathBasicBlock loopBlock = loopAttachment.getLoop();
			 
			HashSet<Integer> allPcsOnThisPath = new HashSet<Integer>();
			for (PathBasicBlock pathBlock : loopBlock.getPathBlocks()) {
				allPcsOnThisPath.addAll(pathBlock.getPcsCoveredThis());
			}
			uniqueLoopPathPcs.put(loopBlock, allPcsOnThisPath);
						
			// Process the loops at the next level down...
			processLoopAttachment(loopAttachment.getSubLoops());
		}
		
		// Now traverse all the pc's in all the loop paths at this level, 
		// and ensure that they are all unique.
		for (PathBasicBlock loopPath : uniqueLoopPathPcs.keySet()) {	
			
			Iterator<Integer> iter = uniqueLoopPathPcs.get(loopPath).iterator();
			while (iter.hasNext()) {
				Integer pc = iter.next();
				
				boolean found = false;				
				for (PathBasicBlock l : uniqueLoopPathPcs.keySet()) {
				    if (loopPath != l) {
				    	Set<Integer> pcs = uniqueLoopPathPcs.get(l);
				    	if (pcs.contains(pc)) {
				    		pcs.remove(pc);
				    		found = true;
				    	}				    					    	
				    }
				}				
				if (found) {
					iter.remove();
				}								
			}						
		}
		m_uniqueLoopPathPcs.putAll(uniqueLoopPathPcs);
	}	
	
	// --------------------------------------------------------------------- //
	// Helper Debug Methods.
	// --------------------------------------------------------------------- //
	public void printCompletedPaths() {
		System.out.printf("\n");
		System.out.printf("==============================\n");
		System.out.printf("COMPLETED PATHS\n");
		System.out.printf("==============================\n");
		summarisePaths(m_completedPaths, "");
	}
	
    private void summarisePaths(List<PathBasicBlock> paths, String indent) {
        for (PathBasicBlock path : paths) {
        	summarisePath(path, indent);
        }
    }
    
    private void summarisePath(PathBasicBlock path, String indent) {
   	    displayPathLevelInfo(path, indent);
   	    
        for (PathBasicBlock block : path.getPathBlocks()) {
            displayBlockLevelInfo(block, indent);                        	
          	displayLoopLevelInfo(block, indent, path);          	
        }
    }

    private void displayPathLevelInfo(PathBasicBlock path, String indent) {            	
		System.out.printf("%spath(%-2d, b%d, p%d)\n", indent, 
				path.getPathId(),
				path.getBlockId(),
				path.getPathBasicBlockId());
    }

    private void displayBlockLevelInfo(PathBasicBlock block, String indent) {
		System.out.printf("%s    block(%-2d, b%d, p%d): ", indent, 
				block.getPathId(),
				block.getBlockId(),
				block.getPathBasicBlockId());
		
        for (XtaInstruction xtaInstruction : block.getInstructionStream()) {
  			System.out.printf("[0x%05x]", xtaInstruction.getPc());
        }
        System.out.printf("\n");		
    }

    private void displayLoopLevelInfo(PathBasicBlock path, String indent, PathBasicBlock base) {
        List<PathBasicBlock> loopPaths = path.getLoopPaths(base);
        if (loopPaths.size() > 0) {
        	summarisePaths(loopPaths, indent + "        ");
        }
    }
}

