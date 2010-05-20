package com.xmos.xta.core.engine;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;

public class Config {
	private int m_verbosity;
	private Case m_case;
	private boolean m_timingFailuresAsErrors;
	private boolean m_warningsAsErrors;
	private boolean m_errorsAsWarnings;

	static final public int VERBOSITY_DISPLAY_INSTRUCTION_NODES = 1;
	static final public int VERBOSITY_DISPLAY_NULL_NODE = 50;
	static final public int VERBOSITY_DISPLAY_NUM_PATHS = 50;
	static final public int VERBOSITY_DISPLAY_ATTRIBUTES = 50;
	static final public int VERBOSITY_DISPLAY_ANCESTORS = 50;
	static final public int VERBOSITY_PROGRESS_MONITOR_ENABLED = 10;
	static final public int VERBOSITY_DISPLAY_KNOWNS_INFO = 50;
	static final public int VERBOSITY_DISPLAY_MEMORY_USAGE = 100;

	public Config() {
		m_verbosity = 0;
		m_case = Case.WORST;
		m_timingFailuresAsErrors = true;
		m_warningsAsErrors = false;
		m_errorsAsWarnings = false;
	}

	public void setVerbosity(int verbosity) {
		m_verbosity = verbosity;
	}

	public int getVerbosity() {
		return m_verbosity;
	}

	public void setCase(Case c) {
		m_case = c;
	}

	public Case getCase() {
		return m_case;
	}
	
	public void setTimingFailuresAsErrors(boolean timingFailuresAsErrors) {
		m_timingFailuresAsErrors = timingFailuresAsErrors;
	}

	public boolean getTimingFailuresAsErrors() {
		return m_timingFailuresAsErrors;
	}
	
	public void setWarningsAsErrors(boolean warningsAsErrors) {
		m_warningsAsErrors = warningsAsErrors;
		if (warningsAsErrors) {
			m_errorsAsWarnings = false;
		}
	}

	public boolean getWarningsAsErrors() {
		return m_warningsAsErrors;
	}
	
	public void setErrorsAsWarnings(boolean errorsAsWarnings) {
		m_errorsAsWarnings = errorsAsWarnings;
		if (errorsAsWarnings) {
			m_warningsAsErrors = false;
		}
	}

	public boolean getErrorsAsWarnings() {
		return m_errorsAsWarnings;
	}
}
