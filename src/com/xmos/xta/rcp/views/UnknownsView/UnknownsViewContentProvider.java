package com.xmos.xta.rcp.views.UnknownsView;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.XtaInstruction;

public class UnknownsViewContentProvider implements IStructuredContentProvider {
    public static final boolean DEBUG = false;

	public Object[] getElements(Object inputElement) {
		if (DEBUG) System.out.printf("UnknownsViewContentProvider: getElements\n");	
		
		ArrayList<XtaInstruction> show = new ArrayList<XtaInstruction>();
		
		for (XtaInstruction instruction : ((Route)inputElement).getUnknowns()){
			show.add(instruction);
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
