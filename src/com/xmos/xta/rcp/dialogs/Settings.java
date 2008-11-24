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

public class Settings {
    public Settings(final TreeView treeView) {
    	Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	    Shell dialog = new Shell(shell, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
	    
	    dialog.setText("Settings");
	    dialog.setSize(200, 200);
	    GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 2;
	    dialog.setLayout(gridLayout);
	    dialog.setBackground(dialog.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	    	    
		createExhustiveControl(dialog, treeView);
		createFNopsControl(dialog, treeView);
		createFrequencyControl(dialog, treeView);
		createNumThreadsControl(dialog, treeView);
		dialog.open();    	
    }

	private void createControlLabel(Composite parent, String str) {
		Label label = new Label(parent, SWT.NONE);		        
        label.setText(str);
        label.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}
	
	private void createExhustiveControl(Composite parent, final TreeView treeView) {
		createControlLabel(parent, "Exhustive:");
		final Button button = new Button(parent, SWT.CHECK);
		button.setSelection(treeView.getExhustive());		
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				treeView.setExhustive(button.getSelection());
			}			
		});
	}	    
		
    private void createFNopsControl(Composite parent, final TreeView treeView) {
		createControlLabel(parent, "Show Fnops:");
		final Button button = new Button(parent, SWT.CHECK);
		button.setSelection(treeView.getFnops());		
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				treeView.setFnops(button.getSelection());
			}			
		});
    }

    private void createFrequencyControl(Composite parent, final TreeView treeView) {
		createControlLabel(parent, "Frequency:");
		final Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setMinimum(0);
		spinner.setMaximum(1000);
		spinner.setSelection(treeView.getFrequency());
		spinner.setIncrement(1);
		spinner.setPageIncrement(10);
		spinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				treeView.setFrequency(spinner.getSelection());
			}			
		});
    }

    private void createNumThreadsControl(Composite parent, final TreeView treeView) {
		createControlLabel(parent, "Num Threads:");
		final Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setMinimum(0);
		spinner.setMaximum(8);
		spinner.setSelection(treeView.getNumThreads());
		spinner.setIncrement(1);
		spinner.setPageIncrement(8);
		spinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				treeView.setNumThreads(spinner.getSelection());
			}			
		});
    }	
}
