package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.core.engine.sections.CodePointTableInfo;

public class AllCallsViewer extends SectionsViewer {
	public AllCallsViewer(TableViewer viewer) {
		super(viewer);
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
