package com.xmos.xta.gui.views.routes;

import java.util.Arrays;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.gui.nodes.Node;

class RoutesViewContentProvider implements ITreeContentProvider  {
    private static final boolean DEBUG = false;

	public RoutesViewContentProvider() {
	}

	public Object[] getChildren(Object parentElement) {
		if (DEBUG) System.out.printf("RoutesViewContentProvider: getChildren: %s\n",
				parentElement.toString());

		Object[] res = ((Node)parentElement).getChildren().toArray();
		if (((Node)parentElement).isRouteNode()) {
			Arrays.sort(res);
		}
		return res;
	}

	public Object getParent(Object element) {
		if (DEBUG) System.out.printf("RoutesViewContentProvider: getParent: %s\n", 
				element.toString());	

		return ((Node)element).getParentNode();
	}

	public boolean hasChildren(Object element) {
		if (DEBUG) System.out.printf("RoutesViewContentProvider: hasChildren: %s\n",
				element.toString());	

		return ((Node)element).getChildren().size() > 0;
	}

	public Object[] getElements(Object inputElement) {
		if (DEBUG) System.out.printf("RoutesViewContentProvider: getElements: %s\n",
				inputElement.toString());	

        return ((Node)inputElement).getChildren().toArray();
	}

	public void dispose() {
		if (DEBUG) System.out.printf("RoutesViewContentProvider: dispose\n");	
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (DEBUG) System.out.printf("RoutesViewContentProvider: inputChanged\n");	
	}
}
