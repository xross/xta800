package com.xmos.xta.rcp;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.util.PropertyChangeEvent;

import com.xmos.xta.engine.Displayer;

public class XtaErrorLog {
    public static final boolean DEBUG = false;	
	
	private static XtaErrorLog m_errorLog;
	private ArrayList<String> m_errorStrings;
	
	private XtaErrorLog() {
		m_errorStrings = new ArrayList<String>();
	}
	
	public static XtaErrorLog getInstance() {
		if (m_errorLog == null) {
			m_errorLog = new XtaErrorLog();					
		}
		return m_errorLog;		
	}
	
	public void addError(String errorString) {
	    if (DEBUG) System.out.printf("XtaErrorLog: addError: %s\n", errorString);    
		m_errorStrings.add(errorString);
		XtaDisplayer.getInstance().broadcastPropertyChange(
				new PropertyChangeEvent(this, Displayer.NOTIFY_ERROR, null, null));
	}
	
	public List<String> getErrorStrings() {
	    if (DEBUG) System.out.printf("XtaErrorLog: getErrorStrings\n");    
		return m_errorStrings;
	}
}


