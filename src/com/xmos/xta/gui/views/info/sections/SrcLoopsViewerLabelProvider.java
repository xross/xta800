package com.xmos.xta.gui.views.info.sections;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;

import com.xmos.xta.engine.sections.SrcLevelLoopInfo;

class SrcLoopsViewerLabelProvider extends SectionsViewerLabelProvider implements ITableLabelProvider {
    static final String PC = "Pc";
    static final String COUNT =  "Iteration Count";
    
    SrcLoopsViewerLabelProvider() {
    	m_columnNames = new String[] { PC, COUNT };
    	m_columnWidths = new int[] { 100, 100 };
    	m_columnAligmnments = new int[] { SWT.LEFT, SWT.LEFT };
    }
    
	public String getColumnText(Object element, int columnIndex) {
		assert((columnIndex >= 0) && (columnIndex < m_columnNames.length));
		
		SrcLevelLoopInfo srcLevelLoopInfo = (SrcLevelLoopInfo)element;	
		if (m_columnNames[columnIndex].equals(PC)) {
			return String.format("0x%x", srcLevelLoopInfo.getPc());
			
    	} else if (m_columnNames[columnIndex].equals(COUNT)) {
			return String.format("%d", srcLevelLoopInfo.getNumIterations());
        }
		assert(false);
		return "";
	}
}
