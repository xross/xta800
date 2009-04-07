package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class BasicBlock implements Comparable<BasicBlock> {

	int m_nodeId;
	private LinkedList<XtaInstruction> m_instructions;
	private ArrayList<IBufferState> m_iBufferStates;
	private ArrayList<BasicBlock> m_nextNode;
	private ArrayList<BasicBlock> m_previousNode;
	private IBufferState m_worstIbuffer;
	private HashSet<Integer> m_pcs;
	
	BasicBlock(int n) {
		m_nodeId = n;
		m_iBufferStates = new ArrayList<IBufferState>();
		m_instructions = new LinkedList<XtaInstruction>();
		m_nextNode = new ArrayList<BasicBlock>();
		m_previousNode = new ArrayList<BasicBlock>();
		m_pcs = new HashSet<Integer>();
	}
		
	public String toString(){
		return "" +this.m_nodeId + " " + m_instructions.getFirst();
	}
	
	public int getId() {
		return m_nodeId;
	}

	public int compareTo(BasicBlock n) {
		if (n == this)
			return 0;
		return -1;
	}
	
	public ArrayList<IBufferState> getIBufferStates(){
		return m_iBufferStates;
	}

	/**
	 * get the IiBuffer that starts with startIbufferState
	 * @param startIbufferState
	 * @return the iBuffer state
	 */
	public IBufferState getIbuffer(int callingPc) {
		ArrayList<IBufferState> ibuffers = new ArrayList<IBufferState>();
		for(IBufferState ib : m_iBufferStates){
			if(ib.getCallingPc().contains(callingPc)){
				ibuffers.add(ib);
			}
		}
		if(ibuffers.size() == 0){
			assert(false);
			return null;
		} else if(ibuffers.size() == 1){
			return ibuffers.get(0);
		} else {
			int worst=-1;
			IBufferState worstIbuff = null;
			for(IBufferState ib : ibuffers){
				if(ib.m_fnops.size() > worst){
					worst = ib.m_fnops.size();
					worstIbuff = ib;
				}
			}
			return worstIbuff;
		}
	}
	
	public void addInstruction(XtaInstruction instruction) {
		instruction.setBasicBlockId(m_nodeId);
		m_instructions.add(instruction);
		m_pcs.add(instruction.getPc());
	}
	
	public HashSet<Integer> getPcs(){
		return m_pcs;
	}
	
	/**
	 * this should only be used when the complete basic block is wanted,
	 * there are no FNOPS in the list nor is it trimmed to size.
	 */
	public LinkedList<XtaInstruction> getInstructions() {
		return m_instructions;
	}
	
	public ArrayList<BasicBlock> getNextNodes() {
		return m_nextNode;
	}

	public ArrayList<BasicBlock> getPrevoiusNodes() {
		return m_previousNode;
	}

	public boolean setIBuferStart(int initialFullness, int previousPc) {

		IBufferState ib = new IBufferState();
		ib.setInitialIBuffer(initialFullness);
		ib.setCallingPc(previousPc);
		for(IBufferState old : m_iBufferStates){
			if(old.getInitialIBuffer() == initialFullness){
				if(!old.getCallingPc().contains(previousPc)) old.getCallingPc().add(previousPc);
				return false;
			}
		}
		boolean modified = false;
		InstructionBuffer instBuff = new InstructionBuffer();
		instBuff.setPc(m_instructions.getFirst().getPc());
		instBuff.m_numBytes = initialFullness;
		for(XtaInstruction inst :  m_instructions){
			while(!instBuff.canIssue(inst.getNumBytes())){
				instBuff.fetch();
				ib.m_fnops.add(inst.getPc());
			}
			if (!inst.isLoadStore()) {
				instBuff.fetch();
			}
			instBuff.issue(inst.getNumBytes());
			
			
			if(inst.getSuccessors().size() > 0){
				
				for(NextPcType succ : inst.getSuccessors()){
					if(succ.isBranch()){
						instBuff.branchTo(succ.getPc(), inst.isLoadStore());
					} else {
						instBuff.setPc(inst.getPc() + inst.getNumBytes());
					}
					if(m_instructions.getLast() == inst){
						IBufferState copyState = new IBufferState(ib);
						InstructionBuffer copy = new InstructionBuffer(instBuff);
						copyState.setFinalIBuffer(copy.m_numBytes);
						modified = addIbuffState(copyState);
					}
				}
			} else {
				instBuff.setPc(inst.getPc() + inst.getNumBytes());
				if(m_instructions.getLast() == inst){
					ib.setFinalIBuffer(instBuff.m_numBytes);
					return addIbuffState(ib);
				}
			}
			
		}
		return modified;
	}
	
	
	/** 
	 * adds unseen iBuffer states to the list of iBuffers for this basic block. 
	 * @param iBuffer
	 * @return return true if the new iBuffer state was added
	 */
	private boolean addIbuffState(IBufferState ib){
		m_iBufferStates.add(ib);
		if(m_worstIbuffer == null) m_worstIbuffer = ib;
		for(IBufferState w : m_iBufferStates){
			if(w.m_fnops.size()> m_worstIbuffer.m_fnops.size()){
				m_worstIbuffer = w;
			}
		}
		return true;
	}

	/**
	 * If the predecessor is unknown for this basic block then use the worst case fnops.
	 * This returns such a case; 
	 * @return fullness of iBuffer in words in the worst case
	 */
	public IBufferState getWorstIbufferStart() {
		IBufferState worst = null;
		int worstCount = -1;
		for(IBufferState ib: m_iBufferStates){
			if(ib.m_fnops.size()> worstCount){
				worst = ib;
				worstCount = ib.m_fnops.size();
			}
		}
		return worst;
	}

	/**
	 * this makes the adds an iBuffer to the 
	 * @return
	 */
	public IBufferState getInitialIBuffer() {
		// TODO Auto-generated method stub
		return null;
	}
}
class IBufferState {
	private int initialIBuffer;
	private int finalIBuffer;
	private int nextPc;
	private ArrayList<Integer> callingPcs;
	ArrayList<Integer> m_fnops;

