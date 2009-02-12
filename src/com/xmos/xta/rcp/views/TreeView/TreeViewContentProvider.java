package com.xmos.xta.rcp.views.TreeView;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.rcp.nodes.Node;

class TreeViewContentProvider implements ITreeContentProvider {
    private static final boolean DEBUG = false;

	public Object[] getChildren(Object parentElement) {
		if (DEBUG) System.out.printf("TreeViewContentProvider: getChildren: %s\n",
				parentElement.toString());

		return ((Node)parentElement).getNodes().toArray();
	}

	public Object getParent(Object element) {
		if (DEBUG) System.out.printf("TreeViewContentProvider: getParent: %s\n", 
				element.toString());	

		return ((Node)element).getParentNode();
	}

	public boolean hasChildren(Object element) {
		if (DEBUG) System.out.printf("TreeViewContentProvider: hasChildren: %s\n",
				element.toString());	

		return getChildren(element).length > 0;
	}

	public Object[] getElements(Object inputElement) {
		if (DEBUG) System.out.printf("TreeViewContentProvider: getElements: %s\n",
				inputElement.toString());	

        return ((Node)inputElement).getNodes().toArray();
	}

	public void dispose() {
		if (DEBUG) System.out.printf("TreeViewContentProvider: dispose\n");	
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (DEBUG) System.out.printf("TreeViewContentProvider: inputChanged\n");	
	}	
}
