package com.xmos.xta.rcp;

import java.util.Date;

import com.xmos.xta.rcp.XtaLog.Level;

public class XtaLogEntry {
	private String m_string;
	private Level m_level;
	private Date m_date;
	
	public XtaLogEntry(String string, Level level, Date date) {
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
