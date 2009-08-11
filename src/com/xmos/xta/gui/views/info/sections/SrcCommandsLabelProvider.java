package com.xmos.xta.gui.views.info.sections;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;

import com.xmos.xta.engine.sections.SrcStringInfo;

class SrcCommandsViewerLabelProvider extends SectionsViewerLabelProvider implements ITableLabelProvider {
    static final String COMMAND = "Command";
    static final String COMPDIR = "Compilation Directory";
    static final String FILENAME = "File Name";
    static final String LINENUMBER = "Line Number";
    
    SrcCommandsViewerLabelProvider() {
    	m_columnNames = new String[] { COMMAND, COMPDIR, FILENAME, LINENUMBER };
    	m_columnWidths = new int[] { 200, 200, 100, 100 };
    	m_columnAligmnments = new int[] { SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT };
    }

	public String getColumnText(Object element, int columnIndex) {
		assert((columnIndex >= 0) && (columnIndex < m_columnNames.length));
		
		SrcStringInfo command = (SrcStringInfo)element;	
		if (m_columnNames[columnIndex].equals(COMMAND)) {
			return String.format("%s", command.getString());

		} else if (m_columnNames[columnIndex].equals(COMPDIR)) {    		
	        return command.getCompilationDir();

    	} else if (m_columnNames[columnIndex].equals(FILENAME)) {    		
	        return command.getFileName();
    	
    	} else if (m_columnNames[columnIndex].equals(LINENUMBER)) {    		
	        return String.format("%d", command.getLineNumber());
        }
		assert(false);
		return "";
	}

	public int compare(Object e1, Object e2, String column) {
		SrcStringInfo p1 = (SrcStringInfo)e1;
		SrcStringInfo p2 = (SrcStringInfo)e2;
		int res = 0;
		if (column.equals(COMMAND)) {
			res = p1.getString().compareTo(p2.getString());
		} else if (column.equals(COMPDIR)) {
			res = p1.getCompilationDir().compareTo(p2.getCompilationDir());
		} else if (column.equals(FILENAME)) {
			res = p1.getFileName().compareTo(p2.getFileName());
		} else if (column.equals(LINENUMBER)) {
			res = p1.getLineNumber() > p2.getLineNumber() ? 1 : -1;
		} else {
			assert(false);
		}
		return res;
	}
}
