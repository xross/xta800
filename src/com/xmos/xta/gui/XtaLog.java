package com.xmos.xta.gui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class XtaLog {
	private static XtaLog m_xtaLog;

	public enum Level {
		ERROR,
		WARNING,
		INFO
	}
	private Map<Level, ArrayList<XtaLogEntry>> m_entries;
	
	private XtaLog() {
		m_entries = new HashMap<Level, ArrayList<XtaLogEntry>>();
		m_entries.put(Level.ERROR,   new ArrayList<XtaLogEntry>());
		m_entries.put(Level.WARNING, new ArrayList<XtaLogEntry>());
		m_entries.put(Level.INFO,    new ArrayList<XtaLogEntry>());
	}
	
	public static XtaLog getInstance() {
		if (m_xtaLog == null) {
			m_xtaLog = new XtaLog();					
		}
		return m_xtaLog;		
	}
	
	public void add(Level level, String message) {
		assert(m_entries.get(level) != null);
		m_entries.get(level).add(new XtaLogEntry(message, level, new Date()));
	}
	
	public List<XtaLogEntry> getAll() {
		List<XtaLogEntry> allEntries = new ArrayList<XtaLogEntry>();

		Iterator<Map.Entry<Level, ArrayList<XtaLogEntry>>> iter = m_entries.entrySet().iterator();
		while (iter.hasNext()) {
			ArrayList<XtaLogEntry> entries = iter.next().getValue(); 
			allEntries.addAll(entries);
		}
		return allEntries;
	}

	public void clearAll() {
		Iterator<Map.Entry<Level, ArrayList<XtaLogEntry>>> iter = m_entries.entrySet().iterator();
		while (iter.hasNext()) {
			ArrayList<XtaLogEntry> entries = iter.next().getValue();
			entries.clear();
		}
	}
	
	public String getLevelString(Level level) {
		switch (level) {
		case ERROR:
			return "ERROR";
		case WARNING:
			return "WARNING";
		case INFO:
			return "INFO";
		default:
			return "Unknown";
		}
	}
}


