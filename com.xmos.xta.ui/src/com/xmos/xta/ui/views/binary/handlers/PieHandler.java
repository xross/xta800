package com.xmos.xta.ui.views.binary.handlers;

import com.xmos.xta.ui.views.binary.BinaryView;

public class PieHandler extends com.xmos.xta.ui.handlers.PieHandler {
	public static final String COMMAND_ID = "com.xmos.xta.ui.binary.pie.command";
	public static final String STATE_ID = "com.xmos.xta.ui.binary.pie.command.toggleState";

	public PieHandler() {
    	super(BinaryView.getEnabledCommands(), BinaryView.ID, COMMAND_ID, STATE_ID);
    }
}
