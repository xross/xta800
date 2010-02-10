package com.xmos.xta.ui.dialogs;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.ui.GuiEngine;

public class SettingsLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	List<String> m_columnNames;
	List<Integer> m_columnAlignment;
	
	public SettingsLabelProvider(Engine engine) {
		m_columnNames = new LinkedList<String>();
		m_columnAlignment = new LinkedList<Integer>();
		
		m_columnNames.add("Core");
		m_columnAlignment.add(SWT.LEFT);
		Vector<Integer> nodeNumbers = engine.getXeLoader().getNodeNumbers();
		for (int nodeNumber : nodeNumbers) {
			m_columnNames.add(String.format("Node %d", nodeNumber));
			m_columnAlignment.add(SWT.CENTER);
		}
	}
	
	public List<String> getColumnNames() {
		return m_columnNames;
	}

	public List<Integer> getColumnAlignments() {
		return m_columnAlignment;
	}

	public String getColumnText(Object element, int columnIndex) {
		int coreNumber = (Integer)element;
		return getText(columnIndex, coreNumber);
	}

	public int compare(Object e1, Object e2, String column) {
		int c1 = (Integer)e1;
		int c2 = (Integer)e2;

		return c1 > c2 ? 1 : -1;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getText(int columnIndex, int coreNumber) {
		if (columnIndex == 0) {
			if (coreNumber == -1) {
				return "Mhz";
			} else {
				return String.format("Core %d", coreNumber);
			}
		} else {
			int nodeNumber = columnIndex - 1;
			
			Engine engine = GuiEngine.getEngine();
			if (coreNumber == -1) {
				return String.format("%.1f", engine.getFrequency(nodeNumber));
			} else {
				return String.format("%d", engine.getNumThreads(nodeNumber, coreNumber));
			}
		}
	}
}
