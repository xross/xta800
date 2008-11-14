package com.xmos.xtagui.views;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xtagui.XtaDisplayer;

public class FunctionView extends ViewPart implements IPropertyChangeListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xtagui.views.Function";

	public FunctionView() {
		if (DEBUG) System.out.printf("Function()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Function: createPartControl\n");			
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Function: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Function: propertyChange: %s\n", event.getProperty());			
	}	
}
