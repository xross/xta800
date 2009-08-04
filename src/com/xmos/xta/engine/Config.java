package com.xmos.xta.engine;

import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;

public class Config {
	public int m_verbosity;
	public int m_numThreads;
	public int m_frequency;
	public Case m_case;
	public boolean m_warnAsErrors;
	public boolean m_timingFailuresAsErrors;

	public Config() {
		m_verbosity = 0;
		m_numThreads = 8;
		m_frequency = 400;
		m_case = Case.WORST;
		m_warnAsErrors = false;
		m_timingFailuresAsErrors = true;
	}

	public Config(Config config) {
		m_verbosity = config.m_verbosity;
		m_numThreads = config.m_numThreads;
		m_frequency = config.m_frequency;
		m_case = config.m_case;
		m_warnAsErrors = config.m_warnAsErrors;
		m_timingFailuresAsErrors = config.m_timingFailuresAsErrors;
	}
}
