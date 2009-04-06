package com.xmos.xta.engine;

public class BasicBlockStats {
	
	public PathBasicBlock m_block;
	
	public BasicBlockStats(PathBasicBlock block){
		m_block = block;
	}
	
	
	public void invalidateAll(){
		m_timeTakenValid = false;
		m_numInstructionsValid = false;
		m_numFNopsValid = false;
		m_numCyclesValid = false;
		m_numPausablesValid = false;
		m_numUnknownsValid = false;
		m_absoluteDoubleTrueKnown = false;
	}
	
	private double m_timeTaken;
	private boolean m_timeTakenValid;
	private int m_numInstructions;
	private boolean m_numInstructionsValid;
	private int m_numFNops;
	private boolean m_numFNopsValid;
	private int m_numCycles;
	private boolean m_numCyclesValid;
	private int m_numPausables;
	private boolean m_numPausablesValid;
	private int m_numUnknowns;
	private boolean m_numUnknownsValid;
	
	// --------------------------------------------------------------------- //
	// Statistics
	// --------------------------------------------------------------------- //	
	
	public double getTimeTaken(boolean recurse) {
		double timeTaken;
		if(recurse){
			if(m_timeTakenValid){
				timeTaken = m_timeTaken; 
			} else {
				timeTaken = getTimeTakenRelative(recurse) + getTimeTakenAbsolute(recurse);
				m_timeTaken = timeTaken;
				m_timeTakenValid = true;
			}
		} else {
			timeTaken = getTimeTakenRelative(false) + getTimeTakenAbsolute(false);
		}
		
		return timeTaken;
	}

	public int getNumInstructions(boolean recurse) {
		int numInstructions;
		if(recurse){
			if(m_numInstructionsValid){
				numInstructions = m_numInstructions; 
			} else {
				numInstructions = getNumInstructionsRelative(recurse) + getNumInstructionsAbsolute(recurse);
				m_numInstructions = numInstructions;
				m_numInstructionsValid = true;
			}
		} else {
			numInstructions = getNumInstructionsRelative(false) + getNumInstructionsAbsolute(false);
		}
		return numInstructions;
	}

	public int getNumFNops(boolean recurse) {
		int numFnops;
		if(recurse){
			if(m_numFNopsValid){
				numFnops = m_numFNops; 
			} else {
				numFnops = getNumFNopsRelative(recurse) + getNumFNopsAbsolute(recurse);
				m_numFNops = numFnops;
				m_numFNopsValid = true;
			}
		} else {
			numFnops = getNumFNopsRelative(false) + getNumFNopsAbsolute(false);
		}
		return numFnops;

	}

	public int getNumCycles(boolean recurse) {
		int numCycles;
		if(recurse){
			if(m_numCyclesValid){
				numCycles = m_numCycles; 
			} else {
				numCycles = getNumCyclesRelative(recurse) + getNumCyclesAbsolute(recurse);
				m_numCycles = numCycles;
				m_numCyclesValid = true;
			}
		} else {
			numCycles = getNumCyclesRelative(false) + getNumCyclesAbsolute(false);
		}
		return numCycles;
	}

	public int getNumPausables(boolean recurse) {
		int numPausables;
		if(recurse){
			if(m_numPausablesValid){
				numPausables = m_numPausables; 
			} else {
				numPausables = getNumPausablesRelative(recurse) + getNumPausablesAbsolute(recurse);
				m_numPausables = numPausables;
				m_numPausablesValid = true;
			}
		} else {
			numPausables = getNumPausablesRelative(false) + getNumPausablesAbsolute(false);
		}
		
		return numPausables;
	}

	public int getNumUnknowns(boolean recurse) {
		int numUnknowns;
		if(recurse){
			if(m_numUnknownsValid){
				numUnknowns = m_numUnknowns; 
			} else {
				numUnknowns = getNumUnknownsRelative(recurse) + getNumUnknownsAbsolute(recurse);
				m_numUnknowns = numUnknowns;
				m_numUnknownsValid = true;
			}
		} else {
			numUnknowns = getNumUnknownsRelative(false) + getNumUnknownsAbsolute(false);
		}
		return numUnknowns;
	}
	

