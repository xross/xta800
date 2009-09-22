package com.xmos.xta.gui.views.info.sections;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xta.engine.Engine;

class SourcesViewerContentProvider extends SectionsViewerContentProvider {
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
