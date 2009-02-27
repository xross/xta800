package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Vector;

import com.xmos.xta.engine.Status.BlockStatus;

public class PathBasicBlock implements Comparable<PathBasicBlock>{
	private static int pathBasicBlockCount;
	private BasicBlock m_basicBlock;
	private PathBasicBlock m_parentBasicBlock;
	private ArrayList<PathBasicBlock> m_loopPaths;
	private int m_seen;
	private int m_pathId;
	private int m_blockId;
	private HashMap<PathBasicBlock, PathAttributes> m_loopPathAttributes;
	private BasicBlockStats m_blockStats;
	private BlockStatus m_status;
	private int m_startInstruction;
	private int m_endInstruction;
	
	//i don't want this here so i'll move it later
	private Unresolved m_unresolved;
	
	public PathBasicBlock(BasicBlock basicBlock, PathBasicBlock parentBasicBlock){
		
		m_parentBasicBlock = parentBasicBlock;
		m_seen = 0;
		m_loopPaths = new ArrayList<PathBasicBlock>();
		m_blockId = pathBasicBlockCount++;
		m_pathId = -1;
		m_loopPathAttributes = new HashMap<PathBasicBlock, PathAttributes>();
		m_blockStats = new BasicBlockStats(this);
		m_status = BlockStatus.SEARCHING;
		m_startInstruction = basicBlock.getInstructions().getFirst().getPc();
		m_endInstruction = basicBlock.getInstructions().getLast().getPc();
		m_basicBlock = basicBlock;
	}
	
	public PathBasicBlock(BasicBlock basicBlock, PathBasicBlock parentBasicBlock, 
			int instPc, boolean start){
		m_basicBlock = basicBlock;
		m_parentBasicBlock = parentBasicBlock;
		m_seen = 0;
		m_loopPaths = new ArrayList<PathBasicBlock>();
		m_blockId = pathBasicBlockCount++;
		m_pathId = -1;
		m_loopPathAttributes = new HashMap<PathBasicBlock, PathAttributes>();
		m_blockStats = new BasicBlockStats(this);
		m_status = BlockStatus.SEARCHING;
		if(start){
			m_startInstruction = instPc;
			m_endInstruction = basicBlock.getInstructions().getLast().getPc();
		} else {
			m_startInstruction = basicBlock.getInstructions().getFirst().getPc();
			m_endInstruction = instPc;
		}
	}
	
	public int getStartPc(){
		return m_startInstruction;
	}
	
	public int getEndPc(){
		return m_endInstruction;
	}
	
	public BasicBlockStats getBlockStats(){
		return m_blockStats;
	}
	

	public Unresolved getUnresolved() {
		return m_unresolved;
	}
	
	public void setStatus(BlockStatus status) {
		m_status = status;
	}
	
	public void setPath(int i) {
		m_pathId = i;
	}
	
	public String toString(){
		return "PathBasicBlock " + m_pathId + " " + m_basicBlock;
	}
	
	public int getPathId(){
		return m_pathId;
	}
	
	public int getBlockId(){
		return m_blockId;
	}
	
	public BasicBlock getBasicBlock(){
		return m_basicBlock;
	}
	
	public PathBasicBlock getParent(){
		return m_parentBasicBlock;
	}
	
	public ArrayList<PathBasicBlock> getPathBlocks() {
		ArrayList<PathBasicBlock> paths = new ArrayList<PathBasicBlock>();
		PathBasicBlock block = this;
		
		do {
			paths.add(0, block);
			if (block.m_loopPaths.contains(this)) {
				break;
			}
			block = block.m_parentBasicBlock;
		} while (block != null);
		
		if (block != null)
			block.m_seen++;

		return paths;
	}
	
	public void doneWithPathBlocks() {
		PathBasicBlock block = this;
		do {
			if (block.m_loopPaths.contains(this)) {
				break;
			}
			block = block.m_parentBasicBlock;
		} while (block != null);

		if (block != null) {
			assert (block.m_seen > 0);
			block.m_seen--;
		}
	}
	
