package com.xmos.xta.ui.views.visualisations.handlers;

import com.xmos.xta.ui.views.binary.BinaryView;
import com.xmos.xta.ui.views.visualisations.VisualisationsView;

public class ZoomResetHandler extends com.xmos.xta.ui.handlers.ZoomResetHandler {
	public static final String COMMAND_ID = "com.xmos.xta.ui.visualisations.zoomreset.command";

	public ZoomResetHandler() {
    	super(VisualisationsView.getEnabledCommands(), BinaryView.ID, COMMAND_ID);
    }}
