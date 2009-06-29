package com.xmos.xta.gui.views.info.sections;

import com.xmos.xta.engine.Engine;

class AllSrcLabelsViewerContentProvider extends SectionsViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		return ((Engine)inputElement).getSrcLabelTable().values().toArray();
	}
}