	public ArrayList<PathBasicBlock> getLoopPaths() {
		if (m_seen == 0) {
			return m_loopPaths;
		} else {
			return new ArrayList<PathBasicBlock>();
		}
	}
	
	public PathAttributes getLoopPathAttributes(PathBasicBlock block) {
		PathAttributes attributes = m_loopPathAttributes.get(block);
		return attributes;
	}
	
	HashMap<PathBasicBlock, PathAttributes> getLoopPathAttributes() {
		return m_loopPathAttributes;
	}
	
	public BlockStatus getStatus() {
		return m_status;
	}

	public int numLoopPaths() {
		if (m_seen == 0) {
			return m_loopPaths.size();
		} else {
			return 0;
		}
	}
	
	public int compareTo(PathBasicBlock path) {
		return (int) (path.getTimeTaken(true) - getTimeTaken(true));
	}
	
	public int getNumPathBlocks() {
		int numPathBlocks = 0;
		PathBasicBlock block = this;

		do {
			++numPathBlocks;
			if (block.m_loopPaths.contains(this)) {
				break;
			}
			block = block.m_parentBasicBlock;
		} while (block != null);

		return numPathBlocks;
	}
	
	public int getNumIterations() {
		PathBasicBlock parent = this;
		while (parent != null) {
			if (parent.m_loopPaths.contains(this)) {
				PathAttributes attributes = parent.getLoopPathAttributes(this);
				return attributes.getNumIterations();
			}
			parent = parent.m_parentBasicBlock;
		}
		return 1;
	}

	public int getLength() {
		int pathLength = 0;
		for (PathBasicBlock block : getPathBlocks()) {
			pathLength += block.getNumInstructionsThis();
		}
		return pathLength;
	}

	public void getAllBlocks(TreeSet<PathBasicBlock> allBlocks) {
		for (PathBasicBlock block : getPathBlocks()) {
			allBlocks.add(block);
			for (PathBasicBlock loopPath : block.getLoopPaths()) {
				loopPath.getAllBlocks(allBlocks);
			}
		}
		doneWithPathBlocks();
	}

	public Vector<XtaInstruction> getInstructions(boolean recurse) {
		Vector<XtaInstruction> insts = new Vector<XtaInstruction>();

		for (PathBasicBlock block : getPathBlocks()) {
			insts.addAll(block.getInstructionsThis());
			if (recurse) {
				for (PathBasicBlock loopPath : block.getLoopPaths()) {
					insts.addAll(loopPath.getInstructions(true));
				}
			}
		}
		doneWithPathBlocks();
		return insts;
	}
	
	
	/*
	 * gets the actual instructions of this block: trimmed with fnops included
	 */
	public Vector<XtaInstruction> getInstructionsThis() {
		Vector<XtaInstruction> pathBasicBlockInstructions = new Vector<XtaInstruction>();
		boolean copy = false;
		for(XtaInstruction inst : m_basicBlock.getInstructionStream()){
			if(inst.getPc() == m_startInstruction){
				copy = true;
			}
			if(copy) {
				pathBasicBlockInstructions.add(inst);
			}
			if(inst.getPc() == m_endInstruction && !inst.isFnop()){
				copy = false;
			}
		}
		
		return pathBasicBlockInstructions;
	}
	
	public HashSet<Integer> getPcsCovered(boolean recurse) {
		HashSet<Integer> pcsCovered = new HashSet<Integer>();
		for (PathBasicBlock block : getPathBlocks()) {
			pcsCovered.addAll(block.getPcsCoveredThis());		
			if (recurse) {
				for (PathBasicBlock loopPath : block.getLoopPaths()) {
					pcsCovered.addAll(loopPath.getPcsCovered(true));
				}
			}
		}
		doneWithPathBlocks();		
		return pcsCovered;
	}
	
