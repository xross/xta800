package com.xmos.xta.gui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPartSite;

public class ThreadStopPointsViewer extends SectionsViewer {
	public ThreadStopPointsViewer(Display display, Composite parent, 
			IWorkbenchPartSite site, TableViewer viewer) {
		super(display, parent, site, viewer);
	}

	SectionsViewerContentProvider createContentProvider() {
		return new ThreadStopPointsViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new ThreadStopPointsViewerLabelProvider();
	}
}
