package com.xmos.xta.rcp.dialogs;

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
import org.eclipse.swt.widgets.Spinner;

import com.xmos.xta.rcp.nodes.PathNode;

public class Iterations extends XtaDialog {
	
	private long m_numIterations;
    private boolean m_absoluteCount;
    
    PathNode m_pathNode;
	
    public Iterations(Shell parent, final PathNode pathNode) {
    	super(parent);
    	m_pathNode = pathNode;
    	m_numIterations = pathNode.getNumIterations();
    	m_absoluteCount = pathNode.getAbsoluteCount();
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

		createAbsoluteCountControl(container, m_pathNode);
		createNumIterationsControl(container, m_pathNode);

    	return container;
    }

	private void createControlLabel(Composite parent, String str) {
		Label label = new Label(parent, SWT.NONE);		        
	    label.setLayoutData(new GridData(GridData.END, GridData.CENTER,
	    		false, false));
        label.setText(str);
	}
	
    private void createAbsoluteCountControl(Composite parent, final PathNode pathNode) {
		createControlLabel(parent, "Make count absolute:");
		final Button button = new Button(parent, SWT.CHECK);
		button.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,
				true, false, 2, 1));
		button.setSelection(pathNode.getAbsoluteCount());		
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_absoluteCount = button.getSelection();
			}			
		});
    }

    private void createNumIterationsControl(Composite parent, final PathNode pathNode) {
		createControlLabel(parent, "Num Iterations:");
		final Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,
				true, false, 2, 1));
		spinner.setMinimum(0);
		spinner.setMaximum(Integer.MAX_VALUE);
		//TODO - fix this
		spinner.setSelection((int) m_numIterations);
		spinner.setIncrement(1);
		spinner.setPageIncrement(10);
		spinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_numIterations = spinner.getSelection();
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
			m_pathNode.setIterations(m_numIterations);
			m_pathNode.setAbsoluteCount(m_absoluteCount);
    	}
    	super.buttonPressed(id);
    }
}
