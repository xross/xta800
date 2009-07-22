package com.xmos.xta.gui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPartSite;

import com.xmos.xta.engine.sections.SrcLevelLoopInfo;

public class SrcLoopsViewer extends SectionsViewer {
	public SrcLoopsViewer(Display display, Composite parent, 
			IWorkbenchPartSite site, TableViewer viewer) {
		super(display, parent, site, viewer);
	}

	SectionsViewerContentProvider createContentProvider() {
		return new SrcLoopsViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new SrcLoopsViewerLabelProvider();
	}

	@Override
	protected int getPc(Object object) {
		return ((SrcLevelLoopInfo)object).getPc();
	}
}
