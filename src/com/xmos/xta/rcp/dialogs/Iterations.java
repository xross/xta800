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

import com.xmos.xta.rcp.views.TreeView;

public class Iterations {
	int m_id;
	int m_instanceId;
	
    public Iterations(final TreeView treeView, int id, int instanceId) {
    	m_id = id;
    	m_instanceId = instanceId;
    	
    	Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	    Shell dialog = new Shell(shell, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
	    
	    dialog.setText("Iterations");
	    dialog.setSize(200, 200);
	    GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 2;
	    dialog.setLayout(gridLayout);
	    dialog.setBackground(dialog.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	    	    
	    createAllInstancesControl(dialog, treeView);
		createAbsoluteCountControl(dialog, treeView);
		createNumIterationsControl(dialog, treeView, id, instanceId);
		dialog.open();    	
    }

	private void createControlLabel(Composite parent, String str) {
		Label label = new Label(parent, SWT.NONE);		        
        label.setText(str);
        label.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}
	
	private void createAllInstancesControl(Composite parent, final TreeView treeView) {
		createControlLabel(parent, "Apply to all instances:");
		final Button button = new Button(parent, SWT.CHECK);
		button.setSelection(treeView.getAllInstances());		
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				treeView.setAllInstances(button.getSelection());
			}			
		});
	}	    
		
    private void createAbsoluteCountControl(Composite parent, final TreeView treeView) {
		createControlLabel(parent, "Make count absolute:");
		final Button button = new Button(parent, SWT.CHECK);
		button.setSelection(treeView.getAbsoluteCount());		
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				treeView.setAbsoluteCount(button.getSelection());
			}			
		});
    }

    private void createNumIterationsControl(Composite parent, final TreeView treeView, int id, int instanceId) {
		createControlLabel(parent, "Num Iterations:");
		final Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setMinimum(0);
		spinner.setMaximum(1000);
		spinner.setSelection(treeView.getNumIterations());
		spinner.setIncrement(1);
		spinner.setPageIncrement(10);
		spinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				treeView.setNumIterations(spinner.getSelection(), m_id, m_instanceId);
			}			
		});
    }
}
