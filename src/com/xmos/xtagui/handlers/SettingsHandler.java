package com.xmos.xtagui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.handlers.HandlerUtil;

import com.xmos.xtagui.views.TreeView;

public class SettingsHandler extends AbstractHandler implements IHandler {
    public static final boolean DEBUG = false;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (DEBUG) System.out.printf("SettingsHandler: execute\n");		
		
		final TreeView treeView = (TreeView)HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(TreeView.ID);
		
		final Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();			
	    final Shell dialog = new Shell(shell, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
	    
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
	    
		return null;
	}
	
	private void createControlLabel(Composite parent, String str) {
		Label label = new Label(parent, SWT.NONE);		        
        label.setText(str);
        label.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}
	
	private void createExhustiveControl(Composite parent, final TreeView treeView) {
		createControlLabel(parent, "Exhustive:");
		final Button exhustive = new Button(parent, SWT.CHECK);
		exhustive.setSelection(treeView.getExhustive());		
		exhustive.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				treeView.setExhustive(exhustive.getSelection());
			}			
		});
	}	    
		
    private void createFNopsControl(Composite parent, final TreeView treeView) {
		createControlLabel(parent, "Show Fnops:");
		final Button fnops = new Button(parent, SWT.CHECK);
		fnops.setSelection(treeView.getFnops());		
		fnops.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				treeView.setFnops(fnops.getSelection());
			}			
		});
    }

    private void createFrequencyControl(Composite parent, final TreeView treeView) {
		createControlLabel(parent, "Frequency:");
		final Spinner frequency = new Spinner(parent, SWT.BORDER);
		frequency.setMinimum(0);
		frequency.setMaximum(1000);
		frequency.setSelection(treeView.getFrequency());
		frequency.setIncrement(1);
	    frequency.setPageIncrement(10);
	    frequency.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				treeView.setFrequency(frequency.getSelection());
			}			
		});
    }

    private void createNumThreadsControl(Composite parent, final TreeView treeView) {
		createControlLabel(parent, "Num Threads:");
		final Spinner numThreads = new Spinner(parent, SWT.BORDER);
		numThreads.setMinimum(0);
		numThreads.setMaximum(8);
		numThreads.setSelection(treeView.getNumThreads());
		numThreads.setIncrement(1);
		numThreads.setPageIncrement(8);
		numThreads.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				treeView.setNumThreads(numThreads.getSelection());
			}			
		});
    }	
}
