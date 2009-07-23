package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.Status.S;
import com.xmos.xta.engine.instructions.XtaInstruction;

@SuppressWarnings("serial")
class IgnoreSectionIterationsException extends Exception {}
@SuppressWarnings("serial")
class InfiniteIterationsException extends Exception {}

public class NodeAttributes {
	private static final boolean DISABLE_STATS_CACHE = false; 
	
    private Node m_node; 

	public enum UnknownType {
    	INSTRUCTION_TIME,
    	LOOP_ITERATIONS,    	
    }
	
	public enum UnknownState {
		UNKNOWN,
		KNOWN,
	}

	public static final long INFINITE_ITERATIONS = -1;
	public static final long IGNORE_SECTION_ITERATIONS = 1;
	
	private double m_cachedWorstTimeTaken;
	private long m_cachedWorstNumCycles;
	private long m_cachedWorstNumInstructions;
	private long m_cachedWorstNumFnops;
	private double m_cachedBestTimeTaken;
	private long m_cachedBestNumCycles;
	private long m_cachedBestNumInstructions;
	private long m_cachedBestNumFnops;

	private Status.S m_cachedWorstTimeTakenStatus;
	private Status.S m_cachedWorstNumCyclesStatus;
	private Status.S m_cachedWorstNumInstructionsStatus;
	private Status.S m_cachedWorstNumFnopsStatus;
	private Status.S m_cachedBestTimeTakenStatus;
	private Status.S m_cachedBestNumCyclesStatus;
	private Status.S m_cachedBestNumInstructionsStatus;
	private Status.S m_cachedBestNumFnopsStatus;
	
	private boolean m_cachedWorstTimeTakenValid;
	private boolean m_cachedWorstNumCyclesValid;
	private boolean m_cachedWorstNumInstructionsValid;
	private boolean m_cachedWorstNumFnopsValid;	
	private boolean m_cachedBestTimeTakenValid;
	private boolean m_cachedBestNumCyclesValid;
	private boolean m_cachedBestNumInstructionsValid;
	private boolean m_cachedBestNumFnopsValid;	
	
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
	
	// ===================================================================== //
	// Display Methods
	// ===================================================================== //
	public String getTimingSummary() { 
	    String timingSummary = "";
	    
	    timingSummary += String.format(" : %s", getTimeTakenStr(Case.WORST));
		if (m_node.isBlockNode()) {					
			timingSummary += String.format(" (x %s)", getTotalIterationsStr(Case.WORST));
		}

	    timingSummary += String.format(" / %s", getTimeTakenStr(Case.BEST));
		if (m_node.isBlockNode()) {
			timingSummary += String.format(" (x %s)", getTotalIterationsStr(Case.BEST));
		}
	    return timingSummary;
	}
	
	public String getTimingDetails() {
		return String.format("%s %s\n", m_node.getTypeName(), m_node.getFunctionName()) +
    	       String.format("Time taken:       %s / %s\n", getTimeTakenStr(Case.WORST), getTimeTakenStr(Case.BEST)) +
               String.format("Thread cycles:    %s / %s\n", getNumCyclesStr(Case.WORST), getNumCyclesStr(Case.BEST)) +
               String.format("Num Instructions: %s / %s\n", getNumInstructionsStr(Case.WORST), getNumInstructionsStr(Case.BEST)) +
               String.format("Num Fnops:        %s / %s\n", getNumFNopsStr(Case.WORST), getNumFNopsStr(Case.BEST)) +            		   
               String.format("Num Paths:        %d\n", getNumPaths()) +            		   
               String.format("Node Id:          %d\n", m_node.getId());
    }
	
	private String getTimeTakenStr(Case c) {
	    Status status = new Status();
	    double value = getTimeTaken(c, status, null);	    
	    switch (status.getStatus()) {
	    case COMPLETED:  return String.format("%.1fns", value);
	    case INFINITE:   return String.format("Infinity");
	    case UNRESOLVED: return String.format("Unresolved");
	    }
	    assert(false);
	    return "";
	}
	
