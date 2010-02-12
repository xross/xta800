package com.xmos.xta.ui.views.binary.handlers;

import com.xmos.xta.ui.views.binary.BinaryView;

public class ZoomInHandler extends com.xmos.xta.ui.handlers.ZoomInHandler {
	public static final String COMMAND_ID = "com.xmos.xta.ui.binary.zoomin.command";

	public ZoomInHandler() {
    	super(BinaryView.getEnabledCommands(), BinaryView.ID, COMMAND_ID);
    }
}
