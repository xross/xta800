package com.xmos.xta.ui.views.routes;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.ui.GuiEngine;

public class RoutesViewStartup implements IStartup, IPropertyChangeListener {

	public void earlyStartup() {
		GuiEngine.addBroadcastListener(this);
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView(RoutesView.ID);
			} catch (PartInitException e) {}
		}
	}			
}
