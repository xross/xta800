package com.xmos.xta.gui.views.info.allsrclabels;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import com.xmos.xta.engine.sections.SrcLabelTableInfo;

public class AllSrcLabelsViewerLabelProvider extends LabelProvider implements ITableLabelProvider {
    private String[] m_columnNames;
    private int[] m_columnWidths;
    private int[] m_columnAligmnments;
    
    static final String PC               = "Pc";
    static final String COMPDIR          = "Compilation Directory";
    static final String FILENAME         = "File Name";
    static final String LINENUMBERSTART  = "Line Number Start";
    static final String LINENUMBEREND    = "Line Number End";
    
    AllSrcLabelsViewerLabelProvider() {
    	m_columnNames = new String[] { PC, COMPDIR, FILENAME, LINENUMBERSTART, LINENUMBEREND };
    	m_columnWidths = new int[] { 100, 200, 100, 100, 100 };
    	m_columnAligmnments = new int[] { SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT };
    }
    
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		assert((columnIndex >= 0) && (columnIndex < m_columnNames.length));
		
		SrcLabelTableInfo srcLabelTableInfo = (SrcLabelTableInfo)element;	
		if (m_columnNames[columnIndex].equals(PC)) {
			return String.format("0x%x", srcLabelTableInfo.getPc());
			
    	} else if (m_columnNames[columnIndex].equals(COMPDIR)) {    		
	        return srcLabelTableInfo.getCompilationDir();

    	} else if (m_columnNames[columnIndex].equals(FILENAME)) {    		
	        return srcLabelTableInfo.getFileName();
    	
    	} else if (m_columnNames[columnIndex].equals(LINENUMBERSTART)) {    		
	        return String.format("%d", srcLabelTableInfo.getLineNumberStart());

    	} else if (m_columnNames[columnIndex].equals(LINENUMBEREND)) {    		
	        return String.format("%d", srcLabelTableInfo.getLineNumberEnd());
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
