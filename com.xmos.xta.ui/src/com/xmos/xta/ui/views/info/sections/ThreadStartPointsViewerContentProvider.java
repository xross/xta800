package com.xmos.xta.ui.views.info.sections;

import com.xmos.xta.core.engine.Engine;

class ThreadStartPointsViewerContentProvider extends SectionsViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		return ((Engine)inputElement).getThreadStartPoints().toArray();
	}
}
