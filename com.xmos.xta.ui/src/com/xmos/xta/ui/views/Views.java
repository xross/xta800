package com.xmos.xta.ui.views;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.dialogs.InstructionTime;
import com.xmos.xta.ui.dialogs.Loop;
import com.xmos.xta.ui.dialogs.LoopPath;
import com.xmos.xta.ui.dialogs.LoopScope;

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
	
	public static void createContextMenu(IMenuManager manager, List<Node> selectedNodes, Shell shell) {
		if (selectedNodes.size() < 1)
			return;

    	manager.add(new Separator());		
    	addTraceInConsole(manager, selectedNodes);    	    	
    	addShowNodeInConsole(manager, selectedNodes);

	    manager.add(new Separator());   	
    	addSetLoopIterations(manager, selectedNodes, shell);    	    	
    	addSetLoopPathIterations(manager, selectedNodes, shell);    	    	
    	addSetLoopScope(manager, selectedNodes, shell);    	    	
    	addSetInstructionTime(manager, selectedNodes, shell);    	    	
    	addExclusion(manager, selectedNodes);    	    	

	    manager.add(new Separator());
    	addFold(manager, selectedNodes);    	    	
    	addUnfold(manager, selectedNodes);    	    	
    	addFoldChildren(manager, selectedNodes);    	    	
    	addUnfoldChildren(manager, selectedNodes);
	}
	
	private static void addTraceInConsole(IMenuManager manager, final List<Node> selectedNodes) {
		Action action = new Action("Trace in console") {
			public void run() {
				GuiEngine.runCommandInConsole("print trace " + 
						getRouteId() + " " + getFirstNode(selectedNodes).getId());			    
			}
		};
		manager.add(action);
		action.setEnabled(oneNodeSelected(selectedNodes) ? true : false);		
	}
	
	private static void addShowNodeInConsole(IMenuManager manager, final List<Node> selectedNodes) {
		Action action = new Action("Show node info in console") {
			public void run() {
				GuiEngine.runCommandInConsole("print node_info " + 
						getRouteId() + " " + getFirstNode(selectedNodes).getId());			    
			}
		};
		manager.add(action);    	
		action.setEnabled(oneNodeSelected(selectedNodes) ? true : false);		
	}
	
	private static void addSetLoopIterations(IMenuManager manager, final List<Node> selectedNodes, final Shell shell) {
		final Node firstNode = getFirstNode(selectedNodes);
		
	    Action action = new Action("Set loop iterations") {
		    public void run() {
		    	new Loop(shell, firstNode);
		    }
	    };
	    manager.add(action);
	    if (oneNodeSelected(selectedNodes) && GuiEngine.getEngine().canSetLoop(firstNode))
	    	action.setEnabled(true);
	    else
	    	action.setEnabled(false);
	}
	
	private static void addSetLoopPathIterations(IMenuManager manager, final List<Node> selectedNodes, final Shell shell) {
		final Node firstNode = getFirstNode(selectedNodes);

		Action action = new Action("Set loop path iterations") {
		    public void run() {
		    	new LoopPath(shell, firstNode);
		    }
	    };
	    manager.add(action);
	    if (oneNodeSelected(selectedNodes) && GuiEngine.getEngine().canSetLoopPath(firstNode))
	    	action.setEnabled(true);
	    else
	    	action.setEnabled(false);		
	}

	private static void addSetLoopScope(IMenuManager manager, final List<Node> selectedNodes, final Shell shell) {
		final Node firstNode = getFirstNode(selectedNodes);

		Action action = new Action("Set loop scope") {
		    public void run() {
		    	new LoopScope(shell, firstNode);
		    }
	    };
	    manager.add(action);
	    if (oneNodeSelected(selectedNodes) && GuiEngine.getEngine().canSetLoopScope(firstNode))
	    	action.setEnabled(true);
	    else
	    	action.setEnabled(false);		
	}
    
	private static void addSetInstructionTime(IMenuManager manager, final List<Node> selectedNodes, final Shell shell) {
		Action action = new Action("Set instruction time") {
		    public void run() {
		    	new InstructionTime(shell, selectedNodes);
		    }
	    };
	    manager.add(action);
	    action.setEnabled(true);
	    for (Node node : selectedNodes) {
	    	// All nodes have to be instructions which can pause
	    	if (node.isInstructionNode()) {
				XtaInstruction instruction = ((InstructionNode)node).getXtaInstruction();				
	    		if (!instruction.canPause()) {
	    			action.setEnabled(false);
	    		}				
	    	} else {
	    		action.setEnabled(false);
	    	}
	    }		
	}

	private static void addExclusion(IMenuManager manager, final List<Node> selectedNodes) {
		final List<XtaInstruction> instructions = getFirstNode(selectedNodes).getInstructions();
	    Action action = new Action("Add to exclusion list") {
		    public void run() {
		    	XtaInstruction instruction = instructions.get(0);
		    	GuiEngine.getEngine().addExclusion(GuiEngine.s_guiCommandSrc, "0x" + Long.toHexString(instruction.getPc())); 
		    }
	    };
	    manager.add(action);
	    if (instructions.size() > 0)
	    	action.setEnabled(true);
	    else
	    	action.setEnabled(false);		
	}

	private static void addFold(IMenuManager manager, final List<Node> selectedNodes) {
		final Node firstNode = getFirstNode(selectedNodes);

		Action action = new Action("Fold") {
		    public void run() {
		    	firstNode.setFolded(true, false);
				GuiEngine.broadcast(Broadcaster.BROADCAST_UPDATE_FOLDING, "");
		    }
	    };
	    manager.add(action);
	    if (oneNodeSelected(selectedNodes) && !firstNode.isFolded() && firstNode.isFunctionNode())
	    	action.setEnabled(true);
	    else
	    	action.setEnabled(false);		
	}

	private static void addUnfold(IMenuManager manager, final List<Node> selectedNodes) {
		final Node firstNode = getFirstNode(selectedNodes);

		Action action = new Action("Unfold") {
		    public void run() {
		    	firstNode.setFolded(false, false);
				GuiEngine.broadcast(Broadcaster.BROADCAST_UPDATE_FOLDING, "");
		    }
	    };
	    manager.add(action);
	    if (oneNodeSelected(selectedNodes) && firstNode.isFolded() && firstNode.isFunctionNode())
	    	action.setEnabled(true);
	    else
	    	action.setEnabled(false);		
	}

	private static void addFoldChildren(IMenuManager manager, final List<Node> selectedNodes) {
		final Node firstNode = getFirstNode(selectedNodes);

		Action action = new Action("Fold children") {
		    public void run() {
		    	firstNode.setFolded(true, true);
				GuiEngine.broadcast(Broadcaster.BROADCAST_UPDATE_FOLDING, "");
		    }
	    };
	    manager.add(action);
	    if (oneNodeSelected(selectedNodes) && firstNode.isFunctionNode())
	    	action.setEnabled(true);
	    else
	    	action.setEnabled(false);		
	}

	private static void addUnfoldChildren(IMenuManager manager, final List<Node> selectedNodes) {
		final Node firstNode = getFirstNode(selectedNodes);

		Action action = new Action("Unfold children") {
		    public void run() {
		    	firstNode.setFolded(false, true);
				GuiEngine.broadcast(Broadcaster.BROADCAST_UPDATE_FOLDING, "");
		    }
	    };
	    manager.add(action);
	    if (oneNodeSelected(selectedNodes) && firstNode.isFunctionNode())
	    	action.setEnabled(true);
	    else
	    	action.setEnabled(false);		
	}
	
	private static int getRouteId() {
		return GuiEngine.getEngine().getSelectedRouteId();
	}
	
	private static Node getFirstNode(List<Node> selectedNodes) {
		return selectedNodes.get(0);
	}
	
	private static boolean oneNodeSelected(List<Node> selectedNodes) {
		return selectedNodes.size() == 1;
	}
}
