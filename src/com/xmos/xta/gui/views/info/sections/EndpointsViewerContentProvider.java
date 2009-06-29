package com.xmos.xta.gui.views.info.sections;

import com.xmos.xta.engine.Engine;

class EndpointsViewerContentProvider extends SectionsViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		return ((Engine)inputElement).getEndpoints().values().toArray();
	}
}
