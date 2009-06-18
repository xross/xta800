package com.xmos.xta.gui.views.info.unknowns;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

public class UnknownsLabelProvider extends LabelProvider implements ITableLabelProvider {
    public static final boolean DEBUG = false;

    private String[] m_columnNames;
    private int[] m_columnWidths;
    private int[] m_columnAligmnments;
    
    static final String PC      = "Pc";
    static final String REASON  = "Reason";
    
    public UnknownsLabelProvider() {
    	m_columnNames = new String[] { PC, REASON };
    	m_columnWidths = new int[] { 100, 500 };
    	m_columnAligmnments = new int[] { SWT.LEFT, SWT.LEFT };
    }
    
	public Image getColumnImage(Object element, int columnIndex) {
		if (DEBUG) System.out.printf("UnknownsLabelProvider: getColumnImage\n");	
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (DEBUG) System.out.printf("UnknownsLabelProvider: getColumnText: index: %d\n", columnIndex);	
		
		assert((columnIndex >= 0) && (columnIndex < m_columnNames.length));
/* TODO		
		Unknown unknown = (Unknown)element;	
		if (m_columnNames[columnIndex].equals(PC)) {
			return String.format("0x%05x", unknown.getPc());
    	} else if (m_columnNames[columnIndex].equals(REASON)) {    		
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
