package com.xmos.xta.gui.views.info.unknowns;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;

public class UnknownsContentProvider implements IStructuredContentProvider {
    public static final boolean DEBUG = false;

	public Object[] getElements(Object inputElement) {
		if (DEBUG) System.out.printf("UnknownsContentProvider: getElements\n");	

	    Collection<Node> nodes = ((Route)inputElement).getUnknowns(UnknownState.UNKNOWN);
		return nodes.toArray();
	}

	public void dispose() {
		if (DEBUG) System.out.printf("UnknownsContentProvider: dispose\n");	
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (DEBUG) System.out.printf("UnknownsContentProvider: inputChanged\n");	
	}
}
