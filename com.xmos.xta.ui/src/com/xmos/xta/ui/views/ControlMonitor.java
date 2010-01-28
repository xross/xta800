package com.xmos.xta.ui.views;

import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;

public class ControlMonitor {
	
	private String m_view;
	private boolean m_enable;
	
	public ControlMonitor(String view, boolean enable) {
		m_view = view;
		m_enable = enable;
	}
	
	public void dispose() {
	}

	public void updateExportJpgEnabled() {
		if (m_enable) {
			GuiEngine.broadcast(Broadcaster.BROADCAST_EXPORT_JPG_ENABLED, m_view);
		} else {
			GuiEngine.broadcast(Broadcaster.BROADCAST_EXPORT_JPG_DISABLED, m_view);
		}
	}

	public void updateZoomFitEnabled() {
		if (m_enable) {
			GuiEngine.broadcast(Broadcaster.BROADCAST_ZOOM_FIT_ENABLED, m_view);
		} else {
			GuiEngine.broadcast(Broadcaster.BROADCAST_ZOOM_FIT_DISABLED, m_view);
		}
	}

	public void updateZoomInEnabled() {
		if (m_enable) {
			GuiEngine.broadcast(Broadcaster.BROADCAST_ZOOM_IN_ENABLED, m_view);
		} else {
			GuiEngine.broadcast(Broadcaster.BROADCAST_ZOOM_IN_DISABLED, m_view);
		}
	}

	public void updateZoomOutEnabled() {
		if (m_enable) {
			GuiEngine.broadcast(Broadcaster.BROADCAST_ZOOM_OUT_ENABLED, m_view);
		} else {
			GuiEngine.broadcast(Broadcaster.BROADCAST_ZOOM_OUT_DISABLED, m_view);
		}
	}

	public void updateZoomResetEnabled() {
		if (m_enable) {
			GuiEngine.broadcast(Broadcaster.BROADCAST_ZOOM_RESET_ENABLED, m_view);
		} else {
			GuiEngine.broadcast(Broadcaster.BROADCAST_ZOOM_RESET_DISABLED, m_view);
		}
	}

	public void updatePieEnabled() {
		if (m_enable) {
			GuiEngine.broadcast(Broadcaster.BROADCAST_PIE_ENABLED, m_view);
		} else {
			GuiEngine.broadcast(Broadcaster.BROADCAST_PIE_DISABLED, m_view);
		}
	}

	public void updateSummaryEnabled() {
		if (m_enable) {
			GuiEngine.broadcast(Broadcaster.BROADCAST_SUMMARY_ENABLED, m_view);
		} else {
			GuiEngine.broadcast(Broadcaster.BROADCAST_SUMMARY_DISABLED, m_view);
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
