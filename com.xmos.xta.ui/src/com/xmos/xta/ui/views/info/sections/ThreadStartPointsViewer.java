package com.xmos.xta.ui.views.info.sections;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.ui.views.info.BaseViewer;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;
import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

public class ThreadStartPointsViewer extends BaseViewer {
	public ThreadStartPointsViewer(TableViewer viewer) {
		super(viewer);
	}

	protected BaseViewerContentProvider createContentProvider() {
		return new ThreadStartPointsViewerContentProvider();
	}
	
	protected BaseViewerLabelProvider createLabelProvider() {
		return new ThreadStartPointsViewerLabelProvider();
	}

	@Override
	protected Set<Long> getPcs(Object object) {
		Set<Long> pcs = new TreeSet<Long>();
		pcs.add((Long)object);
		return pcs;
	}
}
