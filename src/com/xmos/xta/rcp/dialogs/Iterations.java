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
    public Iterations(final TreeView treeView) {    	
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
		createNumIterationsControl(dialog, treeView);
		dialog.open();    	
    }

	private void createControlLabel(Composite parent, String str) {
		Label label = new Label(parent, SWT.NONE);		        
        label.setText(str);
        label.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}
	
	private void createAllInstancesControl(Composite parent, final TreeView treeView) {
		createControlLabel(parent, "Apply to all instances:");
		final Button exhustive = new Button(parent, SWT.CHECK);
		exhustive.setSelection(treeView.getExhustive());		
		exhustive.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				treeView.setAllInstances(exhustive.getSelection());
			}			
		});
	}	    
		
    private void createAbsoluteCountControl(Composite parent, final TreeView treeView) {
		createControlLabel(parent, "Make count absolute:");
		final Button fnops = new Button(parent, SWT.CHECK);
		fnops.setSelection(treeView.getFnops());		
		fnops.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				treeView.setAbsoluteCount(fnops.getSelection());
			}			
		});
    }

    private void createNumIterationsControl(Composite parent, final TreeView treeView) {
		createControlLabel(parent, "Num Iterations:");
		final Spinner frequency = new Spinner(parent, SWT.BORDER);
		frequency.setMinimum(0);
		frequency.setMaximum(1000);
		frequency.setSelection(treeView.getFrequency());
		frequency.setIncrement(1);
	    frequency.setPageIncrement(10);
	    frequency.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				treeView.setNumIterations(frequency.getSelection());
			}			
		});
    }
}
