package com.xmos.xta.core.engine.analysis.structural.nodes.attributes;

public class Iterations {
    private long m_globalIterations;
    private long m_localIterations;

    Iterations() {
    	m_globalIterations = 1;
    	m_localIterations = 1;
    }

    void setGlobalIterations(long globalIterations) {
    	m_globalIterations = globalIterations;
    }

    long getGlobalIterations() {
    	return m_globalIterations;
    }

    void setLocalIterations(long localIterations) {
    	m_localIterations = localIterations;
    }

    long getLocalIterations() {
    	return m_localIterations;
    }
}
