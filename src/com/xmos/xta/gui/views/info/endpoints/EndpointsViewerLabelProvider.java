package com.xmos.xta.gui.views.info.endpoints;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import com.xmos.xta.engine.sections.CodePointInfo;

public class EndpointsViewerLabelProvider extends LabelProvider implements ITableLabelProvider {
    private String[] m_columnNames;
    private int[] m_columnWidths;
    private int[] m_columnAligmnments;
    
    static final String PC          = "Pc";
    static final String LABEL       = "Label";
    static final String COMPDIR     = "Compilation Directory";
    static final String FILENAME    = "File Name";
    static final String LINENUMBER  = "Line Number";
    
    EndpointsViewerLabelProvider() {
    	m_columnNames = new String[] { PC, LABEL, COMPDIR, FILENAME, LINENUMBER };
    	m_columnWidths = new int[] { 100, 100, 200, 100, 100 };
    	m_columnAligmnments = new int[] { SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT };
    }
    
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		assert((columnIndex >= 0) && (columnIndex < m_columnNames.length));
		
		CodePointInfo codePointInfo = (CodePointInfo)element;	
		if (m_columnNames[columnIndex].equals(PC)) {
			return String.format("0x%x", codePointInfo.getPc());
			
    	} else if (m_columnNames[columnIndex].equals(LABEL)) {    		
		    return codePointInfo.getLabel();
		    
    	} else if (m_columnNames[columnIndex].equals(COMPDIR)) {    		
	        return codePointInfo.getCompilationDir();

    	} else if (m_columnNames[columnIndex].equals(FILENAME)) {    		
	        return codePointInfo.getFileName();
    	
    	} else if (m_columnNames[columnIndex].equals(LINENUMBER)) {    		
	        return String.format("%d", codePointInfo.getLineNumber());
        }
		assert(false);
		return "";
	}
		
	public String[] getColumnNames() {
		return m_columnNames;
	}

	public int[] getColumnWidths() {
		return m_columnWidths;
	}

	public int[] getColumnAligmnments() {
		return m_columnAligmnments;
	}			
}
