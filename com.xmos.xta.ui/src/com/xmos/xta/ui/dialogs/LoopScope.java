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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.ui.GuiEngine;

public class LoopScope extends XtaDialog {
    public LoopScope(Shell parent, Node node) {
    	super(parent, new LoopScopeImpl(node), "Loop Scope");
    }
}

class LoopScopeImpl implements XtaImpl {
	private Node m_node;
	private boolean m_scope;
	private Button m_assert;	
	private Text m_reference;
	private ButtonEnabler m_enabler;
	
    public LoopScopeImpl(Node node) {
    	m_node = node;    	
    	
    	if (m_node != null) {
    	    m_scope = GuiEngine.getEngine().getSetLoopScope(node);
    	}
    }

    public Control createDialogArea(Composite parent, ButtonEnabler enabler) {
    	m_enabler = enabler;
    	
    	Composite container = new Composite(parent, SWT.NONE);    	
    	XtaDialog.configureContainer(container);
		createAbsoluteCountControl(container);
		createAssertControls(container);	
    	return container;
    }

    public void buttonPressed(int id) {
    	if (id == IDialogConstants.OK_ID) {
    		if (m_assert.getSelection() || (m_node == null)) {
    		    GuiEngine.getEngine().addLoopScope(GuiEngine.s_guiCommandSrc, m_reference.getText(), m_scope);
    		} else {
    		    GuiEngine.getEngine().setLoopScope(GuiEngine.s_guiCommandSrc, m_node, m_scope);
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
	    
    private void createAbsoluteCountControl(Composite parent) {
    	Group group = XtaDialog.createGroup(parent, null, 2, 2);
    	
		final Button button = new Button(group, SWT.CHECK);
		button.setSelection(m_scope);		
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_scope = button.getSelection();
				updateButtonStatus();
			}			
		});
		
		Label label = new Label(group, SWT.NONE);
		label.setText("make count absolute");
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
