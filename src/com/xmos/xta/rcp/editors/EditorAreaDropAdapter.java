package com.xmos.xta.rcp.editors;

import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.ui.IWorkbenchPage;

public class EditorAreaDropAdapter extends DropTargetAdapter {
	
	public void handleDrop(IWorkbenchPage page, DropTargetEvent event) {
		// Do nothing ... ignore DnD
	}
}