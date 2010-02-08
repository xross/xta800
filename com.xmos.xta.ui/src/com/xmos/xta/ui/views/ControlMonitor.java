package com.xmos.xta.ui.views;

import java.util.Set;

public class ControlMonitor {
	
	private Set<String> m_enabledViews;
	private boolean m_enable;
	
	public ControlMonitor(Set<String> enabledViews, boolean enable) {
		m_enabledViews = enabledViews;
		m_enable = enable;
	}
	
	public void dispose() {
	}

	public void updateButton(String commandId) {
		if (m_enable) {
			m_enabledViews.add(commandId);
		} else {
			m_enabledViews.remove(commandId);
		}
	}

	public void exportJpg() {
	}
	
	public void zoomFit() {
	}
	
	public void zoomOut() {
	}
	
	public void zoomIn() {
	}
	
	public void zoomReset() {
	}

	public void setPie(boolean enable) {
	}

	public void setSummary(boolean enable) {
	}
}
