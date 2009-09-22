package com.xmos.xta.ui.views.info.sections;

import com.xmos.xta.core.engine.Engine;

class EndpointsViewerContentProvider extends SectionsViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		return ((Engine)inputElement).getEndpoints().values().toArray();
	}
}
