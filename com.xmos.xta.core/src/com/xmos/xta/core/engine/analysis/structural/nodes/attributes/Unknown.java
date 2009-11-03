package com.xmos.xta.core.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.Reference;

public class Unknown {
	public enum Type {
    	LOOP_ITERATIONS,
    	LOOP_PATH_ITERATIONS,
    	LOOP_SCOPE,
    	INSTRUCTION_TIME,
    	PATH_TIME,
    	FUNCTION_TIME,
    }

	public enum State {
		UNKNOWN,
		KNOWN,
	}

	public enum Source {
		EXPLICIT,
		IMPLICIT,
	}

	private Node m_node;
	private Type m_type;
	private State m_state; 	
    private Reference[] m_references;
    private Source m_source; 

	public Unknown(Node node, Type type, State state) {
		initialise(node, type, state, Source.EXPLICIT, new Reference[]{});
	}

	public Unknown(Node node, Type type, State state, Source source, Reference[] references) {
		initialise(node, type, state, source, references);
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
	
	public Reference[] getReferences() {
		return m_references;
	}

	public void setState(State state) {
		m_state = state;
	}
	
	public void setExplicit(boolean explicit) {
		;
	}

	public String getDescription() {
		switch(m_type) {			
		case LOOP_ITERATIONS:
			switch(m_state) {
			case UNKNOWN:
				return String.format("Loop iterations on loop " + 
						m_node.getId() + " are unknown");

			case KNOWN:
				return String.format("Loop iterations on loop " + 
						m_node.getId() + " are known");
			}
			break;
			
		case LOOP_PATH_ITERATIONS:
			switch(m_state) {
			case UNKNOWN:
				return String.format("Loop path iterations on node " + 
						m_node.getId() + " are unknown");

			case KNOWN:
				return String.format("Loop path iterations on node " + 
						m_node.getId() + " are known");
			}
			break;

		case LOOP_SCOPE:
			switch(m_state) {
			case UNKNOWN:
				return String.format("Loop scope on node " + 
						m_node.getId() + " has not been set");

			case KNOWN:
				return String.format("Loop scope on node " + 
						m_node.getId() + " has been set");
			}
			break;
			
		case INSTRUCTION_TIME:
			assert(m_node instanceof InstructionNode);
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
							m_node.getAttributes().getFixedTime().getNs());
			}	
			break;
				
		case PATH_TIME:	
			switch(m_state) {
			case UNKNOWN:
				return String.format("Time taken for path (node id: %d) is unknown", m_node.getId());
					
			case KNOWN:
				return String.format("Time taken for path (node id: %d) is set to %.1fns", m_node.getId(),
						m_node.getAttributes().getFixedTime().getNs());
			}
			break;

		case FUNCTION_TIME:	
			switch(m_state) {
			case UNKNOWN:
				return String.format("Time taken for function (node id: %d) is unknown", m_node.getId());
					
			case KNOWN:
				return String.format("Time taken for function (node id: %d) is set to %.1fns", m_node.getId(),
						m_node.getAttributes().getFixedTime().getNs());
			}	
			break;
		}
		assert(false);
		return "";
	}    
	
	public String getScriptStr(String routeIdStr, String[] refStrs) throws Exception {
    	if (m_source == Source.IMPLICIT) {
    		throw new Exception("No script command avaliable (Not explicitly set by user)");
    	}

	    switch (m_type) {
	    case LOOP_ITERATIONS:
	    	return String.format("set loop %s %s %d\n", 
		    		routeIdStr, refStrs[0], 
		    		m_node.getAttributes().getLoopIterations());		    		

	    case LOOP_PATH_ITERATIONS:
			Node condNode = m_node.getEnclosingCondNode();
			return String.format("set looppath %s %s %d\n", 
	    			routeIdStr, refStrs[0], 
	    			condNode.getAttributes().getSetLoopPathIterations(m_node));
	    	
	    case LOOP_SCOPE:		    	
	    	return String.format("set loopscope %s %s %s\n", 
	    			routeIdStr, refStrs[0], 
	    			m_node.getAttributes().getIsAbsolute() ? "a" : "r");

	    case INSTRUCTION_TIME:
	    	return String.format("set time %s %s %s\n", 
	    			routeIdStr, refStrs[0], 
	    			m_node.getAttributes().getFixedTimeString());

	    case PATH_TIME:
	    	return String.format("set path %s %s %s %s\n", 
	    			routeIdStr, refStrs[0], refStrs[1], 
	    			m_node.getAttributes().getFixedTimeString());
	    	
	    case FUNCTION_TIME:
	    	return String.format("set function %s %s %s\n", 
	    			routeIdStr, refStrs[0], 
	    			m_node.getAttributes().getFixedTimeString());
	    }
	    return null;
	}
	
	private void initialise(Node node, Type type, State state, Source source, Reference[] references) {
		m_node = node;
		m_type = type;	
		m_state = state;
		m_references = references;
		m_source = source;
	}
}
