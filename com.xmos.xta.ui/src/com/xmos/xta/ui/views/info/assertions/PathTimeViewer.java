package com.xmos.xta.ui.views.info.assertions;

import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.ui.views.info.BaseViewer;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;
import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

public class PathTimeViewer extends BaseViewer {
	public PathTimeViewer(TableViewer viewer) {
		super(viewer);
	}

	protected BaseViewerContentProvider createContentProvider() {
		return new PathTimeTimeContentProvider();
	}
	
	protected BaseViewerLabelProvider createLabelProvider() {
		return new LabelProvider();
	}

	protected long getPc(Object object) {
		return 0;
	}
}

class PathTimeTimeContentProvider extends BaseViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		return ((Engine)inputElement).getPathTimeAssertions().toArray();
	}
}
