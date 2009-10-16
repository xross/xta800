package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;

public class BranchesViewer extends SectionsViewer {
	public BranchesViewer(TableViewer viewer) {
		super(viewer);
	}

	SectionsViewerContentProvider createContentProvider() {
		return new BranchesViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new BranchesViewerLabelProvider();
	}

	@Override
	protected long getPc(Object object) {
		return ((BranchesContent)object).m_fromPc;
	}
}
