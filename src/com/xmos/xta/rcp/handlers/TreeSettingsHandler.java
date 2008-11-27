package com.xmos.xta.rcp.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.xmos.xta.rcp.dialogs.Settings;
import com.xmos.xta.rcp.views.TreeView;

public class TreeSettingsHandler extends AbstractHandler implements IHandler {
    public static final boolean DEBUG = false;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (DEBUG) System.out.printf("TreeSettingsHandler: execute\n");		
		
		final TreeView treeView = (TreeView)HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(TreeView.ID);
		
		new Settings(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), treeView);	    
		return null;
	}	
}
