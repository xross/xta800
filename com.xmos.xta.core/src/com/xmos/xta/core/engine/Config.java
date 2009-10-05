package com.xmos.xta.core.engine;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;

public class Config {
	public int m_verbosity;
	public int m_numThreads;
	public int m_frequency;
	public Case m_case;
	public boolean m_warnAsErrors;
	public boolean m_timingFailuresAsErrors;

	static final public int VERBOSITY_DISPLAY_INSTRUCTION_NODES = 1;
	static final public int VERBOSITY_DISPLAY_NULL_NODE = 50;
	static final public int VERBOSITY_DISPLAY_NUM_PATHS = 50;
	static final public int VERBOSITY_DISPLAY_ATTRIBUTES = 50;
	static final public int VERBOSITY_DISPLAY_ANCESTORS = 50;	
	static final public int VERBOSITY_PROGRESS_MONITOR_ENABLED = 10;
	
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
