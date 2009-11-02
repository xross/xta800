package com.xmos.xta.ui.views.info.sections;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;

class AllCallsViewerContentProvider extends BaseViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		return ((Engine)inputElement).getCallTable().values().toArray();
	}
}
