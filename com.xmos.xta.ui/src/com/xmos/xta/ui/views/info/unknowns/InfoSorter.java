package com.xmos.xta.ui.views.info.unknowns;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

class InfoSorter extends ViewerSorter {
	private int m_field = 0;
	private int m_direction = 1;
	
	public int compare(Viewer viewer, Object e1, Object e2) {
		Node p1 = (Node)e1;
		Node p2 = (Node)e2;
		switch(m_field) {
		case 0:
			int res2 = p1.getStartPc() > p2.getStartPc() ? 1 : -1;
			return res2 * m_direction;
		case 1:
			int res3 = p1.getAttributes().getUnknownDescription().compareTo(p2.getAttributes().getUnknownDescription());
			return res3 * m_direction;
		default:
			int resd = p1.getBackTrace("").compareTo(p2.getBackTrace(""));
			return resd * m_direction;
		}
	}

	public void setColoumn(int i) {
		if (m_field == i) {
			m_direction = -1 * m_direction;
		} else {
			m_field = i;
			m_direction = 1;
		}
	}
} 	
