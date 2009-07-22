package com.xmos.xta.gui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPartSite;

import com.xmos.xta.engine.sections.CodePointInfo;

public class SrcLabelsViewer extends SectionsViewer {
	public SrcLabelsViewer(Display display, Composite parent, 
			IWorkbenchPartSite site, TableViewer viewer) {
		super(display, parent, site, viewer);
	}

	SectionsViewerContentProvider createContentProvider() {
		return new SrcLabelsViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new SrcLabelsViewerLabelProvider();
	}

	@Override
	protected int getPc(Object object) {
		return ((CodePointInfo)object).getPc();
	}
}
