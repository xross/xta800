package com.xmos.xta.rcp.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.PlatformUI;

import com.xmos.xta.rcp.nodes.PathNode;

public class Iterations {
	private int m_numIterations;
    private boolean m_allInstances;
    private boolean m_absoluteCount;
	
    public Iterations(final PathNode pathNode) {
    	m_numIterations = pathNode.getNumIterations();
    	m_allInstances = pathNode.getAllInstances();
    	m_absoluteCount = pathNode.getAbsoluteCount();
    	
    	Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	    Shell dialog = new Shell(shell, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
	    
	    dialog.setText("Iterations");
	    dialog.setSize(200, 200);
	    dialog.setLocation(Display.getCurrent().getCursorLocation());
	    GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 2;
	    dialog.setLayout(gridLayout);
	    dialog.setBackground(dialog.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	    	    
	    createAllInstancesControl(dialog, pathNode);
		createAbsoluteCountControl(dialog, pathNode);
		createNumIterationsControl(dialog, pathNode);
		createOkButton(dialog, pathNode);
		createApplyButton(dialog, pathNode);
		createCancelButton(dialog, pathNode);
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
				m_allInstances = button.getSelection();
			}			
		});
	}	    
		
    private void createAbsoluteCountControl(Composite parent, final PathNode pathNode) {
		createControlLabel(parent, "Make count absolute:");
		final Button button = new Button(parent, SWT.CHECK);
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
		spinner.setMinimum(0);
		spinner.setMaximum(1000);
		spinner.setSelection(m_numIterations);
		spinner.setIncrement(1);
		spinner.setPageIncrement(10);
		spinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_numIterations = spinner.getSelection();
			}			
		});
    }

    private void createOkButton(final Composite parent, final PathNode pathNode) {
		final Button button = new Button(parent, SWT.PUSH);
		button.setText("Ok");
		
		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				pathNode.setIterations(m_numIterations);
				pathNode.setAbsoluteCount(m_absoluteCount);
				pathNode.setAllInstances(m_allInstances);
				parent.dispose();
			}			
		});
	}	    

    private void createApplyButton(final Composite parent, final PathNode pathNode) {
		final Button button = new Button(parent, SWT.PUSH);
		button.setText("Apply");
		
		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				pathNode.setIterations(m_numIterations);
				pathNode.setAbsoluteCount(m_absoluteCount);
				pathNode.setAllInstances(m_allInstances);
			}			
		});
	}	    

    private void createCancelButton(final Composite parent, final PathNode pathNode) {
		final Button button = new Button(parent, SWT.PUSH);
		button.setText("Cancel");
		
		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				parent.dispose();
			}			
		});
	}	    
}
