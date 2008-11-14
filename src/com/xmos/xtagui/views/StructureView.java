package com.xmos.xtagui.views;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xtagui.XtaDisplayer;

public class StructureView extends ViewPart implements IPropertyChangeListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xtagui.views.Structure";

	public StructureView() {
		if (DEBUG) System.out.printf("Structure()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Structure: createPartControl\n");			
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Structure: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Structure: propertyChange: %s\n", event.getProperty());			
	}	
}
