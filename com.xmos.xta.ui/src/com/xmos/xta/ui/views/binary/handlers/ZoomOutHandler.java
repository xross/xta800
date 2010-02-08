package com.xmos.xta.ui.views.binary.handlers;

import com.xmos.xta.ui.views.binary.BinaryView;

public class ZoomOutHandler extends com.xmos.xta.ui.handlers.ZoomOutHandler {
	public static final String COMMAND_ID = "com.xmos.xta.ui.binary.zoomout.command";

	public ZoomOutHandler() {
    	super(BinaryView.getEnabledCommands(), BinaryView.ID, COMMAND_ID);
    }
}
