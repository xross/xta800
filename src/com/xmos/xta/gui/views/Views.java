package com.xmos.xta.gui.views;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.Broadcaster;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.dialogs.Loop;
import com.xmos.xta.gui.dialogs.LoopPath;
import com.xmos.xta.gui.dialogs.LoopScope;
import com.xmos.xta.gui.views.console.ConsoleView;

public class Views {
	public static void showView(String viewID) {
	    IWorkbench workbench = PlatformUI.getWorkbench();
	    IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
	    try {
		    workbenchWindow.getActivePage().showView(viewID);
	    } catch (PartInitException e) {
		    e.printStackTrace();
	    }
	}
	
	public static void createContextMenu(IMenuManager manager, List<Node> selectedNodes, final Shell shell) {
		if (selectedNodes.size() != 1) {
			return;
		}

		final Node node = selectedNodes.get(0);
		manager.add(new Separator());

    	// Adds the 'Trace in console' option.
    	manager.add(new Action("Trace in console") {
			public void run() {
				ConsoleView console = (ConsoleView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().findView(ConsoleView.ID);
				
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					    getActivePage().showView(ConsoleView.ID);
				} catch (PartInitException e) {}
				
				int routeId = GuiEngine.getEngine().getSelectedRouteId();
				console.runCommand("print trace " + routeId + " " + node.getId());
			}
		});

	    manager.add(new Separator());
    	
    	// Adds the 'Set iterations' option.
	    Action setLoopIterationsAction = new Action("Set loop iterations") {
		    public void run() {
		    	new Loop(shell, node);
		    }
	    };
	    manager.add(setLoopIterationsAction);
	    if (GuiEngine.getEngine().canSetLoopIterations(node))
	    	setLoopIterationsAction.setEnabled(true);
	    else
	    	setLoopIterationsAction.setEnabled(false);

    	// Adds the 'Set iterations' option.
	    Action setLoopPathIterationsAction = new Action("Set loop path iterations") {
		    public void run() {
		    	new LoopPath(shell, node);
		    }
	    };
	    manager.add(setLoopPathIterationsAction);
	    if (GuiEngine.getEngine().canSetLoopPathIterations(node))
	    	setLoopPathIterationsAction.setEnabled(true);
	    else
	    	setLoopPathIterationsAction.setEnabled(false);
	    
    	// Adds the 'Set scope' option.
	    Action setLoopScopeAction = new Action("Set loop scope") {
		    public void run() {
		    	new LoopScope(shell, node);
		    }
	    };
	    manager.add(setLoopScopeAction);
	    if (GuiEngine.getEngine().canSetLoopScope(node))
	    	setLoopScopeAction.setEnabled(true);
	    else
	    	setLoopScopeAction.setEnabled(false);

	    manager.add(new Separator());

    	// Adds folding option.
	    Action foldAction = new Action("Fold") {
		    public void run() {
		    	node.setFolded(true, false);
				GuiEngine.broadcast(Broadcaster.BROADCAST_UPDATE_FOLDING, "");
		    }
	    };
	    manager.add(foldAction);
	    if (!node.isFolded() && node.isFunctionNode())
	    	foldAction.setEnabled(true);
	    else
	    	foldAction.setEnabled(false);

	    Action unfoldAction = new Action("Unfold") {
		    public void run() {
		    	node.setFolded(false, false);
				GuiEngine.broadcast(Broadcaster.BROADCAST_UPDATE_FOLDING, "");
		    }
	    };
	    manager.add(unfoldAction);
	    if (node.isFolded() && node.isFunctionNode())
	    	unfoldAction.setEnabled(true);
	    else
	    	unfoldAction.setEnabled(false);

	    Action foldChildrenAction = new Action("Fold children") {
		    public void run() {
		    	node.setFolded(true, true);
				GuiEngine.broadcast(Broadcaster.BROADCAST_UPDATE_FOLDING, "");
		    }
	    };
	    manager.add(foldChildrenAction);
	    if (node.isFunctionNode())
	    	foldChildrenAction.setEnabled(true);
	    else
	    	foldChildrenAction.setEnabled(false);

	    Action unfoldChildrenAction = new Action("Unfold children") {
		    public void run() {
		    	node.setFolded(false, true);
				GuiEngine.broadcast(Broadcaster.BROADCAST_UPDATE_FOLDING, "");
		    }
	    };
	    manager.add(unfoldChildrenAction);
	    if (node.isFunctionNode())
	    	unfoldChildrenAction.setEnabled(true);
	    else
	    	unfoldChildrenAction.setEnabled(false);
	}
}
