package com.xmos.xta.ui;

import org.eclipse.core.runtime.IProgressMonitor;

import com.xmos.xta.core.engine.ProgressMonitor;

public class GuiProgressMonitor implements ProgressMonitor {
	private IProgressMonitor m_monitor;
	
    public GuiProgressMonitor(IProgressMonitor monitor) {
    	m_monitor = monitor;    	
    }
	
	public void beginTask(String name, int totalWork) {
		m_monitor.beginTask(name, totalWork);
	}

	public boolean isCanceled() {
		return m_monitor.isCanceled();
	}

	public void subTask(String name) {
		m_monitor.subTask(name);
	}

	public void worked(int work) {
		m_monitor.worked(work);
	}
}
