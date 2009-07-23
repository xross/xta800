package com.xmos.xta.gui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import com.xmos.xta.gui.GuiEngine;

public class Settings extends XtaDialog {
    private Spinner m_freqSpinner;
    private Spinner m_threadSpinner;
  
    private int m_oldFrequency;
    private int m_oldNumThreads;
    
    public Settings(Shell parent) {
    	super(parent);
    	m_oldFrequency = getFrequency();
    	m_oldNumThreads = getNumThreads();
    	open();
    }

    protected void configureShell(Shell newShell) {
    	super.configureShell(newShell);
    	configureShell(newShell, "Xta Settings", 0, 0);
    }

    protected Control createDialogArea(Composite parent) {
    	Composite container = (Composite) super.createDialogArea(parent);

		Composite controls = new Composite(parent, SWT.NONE);
	    GridLayout layout = new GridLayout();
	    layout.numColumns = 2;
	    controls.setLayout(layout);
	    controls.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
	    createSettingsBar(controls);

    	return container;
    }

    protected void buttonPressed(int id) {
    	if (id == IDialogConstants.CANCEL_ID) {
    		setFrequency(m_oldFrequency);
    		setNumThreads(m_oldNumThreads);
    	}
    	super.buttonPressed(id);
    }
    
	private void createSettingsBar(Composite controls) {
		Group freqGroup = createGroup(controls, "Frequency", 1, 1);
	    m_freqSpinner = new Spinner(freqGroup, SWT.BORDER);
		m_freqSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		m_freqSpinner.setMinimum(1);
		m_freqSpinner.setMaximum(Integer.MAX_VALUE);
		m_freqSpinner.setSelection(getFrequency());
		m_freqSpinner.setIncrement(1);
		m_freqSpinner.setPageIncrement(10);
		m_freqSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setFrequency(m_freqSpinner.getSelection());
			}			
		});

		Group threadsGroup = createGroup(controls, "Num threads", 1, 1);
		m_threadSpinner = new Spinner(threadsGroup, SWT.BORDER);
		m_threadSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		m_threadSpinner.setMinimum(1);
		m_threadSpinner.setMaximum(8);
		m_threadSpinner.setSelection(getNumThreads());
		m_threadSpinner.setIncrement(1);
		m_threadSpinner.setPageIncrement(4);
		m_threadSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setNumThreads(m_threadSpinner.getSelection());
			}			
		});
	}
	
	private Group createGroup(Composite parent, String description, int numColumns, int span) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
	    GridLayout layout = new GridLayout();
	    layout.numColumns = numColumns;
	    group.setLayout(layout);
	    group.setText(description);
	    group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, span, 1));
	    return group;
	}	
	
	public int getFrequency() {
		return GuiEngine.getEngine().getFrequency();
	}

	public void setFrequency(int frequency) {
		GuiEngine.getEngine().setFrequency(frequency);
	}

	public int getNumThreads() {
		return GuiEngine.getEngine().getNumThreads();
	}

	public void setNumThreads(int numThreads) {
		GuiEngine.getEngine().setNumThreads(numThreads);
	}	
}
