package com.xmos.xta.gui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.GuiEngine;

public class LoopScope extends XtaDialog {
	private Node m_node;
	private boolean m_scope;

    public LoopScope(Shell parent, Node node) {
    	super(parent);
    	m_node = node;
    	m_scope = GuiEngine.getEngine().getSetLoopScope(node);
    	open();
    }

    protected void configureShell(Shell newShell) {
    	super.configureShell(newShell);
    	configureShell(newShell, "Iterations", 300, 150);
    }

    protected Control createDialogArea(Composite parent) {
    	Composite container = (Composite) super.createDialogArea(parent);
    	
	    final GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 3;
	    container.setLayout(gridLayout);
	    
		createAbsoluteCountControl(container);
    	return container;
    }

	private void createControlLabel(Composite parent, String str) {
		Label label = new Label(parent, SWT.NONE);		        
	    label.setLayoutData(new GridData(GridData.END, GridData.CENTER,
	    		false, false));
        label.setText(str);
	}

    private void createAbsoluteCountControl(Composite parent) {
		createControlLabel(parent, "Make count absolute:");
		final Button button = new Button(parent, SWT.CHECK);
		button.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,
				true, false, 2, 1));
		button.setSelection(m_scope);		
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_scope = button.getSelection();
			}			
		});
    }
    
    protected void createButtonsForButtonBar(Composite parent) {
    	createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    	createButton(parent, IDialogConstants.PROCEED_ID, "Apply", false);
    	createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }
    
    protected void buttonPressed(int id) {
    	if ((id == IDialogConstants.OK_ID) || (id == IDialogConstants.PROCEED_ID)) {
    		GuiEngine.getEngine().setLoopScope(GuiEngine.s_guiCommandSrc, m_node, m_scope);  
    	}
    	super.buttonPressed(id);
    } 
}
