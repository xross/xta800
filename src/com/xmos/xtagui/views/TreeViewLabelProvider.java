package com.xmos.xtagui.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.xmos.xtagui.Application;
import com.xmos.xtagui.nodes.Node;

class TreeViewLabelProvider extends CellLabelProvider {
    private static final boolean DEBUG = true;

	private Display m_display;
	private Map<String, Image> m_imageCache;
	
	TreeViewLabelProvider(Display display) {
		if (DEBUG) System.out.printf("TreeViewLabelProvider()\n");	

		m_display = display;
		m_imageCache = new HashMap<String, Image>();
	}

	public void update(ViewerCell cell) {
		if (DEBUG) System.out.printf("TreeViewLabelProvider: update\n");	
		
		Node node = (Node)cell.getElement();		
		cell.setText(node.getDescription());
		cell.setImage(getImageFromCache(node.getTypeName()));
	}

	public String getToolTipText(Object element) {
		if (DEBUG) System.out.printf("TreeViewLabelProvider: getToolTipText\n");	

		Node node = (Node)element;		
		return node.getToolTipText();
	}
		
	private Image getImageFromCache(String nodeType) {
		if (DEBUG) System.out.printf("TreeViewLabelProvider: getImageFromCache: %s\n", nodeType);	

		if (m_imageCache.containsKey(nodeType)) {
			return m_imageCache.get(nodeType);
		}		
		Image image = new Image(m_display, Application.class.getResourceAsStream("icons/" + nodeType + ".png"));
		m_imageCache.put(nodeType, image);
		return image;
	}
}
