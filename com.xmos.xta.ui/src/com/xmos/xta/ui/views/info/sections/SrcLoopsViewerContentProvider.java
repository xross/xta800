package com.xmos.xta.ui.views.info.sections;

import com.xmos.xta.core.engine.Engine;

class SrcLoopsViewerContentProvider extends SectionsViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		return ((Engine)inputElement).getSrcLevelLoops().toArray();
	}
}
