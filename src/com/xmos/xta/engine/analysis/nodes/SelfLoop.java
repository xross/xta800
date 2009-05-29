package com.xmos.xta.engine.analysis.nodes;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import com.xmos.xta.engine.XtaInstruction;

public class SelfLoop extends Node {

	private Node m_loop; 
	
	public SelfLoop(Node loop){
		super();
		m_loop = loop;
	}
	
	public SelfLoop(SelfLoop loop){
		m_loop = loop.m_loop;
	}

	public Node clone() {
		return new SelfLoop(this);
	}

	public void display(int indentLevel) {
		printIndent(indentLevel);
		System.out.printf("selfLoop(%d)\n", m_id);	
		m_loop.display(indentLevel+1); 
	}

	public Collection<Node> getContaintedNodes() {
		return m_loop.getContaintedNodes();
	}

	public String getGraphvizName() {
		return "SelfLoop" + m_id;
	}

	public List<XtaInstruction> getInstructions() {
		return m_loop.getInstructions();
	}

	public long getNumberCycles() {
		return m_loop.getNumberCycles();
	}

	public long getNumberFNOPS() {
		return m_loop.getNumberFNOPS();
	}

	public Set<Integer> getPCsCovered() {
		return m_loop.getPCsCovered();
	}

	public boolean isFunction() {
		return false;
	}

	public int getStartPc() {
		return m_loop.getStartPc();
	}

}
