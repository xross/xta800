package com.xmos.xta.gui.views.routes.selected;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.icons.Icons;

public class SelectedRouteLabelProvider extends CellLabelProvider {
    private static final boolean DEBUG = true;
	
    public SelectedRouteLabelProvider() {
		if (DEBUG) System.out.printf("SelectedRouteLabelProvider()\n");	
	}

	public void update(ViewerCell cell) {
		if (DEBUG) System.out.printf("SelectedRouteLabelProvider: update\n");	

		Node node = (Node)cell.getElement();
		
		String nodeText = node.getTypeName();
		if (node.getFunctionName() != "")
			nodeText += " " + node.getFunctionName();
		nodeText += " " + node.getAttributes().getTimingSummary();
		
		cell.setText(nodeText);
		
		String iconName = node.getTypeName() + "_node";
		if (node.getAttributes().getDisplayUnknown()) {
			iconName += ".unknown";
		}
		cell.setImage(Icons.getIcon(iconName));
	}

	public String getToolTipText(Object element) {
		if (DEBUG) System.out.printf("SelectedRouteLabelProvider: getToolTipText\n");	

		Node node = (Node)element;		
		return node.getAttributes().getTimingDetails();
	}
}
