package com.xmos.xta.rcp.views;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ErrorsViewLabelProvider extends LabelProvider implements ITableLabelProvider {
    public static final boolean DEBUG = false;

	public Image getColumnImage(Object element, int columnIndex) {
		if (DEBUG) System.out.printf("ErrorsViewLabelProvider: getColumnImage\n");	
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (DEBUG) System.out.printf("UnknownsViewLabelProvider: getColumnText: index: %d\n", columnIndex);	
		
		assert(columnIndex == 0);
		return (String)element;
	}
}
