package com.xmos.xtagui.views;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xtagui.XtaDisplayer;

public class Loop extends ViewPart implements IPropertyChangeListener {
    public static final boolean DEBUG = true;
    public static final String ID = "com.xmos.xtagui.views.Loop";

	public Loop() {
		if (DEBUG) System.out.printf("Loop()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Loop: createPartControl\n");			
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Loop: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Loop: propertyChange: %s\n", event.getProperty());			
	}	
}
