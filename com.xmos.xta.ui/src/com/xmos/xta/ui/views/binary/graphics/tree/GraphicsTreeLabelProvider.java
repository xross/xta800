package com.xmos.xta.ui.views.binary.graphics.tree;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import com.xmos.xta.ui.icons.Icons;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsObject;

public class GraphicsTreeLabelProvider extends CellLabelProvider {
	
    public GraphicsTreeLabelProvider() {
	}

	public void update(ViewerCell cell) {
		GraphicsObject object = (GraphicsObject)cell.getElement();
		
		String nodeText = object.getName();
		cell.setText(nodeText);
		
		String iconName = object.getIconName();
		cell.setImage(Icons.getIcon(iconName));
	}

	public String getToolTipText(Object element) {
		GraphicsObject object = (GraphicsObject)element;	
		return object.getToolTip();
	}
}
