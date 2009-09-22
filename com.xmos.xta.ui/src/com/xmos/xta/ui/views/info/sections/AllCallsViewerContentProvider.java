package com.xmos.xta.ui.views.info.sections;

import com.xmos.xta.core.engine.Engine;

class AllCallsViewerContentProvider extends SectionsViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		return ((Engine)inputElement).getCallTable().values().toArray();
	}
}
