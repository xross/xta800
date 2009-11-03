package com.xmos.xta.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.ui.GuiEngine;

public class Settings extends XtaDialog {
    private Spinner m_coreMhzSpinner;
    private Spinner m_threadSpinner;
    private Button m_caseButton;
  
    private int m_oldFrequency;
    private int m_oldNumThreads;
    
    public Settings(Shell parent) {
    	super(parent);
    	m_oldFrequency = getFrequency();
    	m_oldNumThreads = getNumThreads();
    	open();
    }

	public int getFrequency() {
		return GuiEngine.getEngine().getFrequency();
	}

	public void setFrequency(int frequency) {
		GuiEngine.getEngine().setFrequency(GuiEngine.s_guiCommandSrc, frequency);
	}

	public int getNumThreads() {
		return GuiEngine.getEngine().getNumThreads();
	}

	public void setNumThreads(int numThreads) {
		GuiEngine.getEngine().setNumThreads(GuiEngine.s_guiCommandSrc, numThreads);
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
    
    void checkButtonStatus() {
    }    
    
	private void createSettingsBar(Composite controls) {
		Group freqGroup = createGroup(controls, "Core Mhz", 1, 1);
	    m_coreMhzSpinner = new Spinner(freqGroup, SWT.BORDER);
		m_coreMhzSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		m_coreMhzSpinner.setMinimum(1);
		m_coreMhzSpinner.setMaximum(Integer.MAX_VALUE);
		m_coreMhzSpinner.setSelection(getFrequency());
		m_coreMhzSpinner.setIncrement(1);
		m_coreMhzSpinner.setPageIncrement(10);
		m_coreMhzSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setFrequency(m_coreMhzSpinner.getSelection());
			}			
		});

		Group threadsGroup = createGroup(controls, "Num Threads", 1, 1);
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

		Group caseGroup = createGroup(controls, "Print worst case", 1, 1);
		m_caseButton = new Button(caseGroup, SWT.CHECK);
		m_caseButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		m_caseButton.setSelection(isWorstCase());
		m_caseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setCase(m_caseButton.getSelection());
			}
		});
	}
		
	private boolean isWorstCase() {
		return GuiEngine.getEngine().getCase().equals(Case.WORST);
	}

	private void setCase(boolean isWorstCase) {
		GuiEngine.getEngine().setCase(GuiEngine.s_guiCommandSrc, isWorstCase ? Case.WORST : Case.BEST);
	}
}

