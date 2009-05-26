package com.xmos.xta.gui;

import java.util.Date;

import com.xmos.xta.gui.GuiLog.Level;

public class GuiLogEntry {
	private String m_string;
	private Level m_level;
	private Date m_date;
	
	public GuiLogEntry(String string, Level level, Date date) {
		m_string = string;
		m_level = level;
		m_date = date;
	}
	
	public String getString() {
		return m_string;	
	}

	public Date getDate() {
		return m_date;	
	}
	
	public Level getLevel() {
		return m_level;	
	}
}
