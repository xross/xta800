package com.xmos.xta.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class XtaDialog extends Dialog {
    
    public XtaDialog(Shell parent) {
    	super(parent);
    }

    protected void configureShell(Shell shell, String title, int width, int height) {
    	shell.setText(title);
    	
    	if ((width > 0) && (height > 0))
    	    shell.setSize(width, height);

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
}
