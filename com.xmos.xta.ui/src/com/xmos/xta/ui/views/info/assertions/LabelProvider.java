package com.xmos.xta.ui.views.info.assertions;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;

import com.xmos.xta.core.engine.assertions.AssertionInfo;
import com.xmos.xta.ui.views.info.BaseViewerLabelProvider;

class LabelProvider extends BaseViewerLabelProvider implements ITableLabelProvider {
    static final String DESCRIPTION = "Description";
    static final String VALUE       = "Value";
    
    LabelProvider() {
    	m_columnNames = new String[] { DESCRIPTION, VALUE };
    	m_columnWidths = new int[] { 200, 200 };
    	m_columnAligmnments = new int[] { SWT.LEFT, SWT.LEFT };
    }
    
	public String getColumnText(Object element, int columnIndex) {
		assert(element instanceof AssertionInfo);
		assert((columnIndex >= 0) && (columnIndex < m_columnNames.length));
		
		AssertionInfo info = (AssertionInfo)element;
		if (m_columnNames[columnIndex].equals(DESCRIPTION)) {
			return info.getDescription();
			
    	} else if (m_columnNames[columnIndex].equals(VALUE)) {    		
	        return info.getValue();
        }
		assert(false);
		return "";
	}

	public int compare(Object e1, Object e2, String column) {
		assert(e1 instanceof AssertionInfo);
		assert(e2 instanceof AssertionInfo);		
		
		AssertionInfo p1 = (AssertionInfo)e1;
		AssertionInfo p2 = (AssertionInfo)e2;
		int res = 0;
		if (column.equals(DESCRIPTION)) {
			res = p1.getDescription().compareTo(p2.getDescription());
		} else if (column.equals(VALUE)) {
			res = p1.getValue().compareTo(p2.getValue());
		} else {
			assert(false);
		}
		return res;
	}
}
