package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import java.util.Map;

import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
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

	private long m_worstCaseIterations;
	private long m_bestCaseIterations;
	private boolean m_isAbsolute;	
	private boolean m_isLoopHeader;

	public NodeAttributes(Node node) {
		m_node = node;
		m_isUnknown = false;
		m_unknownType = null;		
		m_worstCaseIterations = 0;
		m_bestCaseIterations = 0;
		m_isAbsolute = false;
		m_isLoopHeader = false;				
	}
	
	public Node getNode() {
		return m_node;
	}
	
	public String getTimingSummary() { 
		// Displays the worst case timing.
	    String timingSummary = "";	    
	    timingSummary += String.format(" : %.1fns", getTimeTaken(Case.WORST, false));
		if (m_node.isBlockNode()) {					
			timingSummary += String.format(" (x %d)", 
					m_node.getAttributes().getTotalIterations(Case.WORST));
		}

		// Displays the best case timing.
	    timingSummary += String.format(" - %.1fns", getTimeTaken(Case.BEST, false));
		if (m_node.isBlockNode()) {
			timingSummary += String.format(" (x %d)", 
					m_node.getAttributes().getTotalIterations(Case.BEST));
		}
	    return timingSummary;
	}
	
	public String getTimingDetails() {
		return String.format("Node Id:          %d\n", m_node.getId()) +
    	       String.format("Time taken:       %.1f - %.1f\n", 
    	    		   getTimeTaken(Case.WORST, false), getTimeTaken(Case.BEST, false)) +  	    		   
               String.format("Thread cycles:    %d - %d\n", 
            		   getNumCycles(Case.WORST, false), getNumCycles(Case.BEST, false)) + 
               String.format("Num Instructions: %d - %d\n", 
            		   getNumInstructions(Case.WORST, false), getNumInstructions(Case.BEST, false)) + 
               String.format("Num FNops:        %d - %d\n", 
            		   getNumFNops(Case.WORST, false), getNumFNops(Case.BEST, false)) +
               String.format("Num Iterations:   %d - %d\n", 
            		   m_worstCaseIterations, m_bestCaseIterations);
	}
		
	// --------------------------------------------------------------------- //
	// Statistics Methods
	// --------------------------------------------------------------------- //			
	public double getTimeTaken(Case c, boolean ignoreIterations) {
		double value = 0;
		for (Node child : getNode().getChildren()) {
			value += child.getAttributes().getTimeTaken(c, ignoreIterations);
		}
		return value;
	}

	public long getNumCycles(Case c, boolean ignoreIterations) {
		long value = 0;
		for (Node child : getNode().getChildren()) {
			value += child.getAttributes().getNumCycles(c, ignoreIterations);
		}
		return value;
	}
	
	public long getNumInstructions(Case c, boolean ignoreIterations) {
		long value = 0;
		for (Node child : getNode().getChildren()) {
			value += child.getAttributes().getNumInstructions(c, ignoreIterations);
		}
		return value;
	}
	
	public long getNumFNops(Case c, boolean ignoreIterations) {
		long value = 0;
		for (Node child : getNode().getChildren()) {
			value += child.getAttributes().getNumFNops(c, ignoreIterations);
		}
		return value;
	}

	public long getNumPaths() {
		long value = 1;
		for (Node child : getNode().getChildren()) {
			value *= child.getAttributes().getNumPaths();
		}
		return value;
	}

	// --------------------------------------------------------------------- //
	// Unknown Status Methods
	// --------------------------------------------------------------------- //		
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
						m_node.getAttributes().getInstructionTimeTaken());
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
	
	// --------------------------------------------------------------------- //
	// Loop Iteration Methods
	// --------------------------------------------------------------------- //	
	public long getCummulativeIterations(Case c) {
		Node parent = getNode().getParent();
		if (parent != null) {
			return parent.getAttributes().getCummulativeIterations(c);
		}
		return 1;
	}
	
	
	public long getTotalIterations(Case c) {
		Node enclosingLoopNode = getNode().getEnclosingLoopNode();
		if ((enclosingLoopNode != null) && 
			(!enclosingLoopNode.getAttributes().getIsAbsolute())) {
			
			long cummulativeIterations = 1;
			if (enclosingLoopNode.getParent() != null) {
				cummulativeIterations = enclosingLoopNode.getParent().getAttributes().getCummulativeIterations(c);  
			}
			return getNumIterations(c) * cummulativeIterations;
		}
		return getNumIterations(c);
	}
	
	public void setLoopIterations(long numIterations) {
		setLoopIterations(Case.WORST, numIterations);
		setLoopIterations(Case.BEST, numIterations);
	}

	public void setLoopIterations(Case c, long numIterations) {
		setNumIterations(c, numIterations);

   		for (Node child : m_node.getChildren()) {
   			if (child.isLoopNode()) {
   				// Just set the number of iterations on the loop, 
   				// but don't propagate it down to its children
   				child.getAttributes().setNumIterations(c, numIterations);
   				
   			} else if (child.getAttributes().isLoopHeader()) {
   				// For a loop, the header always executes 
   				// one more time than the body
   				child.getAttributes().setLoopIterations(c, numIterations + 1);
   				
   			} else {
   			    child.getAttributes().setLoopIterations(c, numIterations);
   			}			
    	}		
	}
	
	public boolean setLoopPathIterations(Node loopNode, Node optionNode, long numIterations) {
		assert(false);
		return false;
	}
	
	public void setNumIterations(Case c, long numIterations) {
		switch (c) {
		case WORST: m_worstCaseIterations = numIterations; break;
		case BEST:  m_bestCaseIterations = numIterations; break;		
		}
	}
	
	public long getNumIterations(Case c) {
		switch (c) {
		case WORST: return m_worstCaseIterations;
		case BEST:  return m_bestCaseIterations;
		}
		assert(false);
		return 0;
	}

	public void setIsAbsolute(boolean isAbsolute) {
		m_isAbsolute = isAbsolute;
		for (Node child : m_node.getChildren()) {
			if (!child.isLoopNode()) {
			    child.getAttributes().setIsAbsolute(isAbsolute);
			}
		}
	}
	
	public boolean getIsAbsolute() {
		return m_isAbsolute;
	}
	
	public void setIsLoopHeader(boolean isLoopHeader) {
		m_isLoopHeader = isLoopHeader;
	}

	public boolean isLoopHeader() {
		return m_isLoopHeader;
	}		
	
	public Map<Node, Boolean> getSetOptions() {
		assert(false);
		return null;
	}
	
	// --------------------------------------------------------------------- //
	// Instruction Time Methods
	// --------------------------------------------------------------------- //
	public void setInstructionNumCycles(long numCycles) {
		assert(false);		
	}

	public void unsetInstructionNumCycles() {
		assert(false);		
	}
	
	public long getInstructionNumCycles() {
		assert(false);		
		return 0;
	}

	public void setInstructionTimeTaken(double timeTaken) {
		assert(false);		
	}

	public void unsetInstructionTimeTaken() {
		assert(false);		
	}
	
	public double getInstructionTimeTaken() {
		assert(false);		
		return 0;
	}
	
	public boolean isInstructionUnknown() {
		assert(false);		
		return false;
	}		
}
