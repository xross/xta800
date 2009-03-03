package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.Vector;

import com.xmos.xta.engine.Status.BlockStatus;
import com.xmos.xta.engine.constraint.Constraint;
import com.xmos.xta.engine.constraint.ConstraintAnd;
import com.xmos.xta.engine.constraint.ConstraintFactory;

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
	private Constraint m_constraint;
	private LinkedList<XtaInstruction> m_instructionsTrimmed;
	private LinkedList<XtaInstruction> m_instructionStream;
	private IBufferState m_ibufferState;
	private InstructionDecodeCache m_idc;
	private Engine m_engine;
	//i don't want this here so i'll move it later
	private Unresolved m_unresolved;
	
	public PathBasicBlock(Engine engine, BasicBlock basicBlock, PathBasicBlock parentBasicBlock,
			InstructionDecodeCache idc){
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
		m_idc = idc;
		m_engine = engine;
		if(parentBasicBlock == null){
			m_constraint = m_engine.getConstraint();
		} else {
			m_constraint = parentBasicBlock.getConstraint().clone();
		}
		createIBuffer();
		createTrimmedInstructions(); 
		createInstructionStream(); 
	}
	
	public PathBasicBlock(Engine engine, BasicBlock basicBlock, PathBasicBlock parentBasicBlock, 
			int instPc, boolean start, InstructionDecodeCache idc){
		m_engine = engine;
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
		m_idc=idc;
		if(parentBasicBlock == null){
			m_constraint = m_engine.getConstraint();
		} else {
			m_constraint = parentBasicBlock.getConstraint().clone();
		}
		createIBuffer();
		createTrimmedInstructions(); 
		createInstructionStream(); 
		
	}
	
	/**
	 * get the initial pc of this block
	 * @return the initial pc of this block
	 */
	public int getStartPc(){
		return m_startInstruction;
	}
	
	/**
	 * get the end pc of this block
	 * @return the end pc of this block
	 */
	public int getEndPc(){
		return m_endInstruction;
	}
	
	public BasicBlockStats getBlockStats(){
		return m_blockStats;
	}
	

	public Unresolved getUnresolved() {
		return m_unresolved;
	}
	
	/**
	 * sets the status of this block
	 * @param status
	 */
	public void setStatus(BlockStatus status) {
		m_status = status;
	}
	
	/**
	 * Set the path id for this block. Should only be called when a loop is formed or a 
	 * path is completed
	 * @param id
	 */
	public void setPath(int i) {
		m_pathId = i;
	}
	
	/**
	 * returns a string representation of the block
	 */
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
	
	
	/**
	 * 
	 * @return
	 */
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

	
	/**
	 * for a given initial iBuffer state this returns the actual stream of instructions that 
	 * would occur. It is trimmed to the size of the block
	 */
	public LinkedList<XtaInstruction> getInstructionStream(){
		return m_instructionStream;
	}

	/**
	 * this returns the instructions from the basic block trimmer to the correct size.
	 * It includes no FNOPs
	 */
	public LinkedList<XtaInstruction> getInstructionTrimmed(){
		return m_instructionsTrimmed;
	}
	
	/**
	 * returns the iBuffer of is block
	 * @return returns the iBuffer of is block
	 */
	public IBufferState getIBufferState(){
		return m_ibufferState;
	}
	
	private void createInstructionStream() {
		IBufferState currentIBuffer = getIBufferState();

		if (currentIBuffer.m_fnops.size() == 0) {
			m_instructionStream = getInstructionTrimmed();
		} else {
			LinkedList<XtaInstruction> stream = new LinkedList<XtaInstruction>();
			for (XtaInstruction inst : getInstructionTrimmed()) {
				int fnopNum = 0;
				for (int pc : currentIBuffer.m_fnops) {
					if (pc == inst.getPc()) {
						XtaInstruction fnop = m_idc.getFNop(pc, fnopNum++);
						stream.add(fnop);
					}
				}
				stream.add(inst);
			}
			m_instructionStream = stream;
		}
	}

	private void createIBuffer() {
		if (this.getParent() != null) {
			if (m_startInstruction == getBasicBlock().getInstructions().getFirst().getPc()) {
				m_ibufferState = getBasicBlock().getIbuffer(getParent().getEndPc());
			} else {
				// FIXME - this should be get the correct starting ibuff state,
				// not the worst
				m_ibufferState = getBasicBlock().getWorstIbufferStart();
			}
		} else {
			//m_ibufferState = getBasicBlock().getIbuffer(0);
			m_ibufferState = getBasicBlock().getWorstIbufferStart();
		}
	}
	
	private void createTrimmedInstructions() {
		if(m_startInstruction == getBasicBlock().getInstructions().getFirst().getPc() &&
				m_endInstruction == getBasicBlock().getInstructions().getLast().getPc()){
			m_instructionsTrimmed = getBasicBlock().getInstructions();
		} else {
			LinkedList<XtaInstruction> pathBasicBlockInstructions = new LinkedList<XtaInstruction>();
			boolean copy = false;
			for(XtaInstruction inst : m_basicBlock.getInstructions()){
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
			m_instructionsTrimmed = pathBasicBlockInstructions;
		}
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
		Vector<XtaInstruction> insts = new Vector<XtaInstruction>();;
		for (PathBasicBlock block : getPathBlocks()) {
			insts.addAll(block.getInstructionStream());
			if (recurse) {
				for (PathBasicBlock loopPath : block.getLoopPaths()) {
					insts.addAll(loopPath.getInstructions(true));
				}
			}
		}
		doneWithPathBlocks();
		return insts;
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
		for(XtaInstruction inst : getInstructionTrimmed()){
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
	

	/**
	 * returns the time taken for this block dependent on the calling pc
	 * @param the pc of the block that preceded this one
	 * @return double this time taken for this block
	 */
	public double getTimeTakenThis() {
		double time = 0.0;
		for (XtaInstruction instruction : getInstructionStream()) {
			time += instruction.getTimeTaken();
		}
		return time;
	}

	/**
	 * returns the number of cycles this blocks takes
	 * @param callingPc
	 * @return
	 */
	public int getNumCyclesThis() {
		int numCycles = 0;
		for (XtaInstruction instruction : getInstructionStream()) {
			numCycles += instruction.getNumCycles();
		}
		return numCycles;
	}
	
	/**
	 * returns the number of instructions in the basic block, not including fnops
	 * and trimmed
	 * @param callingPc
	 * @return 
	 */
	public int getNumInstructionsThis() {
		return getInstructionTrimmed().size();
	}

	/**
	 * returns the maximum number of fnops in the trimmed basic block for a given calling pc
	 * @param callingPc
	 * @return
	 */
	public int getNumFNopsThis() {
		int count = 0;
		for(XtaInstruction inst : getInstructionStream()){
			if(inst.isFnop()) count++;
		}
		return count;
	}
			
	/**
	 * returns the number of pausable instructions in the basic block,
	 * this is independent of calling PC
	 */
	public int getNumPausablesThis() {
		int count = 0;
		for(XtaInstruction inst : getInstructionStream()){
			if (inst.canPause()) count++;
		}
		return count;
	}	
	
	/**
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


	public Constraint getConstraint() {
		return m_constraint;
	}

	public void setConstraint(Constraint constraint){
		m_constraint = constraint;
	}
	



}
