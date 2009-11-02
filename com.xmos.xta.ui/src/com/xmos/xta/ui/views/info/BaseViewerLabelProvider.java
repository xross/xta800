package com.xmos.xta.ui.views.info;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public abstract class BaseViewerLabelProvider extends LabelProvider implements ITableLabelProvider {
    protected String[] m_columnNames;
    protected int[] m_columnWidths;
    protected int[] m_columnAligmnments;

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
