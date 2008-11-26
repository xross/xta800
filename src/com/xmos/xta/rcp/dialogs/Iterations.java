package com.xmos.xta.rcp.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.PlatformUI;

import com.xmos.xta.rcp.XtaEngine;
import com.xmos.xta.rcp.nodes.PathNode;
import com.xmos.xta.rcp.views.TreeView;

public class Iterations {
	int m_id;
	int m_instanceId;
	
    public Iterations(final PathNode pathNode) {
    	m_id = pathNode.getId();
    	m_instanceId = pathNode.getInstanceId();
    	
    	Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	    Shell dialog = new Shell(shell, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
	    
	    dialog.setText("Iterations");
	    dialog.setSize(200, 200);
	    GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 2;
	    dialog.setLayout(gridLayout);
	    dialog.setBackground(dialog.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	    	    
	    createAllInstancesControl(dialog, pathNode);
		createAbsoluteCountControl(dialog, pathNode);
		createNumIterationsControl(dialog, pathNode);
		dialog.open();    	
    }

	private void createControlLabel(Composite parent, String str) {
		Label label = new Label(parent, SWT.NONE);		        
        label.setText(str);
        label.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}
	
	private void createAllInstancesControl(Composite parent, final PathNode pathNode) {
		createControlLabel(parent, "Apply to all instances:");
		final Button button = new Button(parent, SWT.CHECK);
		button.setSelection(pathNode.getAllInstances());		
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				pathNode.setAllInstances(button.getSelection());
			}			
		});
	}	    
		
    private void createAbsoluteCountControl(Composite parent, final PathNode pathNode) {
		createControlLabel(parent, "Make count absolute:");
		final Button button = new Button(parent, SWT.CHECK);
		button.setSelection(pathNode.getAbsoluteCount());		
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				pathNode.setAbsoluteCount(button.getSelection());
			}			
		});
    }

    private void createNumIterationsControl(Composite parent, final PathNode pathNode) {
		createControlLabel(parent, "Num Iterations:");
		final Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setMinimum(0);
		spinner.setMaximum(1000);
		spinner.setSelection(pathNode.getNumIterations());
		spinner.setIncrement(1);
		spinner.setPageIncrement(10);
		spinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int numIterations = spinner.getSelection();
				pathNode.setIterations(numIterations);
			}			
		});
    }
}
