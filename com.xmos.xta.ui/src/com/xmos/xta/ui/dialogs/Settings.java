package com.xmos.xta.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.ui.GuiEngine;

public class Settings extends Dialog {
    private Text m_coreMhzText;
    private Spinner m_threadSpinner;
    private Button m_caseButton;
  
    private double m_oldFrequency;
    private int m_oldNumThreads;
    
    public Settings(Shell parent) {
    	super(parent);
    	m_oldFrequency = getFrequency();
    	m_oldNumThreads = getNumThreads();
    	open();
    }

	public double getFrequency() {
		return GuiEngine.getEngine().getFrequency();
	}

	public void setFrequency(double frequency) {
		GuiEngine.getEngine().setFrequency(GuiEngine.s_guiSrc, frequency);
	}

	public int getNumThreads() {
		return GuiEngine.getEngine().getNumThreads();
	}

	public void setNumThreads(int numThreads) {
		GuiEngine.getEngine().setNumThreads(GuiEngine.s_guiSrc, numThreads);
	}	
    
    protected void configureShell(Shell newShell) {
    	super.configureShell(newShell);
    	XtaDialog.configureShell(newShell, "Xta Settings", 0, 0);
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
		Group freqGroup = XtaDialog.createGroup(controls, "Core Mhz", 1, 1);
		m_coreMhzText = new Text(freqGroup, SWT.BORDER | SWT.SINGLE);
		m_coreMhzText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		m_coreMhzText.setText(Double.toString(getFrequency()));
		m_coreMhzText.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				try {
					double frequency = Double.parseDouble(m_coreMhzText.getText());
					setFrequency(frequency);
					enable(true);
				} catch(Exception ex) {
					enable(false);
				}
			}
		});

		Group threadsGroup = XtaDialog.createGroup(controls, "Num Threads", 1, 1);
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

		Group caseGroup = XtaDialog.createGroup(controls, "Print worst case", 1, 1);
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
		GuiEngine.getEngine().setCase(GuiEngine.s_guiSrc, isWorstCase ? Case.WORST : Case.BEST);
	}

	private void enable(boolean enable) {
		Button okButton = getButton(IDialogConstants.OK_ID);
		if (okButton != null) {
			okButton.setEnabled(enable);
		}
	}
}

