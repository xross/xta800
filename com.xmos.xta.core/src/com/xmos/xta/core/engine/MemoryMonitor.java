package com.xmos.xta.core.engine;

import com.xmos.xta.core.engine.utils.HeapUsageMonitor;

public class MemoryMonitor {
	private static final double DEFAULT_MEMORY_USAGE_THRESHOLD = 0.95;
	private boolean m_outOfMemoryAbortion;	

	public MemoryMonitor() {
		m_outOfMemoryAbortion = false;
		
	    try {
			HeapUsageMonitor.setUsageThreshold(DEFAULT_MEMORY_USAGE_THRESHOLD);
		} catch (Exception e) {
			assert(false);
		}
	    HeapUsageMonitor mws = new HeapUsageMonitor();

	    mws.addListener(new HeapUsageMonitor.Listener() {
   	        public void memoryUsageLow(long usedMemory, long maxMemory) {
	            m_outOfMemoryAbortion = true;
	        }
	    });		
	}
 
	public void reset() {
		m_outOfMemoryAbortion = false;
	}

	public boolean outOfMemoryAbort() {
		return m_outOfMemoryAbortion;
	}
	
	public void setThreashold(double threshold) throws Exception {
		HeapUsageMonitor.setUsageThreshold(threshold);
	}

	public double getThreashold() {
		return HeapUsageMonitor.getUsageThreshold();
	}
}
