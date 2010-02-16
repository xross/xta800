package com.xmos.xta.ui.dialogs;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;

import com.xmos.xta.ui.GuiEngine;

public class SettingsCellModifier implements ICellModifier {

	private SettingsLabelProvider m_labelProvider;
	private TableViewer m_viewer;
	
	public SettingsCellModifier(TableViewer viewer, SettingsLabelProvider labelProvider) {
		m_viewer = viewer;
		m_labelProvider = labelProvider;
	}
	
	public boolean canModify(Object element, String property) {
		int columnIndex = m_labelProvider.getColumnNames().indexOf(property);

		if (columnIndex > 0) {
			return true;
		} else {
			return false;
		}
	}

	public Object getValue(Object element, String property) {
		int columnIndex = m_labelProvider.getColumnNames().indexOf(property);
		int coreNumber = (Integer)element;
		return m_labelProvider.getText(columnIndex, coreNumber);
	}

	public void modify(Object element, String property, Object value) {
		// Cause table to refresh from underlying engine
		m_viewer.setInput(GuiEngine.getEngine());
	}
}