	public HashSet<Integer> getPcsCoveredThis() {
		//TODO make this fast
		HashSet<Integer> pcs = new HashSet<Integer>();
		for(XtaInstruction inst : getInstructionsThis()){
			pcs.add(inst.getPc());
		}
		return pcs;
	}
	
	public HashSet<Integer> getPcsCoveredLoopsOnly() {
		HashSet<Integer> pcsCovered = new HashSet<Integer>();

		for (PathBasicBlock loopPath : getLoopPaths()) {
			pcsCovered.addAll(loopPath.getPcsCovered(true));
		}
		return pcsCovered;
	}
	
	// --------------------------------------------------------------------- //
	// Statistics
	// --------------------------------------------------------------------- //	
	
	public double getTimeTaken(boolean recurse) { return m_blockStats.getTimeTaken(recurse);}
	public int getNumInstructions(boolean recurse) { return m_blockStats.getNumInstructions(recurse);}
	public int getNumFNops(boolean recurse) { return m_blockStats.getNumFNops(recurse);}
	public int getNumCycles(boolean recurse) { return m_blockStats.getNumCycles(recurse);}
	public int getNumPausables(boolean recurse) { return m_blockStats.getNumPausables(recurse);}
	public int getNumUnknowns(boolean recurse) { return m_blockStats.getNumUnknowns(recurse);}
	

	// --------------------------------------------------------------------- //
	// 'This' statistics
	// --------------------------------------------------------------------- //		
	public double getTimeTakenThis() {
		double time = 0.0;
		for (XtaInstruction instruction : getInstructionsThis()) {
			time += instruction.getTimeTaken();
		}
		return time;
	}

	public int getNumCyclesThis() {
		int numCycles = 0;
		for (XtaInstruction instruction : getInstructionsThis()) {
			numCycles += instruction.getNumCycles();
		}
		return numCycles;
	}
	
	/*
	 * returns the number of instructions in the basic block, includeing fnops
	 * and trimmed
	 */
	public int getNumInstructionsThis() {
		return getInstructionsThis().size();
	}

	/*
	 * returns the maximum number of fnops in the basic block
	 */
	public int getNumFNopsThis() {
		int maxFnops =0;
		for(IBufferState ib : m_basicBlock.getIBufferStates()){
			maxFnops = Math.max(maxFnops, ib.m_fnops.size());
		}
		return maxFnops;
	}
			
	/*
	 * returns the number of pausable instructions in the basic block
	 */
	public int getNumPausablesThis() {
		int count = 0;
		for(XtaInstruction inst : getInstructionsThis()){
			if (inst.canPause()) count++;
		}
		return count;
	}	
	
	/*
	 * returns the number of shown and not set Unknowns on the basic block
	 */
	public int getNumUnknownsThis() {
		int num = 0;
		for (Unknown unknown : m_basicBlock.getUnknowns()) {
			if (unknown.isShown() && !unknown.isSet())
				num++;
		}
		return num;
	}
	
	// --------------------------------------------------------------------- //
	// 'Loops only' statistics
	// --------------------------------------------------------------------- //		
	
	public double getTimeTakenLoopsOnly() {	return m_blockStats.getTimeTakenLoopsOnly();}
	public int getNumCyclesLoopsOnly() { return m_blockStats.getNumCyclesLoopsOnly();}
	public int getNumInstructionsLoopsOnly() { return m_blockStats.getNumInstructionsLoopsOnly();}
	public int getNumFNopsLoopsOnly() {	return m_blockStats.getNumFNopsLoopsOnly();}
	public int getNumPausablesLoopsOnly() {	return m_blockStats.getNumPausablesLoopsOnly();}
	public int getNumUnknownsLoopsOnly() { return m_blockStats.getNumUnknownsLoopsOnly();}

	public void setUnresolved(Unresolved unresolved) {
		m_unresolved= unresolved;
		
	}

	
	



}
