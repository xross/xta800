package com.xmos.xta.gui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPartSite;

import com.xmos.xta.engine.sections.CodePointInfo;

public class CallsViewer extends SectionsViewer {
	public CallsViewer(Display display, Composite parent, 
			IWorkbenchPartSite site, TableViewer viewer) {
		super(display, parent, site, viewer);
	}

	SectionsViewerContentProvider createContentProvider() {
		return new CallsViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new CallsViewerLabelProvider();
	}

	@Override
	protected int getPc(Object object) {
		return ((CodePointInfo)object).getPc();
	}
}
