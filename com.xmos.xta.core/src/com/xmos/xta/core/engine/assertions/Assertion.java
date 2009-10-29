package com.xmos.xta.core.engine.assertions;

public class Assertion {
    private int m_nodeId;
    private int m_coreId;

    Assertion(int nodeId, int coreId) {
    	m_nodeId = nodeId;
    	m_coreId = coreId;
    }
    
    public int getNodeId() {
    	return m_nodeId;
    }

    public int getCoreId() {
    	return m_coreId;
    }
}
