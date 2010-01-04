package com.xmos.xta.ui.views.info.defines;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;

import com.xmos.xta.core.engine.defines.DefineInfo;
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
		assert(element instanceof DefineInfo);
		assert((columnIndex >= 0) && (columnIndex < m_columnNames.length));
		
		DefineInfo info = (DefineInfo)element;
		if (m_columnNames[columnIndex].equals(DESCRIPTION)) {
			return info.getDescription();
			
    	} else if (m_columnNames[columnIndex].equals(VALUE)) {    		
	        return info.getValue();
        }
		assert(false);
		return "";
	}

	public int compare(Object e1, Object e2, String column) {
		assert(e1 instanceof DefineInfo);
		assert(e2 instanceof DefineInfo);		
		
		DefineInfo p1 = (DefineInfo)e1;
		DefineInfo p2 = (DefineInfo)e2;
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
