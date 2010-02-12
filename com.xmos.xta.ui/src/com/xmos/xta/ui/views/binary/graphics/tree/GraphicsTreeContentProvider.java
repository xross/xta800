package com.xmos.xta.ui.views.binary.graphics.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsObject;

public class GraphicsTreeContentProvider implements ITreeContentProvider  {
	private static Object[] EMPTY_ARRAY = new Object[]{};

	public GraphicsTreeContentProvider() {
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof GraphicsObject){
			return ((GraphicsObject)parentElement).getChildren().toArray();
		}
		
		return EMPTY_ARRAY;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof GraphicsObject){
			return ((GraphicsObject)element).getChildren().size() > 0;
		}
		
		return false;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
