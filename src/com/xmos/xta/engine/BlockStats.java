package com.xmos.xta.engine;

public class BlockStats {
	
	public Block m_block;
	
	public BlockStats(Block block){
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
		int getValue(Block block); 
	}
	interface DoubleLoops { 
		double getValue(Block block); 
	}
	interface IntegerAbsolute { 
		int getValueAbsolute(Block block); 
		int getValueRelative(Block block); 
	}
	interface IntegerRelative { 
		int getValueThis(Block block); 
		int getValueRelative(Block block); 
	}
	interface DoubleAbsolute { 
		double getValueAbsolute(Block block); 
		double getValueRelative(Block block); 
	}
	interface DoubleRelative { 
		double getValueThis(Block block); 
		double getValueRelative(Block block); 
	}
	
	
	
	public double getTimeTakenLoopsOnly() {	
		return getLoopsOnlyDouble(new DoubleLoops() {
			public double getValue(Block block) { return block.getTimeTaken(true); }});
	}
	
	public int getNumCyclesLoopsOnly() {	
		return getLoopsOnlyInteger(new IntegerLoops() {
			public int getValue(Block block) { return block.getNumCycles(true); }});
	}

	public int getNumInstructionsLoopsOnly() {	
		return getLoopsOnlyInteger(new IntegerLoops() {
			public int getValue(Block block) { return block.getNumInstructions(true); }});
	}

	public int getNumFNopsLoopsOnly() {	
		return getLoopsOnlyInteger(new IntegerLoops() {
			public int getValue(Block block) { return block.getNumFNops(true); }});
	}

	public int getNumPausablesLoopsOnly() {	
		return getLoopsOnlyInteger(new IntegerLoops() {
			public int getValue(Block block) { return block.getNumPausables(true); }});
	}
	
	public int getNumUnknownsLoopsOnly() {	
		return getLoopsOnlyInteger(new IntegerLoops() {
			public int getValue(Block block) { return block.getNumUnknowns(true); }});
	}
	
	
	
	// --------------------------------------------------------------------- //
	// Relative statistics
	// --------------------------------------------------------------------- //	

	double getTimeTakenRelative(boolean recurse) {
		return getRelativeDouble(recurse, new DoubleRelative() {
			public double getValueThis(Block block)     { return block.getTimeTakenThis(); }			
			public double getValueRelative(Block block) { return block.getBlockStats().getTimeTakenRelative(true); }});
	}

	int getNumCyclesRelative(boolean recurse) {
		return getRelativeInteger(recurse, new IntegerRelative() {
			public int getValueThis(Block block)     { return block.getNumCyclesThis(); }			
			public int getValueRelative(Block block) { return block.getBlockStats().getNumCyclesRelative(true); }});
	}	
	
	private int getNumInstructionsRelative(boolean recurse) {
		return getRelativeInteger(recurse, new IntegerRelative() {
			public int getValueThis(Block block)     { return block.getNumInstructionsThis(); }			
			public int getValueRelative(Block block) { return block.getBlockStats().getNumInstructionsRelative(true); }});
	}	

	private int getNumFNopsRelative(boolean recurse) {
		return getRelativeInteger(recurse, new IntegerRelative() {
			public int getValueThis(Block block)     { return block.getNumFNopsThis(); }			
			public int getValueRelative(Block block) { return block.getBlockStats().getNumFNopsRelative(true); }});
	}	
	
	private int getNumPausablesRelative(boolean recurse) {
		return getRelativeInteger(recurse, new IntegerRelative() {
			public int getValueThis(Block block)     { return block.getNumPausablesThis(); }			
			public int getValueRelative(Block block) { return block.getBlockStats().getNumPausablesRelative(true); }});
	}	

	private int getNumUnknownsRelative(boolean recurse) {
		return getRelativeInteger(recurse, new IntegerRelative() {
			public int getValueThis(Block block)     { return block.getNumUnknownsThis(); }			
			public int getValueRelative(Block block) { return block.getBlockStats().getNumUnknownsRelative(true); }});
	}				
	
	private double getRelativeDouble(boolean recurse, DoubleRelative val) {
		double result = 0.0;
		for (Block block : m_block.getPathBlocks()) {
			result += val.getValueThis(block);
			if (recurse) {
				for (Block loopPath : block.getLoopPaths()) {

					PathAttributes attributes = block.getLoopPathAttributes(loopPath);
					if (!attributes.isAbsolute()) {
						int numIterations = attributes.getNumIterations();
						if (numIterations > 0) {
							result += val.getValueRelative(loopPath) * numIterations;
						}
					}
				}
			}
		}
		m_block.doneWithPathBlocks();
		return result;
	}

