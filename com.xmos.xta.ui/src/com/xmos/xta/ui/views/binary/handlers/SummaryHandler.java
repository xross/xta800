package com.xmos.xta.ui.views.binary.handlers;

import com.xmos.xta.ui.views.binary.BinaryView;

public class SummaryHandler extends com.xmos.xta.ui.handlers.SummaryHandler {
	public static final String COMMAND_ID = "com.xmos.xta.ui.binary.summary.command";
	public static final String STATE_ID = "com.xmos.xta.ui.binary.summary.command.toggleState";

	public SummaryHandler() {
    	super(BinaryView.getEnabledCommands(), BinaryView.ID, COMMAND_ID, STATE_ID);
    }
}
