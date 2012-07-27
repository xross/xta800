package com.xmos.xta.core.engine.utils;

import com.xmos.libs.core.xnloader.XmlHandler;

public class CoreConfig {
	public int m_numThreads;
	public double m_frequency;

	public static final int MAX_THREADS = 8;
	public static final int DEFAULT_THREADS = 8;
	public static final double DEFAULT_FREQUENCY_MHZ = XmlHandler.DEFAULT_FREQUENCY_HZ / 1000000;
	
	public CoreConfig() {
		m_numThreads = DEFAULT_THREADS;
		m_frequency = DEFAULT_FREQUENCY_MHZ;		
	}

	public CoreConfig(int numThreads, double frequency) {
		m_numThreads = numThreads;
		m_frequency = frequency;		
	}
}
