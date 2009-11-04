package com.xmos.xta.ui.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class Assertions extends Dialog implements ButtonEnabler {
	private CTabFolder m_folder;
	private Map<Control, XtaImpl> m_controls;
	
	public Assertions(Shell parentShell) {
		super(parentShell);
		m_controls = new HashMap<Control, XtaImpl>();		
    	open();		
	}

	public void enable(boolean enable) {
		getButton(IDialogConstants.OK_ID).setEnabled(enable);
	}
	
    protected void buttonPressed(int id) {
    	Control selectedControl = m_folder.getSelection().getControl();
    	m_controls.get(selectedControl).buttonPressed(id);
    	super.buttonPressed(id);
    }

    protected void configureShell(Shell newShell) {
    	super.configureShell(newShell);
    	XtaDialog.configureShell(newShell, "Add assertion", 380, 120);    	
    }

    protected Control createDialogArea(Composite parent) {
	    Composite container = (Composite)super.createDialogArea(parent); 
	    XtaDialog.configureContainer(container);

	    m_folder = new CTabFolder(container, SWT.BORDER);
	    createControl("Loop", new LoopImpl(null));
	    createControl("Loop Path", new LoopPathImpl(null));
	    createControl("Loop Scope", new LoopScopeImpl(null));
	    createControl("Function Time", new FunctionTimeImpl(null));
	    createControl("Path Time", new PathTimeImpl(null));
	    createControl("Instruction Time", new InstructionTimeImpl(null));

    	return container;
    }
    
    protected void createButtonsForButtonBar(Composite parent) {
    	super.createButtonsForButtonBar(parent);
    	getButton(IDialogConstants.OK_ID).setEnabled(false);
    }    

    private void createControl(String title, XtaImpl impl) {
    	CTabItem item = new CTabItem(m_folder, SWT.NONE);
    	item.setText(title);		
    	Control control = impl.createDialogArea(m_folder, this);
    	item.setControl(control);
    	m_controls.put(control, impl);    	
    }    
}
