package com.xmos.xta.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;

import com.xmos.xta.ui.dialogs.Settings;

public class ActionSettings extends ActionDelegate implements IWorkbenchWindowActionDelegate {
	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		new Settings(PlatformUI.getWorkbench().getDisplay().getActiveShell()); 
	}
}
