package com.xmos.xta.gui.views.info.sections;

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
    		for (Integer toPc : branches.m_toPcs) {
    			toPcs += String.format("0x%x ", toPc);
    		}
	        return toPcs;
        }
		assert(false);
		return "";
	}
}
