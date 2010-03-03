package com.xmos.xta.core.engine.utils;

public class NodeCorePair {
    private int m_node;
    private int m_core;
    
    public NodeCorePair(int node, int core) {
    	m_node = node;
    	m_core = core;
    }
    
    public int getNode() {
    	return m_node;
    }

    public int getCore() {
    	return m_core;
    }
    
    public boolean equals(Object obj) {
    	if (obj == null)
    	return false;

        if(!(obj instanceof NodeCorePair))
            return false;

        return (m_node == ((NodeCorePair)obj).getNode()) && 
        	   (m_core == ((NodeCorePair)obj).getCore());
    } 

	public int hashCode() {
       int result = HashCodeUtil.SEED;
	   result = HashCodeUtil.hash(result, m_node);
	   result = HashCodeUtil.hash(result, m_core);
	   return result;
	}    
}
