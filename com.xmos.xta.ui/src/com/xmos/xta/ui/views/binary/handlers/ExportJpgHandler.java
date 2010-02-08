package com.xmos.xta.ui.views.binary.handlers;

import com.xmos.xta.ui.views.binary.BinaryView;

public class ExportJpgHandler extends com.xmos.xta.ui.handlers.ExportJpgHandler {
	public static final String COMMAND_ID = "com.xmos.xta.ui.binary.exportjpg.command";

	public ExportJpgHandler() {
    	super(BinaryView.getEnabledCommands(), BinaryView.ID, COMMAND_ID);
    }
}