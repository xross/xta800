package com.xmos.xta.ui.views.visualisations.handlers;

import com.xmos.xta.ui.views.visualisations.VisualisationsView;

public class ZoomFitHandler extends com.xmos.xta.ui.handlers.ZoomFitHandler {
	public static final String COMMAND_ID = "com.xmos.xta.ui.visualisations.zoomfit.command";

	public ZoomFitHandler() {
    	super(VisualisationsView.getEnabledCommands(), VisualisationsView.ID, COMMAND_ID);
    }
}
