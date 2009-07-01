package com.xmos.xta.gui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPartSite;

public class BranchesViewer extends SectionsViewer {
	public BranchesViewer(Display display, Composite parent, 
			IWorkbenchPartSite site, TableViewer viewer) {
		super(display, parent, site, viewer);
	}

	SectionsViewerContentProvider createContentProvider() {
		return new BranchesViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new BranchesViewerLabelProvider();
	}
}
