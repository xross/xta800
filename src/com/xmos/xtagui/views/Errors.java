package com.xmos.xtagui.views;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xtagui.XtaDisplayer;

public class Errors extends ViewPart implements IPropertyChangeListener {
    public static final boolean DEBUG = true;
    public static final String ID = "com.xmos.xtagui.views.Errors";

	public Errors() {
		if (DEBUG) System.out.printf("Errors()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Errors: createPartControl\n");	
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Errors: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Errors: propertyChange: %s\n", event.getProperty());			
	}	
}
