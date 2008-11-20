package com.xmos.xtagui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;

import com.xmos.xtagui.views.TreeView;

public class LoadHandler extends AbstractHandler implements IHandler {
    public static final boolean DEBUG = false;
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (DEBUG) System.out.printf("LoadHandler: execute\n");			

		TreeView control = (TreeView)HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(TreeView.ID);
		control.executeLoad();				
		return null;
	}
}
