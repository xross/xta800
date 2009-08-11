package com.xmos.xta.gui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPartSite;

public class NoLoadsViewer extends SectionsViewer {
	public NoLoadsViewer(Display display, Composite parent, 
			IWorkbenchPartSite site, TableViewer viewer) {
		super(display, parent, site, viewer);
	}

	SectionsViewerContentProvider createContentProvider() {
		return new NoLoadsViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new NoLoadsViewerLabelProvider();
	}

	@Override
	protected long getPc(Object object) {
		return 0;
	}
}
