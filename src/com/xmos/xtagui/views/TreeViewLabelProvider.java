package com.xmos.xtagui.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.xmos.xtagui.Application;
import com.xmos.xtagui.nodes.Node;

class TreeViewLabelProvider implements ILabelProvider {
    private static final boolean DEBUG = true;

	private Display m_display;
	private Map<String, Image> m_imageCache;
	
	TreeViewLabelProvider(Display display) {
		if (DEBUG) System.out.printf("TreeViewLabelProvider()\n");	

		m_display = display;
		m_imageCache = new HashMap<String, Image>();
	}
	
	public Image getImage(Object element) {
		if (DEBUG) System.out.printf("TreeViewLabelProvider: getImage\n");	

		Node node = (Node)element;
		return getImageFromCache(node.getTypeName());
	}

	public String getText(Object element) {
		if (DEBUG) System.out.printf("TreeViewLabelProvider: getText\n");	

		Node node = (Node)element;
		return node.getDescription();
	}

	public void dispose() {
		if (DEBUG) System.out.printf("TreeViewLabelProvider: dispose\n");	
	}

	public boolean isLabelProperty(Object element, String property) {
		if (DEBUG) System.out.printf("TreeViewLabelProvider: isLabelProperty\n");	
		return false;
	}

	public void addListener(ILabelProviderListener listener) {
		if (DEBUG) System.out.printf("TreeViewLabelProvider: addListener\n");	
	}
	
	public void removeListener(ILabelProviderListener listener) {
		if (DEBUG) System.out.printf("TreeViewLabelProvider: removeListener\n");	
	}	
	
	private Image getImageFromCache(String nodeType) {
		if (DEBUG) System.out.printf("TreeViewLabelProvider: getImageFromCache\n");	

		if (m_imageCache.containsKey(nodeType)) {
			return m_imageCache.get(nodeType);
		}		
		Image image = new Image(m_display, Application.class.getResourceAsStream("icons/" + nodeType + ".png"));
		m_imageCache.put(nodeType, image);
		return image;
	}
}
