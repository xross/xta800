package com.xmos.xta.rcp.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.engine.Route;

public class UnknownsViewContentProvider implements IStructuredContentProvider {
    public static final boolean DEBUG = true;

	public Object[] getElements(Object inputElement) {
		if (DEBUG) System.out.printf("UnknownsViewContentProvider: getElements\n");	
		
		return ((Route)inputElement).getUnknowns().toArray();
	}

	public void dispose() {
		if (DEBUG) System.out.printf("UnknownsViewContentProvider: dispose\n");	
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (DEBUG) System.out.printf("UnknownsViewContentProvider: inputChanged\n");	
	}
}
