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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.ui.GuiEngine;

public class Loop extends XtaDialog {
	private Node m_node;
	private long m_iterations;
	private int m_minIterations;
	private Button m_assert;	
	private Text m_reference;
	
    public Loop(Shell parent, Node node) {
    	super(parent);
    	m_node = node;
    	m_iterations = GuiEngine.getEngine().getSetLoopIterations(node);

    	Node loopNode = m_node.getEnclosingLoopNode();
		assert(loopNode != null);
		if (loopNode.isSelfLoopNode()) {
			m_minIterations = 1;
    	} else { 
			m_minIterations = 0;
    	}
    	open();
    }
    
    protected void configureShell(Shell newShell) {
    	super.configureShell(newShell);
    	configureShell(newShell, "Loop Iterations", 400, 150);
    }

    protected Control createDialogArea(Composite parent) {
    	Composite container = createContainer(parent);	
		createNumIterationsControl(container);
		createAssertControls(container);	
    	return container;
    }

    protected void buttonPressed(int id) {
    	if (id == IDialogConstants.OK_ID) {
    		if (m_assert.getSelection()) {
    			GuiEngine.getEngine().assertLoop(GuiEngine.s_guiCommandSrc, m_reference.getText(), m_iterations);
    		} else {
			    GuiEngine.getEngine().setLoopIterations(GuiEngine.s_guiCommandSrc, m_node, m_iterations);
    		}
    	}
    	super.buttonPressed(id);
    }
    
    void checkButtonStatus() {
    }
    
    private void createNumIterationsControl(Composite parent) {
    	Group group = createGroup(parent, "num iterations", 1, 2);
    	    	
    	final Spinner spinner = new Spinner(group, SWT.BORDER);
    	spinner.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));		
    	spinner.setMinimum(m_minIterations);
    	spinner.setMaximum(Integer.MAX_VALUE);
    	spinner.setEnabled(true);
    	spinner.setSelection((int)m_iterations);
    	spinner.setIncrement(1);
    	spinner.setPageIncrement(10);
    	spinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (spinner.getSelection() >= m_minIterations) {
					m_iterations = spinner.getSelection();
					getButton(IDialogConstants.OK_ID).setEnabled(true);
					
				} else {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}
			}
		});
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
