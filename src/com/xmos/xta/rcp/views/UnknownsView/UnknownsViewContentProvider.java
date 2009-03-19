package com.xmos.xta.rcp.views.UnknownsView;

import java.util.HashSet;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.XtaInstruction;

public class UnknownsViewContentProvider implements IStructuredContentProvider {
    public static final boolean DEBUG = false;

	public Object[] getElements(Object inputElement) {
		if (DEBUG) System.out.printf("UnknownsViewContentProvider: getElements\n");	
		
		HashSet<XtaInstruction> unknownsSet = new HashSet<XtaInstruction>();
		for (XtaInstruction instruction : ((Route)inputElement).getUnknowns()){
			unknownsSet.add(instruction);
		}
		return unknownsSet.toArray();
	}

	public void dispose() {
		if (DEBUG) System.out.printf("UnknownsViewContentProvider: dispose\n");	
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (DEBUG) System.out.printf("UnknownsViewContentProvider: inputChanged\n");	
	}
}
