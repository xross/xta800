package com.xmos.xta.gui.views.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class TreeViewContentProvider implements ITreeContentProvider  {
    private static final boolean DEBUG = true;

	public TreeViewContentProvider() {
		if (DEBUG) System.out.printf("TreeViewContentProvider()\n");
	}

	public Object[] getChildren(Object parentElement) {
		if (DEBUG) System.out.printf("TreeViewContentProvider: getChildren: %s\n",
				parentElement.toString());

		Object[] res = ((Node)parentElement).getChildren().toArray();
		return res;
	}

	public Object getParent(Object element) {
		if (DEBUG) System.out.printf("TreeViewContentProvider: getParent: %s\n", 
				element.toString());	

		return ((Node)element).getParent();
	}

	public boolean hasChildren(Object element) {
		if (DEBUG) System.out.printf("TreeViewContentProvider: hasChildren: %s\n",
				element.toString());	

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
}
