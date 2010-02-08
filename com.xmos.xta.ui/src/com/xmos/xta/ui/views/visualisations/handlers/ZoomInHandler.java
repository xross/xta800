package com.xmos.xta.ui.views.visualisations.handlers;

import com.xmos.xta.ui.views.binary.BinaryView;
import com.xmos.xta.ui.views.visualisations.VisualisationsView;

public class ZoomInHandler extends com.xmos.xta.ui.handlers.ZoomInHandler {
	public static final String COMMAND_ID = "com.xmos.xta.ui.visualisations.zoomin.command";

	public ZoomInHandler() {
    	super(VisualisationsView.getEnabledCommands(), BinaryView.ID, COMMAND_ID);
    }
}
