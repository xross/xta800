package com.xmos.xta.engine.analysis.structural.nodes.attributes;

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

	private boolean m_isAbsolute;	
	private boolean m_isLoopHeader;

	public NodeAttributes(Node node) {
		m_node = node;
		m_isUnknown = false;
		m_unknownType = null;		
		m_isAbsolute = false;
		m_isLoopHeader = false;				
	}
	
	public Node getNode() {
		return m_node;
	}
	
	public String getTimingSummary() { 
	    String timingSummary = "";
	    
		// Displays the worst case timing.
	    long worstCaseIterations = getTotalIterations(Case.WORST);
	    String worstCaseTimingStr = String.format("%.1fns", getTimeTaken(Case.WORST, false));	    
	    timingSummary += String.format(" : %s", worstCaseTimingStr);
		if (m_node.isBlockNode()) {					
			timingSummary += String.format(" (x %d)", worstCaseIterations);
		}

		// Displays the best case timing.
	    long bestCaseIterations = getTotalIterations(Case.BEST);
        String bestCaseTimingStr = String.format("%.1fns", getTimeTaken(Case.BEST, false));
	    timingSummary += String.format(" / %s", bestCaseTimingStr);
		if (m_node.isBlockNode()) {
			timingSummary += String.format(" (x %d)", bestCaseIterations);
		}
	    return timingSummary;
	}
	
	public String getTimingDetails() {		
		String worstCaseTimeTaken    = String.format("%.1fns", getTimeTaken(Case.WORST, false));
		String worstCaseThreadCycles = String.format("%d", getNumCycles(Case.WORST, false));
		String worstCaseInstructions = String.format("%d", getNumInstructions(Case.WORST, false));
		String worstCaseFnops        = String.format("%d", getNumFNops(Case.WORST, false));
		
		String bestCaseTimeTaken    = String.format("%.1fns", getTimeTaken(Case.BEST, false));
		String bestCaseThreadCycles = String.format("%d", getNumCycles(Case.BEST, false));
		String bestCaseInstructions = String.format("%d", getNumInstructions(Case.BEST, false));
		String bestCaseFnops        = String.format("%d", getNumFNops(Case.BEST, false));			
		
		return String.format("%s %s\n", m_node.getTypeName(), m_node.getFunctionName()) +
    	       String.format("Time taken:       %s / %s\n", worstCaseTimeTaken, bestCaseTimeTaken) +
               String.format("Thread cycles:    %s / %s\n", worstCaseThreadCycles, bestCaseThreadCycles) +
               String.format("Num Instructions: %s / %s\n", worstCaseInstructions, bestCaseInstructions) +
               String.format("Num Fnops:        %s / %s\n", worstCaseFnops, bestCaseFnops) +            		   
               String.format("Node Id:          %d\n", m_node.getId());
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
	public long getTotalIterations(Case c) {
		Node enclosingLoopNode = getNode().getEnclosingLoopNode();
		if (enclosingLoopNode != null) {

			// We are inside a loop, so we need to get the loop iterations.
			// If we are in a section, then get them from the section.
			// Otherwise, take the iterations from the enclosing loop.
			long numIterations = 0;
			boolean foundSectionIterations = false;
			Node node = getNode();
			while (node != enclosingLoopNode) {				
				if (node.getParent().isSectionNode()) {
					numIterations = node.getParent().getAttributes().getLoopPathIterations(enclosingLoopNode, node, c);
					foundSectionIterations = true;
					break;
				}
				node = node.getParent();
			}
			if (!foundSectionIterations) {
				numIterations = enclosingLoopNode.getAttributes().getLoopIterations();
			}

			// Need to treat relative and absolute loops differently..
			if (!enclosingLoopNode.getAttributes().getIsAbsolute()) {				
				// We are within a relative loop, so multiply up by the 
				// iterations of the above loops. 				
				long cummulativeIterations = 1;
				Node n = enclosingLoopNode.getParent();
				while (n != null) {
                    if (n.isLoopNode() || n.isSelfLoopNode()) {
                    	cummulativeIterations *= n.getAttributes().getLoopIterations();
                    }					
					n = n.getParent();
				}				
				long headerAdjustment = getIsLoopHeader() ? 1 : 0;				
				return (numIterations + headerAdjustment) * cummulativeIterations;
				
		    } else {
		    	// We are within an absolute loop, so no need to multiply
		    	// up with the above iterations.
		    	long headerAdjustment = getIsLoopHeader() ? numIterations : 0;		    	
				return numIterations + headerAdjustment;		    	
		    }
		}
		// At the outermost scope, i.e. not inside a loop..
		return 1;
	}

	public void setLoopIterations(long numIterations) {
		assert(false);		
	}
	
	public long getLoopIterations() {
		assert(false);
		return 0;
	}
	
	public boolean setLoopPathIterations(Node loopNode, Node optionNode, long numIterations) {
		assert(false);
		return false;
	}
		
	public long getLoopPathIterations(Node loopNode, Node optionNode, Case c) {
		assert(false);
		return 0;
	}
	
	public boolean getSetOption(Node optionNode) {
		assert(false);
		return false;
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
		for (Node child : m_node.getChildren()) {
			if (!child.isLoopNode()) {
			    child.getAttributes().setIsLoopHeader(isLoopHeader);
			}
		}
	}

	public boolean getIsLoopHeader() {
		return m_isLoopHeader;
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
