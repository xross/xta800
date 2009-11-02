package com.xmos.xta.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.ui.GuiEngine;

public class InstructionTime extends XtaDialog {
    private List<Node> m_nodes;    
    private Text m_value;
	private Combo m_units;

	public InstructionTime(Shell parent, List<Node> nodes) {
    	super(parent);
    	assert(nodes.size() > 0);
    	m_nodes = new ArrayList<Node>();
    	m_nodes.addAll(nodes);
    	open();
    }

    protected void configureShell(Shell newShell) {
    	super.configureShell(newShell);
    	configureShell(newShell, "Set Instruction Time", 380,  180);
    }

    protected Control createDialogArea(Composite parent) {
    	Composite container = (Composite) super.createDialogArea(parent);

	    final GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 2;
	    container.setLayout(gridLayout);

	    createValueControl(container);
	    createUnitsControl(container);

    	return container;
    }

    protected void buttonPressed(int id) {
    	if (id == IDialogConstants.OK_ID) {
    		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
    		assert (timeUnit.isValid());
    		for (Node node : m_nodes) {
   				GuiEngine.getEngine().setFixedTime(GuiEngine.s_guiCommandSrc, node, timeUnit);
    		}
    	}
    	super.buttonPressed(id);
    }
    
    private void createValueControl(Composite container) {
		Group group = createGroup(container, "value", 1, 1);
		m_value = new Text(group, SWT.SINGLE | SWT.CENTER);
		m_value.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		m_value.setText(GuiEngine.getEngine().getFixedTime(m_nodes.get(0)).getValueStr());		
		m_value.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				checkButtonStatus();
			}
		});		    	
    }
    
    private void createUnitsControl(Composite container) {
		Group group = createGroup(container, "units", 1, 1);
		m_units = new Combo(group, SWT.SINGLE | SWT.CENTER);
		m_units.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		m_units.setItems(TimeUnit.getUnits());
		m_units.setText(GuiEngine.getEngine().getFixedTime(m_nodes.get(0)).getUnitsStr());
		m_units.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				checkButtonStatus();
			}
		});		    	
    }
        
    private void checkButtonStatus() {
		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
		if (timeUnit.isValid()) {
			getButton(IDialogConstants.OK_ID).setEnabled(true);
		} else {
			getButton(IDialogConstants.OK_ID).setEnabled(false);					
		}    	
    }    
}