	public IBufferState() {
		initialIBuffer = 0;
		finalIBuffer = 0;
		m_fnops = new ArrayList<Integer>();
		callingPcs = new ArrayList<Integer>();
	}

	public IBufferState(IBufferState copy) {
		initialIBuffer = copy.initialIBuffer;
		finalIBuffer = copy.finalIBuffer;
		m_fnops = new ArrayList<Integer>(copy.m_fnops);
		callingPcs = new ArrayList<Integer>(copy.callingPcs);
		nextPc = copy.nextPc;
	}
	
	public ArrayList<Integer> getCallingPc() {
		return callingPcs;
	}

	public void setCallingPc(int callingPc) {
		if(!callingPcs.contains(callingPc) )callingPcs.add(callingPc);
	}
	/**
	 * returns the initial iBuffer fullness in words
	 * @return initial count of words in the iBuffer
	 */
	public int getInitialIBuffer() {
		return initialIBuffer;
	}

	/**
	 * set the count of words in the iBuffer at the start of the basic block 
	 * @param initialIBuffer
	 */
	public void setInitialIBuffer(int initialIBuffer) {
		this.initialIBuffer = initialIBuffer;
	}
	
	/**
	 * returns the final iBuffer fullness in words
	 * @return final count of words in the iBuffer
	 */
	public int getFinalIBuffer() {
		return finalIBuffer;
	}

	/**
	 * set the count of words in the iBuffer at the end of the basic block 
	 * @param finalIBuffer
	 */
	public void setFinalIBuffer(int finalIBuffer) {
		this.finalIBuffer = finalIBuffer;
	}

	/**
	 * set the next PC for an iBuffer stream
	 * @param nextPc the nextPc to set
	 */
	public void setNextPc(int nextPc) {
		this.nextPc = nextPc;
	}

	/**
	 * get the PC of the 
	 * @return the nextPc
	 */
	public int getNextPc() {
		return nextPc;
	}
	
}