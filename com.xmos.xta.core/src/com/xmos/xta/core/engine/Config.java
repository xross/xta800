package com.xmos.xta.core.engine;

import java.util.Map;

import com.xmos.libs.core.utils.IteratableMap;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;

public class Config {
	private int m_verbosity;
	private Case m_case;
	private boolean m_timingFailuresAsErrors;
	private boolean m_warningsAsErrors;
	private boolean m_errorsAsWarnings;	
	private Map<String, Boolean> m_warnings;

	static final public int VERBOSITY_DISPLAY_INSTRUCTION_NODES = 1;
	static final public int VERBOSITY_DISPLAY_NULL_NODE = 50;
	static final public int VERBOSITY_DISPLAY_NUM_PATHS = 50;
	static final public int VERBOSITY_DISPLAY_ATTRIBUTES = 50;
	static final public int VERBOSITY_DISPLAY_ANCESTORS = 50;
	static final public int VERBOSITY_PROGRESS_MONITOR_ENABLED = 10;
	static final public int VERBOSITY_DISPLAY_KNOWNS_INFO = 50;
	static final public int VERBOSITY_DISPLAY_MEMORY_USAGE = 100;

	static final public String WARNING_REMOVED = "removed";
	
	public Config() {
		m_verbosity = 0;
		m_case = Case.WORST;
		m_timingFailuresAsErrors = true;
		m_warningsAsErrors = false;
		m_errorsAsWarnings = false;		
		m_warnings = new IteratableMap<String, Boolean>();
		m_warnings.put(WARNING_REMOVED, false);
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
	
	public Map<String, Boolean> getWarnings() {
	    return m_warnings;
	}
	
	public void setWarningEnabled(String key, boolean value, Notifier notifier, CommandSource src) {
	    if (m_warnings.keySet().contains(key)) {
	        m_warnings.put(key, value);

	    } else {
            notifier.notify(src, Notifier.NOTIFY_ERROR, String.format("Invalid warning string: %s", key));
	    }
	}
	
	public boolean getWarningEnabled(String key) {
        return m_warnings.get(key);
	}
}
