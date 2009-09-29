package com.xmos.xta.ui.views.info.unknowns;

import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPartSite;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.dialogs.InstructionTime;
import com.xmos.xta.ui.dialogs.Loop;

public class UnknownsViewer extends InfoViewer {
	private Action m_setInstructionTimeAction;
	private Action m_setLoopIterationsAction;

	public UnknownsViewer(Display display, Composite parent, IWorkbenchPartSite site, TableViewer tableViewer) {
		super(display, parent, site, tableViewer);
	}

	public void update() {
		getViewer().setInput(GuiEngine.getEngine().getSelectedRoute());
		updateHighlighting(GuiEngine.getSelectedNodes());
	}
	
	public void updateHighlighting(List<Node> selectedNodes) {
		final Set<Node> allInstructionNodes = new TreeSet<Node>();
   		for (Node node : selectedNodes) {
       		node.traverse(new Stack<Node>(), new DefaultNodeTraverser() {
       		    public boolean foundInstructionNode(Node node, Stack<Node> ancestors) {
       		    	allInstructionNodes.add(node);
       		    	return true;
       		    }
       		});    		
    	}

		for (TableItem item : getViewer().getTable().getItems()) {
			Node node = (Node)item.getData();					
			if (allInstructionNodes.contains(node)) {
				item.setBackground(getDisplay().getSystemColor(SWT.COLOR_YELLOW));
			} else {
				item.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			}
		}
	}
	
	InfoViewerContentProvider createContentProvider() {
		return new InfoViewerContentProvider(UnknownState.UNKNOWN);
	}
	
	InfoViewerLabelProvider createLabelProvider() {
		return new InfoViewerLabelProvider();
	}
	
	void enableContextMenuActions(int numInstructionTimeNodes, int numLoopIterationsNodes) {
		m_setLoopIterationsAction.setEnabled(false);
		m_setInstructionTimeAction.setEnabled(false);
		
		if ((numInstructionTimeNodes > 0) && (numLoopIterationsNodes == 0)) {
			m_setInstructionTimeAction.setEnabled(true);					
		} else if ((numLoopIterationsNodes > 0) && (numInstructionTimeNodes == 0)) {
			m_setLoopIterationsAction.setEnabled(true);
		}		
	}	
	
 	void createContextMenuActions(MenuManager menuMgr) {
		// Create the 'Set Instruction Time' action
		m_setInstructionTimeAction = new Action() { 
			public void run() {
     			new InstructionTime(getParent().getShell(), getSelectedNodes());
			}
		}; 
		m_setInstructionTimeAction.setText("Set instruction time");
		menuMgr.add(m_setInstructionTimeAction);
		m_setInstructionTimeAction.setEnabled(false);

		// Create the 'Set Loop Iterations' action
		m_setLoopIterationsAction = new Action() { 
			public void run() {
				ISelection selection = getViewer().getSelection();
		        if (selection instanceof IStructuredSelection) {	            
		            final Object object = ((IStructuredSelection)selection).getFirstElement();
		            if (object instanceof Node) {				
     			        new Loop(getParent().getShell(), (Node)object);
		            }
		        }
			}
		};
		m_setLoopIterationsAction.setText("Set loop iterations");
		menuMgr.add(m_setLoopIterationsAction);
		m_setLoopIterationsAction.setEnabled(false);
	} 		
}
