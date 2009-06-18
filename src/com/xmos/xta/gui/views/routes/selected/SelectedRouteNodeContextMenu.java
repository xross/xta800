package com.xmos.xta.gui.views.routes.selected;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Shell;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.dialogs.Iterations;
import com.xmos.xta.gui.dialogs.Scope;

public class SelectedRouteNodeContextMenu {
    public static void createNodeContextMenu(final Shell parent, IMenuManager manager, final Node node) {
    	// Adds the 'Trace in console' option.
		manager.add(new Separator());
    	manager.add(new Action("Trace in console") {
			public void run() {
				// TODO
				//ConsoleView console = (ConsoleView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				//	getActivePage().findView(ConsoleView.ID);
				//
				//try {
				//	PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				//	    getActivePage().showView(ConsoleView.ID);
				//} catch (PartInitException e) {}
				
				//int routeId = getRouteId();
				//console.runCommand("print trace " + routeId + " b" + m_this.getId());
			}
		});  
 /* TODO   	
    	// Adds the 'Set iterations' option.
	    manager.add(new Separator());
	    manager.add(new Action("Set iterations") {
		    public void run() {
		    	new Iterations(parent, node);
		    }
	    });    	

    	// Adds the 'Set scope' option.
	    manager.add(new Action("Set scope") {
		    public void run() {
		    	new Scope(parent, node);
		    }
	    });
*/    
    }
}
