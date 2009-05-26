package com.xmos.xta.gui.views.info;

import java.util.HashSet;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.unknowns.Unknown;

public class UnknownsContentProvider implements IStructuredContentProvider {
    public static final boolean DEBUG = false;

	public Object[] getElements(Object inputElement) {
		if (DEBUG) System.out.printf("UnknownsContentProvider: getElements\n");	
		
		HashSet<Unknown> unknowns = ((Route)inputElement).getUnknowns().getUnknowns();
		return unknowns.toArray();
	}

	public void dispose() {
		if (DEBUG) System.out.printf("UnknownsContentProvider: dispose\n");	
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (DEBUG) System.out.printf("UnknownsContentProvider: inputChanged\n");	
	}
}
