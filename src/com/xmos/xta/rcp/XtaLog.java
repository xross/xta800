package com.xmos.xta.rcp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XtaLog {
	private static XtaLog m_xtaLog;

	public enum Level {
		ERROR,
		WARNING,
		INFO
	}	
	private Map<Level, ArrayList<String>> m_strings;
	
	private XtaLog() {
		m_strings = new HashMap<Level, ArrayList<String>>();
		m_strings.put(Level.ERROR,   new ArrayList<String>());
		m_strings.put(Level.WARNING, new ArrayList<String>());
		m_strings.put(Level.INFO,    new ArrayList<String>());
	}
	
	public static XtaLog getInstance() {
		if (m_xtaLog == null) {
			m_xtaLog = new XtaLog();					
		}
		return m_xtaLog;		
	}
	
	public void add(Level level, String message) {
		assert(m_strings.get(level) != null);
		m_strings.get(level).add(message);
	}
	
	public List<String> getStrings(Level level) {
		assert(m_strings.get(level) != null);
		return m_strings.get(level);
	}
	
	public void clearStrings(Level level) {
		assert(m_strings.get(level) != null);
		m_strings.get(level).clear();
	}
}


