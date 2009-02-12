package com.xmos.xta.rcp.views.TreeView;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import com.xmos.xta.rcp.icons.Icons;
import com.xmos.xta.rcp.nodes.Node;

class TreeViewLabelProvider extends CellLabelProvider {
    private static final boolean DEBUG = false;
	
	TreeViewLabelProvider() {
		if (DEBUG) System.out.printf("TreeViewLabelProvider()\n");	
	}

	public void update(ViewerCell cell) {
		if (DEBUG) System.out.printf("TreeViewLabelProvider: update\n");	
		
		Node node = (Node)cell.getElement();		
		cell.setText(node.getDescription());
		
		String iconName = node.getTypeName();
		if (node.getNumUnknowns() > 0) {
			iconName += ".unknown";
		}
		cell.setImage(Icons.getIcon(iconName));
	}

	public String getToolTipText(Object element) {
		if (DEBUG) System.out.printf("TreeViewLabelProvider: getToolTipText\n");	

		Node node = (Node)element;		
		return node.getToolTipText();
	}		
}
