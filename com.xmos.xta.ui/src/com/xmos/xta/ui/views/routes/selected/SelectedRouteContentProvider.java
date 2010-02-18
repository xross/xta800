package com.xmos.xta.ui.views.routes.selected;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
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
			return children(parentElement);
		}
		
		return EMPTY_ARRAY;
	}

	public Object getParent(Object element) {
		if (DEBUG) System.out.printf("SelectedRouteContentProvider: getParent: %s\n", 
				element.toString());	

		if (element instanceof Node){
			
			if(element instanceof FunctionNode){
				return ((Node)element).getParent().getParent();
			}
			
			return ((Node)element).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (DEBUG) System.out.printf("SelectedRouteContentProvider: hasChildren: %s\n",
				element.toString());
		
		if (element instanceof Node){
			return children(element).length > 0;
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
	
	private Object [] children(Object parentElement){
		List<Node> childrenWithoutFrn = new LinkedList<Node>();
		
		for(Node child : ((Node)parentElement).iChildren()){
			if(child instanceof FunctionReferenceNode){
				childrenWithoutFrn.addAll(child.iChildren());
			} else {
				childrenWithoutFrn.add(child);
			}
		}
		return childrenWithoutFrn.toArray();
	}
}
