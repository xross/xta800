package com.xmos.xta.engine.analysis.structural.nodes;

import com.xmos.xta.engine.instructions.XtaInstruction;

public class NodeAttributes {
    private Node m_node; 

	public enum UnknownType {
    	INSTRUCTION_TIME,
    	LOOP_ITERATIONS,    	
    }
	
	public enum UnknownState {
		UNKNOWN,
		KNOWN,
	}
	
	private boolean m_isUnknown;
	private UnknownType m_unknownType;
	private UnknownState m_unknownState; 	
    private boolean m_displayUnknown;
	
	public NodeAttributes(Node node) {
		m_node = node;
		m_isUnknown = false;
		m_unknownType = null;
	}

	public NodeAttributes(NodeAttributes nodeAttributes) {
		m_node = nodeAttributes.m_node;
		m_isUnknown = nodeAttributes.m_isUnknown;
		m_unknownType = nodeAttributes.m_unknownType;
	}
	
	public boolean isUnknown() {
		return m_isUnknown;
	}
	
	public void setUnknownType(UnknownType unknownType) {		
		m_isUnknown = true;
		m_unknownType = unknownType;
		setUnknownState(UnknownState.UNKNOWN);
	}

	public UnknownType getUnknownType() {
		assert(m_isUnknown == true);
		return m_unknownType;
	}

	public void setUnknownState(UnknownState unknownState) {
		assert(m_isUnknown == true);
		m_unknownState = unknownState;
	}

	public UnknownState getUnknownState() {
		assert(m_isUnknown == true);
		return m_unknownState;
	}

	public void setDisplayUnknown(boolean displayUnknown) {
		m_displayUnknown = displayUnknown;
	}

	public boolean getDisplayUnknown() {
		return m_displayUnknown;
	}
	
	public String getUnknownDescription() {
		assert(m_isUnknown == true);

		switch(m_unknownType) {
		case INSTRUCTION_TIME:
			XtaInstruction instruction = ((InstructionNode)m_node).getXtaInstruction();
			switch(m_unknownState) {
			case UNKNOWN:
				return String.format("Time taken for instruction (0x%x: %s) is unknown",
						instruction.getPc(),
						instruction.getNmenonic());	
				
			case KNOWN:
				return String.format("Time for instruction (0x%x: %s) set to %.1fns",
						instruction.getPc(),
						instruction.getNmenonic(),
						m_node.getInstructionTimeTaken());
			}
			assert(false);
			return "";
			
		case LOOP_ITERATIONS:
			switch(m_unknownState) {
			case UNKNOWN:
				return String.format("Loop iterations on loop " + 
						m_node.getId() + " are unknown");

			case KNOWN:
				return String.format("Loop iterations on loop " + 
						m_node.getId() + " are known");
			}
			assert(false);
			return "";
		}
		assert(false);
		return "";
	}
}
