package com.xmos.xta.core.engine.analysis.structural.nodes.attributes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Status.S;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Unknown.State;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Unknown.Type;
import com.xmos.xta.core.engine.references.Reference;

@SuppressWarnings("serial")
class IgnoreCondIterationsException extends Exception {}
@SuppressWarnings("serial")
class InfiniteIterationsException extends Exception {}
@SuppressWarnings("serial")
class CondIterationsNotSetException extends Exception {}
@SuppressWarnings("serial")
class NumPathsOverflowException extends Exception {}
@SuppressWarnings("serial")
class SourceLevelIterationsSetException extends Exception {}

public class NodeAttributes {
	private static final boolean DISABLE_STATS_CACHE = false; 
	
    private Node m_node; 

	public enum UnknownType {
    	INSTRUCTION_TIME,
    	LOOP_ITERATIONS,
    	LOOP_PATH_ITERATIONS,
    	LOOP_SCOPE,
    }

	public enum UnknownState {
		UNKNOWN,
		KNOWN,
	}

	public static final long INFINITE_ITERATIONS = -1;
	public static final long IGNORE_COND_ITERATIONS = 1;
	
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
	
	private TimeUnit m_fixedTimeTaken;
	
    private List<Unknown> m_unknowns;
    private boolean m_displayAsUnknown;
	private boolean m_isAbsolute;	
	private boolean m_isLoopHeader;

	public NodeAttributes(Node node) {
		m_node = node;
		m_isAbsolute = false;
		m_isLoopHeader = false;	
		m_unknowns = new ArrayList<Unknown>();
		m_displayAsUnknown = false;		
		m_fixedTimeTaken = null;
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
               String.format("Num Paths:        %s\n", getNumPathsStr()) +            		   
               String.format("Node Id:          %d\n", m_node.getId());
    }
	
	public String getNumPathsStr() {
		try {
			return String.format("%d", getNumPaths());
			
		} catch (NumPathsOverflowException e) {
			return "n/a";
		}		
	}
	
	private String getTimeTakenStr(Case c) {
	    Status status = new Status();
	    double value = getTimeTaken(c, status, null);	    
	    switch (status.getStatus()) {
	    case COMPLETED:     return String.format("%.1fns", value);
	    case INFINITE:      return String.format("Infinity");
	    case UNRESOLVED:    return String.format("Unresolved");
	    case NOT_AVALIABLE: return String.format("n/a");
	    case ZERO:          return String.format("%.1fns", value);
	    }
	    assert(false);
	    return "";
	}
	
	private String getNumCyclesStr(Case c) {
	    Status status = new Status();
	    long value = getNumCycles(c, status, null);	    
	    switch (status.getStatus()) {
	    case COMPLETED:     return String.format("%d", value);
	    case INFINITE:      return String.format("Infinity");
	    case UNRESOLVED:    return String.format("Unresolved");
	    case NOT_AVALIABLE: return String.format("n/a");
	    case ZERO:          return String.format("%d", value);
	    }
	    assert(false);
	    return "";
	}

	private String getNumInstructionsStr(Case c) {
	    Status status = new Status();
	    long value = getNumInstructions(c, status, null);	    
	    switch (status.getStatus()) {
	    case COMPLETED:     return String.format("%d", value);
	    case INFINITE:      return String.format("Infinity");
	    case UNRESOLVED:    return String.format("Unresolved");
	    case NOT_AVALIABLE: return String.format("n/a");
	    case ZERO:          return String.format("%d", value);
	    }
	    assert(false);
	    return "";
	}

