package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;

import com.xmos.xta.core.engine.sections.SrcLabelTableInfo;

class AllSrcLabelsViewerLabelProvider extends SectionsViewerLabelProvider implements ITableLabelProvider {
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

	public int compare(Object e1, Object e2, String column) {
		SrcLabelTableInfo p1 = (SrcLabelTableInfo)e1;
		SrcLabelTableInfo p2 = (SrcLabelTableInfo)e2;
		int res = 0;
		if (column.equals(PC)) {
			res = p1.getPc() > p2.getPc() ? 1 : -1;
		} else if (column.equals(COMPDIR)) {
			res = p1.getCompilationDir().compareTo(p2.getCompilationDir());
		} else if (column.equals(FILENAME)) {
			res = p1.getFileName().compareTo(p2.getFileName());
		} else if (column.equals(LINENUMBERSTART)) {
			res = p1.getLineNumberStart() > p2.getLineNumberStart() ? 1 : -1;
		} else if (column.equals(LINENUMBEREND)) {
			res = p1.getLineNumberEnd() > p2.getLineNumberEnd() ? 1 : -1;
		} else {
			assert(false);
		}
		return res;
	}
}
