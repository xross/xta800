package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPartSite;

public class LabelsViewer extends SectionsViewer {
	public LabelsViewer(Display display, Composite parent, 
			IWorkbenchPartSite site, TableViewer viewer) {
		super(display, parent, site, viewer);
	}

	SectionsViewerContentProvider createContentProvider() {
		return new LabelsViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new LabelsViewerLabelProvider();
	}

	@Override
	protected long getPc(Object object) {
		return ((LabelsContent)object).m_pc;
	}
}
