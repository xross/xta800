package com.xmos.xta.core.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.Reference;

public class Unknown {
	public enum Type {
    	INSTRUCTION_TIME,
    	LOOP_ITERATIONS,
    	LOOP_PATH_ITERATIONS,
    	LOOP_SCOPE,
    }

	public enum State {
		UNKNOWN,
		KNOWN,
	}

	private Node m_node;
	private Type m_type;
	private State m_state; 	
    private Reference m_reference;

	public Unknown(Node node, Type type, State state) {
		initialise(node, type, state, null);
	}
    
	public Unknown(Node node, Type type, State state, Reference reference) {
		initialise(node, type, state, reference);
	}

	public Node getNode() {
		return m_node;
	}

	public Type getType() {
		return m_type;
	}

	public State getState() {
		return m_state;
	}
	
	public Reference getReference() {
		return m_reference;
	}

	public void setState(State state) {
		m_state = state;
	}
	
	public String getDescription() {
		switch(m_type) {
		case INSTRUCTION_TIME:
			XtaInstruction instruction = ((InstructionNode)m_node).getXtaInstruction();
			switch(m_state) {
			case UNKNOWN:
				return String.format("Time taken for instruction (0x%x: %s) is unknown",
						instruction.getPc(),
						instruction.getNmenonic());	
				
			case KNOWN:
				return String.format("Time for instruction (0x%x: %s) set to %.1fns",
						instruction.getPc(),
						instruction.getNmenonic(),
						m_node.getAttributes().getInstructionNs());
			}
			assert(false);
			return "";
			
		case LOOP_ITERATIONS:
			switch(m_state) {
			case UNKNOWN:
				return String.format("Loop iterations on loop " + 
						m_node.getId() + " are unknown");

			case KNOWN:
				return String.format("Loop iterations on loop " + 
						m_node.getId() + " are known");
			}
			assert(false);
			return "";
			
		case LOOP_PATH_ITERATIONS:
			switch(m_state) {
			case UNKNOWN:
				return String.format("Loop path iterations on node " + 
						m_node.getId() + " are unknown");

			case KNOWN:
				return String.format("Loop path iterations on node " + 
						m_node.getId() + " are known");
			}
			assert(false);
			return "";			

		case LOOP_SCOPE:
			switch(m_state) {
			case UNKNOWN:
				return String.format("Loop scope on node " + 
						m_node.getId() + " has not been set");

			case KNOWN:
				return String.format("Loop scope on node " + 
						m_node.getId() + " has been set");
			}
			assert(false);
			return "";			
		}
		assert(false);
		return "";
	}    
	
	private void initialise(Node node, Type type, State state, Reference reference) {
		m_node = node;
		m_type = type;	
		m_state = state;
		m_reference = reference;
	}
}
