package com.xmos.xtagui.views;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import com.xmos.xtagui.icons.Icons;
import com.xmos.xtagui.nodes.Node;

class TreeViewLabelProvider extends CellLabelProvider {
    private static final boolean DEBUG = false;
	
	TreeViewLabelProvider() {
		if (DEBUG) System.out.printf("TreeViewLabelProvider()\n");	
	}

	public void update(ViewerCell cell) {
		if (DEBUG) System.out.printf("TreeViewLabelProvider: update\n");	
		
		Node node = (Node)cell.getElement();		
		cell.setText(node.getDescription());
		cell.setImage(Icons.getIcon(node.getTypeName()));
	}

	public String getToolTipText(Object element) {
		if (DEBUG) System.out.printf("TreeViewLabelProvider: getToolTipText\n");	

		Node node = (Node)element;		
		return node.getToolTipText();
	}		
}
