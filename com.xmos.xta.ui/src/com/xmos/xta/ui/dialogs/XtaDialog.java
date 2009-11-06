package com.xmos.xta.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.ui.GuiEngine;

public abstract class XtaDialog extends Dialog implements ButtonEnabler {
	private XtaImpl m_impl;
	private String m_title;
	
    public XtaDialog(Shell parent, XtaImpl impl, String title) {
    	super(parent);
    	m_impl = impl;
    	m_title = title;
    	open();
    }
    
	public void enable(boolean enable) {
		Button okButton = getButton(IDialogConstants.OK_ID);
		if (okButton != null) {
			okButton.setEnabled(enable);
		}
	}
    
    protected void configureShell(Shell newShell) {
    	super.configureShell(newShell);
    	XtaDialog.configureShell(newShell, m_title, 400, 150);
    }

    protected Control createDialogArea(Composite parent) {
    	Composite container = (Composite)super.createDialogArea(parent);    	
    	m_impl.createDialogArea(container, this);
    	return container;
    }

    protected void buttonPressed(int id) {
    	m_impl.buttonPressed(id);
    	if (id == IDialogConstants.OK_ID) {
    		GuiEngine.broadcast(Notifier.NOTIFY_UPDATE_STATE, "");
    	}
    	super.buttonPressed(id);
    }	
	
    protected void createButtonsForButtonBar(Composite parent) {
    	super.createButtonsForButtonBar(parent);
    	getButton(IDialogConstants.OK_ID).setEnabled(false);
    }
    
    static void configureShell(Shell shell, String title, int width, int height) {
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
    
    static Composite configureContainer(Composite container) {
	    final GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 2;
	    container.setLayout(gridLayout); 
	    return container;
    }
   
    static Group createGroup(Composite parent, String description, int numColumns, int span) {
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
	
    static Text createValueControl(Composite container, final XtaImpl impl) {
		Group group = createGroup(container, "value", 1, 1);
		Text text = new Text(group, SWT.SINGLE | SWT.CENTER | SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				impl.updateButtonStatus();
			}
		});	
		return text;
    }    
	
    static Combo createUnitsControl(Composite container, final XtaImpl impl) {
		Group group = createGroup(container, "units", 1, 1);
		Combo combo = new Combo(group, SWT.SINGLE | SWT.CENTER);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		combo.setItems(TimeUnit.getUnits());
		combo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				impl.updateButtonStatus();
			}
		});
		return combo;
	}
	
    static Button createAssertControl(Composite container) {
		Group group = createGroup(container, null, 2, 2);
		Button button = new Button(group, SWT.CHECK);
		Label label = new Label(group, SWT.NONE);
		label.setText("set as assertion");
		return button;
	}	
}
