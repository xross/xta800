package com.xmos.xta.gui.views.info.log;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.gui.GuiLog;

public class LogViewerContentProvider implements IStructuredContentProvider {
	public Object[] getElements(Object inputElement) {
		return ((GuiLog)inputElement).get().toArray();
	}
	
	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}	
}
