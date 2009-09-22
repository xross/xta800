package com.xmos.xta.gui.dialogs;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.engine.Engine;

public class FunctionsContentProvider implements ITreeContentProvider  {
	private static Object[] EMPTY_ARRAY = new Object[]{};

	public Object[] getChildren(Object parentElement) {
		return EMPTY_ARRAY;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return false;
	}

	public Object[] getElements(Object inputElement) {
		return ((Engine)inputElement).getFunctionNames().toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