	private String getNumCyclesStr(Case c) {
	    Status status = new Status();
	    long value = getNumCycles(c, status, null);	    
	    switch (status.getStatus()) {
	    case COMPLETED:  return String.format("%d", value);
	    case INFINITE:   return String.format("Infinity");
	    case UNRESOLVED: return String.format("Unresolved");
	    }
	    assert(false);
	    return "";
	}

	private String getNumInstructionsStr(Case c) {
	    Status status = new Status();
	    long value = getNumInstructions(c, status, null);	    
	    switch (status.getStatus()) {
	    case COMPLETED:  return String.format("%d", value);
	    case INFINITE:   return String.format("Infinity");
	    case UNRESOLVED: return String.format("Unresolved");
	    }
	    assert(false);
	    return "";
	}

	private String getNumFNopsStr(Case c) {
	    Status status = new Status();
	    long value = getNumFNops(c, status, null);	    
	    switch (status.getStatus()) {
	    case COMPLETED:  return String.format("%d", value);
	    case INFINITE:   return String.format("Infinity");
	    case UNRESOLVED: return String.format("Unresolved");
	    }
	    assert(false);
	    return "";
	}
	
	private String getTotalIterationsStr(Case c) {
		long totalIterations = getTotalIterations(c, null);
		if (totalIterations == INFINITE_ITERATIONS) {
			return "-";
		}
		return String.format("%d", totalIterations);
	}
	
	// ===================================================================== //
	// Statistics Methods
	// ===================================================================== //
	public double getTimeTaken(Case c, Status status, Node ignoreIterationsAboveSection) {
		if (getCachedTimeTakenValid(c) && (ignoreIterationsAboveSection == null)) {
			status.setStatus(getCachedTimeTakenStatus(c));
			return getCachedTimeTaken(c);			
		}

		double value = 0;
		for (Node child : getNode().getChildren()) {
			value += child.getAttributes().getTimeTaken(c, status, ignoreIterationsAboveSection);
		}
		if (ignoreIterationsAboveSection == null) {
			setCachedTimeTaken(c, value);
			setCachedTimeTakenStatus(c, status.getStatus());
			setCachedTimeTakenValid(c, true);				
		}
		return value;
	}

	public long getNumCycles(Case c, Status status, Node ignoreIterationsAboveSection) {
		if (getCachedNumCyclesValid(c) && (ignoreIterationsAboveSection == null)) {
			status.setStatus(getCachedNumCyclesStatus(c));
			return getCachedNumCycles(c);
		}

		long value = 0;
		for (Node child : getNode().getChildren()) {
			value += child.getAttributes().getNumCycles(c, status, ignoreIterationsAboveSection);
		}
		if (ignoreIterationsAboveSection == null) {
			setCachedNumCycles(c, value);
			setCachedNumCyclesStatus(c, status.getStatus());
			setCachedNumCyclesValid(c, true);				
		}
		return value;
	}

	public long getNumInstructions(Case c, Status status, Node ignoreIterationsAboveSection) {
		if (getCachedNumInstructionsValid(c) && (ignoreIterationsAboveSection == null)) {
			status.setStatus(getCachedNumInstructionsStatus(c));
			return getCachedNumInstructions(c);
		}
		
		long value = 0;
		for (Node child : getNode().getChildren()) {
			value += child.getAttributes().getNumInstructions(c, status, ignoreIterationsAboveSection);		
		}
		if (ignoreIterationsAboveSection == null) {
			setCachedNumInstructions(c, value);
			setCachedNumInstructionsStatus(c, status.getStatus());
			setCachedNumInstructionsValid(c, true);
		}
		return value;
	}

