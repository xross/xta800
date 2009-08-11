package com.xmos.xta.gui.views.info.sections;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;

import com.xmos.xta.engine.sections.SrcStringInfo;

class NoLoadsViewerLabelProvider extends SectionsViewerLabelProvider implements ITableLabelProvider {
    static final String FUNCTION = "Function";
    
    NoLoadsViewerLabelProvider() {
    	m_columnNames = new String[] { FUNCTION };
    	m_columnWidths = new int[] { 200 };
    	m_columnAligmnments = new int[] { SWT.LEFT };
    }

	public String getColumnText(Object element, int columnIndex) {
		assert((columnIndex >= 0) && (columnIndex < m_columnNames.length));
		
		SrcStringInfo noLoad = (SrcStringInfo)element;	
		if (m_columnNames[columnIndex].equals(FUNCTION)) {
			return String.format("%s", noLoad.getString());
		}
		assert(false);
		return "";
	}

	public int compare(Object e1, Object e2, String column) {
		SrcStringInfo p1 = (SrcStringInfo)e1;
		SrcStringInfo p2 = (SrcStringInfo)e2;
		int res = 0;
		if (column.equals(FUNCTION)) {
			res = p1.getString().compareTo(p2.getString());
		} else {
			assert(false);
		}
		return res;
	}
}
