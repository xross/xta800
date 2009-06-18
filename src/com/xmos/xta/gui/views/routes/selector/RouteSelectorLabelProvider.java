package com.xmos.xta.gui.views.routes.selector;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import com.xmos.xta.gui.icons.Icons;

public class RouteSelectorLabelProvider extends CellLabelProvider {
    private static final boolean DEBUG = true;
	
    public RouteSelectorLabelProvider() {
		if (DEBUG) System.out.printf("RouteSelectorLabelProvider()\n");	
	}

	public void update(ViewerCell cell) {
		if (DEBUG) System.out.printf("RouteSelectorLabelProvider: update\n");	
/* TODO
		RouteNode node = (Node)cell.getElement();
		
		String nodeText = node.getTypeName();
		if (node.getFunctionName() != "")
			nodeText += " " + node.getFunctionName();
		nodeText += " " + node.getAttributes().getTimingSummary();
		
		cell.setText(nodeText);
		
		String iconName = node.getTypeName() + "_node";
		if (node.getAttributes().getDisplayUnknown()) {
			iconName += ".unknown";
		}
*/		
		RouteNode routeNode = (RouteNode)cell.getElement();
		cell.setText(routeNode.getRoute().getDescription());
		cell.setImage(Icons.getIcon("route"));
	}

	public String getToolTipText(Object element) {
		if (DEBUG) System.out.printf("RouteSelectorLabelProvider: getToolTipText\n");	

		RouteNode routeNode = (RouteNode)element;		
		return routeNode.getRoute().getRouteTiming().getDescription(true);
	}
}
