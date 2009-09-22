package com.xmos.xta.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.ui.GuiEngine;

public class Loop extends XtaDialog {
	private static String DEFAULT_MESSAGE = "Set the iteration count";
	private Node m_node;
	private long m_iterations;
	private int m_minIterations;

	private Label m_status;
	private Spinner m_spinner; 
			
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
    	configureShell(newShell, "Loop Iterations", 300, 150);
    }

    protected Control createDialogArea(Composite parent) {
    	Composite container = (Composite) super.createDialogArea(parent);

	    final GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 2;
	    container.setLayout(gridLayout);

		createStatusLine(container);
		createNumIterationsControl(container);
    	return container;
    }

    private void createStatusLine(Composite container) {
		m_status = new Label(container, SWT.BORDER);
		m_status.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,
				true, false, 2, 1));
		m_status.setText(DEFAULT_MESSAGE);
	}

    private void createNumIterationsControl(Composite parent) {
		m_spinner = new Spinner(parent, SWT.BORDER);
		m_spinner.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,
				true, false, 2, 1));
		
		m_spinner.setMaximum(Integer.MAX_VALUE);
		m_spinner.setMinimum(Integer.MIN_VALUE);
		m_spinner.setEnabled(true);		
		m_spinner.setSelection((int)m_iterations);
		m_spinner.setIncrement(1);
		m_spinner.setPageIncrement(10);
		m_spinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (m_spinner.getSelection() >= m_minIterations) {
					m_status.setText(DEFAULT_MESSAGE);
					m_iterations = m_spinner.getSelection();
					getButton(IDialogConstants.OK_ID).setEnabled(true);
					getButton(IDialogConstants.PROCEED_ID).setEnabled(true);
					
				} else {
					m_status.setText("Iterations must be >= " + Integer.toString(m_minIterations));
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					getButton(IDialogConstants.PROCEED_ID).setEnabled(false);
				}
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
			GuiEngine.getEngine().setLoopIterations(GuiEngine.s_guiCommandSrc, m_node, m_iterations);
    	}
    	super.buttonPressed(id);
    }
}
