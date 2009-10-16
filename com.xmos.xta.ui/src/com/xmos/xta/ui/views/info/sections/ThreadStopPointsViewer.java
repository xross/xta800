package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;

public class ThreadStopPointsViewer extends SectionsViewer {
	public ThreadStopPointsViewer(TableViewer viewer) {
		super(viewer);
	}

	SectionsViewerContentProvider createContentProvider() {
		return new ThreadStopPointsViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new ThreadStopPointsViewerLabelProvider();
	}

	@Override
	protected long getPc(Object object) {
		return (Long)object;
	}
}
