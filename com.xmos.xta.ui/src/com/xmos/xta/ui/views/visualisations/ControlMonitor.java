package com.xmos.xta.ui.views.visualisations;

public interface ControlMonitor {
    void setExportJpgEnabled();
    void setZoomFitEnabled();
    void setZoomInEnabled();
    void setZoomOutEnabled();
    void setZoomResetEnabled();
    
	void exportJpg();
	void zoomFit();
	void zoomOut();
	void zoomIn();
	void zoomReset();
}
