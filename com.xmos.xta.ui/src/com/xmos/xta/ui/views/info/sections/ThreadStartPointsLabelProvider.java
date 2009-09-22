package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;

class ThreadStartPointsViewerLabelProvider extends SectionsViewerLabelProvider implements ITableLabelProvider {
    static final String PC = "Pc";
    
    ThreadStartPointsViewerLabelProvider() {
    	m_columnNames = new String[] { PC };
    	m_columnWidths = new int[] { 100 };
    	m_columnAligmnments = new int[] { SWT.LEFT };
    }

	public String getColumnText(Object element, int columnIndex) {
		assert((columnIndex >= 0) && (columnIndex < m_columnNames.length));
		
		Long pc = (Long)element;	
		if (m_columnNames[columnIndex].equals(PC)) {
			return String.format("0x%x", pc);
		}
		assert(false);
		return "";
	}

	public int compare(Object e1, Object e2, String column) {
		Long p1 = (Long)e1;
		Long p2 = (Long)e2;
		int res = 0;
		if (column.equals(PC)) {
			res = p1 > p2 ? 1 : -1;
		} else {
			assert(false);
		}
		return res;
	}
}
