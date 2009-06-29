package com.xmos.xta.gui.views.info.unknowns;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;

class InfoViewerContentProvider implements IStructuredContentProvider {
    private UnknownState m_unknownState;

    InfoViewerContentProvider(UnknownState unknownState) {
    	m_unknownState = unknownState;
    }
    
	public Object[] getElements(Object inputElement) {
	    Collection<Node> nodes = ((Route)inputElement).getUnknowns(m_unknownState);
		return nodes.toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
