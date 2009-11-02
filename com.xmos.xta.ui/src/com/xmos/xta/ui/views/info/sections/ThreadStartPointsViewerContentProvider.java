package com.xmos.xta.ui.views.info.sections;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;

class ThreadStartPointsViewerContentProvider extends BaseViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		return ((Engine)inputElement).getThreadStartPoints().toArray();
	}
}
