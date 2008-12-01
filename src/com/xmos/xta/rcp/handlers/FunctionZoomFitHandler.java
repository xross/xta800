package com.xmos.xta.rcp.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;

import com.xmos.xta.rcp.views.FunctionView;

public class FunctionZoomFitHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		FunctionView control = (FunctionView)HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(FunctionView.ID);
		control.zoomFit();
		return null;
	}

}
