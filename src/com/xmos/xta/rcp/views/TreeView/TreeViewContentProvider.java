package com.xmos.xta.rcp.views.TreeView;

import java.util.Arrays;

import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.rcp.nodes.Node;

class TreeViewContentProvider implements ILazyTreeContentProvider  {
    private static final boolean DEBUG = false;

    private TreeViewer m_treeViewer;
    
	public TreeViewContentProvider(TreeViewer viewer) {
    	m_treeViewer = viewer;
	}

	public Object[] getChildren(Object parentElement) {
		if (DEBUG) System.out.printf("TreeViewContentProvider: getChildren: %s\n",
				parentElement.toString());

		Object[] res = ((Node)parentElement).getChildren().toArray();
		if (((Node)parentElement).isRouteNode()) {
			Arrays.sort(res);
		}
		return res;
	}

	public Object getParent(Object element) {
		if (DEBUG) System.out.printf("TreeViewContentProvider: getParent: %s\n", 
				element.toString());	

		return ((Node)element).getParentNode();
	}

	public boolean hasChildren(Object element) {
		if (DEBUG) System.out.printf("TreeViewContentProvider: hasChildren: %s\n",
				element.toString());	

		//return getChildren(element).length > 0;
		return ((Node)element).getChildren().size() > 0;
	}

	public Object[] getElements(Object inputElement) {
		if (DEBUG) System.out.printf("TreeViewContentProvider: getElements: %s\n",
				inputElement.toString());	

        return ((Node)inputElement).getChildren().toArray();
	}

	public void dispose() {
		if (DEBUG) System.out.printf("TreeViewContentProvider: dispose\n");	
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (DEBUG) System.out.printf("TreeViewContentProvider: inputChanged\n");	
	}

	public void updateChildCount(Object element, int currentChildCount) {
		if (DEBUG) System.out.printf("TreeViewContentProvider: updateChildCount\n");	
		int count = getChildren((Node)element).length;
		if(count != currentChildCount){
			m_treeViewer.setChildCount(element, count);
		}
	}

	public void updateElement(Object parent, int index) {
		if (DEBUG) System.out.printf("TreeViewContentProvider: updateElement\n");	
		Node child = ((Node)parent).getChildren().get(index);
		if (child != null) {
			m_treeViewer.replace(parent, index, child);
			updateChildCount(child, 0);
		}
	}	
}
