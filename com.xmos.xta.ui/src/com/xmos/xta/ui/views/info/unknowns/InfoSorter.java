package com.xmos.xta.ui.views.info.unknowns;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown;

class InfoSorter extends ViewerSorter {
	private int m_field = 0;
	private int m_direction = 1;
	
	public int compare(Viewer viewer, Object e1, Object e2) {
		Unknown p1 = (Unknown)e1;
		Unknown p2 = (Unknown)e2;
		switch(m_field) {
		case 0:
			int res2 = p1.getNode().getStartPc() > p2.getNode().getStartPc() ? 1 : -1;
			return res2 * m_direction;
		case 1:
			int res3 = p1.getDescription().compareTo(p2.getDescription());
			return res3 * m_direction;
		case 2:
			int resd = p1.getNode().getBackTrace("").compareTo(p2.getNode().getBackTrace(""));
			return resd * m_direction;
		case 3:
			int res4 = p1.getLevel().toString().compareTo(p2.getLevel().toString());
			return res4 * m_direction;
		default:
			assert(false);
			return 0;
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
