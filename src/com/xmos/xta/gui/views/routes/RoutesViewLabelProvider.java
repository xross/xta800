package com.xmos.xta.gui.views.routes;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import com.xmos.xta.gui.icons.Icons;
import com.xmos.xta.gui.nodes.Node;

class RoutesViewLabelProvider extends CellLabelProvider {
    private static final boolean DEBUG = false;
	
	RoutesViewLabelProvider() {
		if (DEBUG) System.out.printf("RoutesViewLabelProvider()\n");	
	}

	public void update(ViewerCell cell) {
		if (DEBUG) System.out.printf("RoutesViewLabelProvider: update\n");	
		
		Node node = (Node)cell.getElement();		
		cell.setText(node.getDescription());
		
		String iconName = node.getTypeName();
		if (node.getNumUnknowns() > 0) {
			iconName += ".unknown";
		}
		cell.setImage(Icons.getIcon(iconName));
	}

	public String getToolTipText(Object element) {
		if (DEBUG) System.out.printf("RoutesViewLabelProvider: getToolTipText\n");	

		Node node = (Node)element;		
		return node.getToolTipText();
	}
}
