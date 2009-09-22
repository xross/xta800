package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPartSite;

import com.xmos.xta.core.engine.sections.CodePointTableInfo;

public class AllCallsViewer extends SectionsViewer {
	public AllCallsViewer(Display display, Composite parent, 
			IWorkbenchPartSite site, TableViewer viewer) {
		super(display, parent, site, viewer);
	}

	SectionsViewerContentProvider createContentProvider() {
		return new AllCallsViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new AllCallsViewerLabelProvider();
	}

	@Override
	protected long getPc(Object object) {
		return ((CodePointTableInfo)object).getPc();
	}
}
