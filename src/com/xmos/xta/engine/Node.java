package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.LinkedList;

public class Node implements Comparable<Node> {

	int m_nodeId;
	private LinkedList<XtaInstruction> m_instructions;
	private ArrayList<IBufferState> m_iBufferStates;
	private ArrayList<Node> m_nextNode;
	private ArrayList<Node> m_previousNode;

	Node(int n) {
		m_nodeId = n;
		m_iBufferStates = new ArrayList<IBufferState>();
		m_instructions = new LinkedList<XtaInstruction>();
		m_nextNode = new ArrayList<Node>();
		m_previousNode = new ArrayList<Node>();
	}

	public int compareTo(Node n) {
		if (n == this)
			return 0;
		return -1;
	}

	public ArrayList<IBufferState> getIBufferStates(){
		return m_iBufferStates;
	}
	
	public int getWorstFinalIBuffer() {
		int worst = 0;
		for (IBufferState ibuff : m_iBufferStates) {
			worst = Math.max(worst, ibuff.finalIBuffer);
		}
		return worst;
	}

	public boolean getFinalIBufferKnown(){
		return false;
	}
	
	public int getWorstStartIBuffer() {
		int worst = 0;
		for (IBufferState ibuff : m_iBufferStates) {
			worst = Math.max(worst, ibuff.finalIBuffer);
		}
		return worst;
	}

	public LinkedList<XtaInstruction> getInstructions() {
		return m_instructions;
	}

	public ArrayList<Node> getNextNodes() {
		return m_nextNode;
	}

	public ArrayList<Node> getPrevoiusNodes() {
		return m_previousNode;
	}

	public boolean setIBuferStart(int initialFullness) {
		boolean  alreadyDone = false;
		for(IBufferState ib : m_iBufferStates){
			if(ib.initialIBuffer == initialFullness){
				alreadyDone = true;
			}
		}
		if(!alreadyDone){
			//parseBlock and 
			IBufferState ib = new IBufferState();
			ib.initialIBuffer = initialFullness;
			ib.finalIBuffer = initialFullness;
			for(XtaInstruction inst :  m_instructions){
				//simulate ibuffer
				if(inst.isLoadStore()){
					
				}
				
			}
		}
		return !alreadyDone;
	}

}

class IBufferState {
	boolean finalIBufferKnown = false;
	int initialIBuffer;
	int finalIBuffer;

	ArrayList<Integer> m_fnops;

	public IBufferState() {
		initialIBuffer = 0;
		finalIBuffer = 0;
		m_fnops = new ArrayList<Integer>();
	}

}