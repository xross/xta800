package com.xmos.xta.rcp.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.rcp.XtaErrorLog;

public class ErrorsViewContentProvider implements IStructuredContentProvider {
    public static final boolean DEBUG = false;

	public Object[] getElements(Object inputElement) {
		if (DEBUG) System.out.printf("ErrorsViewContentProvider: getElements\n");	
		
		return ((XtaErrorLog)inputElement).getErrorStrings().toArray();
	}

	public void dispose() {
		if (DEBUG) System.out.printf("ErrorsViewContentProvider: dispose\n");	
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (DEBUG) System.out.printf("ErrorsViewContentProvider: inputChanged\n");	
	}
}
