package com.xmos.xta.gui.dialogs;

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

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.GuiEngine;

public class LoopPath extends XtaDialog {
	private Node m_node;
	private long m_iterations;
		
    public LoopPath(Shell parent, Node node) {
    	super(parent);
    	m_node = node;
    	m_iterations = GuiEngine.getEngine().getSetLoopPathIterations(node);
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

		createNumIterationsControl(container);
    	return container;
    }

	private void createControlLabel(Composite parent, String str) {
		Label label = new Label(parent, SWT.NONE);		        
	    label.setLayoutData(new GridData(GridData.END, GridData.CENTER,
	    		false, false));
        label.setText(str);
	}

    private void createNumIterationsControl(Composite parent) {
		createControlLabel(parent, "Num Iterations:");
		final Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,
				true, false, 2, 1));

		spinner.setMinimum(0);
		spinner.setMaximum(Integer.MAX_VALUE);		
		spinner.setEnabled(true);
		spinner.setSelection((int)m_iterations);
		spinner.setIncrement(1);
		spinner.setPageIncrement(10);
		spinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_iterations = spinner.getSelection();
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
    		if (!GuiEngine.getEngine().setLoopPathIterations(m_node, m_iterations)) {
    			GuiEngine.notify(Notifier.NOTIFY_ERROR, "Error: Failed to set loop path iterations");
    		} else {
        		super.buttonPressed(id);
     		}
    	} else {
    		super.buttonPressed(id);
    	}
    }
}
