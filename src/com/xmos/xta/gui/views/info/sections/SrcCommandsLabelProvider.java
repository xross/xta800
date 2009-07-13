package com.xmos.xta.gui.views.info.sections;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;

class SrcCommandsViewerLabelProvider extends SectionsViewerLabelProvider implements ITableLabelProvider {
    static final String COMMAND = "Command";
    
    SrcCommandsViewerLabelProvider() {
    	m_columnNames = new String[] { COMMAND };
    	m_columnWidths = new int[] { 300 };
    	m_columnAligmnments = new int[] { SWT.LEFT };
    }

	public String getColumnText(Object element, int columnIndex) {
		assert((columnIndex >= 0) && (columnIndex < m_columnNames.length));
		
		String command = (String)element;	
		if (m_columnNames[columnIndex].equals(COMMAND)) {
			return String.format("%s", command);
		}
		assert(false);
		return "";
	}
}
