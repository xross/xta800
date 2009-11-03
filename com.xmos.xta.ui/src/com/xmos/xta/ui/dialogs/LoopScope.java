package com.xmos.xta.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.ui.GuiEngine;

public class LoopScope extends XtaDialog {
	private Node m_node;
	private boolean m_scope;
	private Button m_assert;	
	private Text m_reference;
	
    public LoopScope(Shell parent, Node node) {
    	super(parent);
    	m_node = node;
    	m_scope = GuiEngine.getEngine().getSetLoopScope(node);
    	open();
    }

    protected void configureShell(Shell newShell) {
    	super.configureShell(newShell);
    	configureShell(newShell, "Loop Scope", 380, 150);
    }

    protected Control createDialogArea(Composite parent) {
	    Composite container = createContainer(parent);	    
		createAbsoluteCountControl(container);
		createAssertControls(container);	
    	return container;
    }

    protected void buttonPressed(int id) {
    	if (id == IDialogConstants.OK_ID) {
    		if (m_assert.getSelection()) {
    		    GuiEngine.getEngine().assertLoopScope(GuiEngine.s_guiCommandSrc, m_reference.getText(), m_scope);
    		} else {
    		    GuiEngine.getEngine().setLoopScope(GuiEngine.s_guiCommandSrc, m_node, m_scope);
    		}
    	}
    	super.buttonPressed(id);
    } 
    
    void checkButtonStatus() {
    }
    
    private void createAbsoluteCountControl(Composite parent) {
    	Group group = createGroup(parent, null, 2, 2);
    	
		final Button button = new Button(group, SWT.CHECK);
		button.setSelection(m_scope);		
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_scope = button.getSelection();
			}			
		});
		
		Label label = new Label(group, SWT.NONE);
		label.setText("make count absolute");
    }
    
	private void createAssertControls(Composite container) {
		Group group = createGroup(container, "set as assertion", 2, 2);
		
		m_assert = new Button(group, SWT.CHECK);
		m_assert.setSelection(false);
		m_assert.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_reference.setEnabled(m_assert.getSelection());
			}			
		});
		
		m_reference = new Text(group, SWT.BORDER);
		m_reference.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));		
		m_reference.setText(m_node.getReferenceStr());
		m_reference.setEnabled(false);
		m_reference.setEditable(false);
	}    
}