	// --------------------------------------------------------------------- //
	// 'Loops only' statistics
	// --------------------------------------------------------------------- //		
	interface IntegerLoops { 
		int getValue(PathBasicBlock block); 
	}
	interface DoubleLoops { 
		double getValue(PathBasicBlock block); 
	}
	interface IntegerAbsolute { 
		int getValueAbsolute(PathBasicBlock block); 
		int getValueRelative(PathBasicBlock block); 
	}
	interface IntegerRelative { 
		int getValueThis(PathBasicBlock block); 
		int getValueRelative(PathBasicBlock block); 
	}
	interface DoubleAbsolute { 
		double getValueAbsolute(PathBasicBlock block); 
		double getValueRelative(PathBasicBlock block); 
	}
	interface DoubleRelative { 
		double getValueThis(PathBasicBlock block); 
		double getValueRelative(PathBasicBlock block); 
	}
	
	
	
	public double getTimeTakenLoopsOnly() {	
		return getLoopsOnlyDouble(new DoubleLoops() {
			public double getValue(PathBasicBlock block) { return block.getTimeTaken(true); }});
	}
	
	public int getNumCyclesLoopsOnly() {	
		return getLoopsOnlyInteger(new IntegerLoops() {
			public int getValue(PathBasicBlock block) { return block.getNumCycles(true); }});
	}

	public int getNumInstructionsLoopsOnly() {	
		return getLoopsOnlyInteger(new IntegerLoops() {
			public int getValue(PathBasicBlock block) { return block.getNumInstructions(true); }});
	}

	public int getNumFNopsLoopsOnly() {	
		return getLoopsOnlyInteger(new IntegerLoops() {
			public int getValue(PathBasicBlock block) { return block.getNumFNops(true); }});
	}

	public int getNumPausablesLoopsOnly() {	
		return getLoopsOnlyInteger(new IntegerLoops() {
			public int getValue(PathBasicBlock block) { return block.getNumPausables(true); }});
	}
	
	public int getNumUnknownsLoopsOnly() {	
		return getLoopsOnlyInteger(new IntegerLoops() {
			public int getValue(PathBasicBlock block) { return block.getNumUnknowns(true); }});
	}
	
	
	
	// --------------------------------------------------------------------- //
	// Relative statistics
	// --------------------------------------------------------------------- //	

	private double getTimeTakenRelative(boolean recurse) {
		return getRelativeDouble(recurse, new DoubleRelative() {
			public double getValueThis(PathBasicBlock block)     { return block.getTimeTakenThis(); }			
			public double getValueRelative(PathBasicBlock block) { return block.getBlockStats().getTimeTakenRelative(true); }});
	}

	private int getNumCyclesRelative(boolean recurse) {
		return getRelativeInteger(recurse, new IntegerRelative() {
			public int getValueThis(PathBasicBlock block)     { return block.getNumCyclesThis(); }			
			public int getValueRelative(PathBasicBlock block) { return block.getBlockStats().getNumCyclesRelative(true); }});
	}	
	
	private int getNumInstructionsRelative(boolean recurse) {
		return getRelativeInteger(recurse, new IntegerRelative() {
			public int getValueThis(PathBasicBlock block)     { return block.getNumInstructionsThis(); }			
			public int getValueRelative(PathBasicBlock block) { return block.getBlockStats().getNumInstructionsRelative(true); }});
	}	

	private int getNumFNopsRelative(boolean recurse) {
		return getRelativeInteger(recurse, new IntegerRelative() {
			public int getValueThis(PathBasicBlock block)     { return block.getNumFNopsThis(); }			
			public int getValueRelative(PathBasicBlock block) { return block.getBlockStats().getNumFNopsRelative(true); }});
	}	
	
