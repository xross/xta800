package com.xmos.xta.ui.views.binary.handlers;

import com.xmos.xta.ui.views.binary.BinaryView;

public class ZoomResetHandler extends com.xmos.xta.ui.handlers.ZoomResetHandler {
	public static final String COMMAND_ID = "com.xmos.xta.ui.binary.zoomreset.command";

	public ZoomResetHandler() {
    	super(BinaryView.getEnabledCommands(), BinaryView.ID, COMMAND_ID);
    }
}
