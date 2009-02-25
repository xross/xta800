package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.LinkedList;

public class BasicBlock implements Comparable<BasicBlock> {

	int m_nodeId;
	private LinkedList<XtaInstruction> m_instructions;
	private ArrayList<IBufferState> m_iBufferStates;
	private ArrayList<BasicBlock> m_nextNode;
	private ArrayList<BasicBlock> m_previousNode;
	private ArrayList<Unknown> m_unknowns;
	private IBufferState m_worstIbuffer;
	
	BasicBlock(int n) {
		m_nodeId = n;
		m_iBufferStates = new ArrayList<IBufferState>();
		m_instructions = new LinkedList<XtaInstruction>();
		m_nextNode = new ArrayList<BasicBlock>();
		m_previousNode = new ArrayList<BasicBlock>();
		m_unknowns = new ArrayList<Unknown>();
	}
	
	public ArrayList<Unknown> getUnknowns(){
		return m_unknowns;
	}
	
	public String toString(){
		return "node: " + this.m_nodeId + " " + m_instructions.getFirst();
	}

	public int compareTo(BasicBlock n) {
		if (n == this)
			return 0;
		return -1;
	}
	
	public ArrayList<IBufferState> getIBufferStates(){
		return m_iBufferStates;
	}

	/*
	 * this returns a list of instructions with the worst case fnops inserted
	 */
	public LinkedList<XtaInstruction> getInstructionStream() {
		LinkedList<XtaInstruction> worst = new LinkedList<XtaInstruction>();
		for(XtaInstruction inst : m_instructions){
			for(int pc : m_worstIbuffer.m_fnops){
				if(pc == inst.getPc()){
					worst.add(new NullInstruction("--FNOP--", inst.getConfig(), 0, pc, 0));
				}
			}
			worst.add(inst);
		}
		return worst;
	}

	public LinkedList<XtaInstruction> getInstructions() {
		return m_instructions;
	}
	
	public ArrayList<BasicBlock> getNextNodes() {
		return m_nextNode;
	}

	public ArrayList<BasicBlock> getPrevoiusNodes() {
		return m_previousNode;
	}

	public boolean setIBuferStart(int initialFullness) {
		IBufferState ib = new IBufferState();
		ib.initialIBuffer = initialFullness;
		
		for(IBufferState old : m_iBufferStates){
			if(old.initialIBuffer == initialFullness){
				return false;
			}
		}
		
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
						ib.finalIBuffer = instBuff.m_numBytes;
						ib.finalIBufferKnown = true;
						addIbuffState(ib);
					}
				}
			} else {
				instBuff.setPc(inst.getPc() + inst.getNumBytes());
				if(m_instructions.getLast() == inst){
					ib.finalIBuffer = instBuff.m_numBytes;
					ib.finalIBufferKnown = true;
					addIbuffState(ib);
				}
			}
		}
		return true;
	}

	private boolean addIbuffState(IBufferState ib){
		for(IBufferState old : m_iBufferStates){
			if(old.finalIBuffer == ib.finalIBuffer){
				return false;
			}
		}
		m_iBufferStates.add(ib);
		//update the worst iBuffer
		if(m_worstIbuffer == null) m_worstIbuffer = ib;
		for(IBufferState w : m_iBufferStates){
			if(w.m_fnops.size()> m_worstIbuffer.m_fnops.size()){
				m_worstIbuffer = w;
			}
		}
		return true;
	}
	
	
}

class IBufferState {
	boolean finalIBufferKnown;
	int initialIBuffer;
	int finalIBuffer;

	ArrayList<Integer> m_fnops;

	public IBufferState() {
		finalIBufferKnown = false;
		initialIBuffer = 0;
		finalIBuffer = 0;
		m_fnops = new ArrayList<Integer>();
	}

	
	
	
}