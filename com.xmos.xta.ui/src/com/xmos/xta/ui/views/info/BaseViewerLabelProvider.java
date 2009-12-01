package com.xmos.xta.ui.views.info;

import java.util.Iterator;
import java.util.Set;

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

	protected int comparePcSets(Set<Long> pcs1, Set<Long> pcs2) {
		int res = 0;
		if (pcs1.size() > pcs2.size()) {
			return 1;
		} else {
			Iterator<Long> it1 = pcs1.iterator();
			Iterator<Long> it2 = pcs2.iterator();
			while (it1.hasNext() && (res == 0)) {
				long pc1 = it1.next();
				long pc2 = it2.next();
				if (pc1 > pc2) {
					return 1;
				} else if (pc1 < pc2) {
					return -1;
				}
			}
		}
		return 0;
	}
}
