package com.xmos.xta.gui.views.info.sections;

import com.xmos.xta.engine.Engine;

class ExclusionsViewerContentProvider extends SectionsViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		return ((Engine)inputElement).getExclusions().toArray();
	}
}
