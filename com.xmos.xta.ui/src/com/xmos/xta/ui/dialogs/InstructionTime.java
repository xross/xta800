package com.xmos.xta.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.ui.GuiEngine;

public class InstructionTime extends XtaDialog {
    private List<Node> m_nodes;    
    private Text m_value;
	private Combo m_units;
	private Button m_assert;
	
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
	    Composite container = createContainer(parent);

	    m_value = createValueControl(container);
	    m_value.setText(GuiEngine.getEngine().getFixedTime(m_nodes.get(0)).getValueStr());	
	    
	    m_units = createUnitsControl(container);
	    m_units.setText(GuiEngine.getEngine().getFixedTime(m_nodes.get(0)).getUnitsStr());

	    m_assert = createAssertControl(container);
	    
    	return container;
    }

    protected void buttonPressed(int id) {
    	if (id == IDialogConstants.OK_ID) {
    		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
    		assert (timeUnit.isValid());
    		for (Node node : m_nodes) {
    			if (m_assert.getSelection()) {
    			    //TODO	
    			} else {
   				    GuiEngine.getEngine().setFixedTime(GuiEngine.s_guiCommandSrc, node, timeUnit, Type.INSTRUCTION_TIME);
    			}
    		}
    	}
    	super.buttonPressed(id);
    }
    
    void checkButtonStatus() {
		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
		if (timeUnit.isValid()) {
			getButton(IDialogConstants.OK_ID).setEnabled(true);
		} else {
			getButton(IDialogConstants.OK_ID).setEnabled(false);					
		}    	
    }    
}
