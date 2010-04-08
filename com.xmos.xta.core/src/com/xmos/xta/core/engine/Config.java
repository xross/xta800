package com.xmos.xta.core.engine;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;

public class Config {
	public int m_verbosity;
	public Case m_case;
	public boolean m_timingFailuresAsErrors;
	private boolean m_warningsAsErrors;
	private boolean m_errorsAsWarnings;

	static final public int VERBOSITY_DISPLAY_INSTRUCTION_NODES = 1;
	static final public int VERBOSITY_DISPLAY_NULL_NODE = 50;
	static final public int VERBOSITY_DISPLAY_NUM_PATHS = 50;
	static final public int VERBOSITY_DISPLAY_ATTRIBUTES = 50;
	static final public int VERBOSITY_DISPLAY_ANCESTORS = 50;
	static final public int VERBOSITY_PROGRESS_MONITOR_ENABLED = 10;
	static final public int VERBOSITY_DISPLAY_KNOWNS_INFO = 50;

	public Config() {
		m_verbosity = 0;
		m_case = Case.WORST;
		m_timingFailuresAsErrors = true;
		m_warningsAsErrors = false;
		m_errorsAsWarnings = false;
	}
	
	public void setWarningsAsErrors(boolean warningsAsErrors) {
		m_warningsAsErrors = warningsAsErrors;
		if (warningsAsErrors) {
			m_errorsAsWarnings = false;
		}
	}

	public void setErrorsAsWarnings(boolean errorsAsWarnings) {
		m_errorsAsWarnings = errorsAsWarnings;
		if (errorsAsWarnings) {
			m_warningsAsErrors = false;
		}
	}
	
	public boolean getWarningsAsErrors() {
		return m_warningsAsErrors;
	}

	public boolean getErrorsAsWarnings() {
		return m_errorsAsWarnings;
	}
}
