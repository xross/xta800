package com.xmos.xta.ui.views.binary.handlers;

import com.xmos.xta.ui.views.binary.BinaryView;

public class ZoomFitHandler extends com.xmos.xta.ui.handlers.ZoomFitHandler {
	public static final String COMMAND_ID = "com.xmos.xta.ui.binary.zoomfit.command";

	public ZoomFitHandler() {
    	super(BinaryView.getEnabledCommands(), BinaryView.ID, COMMAND_ID);
    }
}
