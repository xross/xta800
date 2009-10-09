package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPartSite;

public class SystemsViewer extends SectionsViewer {
	public SystemsViewer(Display display, Composite parent, 
			IWorkbenchPartSite site, TableViewer viewer) {
		super(display, parent, site, viewer);
	}

	SectionsViewerContentProvider createContentProvider() {
		return new SystemsViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new SystemsViewerLabelProvider();
	}

	@Override
	protected long getPc(Object object) {
		return 0;
	}
}
