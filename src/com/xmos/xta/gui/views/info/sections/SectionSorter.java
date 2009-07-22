package com.xmos.xta.gui.views.info.sections;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

class SectionSorter extends ViewerSorter {
	private String m_field = "";
	private int m_direction = 1;
	private SectionsViewerLabelProvider m_labelProvider;
	
	SectionSorter(SectionsViewerLabelProvider labelProvider) {
		m_labelProvider = labelProvider;
	}
	
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (m_field.equals("")) {
			return 0;
		} else {
			int res = m_labelProvider.compare(e1, e2, m_field);
			return res * m_direction;
		}
	}

	public void setColoumn(String field) {
		if (m_field.equals(field)) {
			m_direction = -1 * m_direction;
		} else {
			m_field = field;
			m_direction = 1;
		}
	}
} 	
