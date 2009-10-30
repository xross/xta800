package com.xmos.xta.ui.views.info.exclusions;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;

import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

class ExclusionsViewerLabelProvider extends BaseViewerLabelProvider implements ITableLabelProvider {
    static final String PC          = "Pc";
    static final String DESCRIPTION = "Description";
    
    ExclusionsViewerLabelProvider() {
    	m_columnNames = new String[] { PC, DESCRIPTION };
    	m_columnWidths = new int[] { 100, 200 };
    	m_columnAligmnments = new int[] { SWT.LEFT, SWT.LEFT };
    }

	public String getColumnText(Object element, int columnIndex) {
		assert((columnIndex >= 0) && (columnIndex < m_columnNames.length));
		
		Reference reference = (Reference)element;	
		if (m_columnNames[columnIndex].equals(PC)) {
			return String.format("0x%x", reference.getPc());
			
    	} else if (m_columnNames[columnIndex].equals(DESCRIPTION)) {    		
		    return reference.getRef();
		    
    	}
		assert(false);
		return "";
	}

	public int compare(Object e1, Object e2, String column) {
		Reference p1 = (Reference)e1;
		Reference p2 = (Reference)e2;
		int res = 0;
		if (column.equals(PC)) {
			res = p1.getPc() > p2.getPc() ? 1 : -1;
		} else if (column.equals(DESCRIPTION)) {
			res = p1.getRef().compareTo(p2.getRef());
		} else {
			assert(false);
		}
		return res;
	}
}
