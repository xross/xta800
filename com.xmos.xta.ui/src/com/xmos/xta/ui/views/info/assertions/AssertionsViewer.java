package com.xmos.xta.ui.views.info.assertions;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.ui.views.info.BaseViewer;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;
import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

public class AssertionsViewer extends BaseViewer {
	public AssertionsViewer(TableViewer viewer) {
		super(viewer);
	}

	protected BaseViewerContentProvider createContentProvider() {
		return new AssertionsViewerContentProvider();
	}
	
	protected BaseViewerLabelProvider createLabelProvider() {
		return new AssertionsViewerLabelProvider();
	}

	@Override
	protected long getPc(Object object) {
		return 0;
	}
}
