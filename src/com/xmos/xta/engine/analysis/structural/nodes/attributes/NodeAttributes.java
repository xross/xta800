package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import java.util.HashMap;
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

	private Map<Case, Double> m_cachedTimeTaken;
	private Map<Case, Long> m_cachedNumCycles;
	private Map<Case, Long> m_cachedNumInstructions;
	private Map<Case, Long> m_cachedNumFnops;
	
	private Map<Case, Boolean> m_cachedTimeTakenValid;
	private Map<Case, Boolean> m_cachedNumCyclesValid;
	private Map<Case, Boolean> m_cachedNumInstructionsValid;
	private Map<Case, Boolean> m_cachedNumFnopsValid;	
	
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
		initialiseCachedStats();
		invalidateCachedStats();
	}
	
	public Node getNode() {
		return m_node;
	}
	
	public String getTimingSummary() { 
	    String timingSummary = "";
	    
		// Displays the worst case timing.
	    String worstCaseTimingStr = String.format("%.1fns", getTimeTaken(Case.WORST, false));	    
	    timingSummary += String.format(" : %s", worstCaseTimingStr);
		if (m_node.isBlockNode()) {					
			timingSummary += String.format(" (x %d)", getTotalIterations(Case.WORST));
		}

		// Displays the best case timing.
        String bestCaseTimingStr = String.format("%.1fns", getTimeTaken(Case.BEST, false));
	    timingSummary += String.format(" / %s", bestCaseTimingStr);
		if (m_node.isBlockNode()) {
			timingSummary += String.format(" (x %d)", getTotalIterations(Case.BEST));
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
		if (!getCachedTimeTakenValid(c) && !ignoreIterations) {
			double value = 0;
			for (Node child : getNode().getChildren()) {
				value += child.getAttributes().getTimeTaken(c, ignoreIterations);
			}
			setCachedTimeTaken(c, value);
			setCachedTimeTakenValid(c, true);
		}
		return getCachedTimeTaken(c);			
	}

	public long getNumCycles(Case c, boolean ignoreIterations) {
		if (!getCachedNumCyclesValid(c) && !ignoreIterations) {
			long value = 0;
			for (Node child : getNode().getChildren()) {
				value += child.getAttributes().getNumCycles(c, ignoreIterations);
			}			
			setCachedNumCycles(c, value);
			setCachedNumCyclesValid(c, true);
		}
		return getCachedNumCycles(c);
	}

	public long getNumInstructions(Case c, boolean ignoreIterations) {
		if (!getCachedNumInstructionsValid(c) && !ignoreIterations) {
			long value = 0;
			for (Node child : getNode().getChildren()) {
				value += child.getAttributes().getNumInstructions(c, ignoreIterations);
			}			
			setCachedNumInstructions(c, value);
			setCachedNumInstructionsValid(c, true);
		}
		return getCachedNumInstructions(c);
	}

	public long getNumFNops(Case c, boolean ignoreIterations) {
		if (!getCachedNumFnopsValid(c) && !ignoreIterations) {
			long value = 0;
			for (Node child : getNode().getChildren()) {
				value += child.getAttributes().getNumFNops(c, ignoreIterations);
			}			
			setCachedNumFnops(c, value);
			setCachedNumFnopsValid(c, true);
		}
		return getCachedNumFnops(c);
	}	

	public long getNumPaths() {
		long value = 1;
		for (Node child : getNode().getChildren()) {
			value *= child.getAttributes().getNumPaths();
		}
		return value;
	}

	// --------------------------------------------------------------------- //
	// Cached Statistics Methods
	// --------------------------------------------------------------------- //			
	void initialiseCachedStats() {
		m_cachedTimeTaken       = new HashMap<Case, Double>();		
		m_cachedNumCycles       = new HashMap<Case, Long>();
		m_cachedNumInstructions = new HashMap<Case, Long>();
		m_cachedNumFnops        = new HashMap<Case, Long>();
		
		m_cachedTimeTakenValid            = new HashMap<Case, Boolean>(); 
		m_cachedNumCyclesValid       = new HashMap<Case, Boolean>(); 
		m_cachedNumInstructionsValid = new HashMap<Case, Boolean>(); 
		m_cachedNumFnopsValid        = new HashMap<Case, Boolean>(); 
	}
	
	public void invalidateCachedStats() {
		setCachedTimeTaken(Case.WORST, (double)0);
		setCachedTimeTaken(Case.BEST, (double)0);		
		setCachedNumCycles(Case.WORST, (long)0);
		setCachedNumCycles(Case.BEST, (long)0);
		setCachedNumInstructions(Case.WORST, (long)0);
		setCachedNumInstructions(Case.BEST, (long)0);
		setCachedNumFnops(Case.WORST, (long)0);
		setCachedNumFnops(Case.BEST, (long)0);		
		
		setCachedTimeTakenValid(Case.WORST, false);
		setCachedTimeTakenValid(Case.BEST, false);		
		setCachedNumCyclesValid(Case.WORST, false);
		setCachedNumCyclesValid(Case.BEST, false);
		setCachedNumInstructionsValid(Case.WORST, false);
		setCachedNumInstructionsValid(Case.BEST, false);
		setCachedNumFnopsValid(Case.WORST, false);
		setCachedNumFnopsValid(Case.BEST, false);
    }

	double getCachedTimeTaken(Case c)     { return m_cachedTimeTaken.get(c); }
	long getCachedNumCycles(Case c)       { return m_cachedNumCycles.get(c); }
	long getCachedNumInstructions(Case c) { return m_cachedNumInstructions.get(c); }
	long getCachedNumFnops(Case c)        { return m_cachedNumFnops.get(c); }

	void setCachedTimeTaken(Case c, Double value)     { m_cachedTimeTaken.put(c, value); }
	void setCachedNumCycles(Case c, Long value)       { m_cachedNumCycles.put(c, value); }
	void setCachedNumInstructions(Case c, Long value) { m_cachedNumInstructions.put(c, value); }
	void setCachedNumFnops(Case c, Long value)        { m_cachedNumFnops.put(c, value); }
	
	boolean getCachedTimeTakenValid(Case c)       { return m_cachedTimeTakenValid.get(c); }
	boolean getCachedNumCyclesValid(Case c)       { return m_cachedNumCyclesValid.get(c); }
	boolean getCachedNumInstructionsValid(Case c) { return m_cachedNumInstructionsValid.get(c); }
	boolean getCachedNumFnopsValid(Case c)        { return m_cachedNumFnopsValid.get(c); }
	
	void setCachedTimeTakenValid(Case c, boolean value)       { m_cachedTimeTakenValid.put(c, value); }
	void setCachedNumCyclesValid(Case c, boolean value)       { m_cachedNumCyclesValid.put(c, value); }
	void setCachedNumInstructionsValid(Case c, boolean value) { m_cachedNumInstructionsValid.put(c, value); }
	void setCachedNumFnopsValid(Case c, boolean value)        { m_cachedNumFnopsValid.put(c, value); }

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
	
	public void resetPathIterations() {
		assert(false);		
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
