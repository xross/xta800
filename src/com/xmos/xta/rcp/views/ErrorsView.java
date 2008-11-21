package com.xmos.xta.rcp.views;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.rcp.XtaDisplayer;
import com.xmos.xta.rcp.XtaErrorLog;

public class ErrorsView extends ViewPart implements IPropertyChangeListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.rcp.views.Errors";

	private StyledText m_text;
    
	public ErrorsView() {
		if (DEBUG) System.out.printf("Errors()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Errors: createPartControl\n");	
		 
		m_text = new StyledText(parent, SWT.V_SCROLL);	
		m_text.setEditable(false); 
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Errors: setFocus\n");
		updateDisplay();
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Errors: propertyChange: %s\n", event.getProperty());
		
		if (event.getProperty().equals("Error")) {
			updateDisplay();
		}
	}
	
	private void updateDisplay() {
		m_text.setText("");
		for (String errorString : XtaErrorLog.getInstance().getErrorStrings()) {
			m_text.append("Error: " + errorString + "\n");
		}
		m_text.setSelection(m_text.getCharCount());
	}
}
