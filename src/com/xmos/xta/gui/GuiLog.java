package com.xmos.xta.gui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GuiLog {
	private List<GuiLogEntry> m_entries;

	public enum Level {
		ERROR,
		WARNING,
		INFO
	}
	
	public GuiLog() {
		m_entries = new ArrayList<GuiLogEntry>();
	}
		
	public void add(Level level, String message) {
		m_entries.add(new GuiLogEntry(message, level, new Date()));
	}

	public List<GuiLogEntry> get() {
		return m_entries;
	}

	public void clear() {
		m_entries.clear();
	}	
}


