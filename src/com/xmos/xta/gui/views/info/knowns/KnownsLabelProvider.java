package com.xmos.xta.gui.views.info.knowns;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

public class KnownsLabelProvider extends LabelProvider implements ITableLabelProvider {
    public static final boolean DEBUG = false;

    private String[] m_columnNames;
    private int[] m_columnWidths;
    private int[] m_columnAligmnments;
    
    static final String PC    = "Pc";
    static final String DESC  = "Description";
    
    public KnownsLabelProvider() {
    	m_columnNames = new String[] { PC, DESC };
    	m_columnWidths = new int[] { 100, 500 };
    	m_columnAligmnments = new int[] { SWT.LEFT, SWT.LEFT };
    }
    
	public Image getColumnImage(Object element, int columnIndex) {
		if (DEBUG) System.out.printf("SetTimesLabelProvider: getColumnImage\n");	
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (DEBUG) System.out.printf("SetTimesLabelProvider: getColumnText: index: %d\n", columnIndex);	
		
		assert((columnIndex >= 0) && (columnIndex < m_columnNames.length));
/* TODO		
		Unknown unknown = (Unknown)element;	
		if (m_columnNames[columnIndex].equals(PC)) {
			return String.format("0x%05x", unknown.getPc());
    	} else if (m_columnNames[columnIndex].equals(DESC)) {    		
		    return unknown.getDescription();
	    }
	    		*/
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
