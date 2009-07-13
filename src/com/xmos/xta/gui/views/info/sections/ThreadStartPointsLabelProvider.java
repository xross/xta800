package com.xmos.xta.gui.views.info.sections;

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
		
		Integer pc = (Integer)element;	
		if (m_columnNames[columnIndex].equals(PC)) {
			return String.format("0x%x", pc);
		}
		assert(false);
		return "";
	}
}
