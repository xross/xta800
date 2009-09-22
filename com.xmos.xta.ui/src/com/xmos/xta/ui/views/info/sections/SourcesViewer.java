package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPartSite;

public class SourcesViewer extends SectionsViewer {
	public SourcesViewer(Display display, Composite parent, 
			IWorkbenchPartSite site, TableViewer viewer) {
		super(display, parent, site, viewer);
	}

	SectionsViewerContentProvider createContentProvider() {
		return new SourcesViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new SourcesViewerLabelProvider();
	}

	@Override
	protected long getPc(Object object) {
		return 0;
	}
}
