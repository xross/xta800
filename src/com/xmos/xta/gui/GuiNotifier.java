package com.xmos.xta.gui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.xmos.xta.engine.Notifier;

public class GuiNotifier implements Notifier {
	public void notify(String message, String argument) {		
		if (message.equals(NOTIFY_ERROR)) {
			displayMessageBox("Error", "Error detected\n(See log for details)");
			
		} else if (message.equals(NOTIFY_WARNING)) {
			displayMessageBox("Warning", "Warnings detected\n(See log for details)");
			
		} else if (message.equals(NOTIFY_ANALYSIS_ABORTED)) {
			displayAbortDetails();
		}		
		GuiEngine.broadcast(message, argument);			
	}
	
	private void displayMessageBox(final String title, final String message) {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().syncExec(new Runnable() {
			public void run() {
				Status status = new Status(IStatus.ERROR, "My Plug-in ID", 0, message, null);
		        Shell shell = workbench.getDisplay().getActiveShell();
				ErrorDialog.openError(shell, title, null, status);
			}
		});
	}
	
	private void displayAbortDetails() {
		String abortString = "Functions Visited: ";
		for (String function : GuiEngine.getEngine().getFunctionsVisited()) {
			abortString += function + ", ";
		}			
		abortString = abortString.substring(0, abortString.length() - 2);
		displayMessageBox("Aborted", abortString);								
	}
}
