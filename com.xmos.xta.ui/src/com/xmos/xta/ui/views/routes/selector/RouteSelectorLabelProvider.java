package com.xmos.xta.ui.views.routes.selector;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming.Level;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Unknown.State;
import com.xmos.xta.ui.icons.Icons;

public class RouteSelectorLabelProvider extends CellLabelProvider {
    private static final boolean DEBUG = false;
	
    public RouteSelectorLabelProvider() {
		if (DEBUG) System.out.printf("RouteSelectorLabelProvider()\n");	
	}

	public void update(ViewerCell cell) {
		if (DEBUG) System.out.printf("RouteSelectorLabelProvider: update\n");	
		
		RouteNode routeNode = (RouteNode)cell.getElement();
		Route route = routeNode.getRoute();
		cell.setText(route.getRouteDescription().getDescription());
		RouteTiming routeTiming = route.getRouteTiming();
		String iconName = "";
		if (routeTiming.isTimingSpecified()) {
			if (routeTiming.passedTiming()) {
				iconName = "required_pass";
			} else {
				iconName = "required_fail";
			}
		} else {
			iconName = "required_notset";
		}
		
		if (route.getRouteUnknowns().getUnknowns(State.UNKNOWN).size() > 0) {
			iconName += ".unknown";
		}
		cell.setImage(Icons.getIcon(iconName));
	}

	public String getToolTipText(Object element) {
		if (DEBUG) System.out.printf("RouteSelectorLabelProvider: getToolTipText\n");	

		RouteNode routeNode = (RouteNode)element;		
		return routeNode.getRoute().getRouteTiming().getDescription(Level.COMPLETE);
	}

    public boolean useNativeToolTip(Object object) {
        if (DEBUG) System.out.printf("RouteSelectorLabelProvider: useNativeToolTips\n");
        return true;
    }
}
