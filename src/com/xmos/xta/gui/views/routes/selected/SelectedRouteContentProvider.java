package com.xmos.xta.gui.views.routes.selected;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class SelectedRouteContentProvider implements ITreeContentProvider  {
    private static final boolean DEBUG = false;

	public SelectedRouteContentProvider() {
		if (DEBUG) System.out.printf("SelectedRouteContentProvider()\n");
	}

	public Object[] getChildren(Object parentElement) {
		if (DEBUG) System.out.printf("SelectedRouteContentProvider: getChildren: %s\n",
				parentElement.toString());

		Object[] res = ((Node)parentElement).getChildren().toArray();
		return res;
	}

	public Object getParent(Object element) {
		if (DEBUG) System.out.printf("SelectedRouteContentProvider: getParent: %s\n", 
				element.toString());	

		return ((Node)element).getParent();
	}

	public boolean hasChildren(Object element) {
		if (DEBUG) System.out.printf("SelectedRouteContentProvider: hasChildren: %s\n",
				element.toString());	

		return ((Node)element).getChildren().size() > 0;
	}

	public Object[] getElements(Object inputElement) {
		if (DEBUG) System.out.printf("SelectedRouteContentProvider: getElements: %s\n",
				inputElement.toString());	

        return ((Node)inputElement).getChildren().toArray();
	}

	public void dispose() {
		if (DEBUG) System.out.printf("SelectedRouteContentProvider: dispose\n");	
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (DEBUG) System.out.printf("SelectedRouteContentProvider: inputChanged\n");	
	}
}
