package com.xmos.xta.rcp.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;

import com.xmos.xta.rcp.views.StructureView.StructureView;

public class StructureZoomResetHandler extends AbstractHandler implements IHandler {
    public static final boolean DEBUG = false;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (DEBUG) System.out.printf("StructureZoomResetHandler: execute\n");			

		StructureView control = (StructureView)HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(StructureView.ID);
		control.zoomReset();
		return null;
	}

}
