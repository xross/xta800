package com.xmos.xta.gui.views.info.sections;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;

import com.xmos.dwarfhandler.DebugFile;

class SourcesViewerLabelProvider extends SectionsViewerLabelProvider implements ITableLabelProvider {
    static final String COMPDIR = "Compilation Directory";
    static final String FILENAME = "File Name";
    
    SourcesViewerLabelProvider() {
    	m_columnNames = new String[] { COMPDIR, FILENAME };
    	m_columnWidths = new int[] { 300, 200 };
    	m_columnAligmnments = new int[] { SWT.LEFT, SWT.LEFT };
    }
    
	public String getColumnText(Object element, int columnIndex) {
		assert((columnIndex >= 0) && (columnIndex < m_columnNames.length));
		
		DebugFile debugFile = (DebugFile)element;	
		if (m_columnNames[columnIndex].equals(COMPDIR)) {    		
	        return debugFile.getBaseDir();

    	} else if (m_columnNames[columnIndex].equals(FILENAME)) {    		
	        return debugFile.getFileName();
    	
    	}
		assert(false);
		return "";
	}

	public int compare(Object e1, Object e2, String column) {
		DebugFile p1 = (DebugFile)e1;
		DebugFile p2 = (DebugFile)e2;
		int res = 0;
		if (column.equals(COMPDIR)) {
			res = p1.getBaseDir().compareTo(p2.getBaseDir());
		} else if (column.equals(FILENAME)) {
			res = p1.getFileName().compareTo(p2.getFileName());
		} else {
			assert(false);
		}
		return res;
	}
}
