package com.xmos.xta.gui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPartSite;

import com.xmos.xta.engine.sections.SrcLabelTableInfo;

public class AllSrcLabelsViewer extends SectionsViewer {
	public AllSrcLabelsViewer(Display display, Composite parent, 
			IWorkbenchPartSite site, TableViewer viewer) {
		super(display, parent, site, viewer);
	}

	SectionsViewerContentProvider createContentProvider() {
		return new AllSrcLabelsViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new AllSrcLabelsViewerLabelProvider();
	}

	@Override
	protected int getPc(Object object) {
		return ((SrcLabelTableInfo)object).getPc();
	}
}