	public long getNumFNops(Case c, Status status, Node ignoreIterationsAboveSection) {
		if (getCachedNumFnopsValid(c) && (ignoreIterationsAboveSection == null)) {
			status.setStatus(getCachedNumFnopsStatus(c));
			return getCachedNumFnops(c);
		}
		
		long value = 0;
		for (Node child : getNode().getChildren()) {
			value += child.getAttributes().getNumFNops(c, status, ignoreIterationsAboveSection);
		}			
		if (ignoreIterationsAboveSection == null) {
			setCachedNumFnops(c, value);
			setCachedNumFnopsStatus(c, status.getStatus());
			setCachedNumFnopsValid(c, true);
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

	// ===================================================================== //
	// Cached Statistics Methods
	// ===================================================================== //
	void initialiseCachedStats() {
		m_cachedWorstTimeTaken       = 0;
		m_cachedBestTimeTaken        = 0;		
		m_cachedWorstNumCycles       = 0;
		m_cachedBestNumCycles        = 0;
		m_cachedWorstNumInstructions = 0;
		m_cachedBestNumInstructions  = 0;
		m_cachedWorstNumFnops        = 0;
		m_cachedBestNumFnops         = 0;

		m_cachedWorstTimeTakenStatus       = S.COMPLETED;
		m_cachedBestTimeTakenStatus        = S.COMPLETED;		
		m_cachedWorstNumCyclesStatus       = S.COMPLETED;
		m_cachedBestNumCyclesStatus        = S.COMPLETED;
		m_cachedWorstNumInstructionsStatus = S.COMPLETED;
		m_cachedBestNumInstructionsStatus  = S.COMPLETED;
		m_cachedWorstNumFnopsStatus        = S.COMPLETED;
		m_cachedBestNumFnopsStatus         = S.COMPLETED;
		
		m_cachedWorstTimeTakenValid       = false; 
		m_cachedBestTimeTakenValid        = false; 
		m_cachedWorstNumCyclesValid       = false; 
		m_cachedBestNumCyclesValid        = false; 
		m_cachedWorstNumInstructionsValid = false; 
		m_cachedBestNumInstructionsValid  = false; 
		m_cachedWorstNumFnopsValid        = false; 
		m_cachedBestNumFnopsValid         = false; 
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

	double getCachedTimeTaken(Case c)     { return (c == Case.WORST) ? m_cachedWorstTimeTaken       : m_cachedBestTimeTaken; }	
	long getCachedNumCycles(Case c)       { return (c == Case.WORST) ? m_cachedWorstNumCycles       : m_cachedBestNumCycles; }
	long getCachedNumInstructions(Case c) { return (c == Case.WORST) ? m_cachedWorstNumInstructions : m_cachedBestNumInstructions; }
	long getCachedNumFnops(Case c)        { return (c == Case.WORST) ? m_cachedWorstNumFnops        : m_cachedBestNumFnops; }

	void setCachedTimeTaken(Case c, Double value)     { if (c == Case.WORST) m_cachedWorstTimeTaken = value;       else m_cachedBestTimeTaken = value; }
	void setCachedNumCycles(Case c, Long value)       { if (c == Case.WORST) m_cachedWorstNumCycles = value;       else m_cachedBestNumCycles = value; }
	void setCachedNumInstructions(Case c, Long value) { if (c == Case.WORST) m_cachedWorstNumInstructions = value; else m_cachedBestNumInstructions = value; }
	void setCachedNumFnops(Case c, Long value)        { if (c == Case.WORST) m_cachedWorstNumFnops = value;        else m_cachedBestNumFnops = value; }

	Status.S getCachedTimeTakenStatus(Case c)       { return (c == Case.WORST) ? m_cachedWorstTimeTakenStatus       : m_cachedBestTimeTakenStatus; }	
	Status.S getCachedNumCyclesStatus(Case c)       { return (c == Case.WORST) ? m_cachedWorstNumCyclesStatus       : m_cachedBestNumCyclesStatus; }
	Status.S getCachedNumInstructionsStatus(Case c) { return (c == Case.WORST) ? m_cachedWorstNumInstructionsStatus : m_cachedBestNumInstructionsStatus; }
	Status.S getCachedNumFnopsStatus(Case c)        { return (c == Case.WORST) ? m_cachedWorstNumFnopsStatus        : m_cachedBestNumFnopsStatus; }

	void setCachedTimeTakenStatus(Case c, Status.S status)       { if (c == Case.WORST) m_cachedWorstTimeTakenStatus = status;       else m_cachedBestTimeTakenStatus = status; }
	void setCachedNumCyclesStatus(Case c, Status.S status)       { if (c == Case.WORST) m_cachedWorstNumCyclesStatus = status;       else m_cachedBestNumCyclesStatus = status; }
	void setCachedNumInstructionsStatus(Case c, Status.S status) { if (c == Case.WORST) m_cachedWorstNumInstructionsStatus = status; else m_cachedBestNumInstructionsStatus = status; }
	void setCachedNumFnopsStatus(Case c, Status.S status)        { if (c == Case.WORST) m_cachedWorstNumFnopsStatus = status;        else m_cachedBestNumFnopsStatus = status; }
	
	boolean getCachedTimeTakenValid(Case c)       { return (c == Case.WORST) ? m_cachedWorstTimeTakenValid       : m_cachedBestTimeTakenValid; }	
	boolean getCachedNumCyclesValid(Case c)       { return (c == Case.WORST) ? m_cachedWorstNumCyclesValid       : m_cachedBestNumCyclesValid; }
	boolean getCachedNumInstructionsValid(Case c) { return (c == Case.WORST) ? m_cachedWorstNumInstructionsValid : m_cachedBestNumInstructionsValid; }
	boolean getCachedNumFnopsValid(Case c)        { return (c == Case.WORST) ? m_cachedWorstNumFnopsValid        : m_cachedBestNumFnopsValid; }

	void setCachedTimeTakenValid(Case c, boolean value)       { if (!DISABLE_STATS_CACHE) { if (c == Case.WORST) m_cachedWorstTimeTakenValid = value;       else m_cachedBestTimeTakenValid = value; } }
	void setCachedNumCyclesValid(Case c, boolean value)       { if (!DISABLE_STATS_CACHE) { if (c == Case.WORST) m_cachedWorstNumCyclesValid = value;       else m_cachedBestNumCyclesValid = value; } }
	void setCachedNumInstructionsValid(Case c, boolean value) { if (!DISABLE_STATS_CACHE) { if (c == Case.WORST) m_cachedWorstNumInstructionsValid = value; else m_cachedBestNumInstructionsValid = value; } }
	void setCachedNumFnopsValid(Case c, boolean value)        { if (!DISABLE_STATS_CACHE) { if (c == Case.WORST) m_cachedWorstNumFnopsValid = value;        else m_cachedBestNumFnopsValid = value; } }
	
	// ===================================================================== //
	// Unknown Status Methods
	// ===================================================================== //
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
	
	// ===================================================================== //
	// Loop Iteration Methods
	// ===================================================================== //
	public long getTotalIterations(Case c, Node ignoreIterationsAboveSection) {
		try {
			Iterations iterations = getTotalIterations(c, getNode(), ignoreIterationsAboveSection);
			return iterations.getGlobalIterations() * iterations.getLocalIterations();			
			
		} catch (IgnoreSectionIterationsException e) {
			return IGNORE_SECTION_ITERATIONS;
			
		} catch (InfiniteIterationsException e) {
			return INFINITE_ITERATIONS;
		}
	}

	public boolean loopIterationsValid(long numIterations) {
		assert(false);
		return false;
	}
	
	public void setLoopIterations(long numIterations) {
		assert(false);
	}
	
	public long getLoopIterations() {
		assert(false);
		return 0;
	}

	public boolean loopPathIterationsValid(Node loopNode, Node optionNode, long numIterations) {
		assert(false);
		return false;
	}
	
	public void setLoopPathIterations(Node loopNode, Node optionNode, long numIterations) {
		assert(false);
	}
		
	public long getLoopPathIterations(long loopIterations, Node optionNode, Case c) {
		assert(false);
		return 0;
	}
	
	public long getSetLoopPathIterations(Node optionNode) {
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
	}

	public boolean getIsAbsolute() {
		return m_isAbsolute;
	}
	
	public void setIsLoopHeader(boolean isLoopHeader) {
		m_isLoopHeader = isLoopHeader;
	}

	public boolean getIsLoopHeader() {
		return m_isLoopHeader;
	}		

	Iterations getTotalIterations(Case c, Node child, Node ignoreIterationsAboveSection) 
	    throws IgnoreSectionIterationsException, InfiniteIterationsException {
		
		if (getNode().getParent() != null) {
			return getNode().getParent().getAttributes().getTotalIterations(
					c, getNode(), ignoreIterationsAboveSection);
		}
		return new Iterations();		
	}
	
	// ===================================================================== //
	// Instruction Time Methods
	// ===================================================================== //
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
}
