package com.xmos.xta.rcp.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;

import com.xmos.xta.rcp.views.function.FunctionView;

public class FunctionZoomInHandler extends AbstractHandler implements IHandler {
    public static final boolean DEBUG = false;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (DEBUG) System.out.printf("FunctionZoomInHandler: execute\n");			

		FunctionView control = (FunctionView)HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(FunctionView.ID);
		control.zoomIn();
		return null;
	}

}
