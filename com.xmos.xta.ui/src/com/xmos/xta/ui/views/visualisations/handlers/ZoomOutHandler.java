package com.xmos.xta.ui.views.visualisations.handlers;

import com.xmos.xta.ui.views.binary.BinaryView;
import com.xmos.xta.ui.views.visualisations.VisualisationsView;

public class ZoomOutHandler extends com.xmos.xta.ui.handlers.ZoomOutHandler {
	public static final String COMMAND_ID = "com.xmos.xta.ui.visualisations.zoomout.command";

	public ZoomOutHandler() {
    	super(VisualisationsView.getEnabledCommands(), BinaryView.ID, COMMAND_ID);
    }
}
