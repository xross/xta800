package com.xmos.xta.gui.dialogs;


import java.util.Set;

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

import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.GuiEngine;

public class Iterations extends XtaDialog {
	
	private long m_numIterations;
		
    public Iterations(Shell parent, Node node) {
    	super(parent);
    	
    	// TODO
    	//m_tail = tail;
    	//m_header = m_tail.getPathBlocks().get(0);
    	//try {
		//	m_numIterations = m_header.getLoops().getHardLoopIterations(m_tail);
		//} catch (Exception e) {
		//	m_numIterations = 0;
		//}
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

	    // TODO
		//createNumIterationsControl(container);

    	return container;
    }
// TODO    
/*
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
    		// Just use any pc that is unique to this particular loop path.
    		Set<Integer> pcs = m_route.getUniqueLoopPathPcs(m_tail);
    		assert(pcs.size() > 0);
    		
    		GuiEngine.getEngine().setLoopIterations(
    				m_route.getRouteId(), pcs.iterator().next(), m_numIterations);
    	}
    	super.buttonPressed(id);
    }
*/    
}