	private int getNumPausablesRelative(boolean recurse) {
		return getRelativeInteger(recurse, new IntegerRelative() {
			public int getValueThis(PathBasicBlock block)     { return block.getNumPausablesThis(); }			
			public int getValueRelative(PathBasicBlock block) { return block.getBlockStats().getNumPausablesRelative(true); }});
	}	

	private int getNumUnknownsRelative(boolean recurse) {
		return getRelativeInteger(recurse, new IntegerRelative() {
			public int getValueThis(PathBasicBlock block)     { return block.getNumUnknownsThis(); }			
			public int getValueRelative(PathBasicBlock block) { return block.getBlockStats().getNumUnknownsRelative(true); }});
	}				
	
	private double getRelativeDouble(boolean recurse, DoubleRelative val) {
		double result = 0.0;
		for (PathBasicBlock block : m_block.getPathBlocks()) {
			result += val.getValueThis(block);
			if (recurse) {
				for (PathBasicBlock loopPath : block.getLoopPaths(m_block)) {
					
					boolean isAbsolute;
					try {
						isAbsolute = block.getLoops().isAbsolute(loopPath);
					} catch (Exception e) {
						isAbsolute = false;
					}
					
					if(!isAbsolute){
						long relativeLoops;
						try {
							relativeLoops = block.getLoops().getLoopIterations(loopPath);
						} catch (Exception e) {
							relativeLoops = 0;
						}
						if (relativeLoops != -1) {
							if (relativeLoops > 0) {
								result += val.getValueRelative(loopPath) * relativeLoops;
							}
						}
					}
				}
			}
		}
		return result;
	}

