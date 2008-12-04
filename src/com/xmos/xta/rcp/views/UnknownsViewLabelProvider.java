package com.xmos.xta.rcp.views;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import com.xmos.xta.engine.Unknown;

public class UnknownsViewLabelProvider extends LabelProvider implements ITableLabelProvider {
    public static final boolean DEBUG = false;

    private String[] m_columnNames;
    private int[] m_columnWidths;
    private int[] m_columnAligmnments;
    
    private static final String BLOCKID = "Block Id";
    private static final String TYPE   = "Type";
    private static final String PC     = "Pc";
    private static final String REASON = "Reason";
    
    public UnknownsViewLabelProvider() {
    	m_columnNames = new String[] { BLOCKID, TYPE, PC, REASON };
    	m_columnWidths = new int[] { 100, 100, 100, 500 };
    	m_columnAligmnments = new int[] { SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT };
    }
    
	public Image getColumnImage(Object element, int columnIndex) {
		if (DEBUG) System.out.printf("UnknownsViewLabelProvider: getColumnImage\n");	
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (DEBUG) System.out.printf("UnknownsViewLabelProvider: getColumnText: index: %d\n", columnIndex);	
		
		assert((columnIndex >= 0) && (columnIndex < m_columnNames.length));
		
		Unknown unknown = (Unknown)element;	
		if (m_columnNames[columnIndex].equals(BLOCKID)) {
			return "Block " + unknown.getPathId();
		} else if (m_columnNames[columnIndex].equals(TYPE)) {
			return unknown.getTypeString();			
		} else if (m_columnNames[columnIndex].equals(PC)) {
			return String.format("0x%08x", unknown.getPc());
    	} else if (m_columnNames[columnIndex].equals(REASON)) {
	    	return unknown.getReason();			
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
