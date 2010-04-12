package com.xmos.xta.core.engine.utils;

import com.xmos.libs.xnloader.XnLoader;

public class CoreId implements Comparable<CoreId> {
    private int m_nodeChainIndex;
    private int m_coreNumber;
    private String m_coreReference;
    private String m_nodeId;
    
    public CoreId(XnLoader xnLoader, int nodeChainIndex, int coreNumber) {
    	m_nodeChainIndex = nodeChainIndex;
    	m_coreNumber = coreNumber;
    	
    	m_nodeId = null;
    	m_coreReference = null;

    	if (xnLoader == null) {
    		return;
    	}
    	m_nodeId = xnLoader.getNodeId(nodeChainIndex);
		m_coreReference = xnLoader.getCoreReference(nodeChainIndex, coreNumber);
    }
    
    public int getNodeChainIndex() {
    	return m_nodeChainIndex;
    }

    public String getNodeId() {
    	assert(m_nodeId != null);
    	return m_nodeId;
    }

	public String getCoreReference() {
    	assert(m_coreReference != null);
		return m_coreReference;
	}

    public int getCoreNumber() {
    	return m_coreNumber;
    }
    
    public boolean equals(Object obj) {
    	if (obj == null)
    	return false;

        if(!(obj instanceof CoreId))
            return false;

        return (m_nodeChainIndex == ((CoreId)obj).getNodeChainIndex()) && 
        	   (m_coreNumber == ((CoreId)obj).getCoreNumber());
    } 

	public int hashCode() {
       int result = HashCodeUtil.SEED;
	   result = HashCodeUtil.hash(result, m_nodeChainIndex);
	   result = HashCodeUtil.hash(result, m_coreNumber);
	   return result;
	}

	public int compareTo(CoreId o) {
		if (m_nodeChainIndex == o.m_nodeChainIndex) {
			return m_coreNumber - o.m_coreNumber;
		}
		return m_nodeChainIndex - o.m_nodeChainIndex;
	}    

	public String toString() {
		if (m_coreReference != null) {
			return m_coreReference;
		} else {
			return m_nodeChainIndex + ":" + m_coreNumber;
		}
	}

	public String getDescription() {
		if (m_coreReference != null) {
			return m_coreReference + " (NodeId " + m_nodeId + ")";
		} else {
			return "Node: " + m_nodeChainIndex + " Core: " + m_coreNumber;
		}
	}
}
