package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.ui.views.info.BaseViewer;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;
import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

public class SourcesViewer extends BaseViewer {
	public SourcesViewer(TableViewer viewer) {
		super(viewer);
	}

	protected BaseViewerContentProvider createContentProvider() {
		return new SourcesViewerContentProvider();
	}
	
	protected BaseViewerLabelProvider createLabelProvider() {
		return new SourcesViewerLabelProvider();
	}

	protected long getPc(Object object) {
		return 0;
	}
}
