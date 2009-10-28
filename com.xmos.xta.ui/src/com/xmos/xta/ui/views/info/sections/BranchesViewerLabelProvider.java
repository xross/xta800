package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;

class BranchesViewerLabelProvider extends SectionsViewerLabelProvider implements ITableLabelProvider {
    static final String FROMPC = "From Pc";
    static final String TOPCS =  "To Pcs";
    
    BranchesViewerLabelProvider() {
    	m_columnNames = new String[] { FROMPC, TOPCS };
    	m_columnWidths = new int[] { 100, 400 };
    	m_columnAligmnments = new int[] { SWT.LEFT, SWT.LEFT };
    }
    
	public String getColumnText(Object element, int columnIndex) {
		assert((columnIndex >= 0) && (columnIndex < m_columnNames.length));
		
		BranchesContent branches = (BranchesContent)element;	
		if (m_columnNames[columnIndex].equals(FROMPC)) {
			return String.format("0x%x", branches.m_fromPc);
			
    	} else if (m_columnNames[columnIndex].equals(TOPCS)) {
    		String toPcs = "";
    		for (Long toPc : branches.m_toPcs) {
    			toPcs += "0x" + Long.toHexString(toPc) + ", ";
    		}
	        return toPcs;
        }
		assert(false);
		return "";
	}

	public int compare(Object e1, Object e2, String column) {
		BranchesContent p1 = (BranchesContent)e1;
		BranchesContent p2 = (BranchesContent)e2;
		int res = 0;
		if (column.equals(FROMPC)) {
			res = p1.m_fromPc > p2.m_fromPc ? 1 : -1;
		} else if (column.equals(TOPCS)) {
			res = p1.m_toPcs.size() > p2.m_toPcs.size() ? 1 : -1;
		} else {
			assert(false);
		}
		return res;
	}
}
