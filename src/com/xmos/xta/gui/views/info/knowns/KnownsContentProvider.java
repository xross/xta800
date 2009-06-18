package com.xmos.xta.gui.views.info.knowns;

import java.util.HashSet;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.engine.analysis.routes.Route;

public class KnownsContentProvider implements IStructuredContentProvider {
    public static final boolean DEBUG = false;

	public Object[] getElements(Object inputElement) {
		if (DEBUG) System.out.printf("UnknownsViewContentProvider: getElements\n");	
// TODO		
//		HashSet<Unknown> knowns = ((Route)inputElement).getUnknowns().getKnowns();
//		return knowns.toArray();
return null;
	}

	public void dispose() {
		if (DEBUG) System.out.printf("UnknownsViewContentProvider: dispose\n");	
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (DEBUG) System.out.printf("UnknownsViewContentProvider: inputChanged\n");	
	}
}
