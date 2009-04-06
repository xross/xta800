package com.xmos.xta.rcp.dialogs;


import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import com.xmos.xta.engine.LoopAttachment;
import com.xmos.xta.engine.PathBasicBlock;
import com.xmos.xta.rcp.XtaEngine;

public class Iterations extends XtaDialog {
	
	private long m_numIterations;
	private int m_routeId;
	
	
	PathBasicBlock m_tail;
	PathBasicBlock m_header;
	
    public Iterations(Shell parent, final PathBasicBlock tail, int routeId) {
    	super(parent);
    	m_routeId = routeId;
    	
    	m_tail = tail;
    	m_header = m_tail.getPathBlocks().get(0);
    	try {
			m_numIterations = m_header.getLoops().getHardLoopIterations(m_tail);
		} catch (Exception e) {
			m_numIterations = 0;
		}
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

		createNumIterationsControl(container);

    	return container;
    }

	private void createControlLabel(Composite parent, String str) {
		Label label = new Label(parent, SWT.NONE);		        
	    label.setLayoutData(new GridData(GridData.END, GridData.CENTER,
	    		false, false));
        label.setText(str);
	}

    private void createNumIterationsControl(Composite parent) {
		createControlLabel(parent, "Num Iterations:");
		final Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,
				true, false, 2, 1));
		spinner.setMinimum(0);
		
		long total = 0;
		for(LoopAttachment loop : m_header.getLoops().getSubLoops()){
			try {
				if(loop.getLoop() != m_tail)
					total+=m_header.getLoops().getHardLoopIterations(loop.getLoop());
			} catch (Exception e1) {
			}
		}
		long loopmax;
		boolean unset = false;
		try {
			loopmax = m_header.getLoops().getLoopMax(m_tail);
		} catch (Exception e1) {
			loopmax = 0;
			unset = true;
		}
		if(loopmax - total == 0 && !unset){
			spinner.setEnabled(false);
		} else {
			spinner.setEnabled(true);
		}
		spinner.setMaximum((int) (loopmax - total));
		
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
    		int pc = m_tail.getInstructionTrimmed().getFirst().getPc();			
    		XtaEngine.getEngine().setLoopIterations(m_routeId, pc, m_numIterations);
    	}
    	super.buttonPressed(id);
    }
}
