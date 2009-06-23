package com.xmos.xta.gui.views.info;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class InfoViewerLabelProvider extends LabelProvider implements ITableLabelProvider {
    private String[] m_columnNames;
    private int[] m_columnWidths;
    private int[] m_columnAligmnments;
    
    static final String PC    = "Pc";
    static final String DESC  = "Description";
    
    public InfoViewerLabelProvider() {
    	m_columnNames = new String[] { PC, DESC };
    	m_columnWidths = new int[] { 100, 500 };
    	m_columnAligmnments = new int[] { SWT.LEFT, SWT.LEFT };
    }
    
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		assert((columnIndex >= 0) && (columnIndex < m_columnNames.length));
		
		Node node = (Node)element;	
		if (m_columnNames[columnIndex].equals(PC)) {
			return String.format("0x%x", node.getStartPc());
    	} else if (m_columnNames[columnIndex].equals(DESC)) {    		
		    return node.getAttributes().getUnknownDescription();
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
