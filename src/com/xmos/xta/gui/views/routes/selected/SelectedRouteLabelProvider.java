package com.xmos.xta.gui.views.routes.selected;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.icons.Icons;

public class SelectedRouteLabelProvider extends CellLabelProvider {
    private static final boolean DEBUG = false;
	
    public SelectedRouteLabelProvider() {
		if (DEBUG) System.out.printf("SelectedRouteLabelProvider()\n");	
	}

	public void update(ViewerCell cell) {
		if (DEBUG) System.out.printf("SelectedRouteLabelProvider: update\n");	

		Node node = (Node)cell.getElement();
		
		String nodeText = node.getTypeName();
		nodeText += " " + node.getDescription();
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
		String toolTip = node.getAttributes().getTimingDetails();
		return toolTip.substring(0, toolTip.length()-1);
	}

    public boolean useNativeToolTip(Object object) {
        if (DEBUG) System.out.printf("SelectedRouteLabelProvider: useNativeToolTips\n");
        return true;
    }
}
