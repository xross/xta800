package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public abstract class SectionsViewerLabelProvider extends LabelProvider implements ITableLabelProvider {
    String[] m_columnNames;
    int[] m_columnWidths;
    int[] m_columnAligmnments;
        
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
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
	
	public abstract int compare(Object e1, Object e2, String column);
}
