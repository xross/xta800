package com.xmos.xta.ui;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class GuiNotifier implements Notifier {
	private boolean m_holdMessages;
	private int m_heldMessageCount;
	
	public void notify(CommandSource src, String message, String argument) {
		if (message.equals(Notifier.NOTIFY_ERROR)) {
			log(IMarker.PROBLEM, argument, IMarker.SEVERITY_ERROR, src);
			displayMessageBox("Error", argument);
			
		} else if (message.equals(Notifier.NOTIFY_WARNING)) {
			log(IMarker.PROBLEM, argument, IMarker.SEVERITY_WARNING, src);
			if (m_holdMessages) {
				m_heldMessageCount++;
			} else {
				displayMessageBox("Warning", argument);
			}
		
		} else if (message.equals(Notifier.NOTIFY_INFO)) {
			log(IMarker.MESSAGE, argument, IMarker.SEVERITY_INFO, src);

		} else if (message.equals(HOLD_MESSAGES)) {
			m_holdMessages = true;

		} else if (message.equals(RELEASE_MESSAGES)) {
			m_holdMessages = false;
			if (m_heldMessageCount > 0) {
				displayMessageBox("Warning", "Multiple warnings detected\n(See Problems view for details)");
				m_heldMessageCount = 0;
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

	private void log(String type, String argument, int severity, CommandSource src) {
		try {
			IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			IMarker marker = workspaceRoot.createMarker(type);
			marker.setAttribute(IMarker.MESSAGE, argument);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (src.getLineNum() > 0) {
				marker.setAttribute(IMarker.LOCATION,
						String.format("%s:%d", src.getFileName(), src.getLineNum()));
			} else {
				marker.setAttribute(IMarker.LOCATION, "xta");
			}
		} catch (CoreException e) {
		}
	}
}
