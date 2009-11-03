package com.xmos.xta.ui.views.info.unknowns;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.State;

class InfoViewerContentProvider implements IStructuredContentProvider {
    private State m_unknownState;

    InfoViewerContentProvider(State unknownState) {
    	m_unknownState = unknownState;
    }
    
	public Object[] getElements(Object inputElement) {
	    Collection<Unknown> unknowns = ((Route)inputElement).getRouteUnknowns().getUnknowns(m_unknownState);
		return unknowns.toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
