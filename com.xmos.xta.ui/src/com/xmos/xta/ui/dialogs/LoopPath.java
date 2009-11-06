package com.xmos.xta.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.ui.GuiEngine;

public class LoopPath extends XtaDialog {
    public LoopPath(Shell parent, Node node) {
    	super(parent, new LoopPathImpl(node), "Loop Path Iterations");
    }
}

class LoopPathImpl implements XtaImpl {
	private Node m_node;
	private long m_iterations;
	private int m_minIterations;
	private Button m_assert;	
	private Text m_reference;
	private ButtonEnabler m_enabler;
	
	public LoopPathImpl(Node node) {
	   	m_node = node;	   	
	   	
    	if (m_node != null) {
        	m_iterations = GuiEngine.getEngine().getSetLoopPath(node);	    	
        	Node loopNode = m_node.getEnclosingLoopNode();
    		assert(loopNode != null);
    		if (loopNode.isSelfLoopNode()) {
    			m_minIterations = 1;
        	} else { 
    			m_minIterations = 0;
        	}    		
    	} else {
    		m_iterations = 0;
    		m_minIterations = 0;
    	}
    }
	
    public Control createDialogArea(Composite parent, ButtonEnabler enabler) {
    	m_enabler = enabler;
    	
    	Composite container = new Composite(parent, SWT.NONE);    	
    	XtaDialog.configureContainer(container);
		createNumIterationsControl(container);
		createAssertControls(container);	
    	return container;
    }
	
    public void buttonPressed(int id) {
    	if (id == IDialogConstants.OK_ID) {  
    		if (m_assert.getSelection() || (m_node == null)) {
    		    GuiEngine.getEngine().addLoopPath(GuiEngine.s_guiCommandSrc, m_reference.getText(), m_iterations);
    		} else {
    		    GuiEngine.getEngine().setLoopPath(GuiEngine.s_guiCommandSrc, m_node, m_iterations);
    		}
    	}
    }
	    
	public void updateButtonStatus() {
		Reference ref = ReferenceFactory.createReference(m_reference.getText(), ReferenceClass.ANY_REF);    	
		if (ref.resolve(GuiEngine.getEngine())) {
			m_enabler.enable(true);
		} else {
			m_enabler.enable(false);
		}    	
	}
    
    private void createNumIterationsControl(Composite parent) {
       	Group group = XtaDialog.createGroup(parent, "num iterations", 1, 2);

		final Spinner spinner = new Spinner(group, SWT.BORDER);
		spinner.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		spinner.setMinimum(m_minIterations);
		spinner.setMaximum(Integer.MAX_VALUE);		
		spinner.setEnabled(true);
		spinner.setSelection((int)m_iterations);
		spinner.setIncrement(1);
		spinner.setPageIncrement(10);
		spinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (spinner.getSelection() >= m_minIterations) {
					m_iterations = spinner.getSelection();
					m_enabler.enable(true);
					
				} else {
					m_enabler.enable(false);
				}
			}			
		});
    }
	
	private void createAssertControls(Composite container) {
		Group group = XtaDialog.createGroup(container, "set as assertion", 2, 2);
		
		m_assert = new Button(group, SWT.CHECK);
		m_assert.setSelection(false);
		m_assert.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_reference.setEnabled(m_assert.getSelection());
			}			
		});
		
		m_reference = new Text(group, SWT.BORDER);
		m_reference.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));		
		
		if (m_node != null) {
		    m_reference.setText(m_node.getReferenceStr());
			m_reference.setEnabled(false);
			m_reference.setEditable(false);
		} else {
			m_assert.setSelection(true);
			m_assert.setEnabled(false);
			m_reference.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					updateButtonStatus();
				}				
			});
		}
	}    
}
