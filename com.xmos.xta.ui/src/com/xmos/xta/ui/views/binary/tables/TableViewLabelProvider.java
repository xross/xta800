package com.xmos.xta.ui.views.binary.tables;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class TableViewLabelProvider extends LabelProvider implements ITableLabelProvider  {
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		assert(element instanceof TableElement);
		TableElement tableElement = (TableElement)element;	
		
		switch (columnIndex) {
		case TableView.NAME:
			return tableElement.getName();
			
		case TableView.ADDRESS:
			return String.format("0x%x", tableElement.getAddress());
			
		case TableView.SIZE:
			return String.format("0x%x", tableElement.getSize());
			
		default:
			assert(false);
			return "";			
		}
	}
}
