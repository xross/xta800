package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;

import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

class SystemsViewerLabelProvider extends BaseViewerLabelProvider implements ITableLabelProvider {
    static final String FUNCTION = "Function";
    
    SystemsViewerLabelProvider() {
    	m_columnNames = new String[] { FUNCTION };
    	m_columnWidths = new int[] { 200 };
    	m_columnAligmnments = new int[] { SWT.LEFT };
    }

	public String getColumnText(Object element, int columnIndex) {
		assert((columnIndex >= 0) && (columnIndex < m_columnNames.length));
		
		String functionName = (String)element;	
		if (m_columnNames[columnIndex].equals(FUNCTION)) {
			return String.format("%s", functionName);
		}
		assert(false);
		return "";
	}

	public int compare(Object e1, Object e2, String column) {
		String p1 = (String)e1;
		String p2 = (String)e2;
		int res = 0;
		if (column.equals(FUNCTION)) {
			res = p1.compareTo(p2);
		} else {
			assert(false);
		}
		return res;
	}
}