	private int getRelativeInteger(boolean recurse, IntegerRelative val) {
		int result = 0;
		for (PathBasicBlock block : m_block.getPathBlocks()) {
			result += val.getValueThis(block);
			if (recurse) {
				for (PathBasicBlock loopPath : block.getLoopPaths(m_block)) {
					boolean isAbsolute;
					try {
						isAbsolute = block.getLoops().isAbsolute(loopPath);
					} catch (Exception e) {
						isAbsolute = false;
					}
					
					if(!isAbsolute){
						long relativeLoops;
						try {
							relativeLoops = block.getLoops().getLoopIterations(loopPath);
						} catch (Exception e) {
							relativeLoops = 0;
						}
						if (relativeLoops != -1) {
							if (relativeLoops > 0) {
								result += val.getValueRelative(loopPath) * relativeLoops;
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	
	// --------------------------------------------------------------------- //
	// Absolute statistics
	// --------------------------------------------------------------------- //	

	private double getTimeTakenAbsolute(boolean recurse) {
		return getAbsoluteDouble(recurse, new DoubleAbsolute() {
			public double getValueAbsolute(PathBasicBlock block) { return block.getBlockStats().getTimeTakenAbsolute(true); }
			public double getValueRelative(PathBasicBlock block) { return block.getBlockStats().getTimeTakenRelative(false); }});
	}

	private int getNumCyclesAbsolute(boolean recurse) {
		return getAbsoluteInteger(recurse, new IntegerAbsolute() {
			public int getValueAbsolute(PathBasicBlock block) { return block.getBlockStats().getNumCyclesAbsolute(true); }
			public int getValueRelative(PathBasicBlock block) { return block.getBlockStats().getNumCyclesRelative(false); }});
	}
	
	private int getNumInstructionsAbsolute(boolean recurse) {
		return getAbsoluteInteger(recurse, new IntegerAbsolute() {
			public int getValueAbsolute(PathBasicBlock block) { return block.getBlockStats().getNumInstructionsAbsolute(true); }
			public int getValueRelative(PathBasicBlock block) { return block.getBlockStats().getNumInstructionsRelative(false); }});
	}

	private int getNumFNopsAbsolute(boolean recurse) {
		return getAbsoluteInteger(recurse, new IntegerAbsolute() {
			public int getValueAbsolute(PathBasicBlock block) { return block.getBlockStats().getNumFNopsAbsolute(true); }
			public int getValueRelative(PathBasicBlock block) { return block.getBlockStats().getNumFNopsRelative(false); }});
	}

	private int getNumPausablesAbsolute(boolean recurse) {
		return getAbsoluteInteger(recurse, new IntegerAbsolute() {
			public int getValueAbsolute(PathBasicBlock block) { return block.getBlockStats().getNumPausablesAbsolute(true); }
			public int getValueRelative(PathBasicBlock block) { return block.getBlockStats().getNumPausablesRelative(false); }});
	}

	private int getNumUnknownsAbsolute(boolean recurse) {
		return getAbsoluteInteger(recurse, new IntegerAbsolute() {
			public int getValueAbsolute(PathBasicBlock block) { return block.getBlockStats().getNumUnknownsAbsolute(true); }
			public int getValueRelative(PathBasicBlock block) { return block.getBlockStats().getNumUnknownsRelative(false); }});
	}
	
	
	boolean m_absoluteDoubleTrueKnown = false;
	double m_absoluteDoubleTrue;
	
	private double getAbsoluteDouble(boolean recurse, DoubleAbsolute val) {
		double result = 0.0;
		if (recurse) {
			if(m_absoluteDoubleTrueKnown){
				result =  m_absoluteDoubleTrue;
			} else {
				for (PathBasicBlock block : m_block.getPathBlocks()) {
					for (PathBasicBlock loopPath : block.getLoopPaths(m_block)) {
						result += val.getValueAbsolute(loopPath);
						
						boolean isAbsolute;
						try {
							isAbsolute = block.getLoops().isAbsolute(loopPath);
						} catch (Exception e) {
							isAbsolute = false;
						}
						if(isAbsolute){
						
							long absoluteLoops;
							try {
								absoluteLoops = block.getLoops().getLoopIterations(loopPath);
							} catch (Exception e) {
								absoluteLoops = 0;
							}
							if (absoluteLoops != -1) {
								if (absoluteLoops > 0) {
									result += val.getValueRelative(loopPath) * absoluteLoops;
								}
							}
						}
					}
				}
				m_absoluteDoubleTrue = result;
				m_absoluteDoubleTrueKnown = true;
			}
		}
		return result;
	}

	
	private int getAbsoluteInteger(boolean recurse, IntegerAbsolute val) {
		int result = 0;
		if (recurse) {
			for (PathBasicBlock block : m_block.getPathBlocks()) {
				for (PathBasicBlock loopPath : block.getLoopPaths(m_block)) {
					result += val.getValueAbsolute(loopPath);
					boolean isAbsolute;
					try {
						isAbsolute = block.getLoops().isAbsolute(loopPath);
					} catch (Exception e) {
						isAbsolute = false;
					}
					if(isAbsolute){
					
						long absoluteLoops;
						try {
							absoluteLoops = block.getLoops().getLoopIterations(loopPath);
						} catch (Exception e) {
							absoluteLoops = 0;
						}
						if (absoluteLoops != -1) {
							if (absoluteLoops > 0) {
								result += val.getValueRelative(loopPath) * absoluteLoops;
							}
						}
					}
				}
			}
		}
		
		return result;
	}
	
	private double getLoopsOnlyDouble(DoubleLoops val) {	
        double result = 0.0;
        for (PathBasicBlock loopPath : m_block.getLoopPaths(m_block)) {
        	long numIterations;
			try {
				numIterations = m_block.getLoops().getLoopIterations(loopPath);
			} catch (Exception e) {
				numIterations = 0;
			}
            result += val.getValue(loopPath) * numIterations;
        }
        return result;
	}

    private int getLoopsOnlyInteger(IntegerLoops val) {
        int result = 0;
        for (PathBasicBlock loopPath : m_block.getLoopPaths(m_block)) {
        	long numIterations;
			try {
				numIterations = m_block.getLoops().getLoopIterations(loopPath);
			} catch (Exception e) {
				numIterations = 0;
			}
            result += val.getValue(loopPath) * numIterations;
        }
        return result;
    }
}
