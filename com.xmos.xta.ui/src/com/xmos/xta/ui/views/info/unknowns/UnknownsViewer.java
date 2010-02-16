package com.xmos.xta.ui.views.info.unknowns;

import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPartSite;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.State;
import com.xmos.xta.ui.GuiEngine;

public class UnknownsViewer extends InfoViewer {

	public UnknownsViewer(TableViewer tableViewer, Composite parent, IWorkbenchPartSite site) {
		super(tableViewer, parent, site);
		update();
	}

	public void update() {
		getViewer().setInput(GuiEngine.getEngine().getSelectedRoute());
		updateHighlighting(GuiEngine.getSelectedNodes());	
	}
	
	public void updateHighlighting(List<Node> selectedNodes) {
		final Set<Node> allInstructionNodes = new TreeSet<Node>();
   		for (Node node : selectedNodes) {
       		node.traverse(new Stack<Node>(), new DefaultNodeTraverser() {
       		    public TraverseStatus foundInstructionNode(Node node, Stack<Node> ancestors) {
       		    	allInstructionNodes.add(node);
       		    	return TraverseStatus.CONTINUE;
       		    }
       		});    		
    	}

		for (TableItem item : getViewer().getTable().getItems()) {
			Unknown unknown = (Unknown)item.getData();					
			if (allInstructionNodes.contains(unknown.getNode())) {
				item.setBackground(getDisplay().getSystemColor(SWT.COLOR_YELLOW));
			} else {
				item.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			}
		}
	}
	
	InfoViewerContentProvider createContentProvider() {
		return new InfoViewerContentProvider(State.UNKNOWN);
	}
	
	InfoViewerLabelProvider createLabelProvider() {
		return new InfoViewerLabelProvider();
	}
}
