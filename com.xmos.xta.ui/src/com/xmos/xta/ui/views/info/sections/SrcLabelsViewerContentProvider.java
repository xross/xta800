package com.xmos.xta.ui.views.info.sections;

import com.xmos.xta.core.engine.Engine;

class SrcLabelsViewerContentProvider extends SectionsViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		return ((Engine)inputElement).getSrcLabels().values().toArray();
	}
}
