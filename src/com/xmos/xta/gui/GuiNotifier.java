package com.xmos.xta.gui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.xmos.xta.engine.CommandSource;
import com.xmos.xta.engine.Notifier;

public class GuiNotifier implements Notifier {
	private boolean m_holdMessages;
	private int m_heldMessageCount;
	
	public void notify(CommandSource src, String message, String argument) {		
		if (message.equals(NOTIFY_ERROR)) {
			displayMessageBox("Error", argument);

		} else if (message.equals(HOLD_MESSAGES)) {
			m_holdMessages = true;

		} else if (message.equals(RELEASE_MESSAGES)) {
			m_holdMessages = false;
			if (m_heldMessageCount > 0) {
				displayMessageBox("Warning", "Multiple warnings detected\n(See Info/Log for details)");
				m_heldMessageCount = 0;
			}

		} else if (message.equals(NOTIFY_WARNING)) {
			if (m_holdMessages) {
				m_heldMessageCount++;
			} else {
				displayMessageBox("Warning", argument);
			}

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

		StringBuffer sb = new StringBuffer();
		sb.append("Functions Visited: ");
		for (String function : GuiEngine.getEngine().getFunctionsVisited()) {
			sb.append(function + ", ");
		}			
		displayMessageBox("Aborted", sb.substring(0, sb.length() - 2).toString());								
	}
}
