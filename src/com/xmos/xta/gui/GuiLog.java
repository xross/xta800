package com.xmos.xta.gui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GuiLog {
	private Map<Level, ArrayList<GuiLogEntry>> m_entries;

	public enum Level {
		ERROR,
		WARNING,
		INFO
	}
	
	public GuiLog() {
		m_entries = new HashMap<Level, ArrayList<GuiLogEntry>>();
		m_entries.put(Level.ERROR,   new ArrayList<GuiLogEntry>());
		m_entries.put(Level.WARNING, new ArrayList<GuiLogEntry>());
		m_entries.put(Level.INFO,    new ArrayList<GuiLogEntry>());
	}
		
	public void add(Level level, String message) {
		assert(m_entries.get(level) != null);
		m_entries.get(level).add(new GuiLogEntry(message, level, new Date()));
	}
	
	public List<GuiLogEntry> getAll() {
		List<GuiLogEntry> allEntries = new ArrayList<GuiLogEntry>();

		Iterator<Map.Entry<Level, ArrayList<GuiLogEntry>>> iter = m_entries.entrySet().iterator();
		while (iter.hasNext()) {
			ArrayList<GuiLogEntry> entries = iter.next().getValue(); 
			allEntries.addAll(entries);
		}
		return allEntries;
	}

	public void clearAll() {
		Iterator<Map.Entry<Level, ArrayList<GuiLogEntry>>> iter = m_entries.entrySet().iterator();
		while (iter.hasNext()) {
			ArrayList<GuiLogEntry> entries = iter.next().getValue();
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


