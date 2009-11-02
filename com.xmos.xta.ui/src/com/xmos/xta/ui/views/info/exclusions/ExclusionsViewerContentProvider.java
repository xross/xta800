package com.xmos.xta.ui.views.info.exclusions;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;

class ExclusionsViewerContentProvider extends BaseViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		return ((Engine)inputElement).getExclusions().toArray();
	}
}
