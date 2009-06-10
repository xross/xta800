package com.xmos.xta.engine.analysis.structural.nodes;

public class IBufferVector {

	private static final int IBUFFERSIZE = 4;
	int[] m_numBytesCount;

	public IBufferVector() {
		m_numBytesCount = new int[IBUFFERSIZE];
		for (int i = 0; i < IBUFFERSIZE; i++) {
			m_numBytesCount[i] = 0;
		}
	}

	public int[] getVectors() {
		return m_numBytesCount;
	}

	public void add(int numBytes, int quantity) {
		m_numBytesCount[numBytes] += quantity;
	}

}
