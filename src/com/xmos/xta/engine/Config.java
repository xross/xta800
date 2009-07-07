package com.xmos.xta.engine;

import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;

public class Config {
	public int m_verbosity = 0;
	public int m_numThreads = 8;
	public int m_frequency = 400;
	public Case m_case = Case.WORST;
}
