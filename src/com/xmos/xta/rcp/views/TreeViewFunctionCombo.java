package com.xmos.xta.rcp.views;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class TreeViewFunctionCombo extends ControlContribution {
	Combo m_combo;
	
	public TreeViewFunctionCombo(String str) {
		super(str);
	}

	public void addEntry(String str) {		
		m_combo.add(str);
	}
	
	public String getCurrentEntry() {
		return m_combo.getText();
	}
	
	protected Control createControl(Composite parent) {
		m_combo = new Combo(parent, SWT.NONE | SWT.DROP_DOWN | SWT.READ_ONLY);
		return m_combo;
	}
}
