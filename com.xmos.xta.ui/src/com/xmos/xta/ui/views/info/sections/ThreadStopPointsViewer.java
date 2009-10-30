package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.ui.views.info.BaseViewer;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;
import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

public class ThreadStopPointsViewer extends BaseViewer {
	public ThreadStopPointsViewer(TableViewer viewer) {
		super(viewer);
	}

	protected BaseViewerContentProvider createContentProvider() {
		return new ThreadStopPointsViewerContentProvider();
	}
	
	protected BaseViewerLabelProvider createLabelProvider() {
		return new ThreadStopPointsViewerLabelProvider();
	}

	@Override
	protected long getPc(Object object) {
		return (Long)object;
	}
}
