package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;

public class ThreadStartPointsViewer extends SectionsViewer {
	public ThreadStartPointsViewer(TableViewer viewer) {
		super(viewer);
	}

	SectionsViewerContentProvider createContentProvider() {
		return new ThreadStartPointsViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new ThreadStartPointsViewerLabelProvider();
	}

	@Override
	protected long getPc(Object object) {
		return (Long)object;
	}
}
