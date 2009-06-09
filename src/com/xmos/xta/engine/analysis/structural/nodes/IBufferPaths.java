package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.List;

import com.xmos.xta.engine.analysis.controlflow.InstructionBuffer;

public class IBufferPaths {

	private Node m_parent;
	private InstructionBuffer m_ib;
	private List<Integer> m_fnopPcs;
	
	public IBufferPaths(Node parent, InstructionBuffer ib, List<Integer> pcs) {
		super();
		m_parent = parent;
		m_ib = ib;
		m_fnopPcs = pcs;
	}

	public Node getParent() {
		return m_parent;
	}

	public InstructionBuffer getInstructionBuffer() {
		return m_ib;
	}

	public List<Integer> getFnopPcs() {
		return m_fnopPcs;
	}
	
}
