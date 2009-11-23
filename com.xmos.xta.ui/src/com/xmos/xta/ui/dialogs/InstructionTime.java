package com.xmos.xta.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.ui.GuiEngine;

public class InstructionTime extends XtaDialog {
    public InstructionTime(Shell parent, List<Node> nodes) {
    	super(parent, new InstructionTimeImpl(nodes), "Instruction Time");
    }
}

class InstructionTimeImpl implements XtaImpl {
	private static final String SEPARATOR_CHAR = " ";
	
    private List<Node> m_nodes;    
    private Text m_value;
	private Combo m_units;
	private Button m_assert;
	private Text m_reference;
	private ButtonEnabler m_enabler;
	
	public InstructionTimeImpl(List<Node> nodes) {
    	if (nodes != null) {
    	    m_nodes = new ArrayList<Node>();
    	    m_nodes.addAll(nodes);
    	}
    }

    public Control createDialogArea(Composite parent, ButtonEnabler enabler) {
    	m_enabler = enabler;
    	
    	Composite container = new Composite(parent, SWT.NONE);    	
	    XtaDialog.configureContainer(container);

	    m_value = XtaDialog.createValueControl(container, this);	    
	    m_units = XtaDialog.createUnitsControl(container, this);
	    createAssertControls(container);	    

	    if (m_nodes != null) {
	        m_value.setText(GuiEngine.getEngine().getInstructionTime(m_nodes.get(0)).getValueStr());	
    	    m_units.setText(GuiEngine.getEngine().getInstructionTime(m_nodes.get(0)).getUnitsStr());
	    }
    	return container;
    }

    public void buttonPressed(int id) {
    	if (id == IDialogConstants.OK_ID) {
    		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
    		assert (timeUnit.isValid());
    		
    		if (m_nodes == null) {
				GuiEngine.getEngine().addInstructionTime(GuiEngine.s_guiSrc, m_reference.getText(), timeUnit);
    			
    		} else {
        		for (Node node : m_nodes) {
        			if (m_assert.getSelection()) {
        				GuiEngine.getEngine().addInstructionTime(GuiEngine.s_guiSrc, node.getReferenceStr(), timeUnit);
        			}
   				    GuiEngine.getEngine().setInstructionTime(GuiEngine.s_guiSrc, node, timeUnit);
        		}    			
    		}
    	}
    }    
	
    public void updateButtonStatus() {
		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
		if (timeUnit.isValid()) {
			m_enabler.enable(true);
		} else {
			m_enabler.enable(false);
		}    	

		boolean allValid = true;
		String[] references = m_reference.getText().split(SEPARATOR_CHAR);
		for (String reference : references) {
			Reference ref = ReferenceFactory.createReference(reference.trim(), ReferenceClass.ANY_REF);    	
			if (!ref.resolve(GuiEngine.getEngine())) {
				allValid = false;
				break;
			}			
		}			
		m_enabler.enable(allValid);
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
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.widthHint = 100;
		m_reference.setLayoutData(data);
		
		m_reference.setText("");
		
		if (m_nodes != null) {
			for (Node node : m_nodes) {
				if (m_reference.getText().length() > 0) {
					m_reference.append(SEPARATOR_CHAR);
				}				
			    m_reference.append(node.getReferenceStr());
			}
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
