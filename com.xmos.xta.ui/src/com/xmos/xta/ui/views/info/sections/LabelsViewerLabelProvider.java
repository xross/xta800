package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;

class LabelsViewerLabelProvider extends SectionsViewerLabelProvider implements ITableLabelProvider {
    static final String LABEL = "Label";
    static final String PC    =  "Pc";
    
    LabelsViewerLabelProvider() {
    	m_columnNames = new String[] { LABEL, PC };
    	m_columnWidths = new int[] { 200, 100 };
    	m_columnAligmnments = new int[] { SWT.LEFT, SWT.LEFT };
    }
    
	public String getColumnText(Object element, int columnIndex) {
		assert((columnIndex >= 0) && (columnIndex < m_columnNames.length));
		
		LabelsContent labels = (LabelsContent)element;	
		if (m_columnNames[columnIndex].equals(LABEL)) {
			return labels.m_label;
			
    	} else if (m_columnNames[columnIndex].equals(PC)) {
			return String.format("0x%x", labels.m_pc);
        }
		assert(false);
		return "";
	}

	public int compare(Object e1, Object e2, String column) {
		LabelsContent p1 = (LabelsContent)e1;
		LabelsContent p2 = (LabelsContent)e2;
		int res = 0;
		if (column.equals(PC)) {
			res = p1.m_pc > p2.m_pc ? 1 : -1;
		} else if (column.equals(LABEL)) {
			res = p1.m_label.compareTo(p2.m_label);
		} else {
			assert(false);
		}
		return res;
	}
}
