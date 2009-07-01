package com.xmos.xta.gui.views.info.sections;

import com.xmos.xta.engine.Engine;

class SrcLabelsViewerContentProvider extends SectionsViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		return ((Engine)inputElement).getSrcLabels().values().toArray();
	}
}
