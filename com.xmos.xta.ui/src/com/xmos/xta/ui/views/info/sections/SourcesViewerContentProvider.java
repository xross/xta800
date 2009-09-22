package com.xmos.xta.ui.views.info.sections;

import java.util.ArrayList;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xta.core.engine.Engine;

class SourcesViewerContentProvider extends SectionsViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		Engine engine = ((Engine)inputElement);
		
	    DebugInfo debugInfo = engine.getXeLoader().getDebugInfo(engine.getNodeId(), engine.getCoreId());
	    if (debugInfo == null) {
	    	return new ArrayList<Object>().toArray();
	    }
		return debugInfo.getDebugEngine().getDebugFiles().toArray();
	}
}
