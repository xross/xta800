package com.xmos.xta.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public abstract class XtaDialog extends Dialog {
    
    public XtaDialog(Shell parent) {
    	super(parent);
    }

    protected void configureShell(Shell shell, String title, int width, int height) {
    	shell.setText(title);
    	
    	// Ensure the shell positioning is next to mouse
    	Point pos = Display.getCurrent().getCursorLocation();
		Rectangle displayBounds = Display.getCurrent().getBounds();
		if (pos.x + width > displayBounds.width) {
			pos.x = displayBounds.width - width - 10;
		}
		if (pos.y + height > displayBounds.height) {
			pos.y = displayBounds.height - height - 10;
		}
		shell.setLocation(pos);
    }
    
	Group createGroup(Composite parent, String description, int numColumns, int span) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
	    GridLayout layout = new GridLayout();
	    layout.numColumns = numColumns;
	    group.setLayout(layout);
	    group.setText(description);
	    group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, span, 1));
	    return group;
	}    
}
