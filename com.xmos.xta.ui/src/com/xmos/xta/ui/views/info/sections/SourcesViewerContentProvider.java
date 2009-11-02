package com.xmos.xta.ui.views.info.sections;

import com.xmos.libs.xeloader.DebugInfo;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.ui.views.info.BaseViewerContentProvider;

class SourcesViewerContentProvider extends BaseViewerContentProvider {
	private static Object[] EMPTY_ARRAY = new Object[]{};

	public Object[] getElements(Object inputElement) {
		Engine engine = ((Engine)inputElement);
		
	    DebugInfo debugInfo = engine.getXeLoader().getDebugInfo(engine.getNodeId(), engine.getCoreId());
	    if (debugInfo == null) {
	    	return EMPTY_ARRAY;
	    }
		return debugInfo.getDebugEngine().getDebugFiles().toArray();
	}
}
