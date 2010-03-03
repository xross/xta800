package com.xmos.xta.core.engine;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;

public class Config {
	public int m_verbosity;
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
		m_case = Case.WORST;
		m_warnAsErrors = false;
		m_timingFailuresAsErrors = true;
	}
}
