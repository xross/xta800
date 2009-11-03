package com.xmos.xta.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.xmos.xta.core.engine.TimeUnit;

public abstract class XtaDialog extends Dialog {
    
    public XtaDialog(Shell parent) {
    	super(parent);
    }

    abstract void checkButtonStatus();

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
    
    Composite createContainer(Composite parent) {
    	Composite container = (Composite)super.createDialogArea(parent);
	    final GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 2;
	    container.setLayout(gridLayout); 
	    return container;
    }
   
	Group createGroup(Composite parent, String description, int numColumns, int span) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
	    GridLayout layout = new GridLayout();
	    layout.numColumns = numColumns;
	    group.setLayout(layout);	    
	    if (description != null) {
	        group.setText(description);
	    }
	    group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, span, 1));
	    return group;
	}  
	
    Text createValueControl(Composite container) {
		Group group = createGroup(container, "value", 1, 1);
		Text text = new Text(group, SWT.SINGLE | SWT.CENTER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				checkButtonStatus();
			}
		});	
		return text;
    }    
	
	Combo createUnitsControl(Composite container) {
		Group group = createGroup(container, "units", 1, 1);
		Combo combo = new Combo(group, SWT.SINGLE | SWT.CENTER);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		combo.setItems(TimeUnit.getUnits());
		combo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				checkButtonStatus();
			}
		});
		return combo;
	}
	
	Button createAssertControl(Composite container) {
		Group group = createGroup(container, null, 2, 2);
		Button button = new Button(group, SWT.CHECK);
		Label label = new Label(group, SWT.NONE);
		label.setText("set as assertion");
		return button;
	}	
}
