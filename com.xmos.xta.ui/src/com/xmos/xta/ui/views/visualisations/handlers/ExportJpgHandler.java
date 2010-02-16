package com.xmos.xta.ui.views.visualisations.handlers;

import com.xmos.xta.ui.views.visualisations.VisualisationsView;

public class ExportJpgHandler extends com.xmos.xta.ui.handlers.ExportJpgHandler {
	public static final String COMMAND_ID = "com.xmos.xta.ui.visualisations.exportjpg.command";

	public ExportJpgHandler() {
    	super(VisualisationsView.getEnabledCommands(), VisualisationsView.ID, COMMAND_ID);
    }
}