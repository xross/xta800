package com.xmos.xta.gui.views.routes.selector;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class RouteSelectorContentProvider implements ITreeContentProvider  {
    private static final boolean DEBUG = true;

	public RouteSelectorContentProvider() {
		if (DEBUG) System.out.printf("RouteSelectorContentProvider()\n");
	}

	public Object[] getChildren(Object parentElement) {
		if (DEBUG) System.out.printf("RouteSelectorContentProvider: getChildren: %s\n",
				parentElement.toString());

		Object[] res = ((RouteNode)parentElement).getChildren().toArray();
		return res;
	}

	public Object getParent(Object element) {
		if (DEBUG) System.out.printf("RouteSelectorContentProvider: getParent: %s\n", 
				element.toString());	

		return ((RouteNode)element).getParent();
	}

	public boolean hasChildren(Object element) {
		if (DEBUG) System.out.printf("RouteSelectorContentProvider: hasChildren: %s\n",
				element.toString());	

		return ((RouteNode)element).getChildren().size() > 0;
	}

	public Object[] getElements(Object inputElement) {
		if (DEBUG) System.out.printf("RouteSelectorContentProvider: getElements: %s\n",
				inputElement.toString());	

        return ((RouteNode)inputElement).getChildren().toArray();
	}

	public void dispose() {
		if (DEBUG) System.out.printf("RouteSelectorContentProvider: dispose\n");	
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (DEBUG) System.out.printf("RouteSelectorContentProvider: inputChanged\n");	
	}
}
