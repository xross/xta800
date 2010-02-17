package com.xmos.xta.ui.views.binary.tables;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class TableViewSorter extends ViewerSorter {
	private int m_field = 0;
	private int m_direction = 1;
	
	public int compare(Viewer viewer, Object e1, Object e2) {
		assert(e1 instanceof TableElement);
		TableElement p1 = (TableElement)e1;
		assert(e2 instanceof TableElement);
		TableElement p2 = (TableElement)e2;

		switch (m_field) {
		case TableView.NAME:
			int resName = p1.getName().compareTo(p2.getName());
			return resName * m_direction;
			
		case TableView.ADDRESS:
			int resAddr = p1.getAddress() > p2.getAddress() ? 1 : -1;
			return resAddr * m_direction;
			
		case TableView.SIZE:
			int resSize = p1.getSize() > p2.getSize() ? 1 : -1;
			return resSize * m_direction;

		default:
			assert(false);
			return 0;
		}
	}

	public void setColoumn(int i) {
		if (m_field == i) {
			m_direction = -1 * m_direction;
		} else {
			m_field = i;
			m_direction = 1;
		}
	}
}
