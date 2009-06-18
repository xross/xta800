package com.xmos.xta.gui.views.routes.selector;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import com.xmos.xta.gui.icons.Icons;

public class RouteSelectorLabelProvider extends CellLabelProvider {
    private static final boolean DEBUG = false;
	
    public RouteSelectorLabelProvider() {
		if (DEBUG) System.out.printf("RouteSelectorLabelProvider()\n");	
	}

	public void update(ViewerCell cell) {
		if (DEBUG) System.out.printf("RouteSelectorLabelProvider: update\n");	
		
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
