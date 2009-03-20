package com.xmos.xta.rcp.views.InfoView;

import java.util.HashSet;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.XtaInstruction;

public class UnknownsContentProvider implements IStructuredContentProvider {
    public static final boolean DEBUG = false;

	public Object[] getElements(Object inputElement) {
		if (DEBUG) System.out.printf("UnknownsContentProvider: getElements\n");	
		
		HashSet<XtaInstruction> unknownsSet = new HashSet<XtaInstruction>();
		for (XtaInstruction instruction : ((Route)inputElement).getUnknowns()){
			unknownsSet.add(instruction);
		}
		return unknownsSet.toArray();
	}

	public void dispose() {
		if (DEBUG) System.out.printf("UnknownsContentProvider: dispose\n");	
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (DEBUG) System.out.printf("UnknownsContentProvider: inputChanged\n");	
	}
}
