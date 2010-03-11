package com.xmos.xta.core.engine.defines;

import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.NodeCorePair;

public class LoopInfo implements DefineInfo {
	private Reference m_reference;
	private long m_iterations;

	public LoopInfo(Reference reference, long iterations) {
		m_reference = reference;
		m_iterations = iterations;
	}

	public Reference getReference() {
		return m_reference;
	}

	public long getIterations() {
		return m_iterations;
	}

	public String toString() {
		return String.format("%s: %d", m_reference, m_iterations);
	}

    public String getDescription() {
    	return m_reference.getRef();
    }

    public String getValue() {
    	return String.format("%d", m_iterations);
    }

    public NodeCorePair getNodeCore() {
    	return m_reference.getNodeCore();
    }
    
	public boolean equals(Object o) {
		boolean ret = false;
		if(o!=null) {
			if(o instanceof LoopInfo){
				ret = ((LoopInfo)o).m_reference.equals(m_reference) &&
		              (((LoopInfo)o).m_reference.getNodeNumber() == m_reference.getNodeNumber()) &&
		              (((LoopInfo)o).m_reference.getCoreNumber() == m_reference.getCoreNumber());
			}
		}
		return ret;
		
	}
}