	private int getRelativeInteger(boolean recurse, IntegerRelative val) {
		int result = 0;
		for (Block block : m_block.getPathBlocks()) {
			result += val.getValueThis(block);
			if (recurse) {
				for (Block loopPath : block.getLoopPaths()) {

					PathAttributes attributes = block.getLoopPathAttributes(loopPath);
					if (!attributes.isAbsolute()) {
						int numIterations = attributes.getNumIterations();
						if (numIterations > 0) {
							result += val.getValueRelative(loopPath) * numIterations;
						}
					}
				}
			}
		}
		m_block.doneWithPathBlocks();
		return result;
	}
	
	
	// --------------------------------------------------------------------- //
	// Absolute statistics
	// --------------------------------------------------------------------- //	

	private double getTimeTakenAbsolute(boolean recurse) {
		return getAbsoluteDouble(recurse, new DoubleAbsolute() {
			public double getValueAbsolute(Block block) { return block.getBlockStats().getTimeTakenAbsolute(true); }
			public double getValueRelative(Block block) { return block.getBlockStats().getTimeTakenRelative(false); }});
	}

	private int getNumCyclesAbsolute(boolean recurse) {
		return getAbsoluteInteger(recurse, new IntegerAbsolute() {
			public int getValueAbsolute(Block block) { return block.getBlockStats().getNumCyclesAbsolute(true); }
			public int getValueRelative(Block block) { return block.getBlockStats().getNumCyclesRelative(false); }});
	}
	
	private int getNumInstructionsAbsolute(boolean recurse) {
		return getAbsoluteInteger(recurse, new IntegerAbsolute() {
			public int getValueAbsolute(Block block) { return block.getBlockStats().getNumInstructionsAbsolute(true); }
			public int getValueRelative(Block block) { return block.getBlockStats().getNumInstructionsRelative(false); }});
	}

	private int getNumFNopsAbsolute(boolean recurse) {
		return getAbsoluteInteger(recurse, new IntegerAbsolute() {
			public int getValueAbsolute(Block block) { return block.getBlockStats().getNumFNopsAbsolute(true); }
			public int getValueRelative(Block block) { return block.getBlockStats().getNumFNopsRelative(false); }});
	}

	private int getNumPausablesAbsolute(boolean recurse) {
		return getAbsoluteInteger(recurse, new IntegerAbsolute() {
			public int getValueAbsolute(Block block) { return block.getBlockStats().getNumPausablesAbsolute(true); }
			public int getValueRelative(Block block) { return block.getBlockStats().getNumPausablesRelative(false); }});
	}

	private int getNumUnknownsAbsolute(boolean recurse) {
		return getAbsoluteInteger(recurse, new IntegerAbsolute() {
			public int getValueAbsolute(Block block) { return block.getBlockStats().getNumUnknownsAbsolute(true); }
			public int getValueRelative(Block block) { return block.getBlockStats().getNumUnknownsRelative(false); }});
	}
	
	
	boolean m_absoluteDoubleTrueKnown = false;
	double m_absoluteDoubleTrue;
	
	private double getAbsoluteDouble(boolean recurse, DoubleAbsolute val) {
		double result = 0.0;
		if (recurse) {
			if(m_absoluteDoubleTrueKnown){
				result =  m_absoluteDoubleTrue;
			} else {
				for (Block block : m_block.getPathBlocks()) {
					for (Block loopPath : block.getLoopPaths()) {
						result += val.getValueAbsolute(loopPath);
						PathAttributes attributes = block.getLoopPathAttributes(loopPath);
						if (attributes.isAbsolute()) {
							int numIterations = attributes.getNumIterations();
							if (numIterations > 0) {
								result += val.getValueRelative(loopPath) * numIterations;
							}
						}
					}
				}
				m_absoluteDoubleTrue = result;
				m_absoluteDoubleTrueKnown = true;
				m_block.doneWithPathBlocks();
			}
			
		}
		
		return result;
	}

	
	private int getAbsoluteInteger(boolean recurse, IntegerAbsolute val) {
		int result = 0;
		if (recurse) {
			
			for (Block block : m_block.getPathBlocks()) {
				for (Block loopPath : block.getLoopPaths()) {
					result += val.getValueAbsolute(loopPath);
					PathAttributes attributes = block.getLoopPathAttributes(loopPath);
					if (attributes.isAbsolute()) {
						int numIterations = attributes.getNumIterations();
						if (numIterations > 0) {
							result += val.getValueRelative(loopPath) * numIterations;
						}
					}
				}
			}
			m_block.doneWithPathBlocks();
		}
		
		return result;
	}
	
	private double getLoopsOnlyDouble(DoubleLoops val) {	
        double result = 0.0;
        for (Block loopPath : m_block.getLoopPaths()) {
            int numIterations = m_block.getLoopPathAttributes(loopPath).getNumIterations();
            result += val.getValue(loopPath) * numIterations;
        }
        return result;
	}

    private int getLoopsOnlyInteger(IntegerLoops val) {
        int result = 0;
        for (Block loopPath : m_block.getLoopPaths()) {
            int numIterations = m_block.getLoopPathAttributes(loopPath).getNumIterations();
            result += val.getValue(loopPath) * numIterations;
        }
        return result;
    }
}