	private String getNumFNopsStr(Case c) {
	    Status status = new Status();
	    long value = getNumFNops(c, status, null);	    
	    switch (status.getStatus()) {
	    case COMPLETED:     return String.format("%d", value);
	    case INFINITE:      return String.format("Infinity");
	    case UNRESOLVED:    return String.format("Unresolved");
	    case NOT_AVALIABLE: return String.format("n/a");
	    case ZERO:          return String.format("%d", value);
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
	public double getTimeTaken(Case c, Status status, Node ignoreIterationsAboveCond) {
		if (getFixedTime() != null) {
			return getFixedTime().getNs();
		}
		
		if (getCachedTimeTakenValid(c) && (ignoreIterationsAboveCond == null)) {
			status.setStatus(getCachedTimeTakenStatus(c));
			return getCachedTimeTaken(c);			
		}

		double value = 0;
		for (Node child : getNode().getIndirectChildren()) {
			Status childStatus = new Status();
			value += child.getAttributes().getTimeTaken(c, childStatus, ignoreIterationsAboveCond);
			status.mergeStatus(childStatus.getStatus());
		}

		if (m_node.isUnresolved()) {
			status.mergeStatus(S.UNRESOLVED);
		}
		
		if (ignoreIterationsAboveCond == null) {
			setCachedTimeTaken(c, value);
			setCachedTimeTakenStatus(c, status.getStatus());
			setCachedTimeTakenValid(c, true);				
		}
		return value;
	}

	public long getNumCycles(Case c, Status status, Node ignoreIterationsAboveCond) {
		if (getCachedNumCyclesValid(c) && (ignoreIterationsAboveCond == null)) {
			status.setStatus(getCachedNumCyclesStatus(c));
			return getCachedNumCycles(c);
		}

		long value = 0;
		for (Node child : getNode().getIndirectChildren()) {
			value += child.getAttributes().getNumCycles(c, status, ignoreIterationsAboveCond);
		}
		if (ignoreIterationsAboveCond == null) {
			setCachedNumCycles(c, value);
			setCachedNumCyclesStatus(c, status.getStatus());
			setCachedNumCyclesValid(c, true);				
		}
		return value;
	}

	public long getNumInstructions(Case c, Status status, Node ignoreIterationsAboveCond) {
		if (getCachedNumInstructionsValid(c) && (ignoreIterationsAboveCond == null)) {
			status.setStatus(getCachedNumInstructionsStatus(c));
			return getCachedNumInstructions(c);
		}
		
		long value = 0;
		for (Node child : getNode().getIndirectChildren()) {
			value += child.getAttributes().getNumInstructions(c, status, ignoreIterationsAboveCond);		
		}
		if (ignoreIterationsAboveCond == null) {
			setCachedNumInstructions(c, value);
			setCachedNumInstructionsStatus(c, status.getStatus());
			setCachedNumInstructionsValid(c, true);
		}
		return value;
	}

	public long getNumFNops(Case c, Status status, Node ignoreIterationsAboveCond) {
		if (getCachedNumFnopsValid(c) && (ignoreIterationsAboveCond == null)) {
			status.setStatus(getCachedNumFnopsStatus(c));
			return getCachedNumFnops(c);
		}
		
		long value = 0;
		for (Node child : getNode().getIndirectChildren()) {
			value += child.getAttributes().getNumFNops(c, status, ignoreIterationsAboveCond);
		}			
		if (ignoreIterationsAboveCond == null) {
			setCachedNumFnops(c, value);
			setCachedNumFnopsStatus(c, status.getStatus());
			setCachedNumFnopsValid(c, true);
		}
		return value;
	}	

	public long getNumPaths() throws NumPathsOverflowException {
		long value = 1;
		for (Node child : getNode().getIndirectChildren()) {
			value = multiplyNumPaths(value, child.getAttributes().getNumPaths());
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
		
		invalidateCacheIterations();
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
	// Fixed Time Methods
	// ===================================================================== //
	public void setFixedTime(TimeUnit timeUnit) {
		m_fixedTimeTaken = timeUnit;
	}

	public TimeUnit getFixedTime() {
		return m_fixedTimeTaken;
	}
		
	public String getFixedTimeString() {
		assert (m_fixedTimeTaken != null);
		return m_fixedTimeTaken.getDescription(false);
	}
		
	// ===================================================================== //
	// Unknowns Methods
	// ===================================================================== //
    public List<Unknown> getUnknowns() {
    	return m_unknowns;
    }

    public void addUnknown(Type type) {
    	removeUnknownOfType(type);
    	m_unknowns.add(new Unknown(m_node, type, State.UNKNOWN));
    }

    public void addKnown(Type type, Reference reference) {
    	removeUnknownOfType(type);
    	m_unknowns.add(new Unknown(m_node, type, State.KNOWN, reference));
    }
    
	public boolean hasUnknowns() {
		for (Unknown unknown : m_unknowns) {
			if (unknown.getState() == State.UNKNOWN)
				return true;
		}
		return false;
	}

	public boolean hasKnowns() {
		for (Unknown unknown : m_unknowns) {
			if (unknown.getState() == State.KNOWN)
				return true;
		}
		return false;
	}
	
	public void setDisplayAsUnknown(boolean displayAsUnknown) {
		m_displayAsUnknown = displayAsUnknown;
	}

	public boolean getDisplayAsUnknown() {
		return m_displayAsUnknown;
	}
	
	private void removeUnknownOfType(Type type) {
    	Iterator<Unknown> iter = m_unknowns.iterator();
    	while (iter.hasNext()) {
    		if (iter.next().getType() == type)
    			iter.remove();
    	}
	}
	
	// ===================================================================== //
	// Loop Iteration Methods
	// ===================================================================== //
	public long getTotalIterations(Case c, Node ignoreIterationsAboveCond) {
		try {
			Iterations iterations = getTotalIterations(c, getNode(), ignoreIterationsAboveCond);
			return iterations.getGlobalIterations() * iterations.getLocalIterations();			
			
		} catch (IgnoreCondIterationsException e) {
			return IGNORE_COND_ITERATIONS;
			
		} catch (InfiniteIterationsException e) {
			return INFINITE_ITERATIONS;
		}
	}

	public void loopIterationsValid(long numIterations) throws Exception {		
		assert(false);
	}
	
	public void setLoopIterations(CommandSource src, Engine engine,
			long numIterations, boolean sourceLevelIterations) {
		assert(false);
	}
	
	public long getLoopIterations() {
		assert(false);
		return 0;
	}

	public boolean isSourceLevelIterations() {
		assert(false);
		return false;
	}
	
	public void loopPathIterationsValid(Node optionNode, long numIterations) throws Exception {
		assert(false);
	}
	
	public void setLoopPathIterations(Node optionNode, long numIterations) {
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

	long getLocalIterations(Node child) 
        throws CondIterationsNotSetException, InfiniteIterationsException {
		
		if (getNode().getParent() != null) {
			return getNode().getParent().getAttributes().getLocalIterations(getNode());
		}
		return 1;				
	}
	
	Iterations getTotalIterations(Case c, Node child, Node ignoreIterationsAboveCond) 
	    throws IgnoreCondIterationsException, InfiniteIterationsException {
		
		if (getNode().getParent() != null) {
			return getNode().getParent().getAttributes().getTotalIterations(
					c, getNode(), ignoreIterationsAboveCond);
		}
		return new Iterations();		
	}
	
	void invalidateCacheIterations() {
	}
	
	public boolean hasFloatingIterations() {
		assert(false);
		return false;
	}
	
	// ===================================================================== //
	// Checked Arithmetic Methods
	// ===================================================================== //	
	long addNumPaths(long op1, long op2) throws NumPathsOverflowException {
		long result = op1 + op2;
		if (result < 0) {
			throw new NumPathsOverflowException();
		}
		return result;
	}

	long multiplyNumPaths(long op1, long op2) throws NumPathsOverflowException {
		long result = op1 * op2;
		if (result < 0) {
			throw new NumPathsOverflowException();
		}
		return result;
	}
}
