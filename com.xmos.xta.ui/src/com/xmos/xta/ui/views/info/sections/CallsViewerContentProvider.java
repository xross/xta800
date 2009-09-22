package com.xmos.xta.ui.views.info.sections;

import com.xmos.xta.core.engine.Engine;

class CallsViewerContentProvider extends SectionsViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		return ((Engine)inputElement).getCalls().values().toArray();
	}
}
