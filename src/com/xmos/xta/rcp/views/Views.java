package com.xmos.xta.rcp.views;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class Views {
	public static void showView(String viewID) {
	    IWorkbench workbench = PlatformUI.getWorkbench();
	    IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
	    try {
		    workbenchWindow.getActivePage().showView(viewID);
	    } catch (PartInitException e) {
		    e.printStackTrace();
	    }
	}
}
