package com.xmos.xta.ui.views.routes.selected;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class SelectedRouteContentProvider implements ITreeContentProvider  {
	private static Object[] EMPTY_ARRAY = new Object[]{};
    private static final boolean DEBUG = false;

	public SelectedRouteContentProvider() {
		if (DEBUG) System.out.printf("SelectedRouteContentProvider()\n");
	}

	public Object[] getChildren(Object parentElement) {
		if (DEBUG) System.out.printf("SelectedRouteContentProvider: getChildren: %s\n",
				parentElement.toString());

		if (parentElement instanceof Node){
			return ((Node)parentElement).iChildren().toArray();
		}
		
		return EMPTY_ARRAY;
	}

	public Object getParent(Object element) {
		if (DEBUG) System.out.printf("SelectedRouteContentProvider: getParent: %s\n", 
				element.toString());	

		if (element instanceof Node){
			return ((Node)element).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (DEBUG) System.out.printf("SelectedRouteContentProvider: hasChildren: %s\n",
				element.toString());
		
		if (element instanceof Node){
			return ((Node)element).iChildren().size() > 0;
		}
		
		return false;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
		if (DEBUG) System.out.printf("SelectedRouteContentProvider: dispose\n");	
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (DEBUG) System.out.printf("SelectedRouteContentProvider: inputChanged\n");	
	}
}
