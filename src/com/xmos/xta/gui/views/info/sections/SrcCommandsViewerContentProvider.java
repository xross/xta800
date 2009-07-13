package com.xmos.xta.gui.views.info.sections;

import com.xmos.xta.engine.Engine;

class SrcCommandsViewerContentProvider extends SectionsViewerContentProvider {
	public Object[] getElements(Object inputElement) {
		return ((Engine)inputElement).getSrcLevelCommands().toArray();
	}
}
