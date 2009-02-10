package com.xmos.xta.rcp.views;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.Unknown;

public class UnknownsViewContentProvider implements IStructuredContentProvider {
    public static final boolean DEBUG = false;

	public Object[] getElements(Object inputElement) {
		if (DEBUG) System.out.printf("UnknownsViewContentProvider: getElements\n");	
		
		ArrayList<Unknown> show = new ArrayList<Unknown>();
		
		for (Unknown unknown : ((Route)inputElement).getUnknowns()){
			if (unknown.isShown() && !unknown.isSet()) {
				show.add(unknown);
			}
		}
		return show.toArray();
	}

	public void dispose() {
		if (DEBUG) System.out.printf("UnknownsViewContentProvider: dispose\n");	
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (DEBUG) System.out.printf("UnknownsViewContentProvider: inputChanged\n");	
	}
}
