package com.xmos.xta.ui.dialogs;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.ui.GuiEngine;

public class InstructionTime extends XtaDialog {
    public InstructionTime(Shell parent, Collection<Node> nodes) {
    	super(parent, new InstructionTimeImplNode(nodes), "Instruction Time");
    }

    public InstructionTime(Shell parent, Route route) {
    	super(parent, new InstructionTimeImplRoute(route), "Instruction Time");
    }
}

abstract class InstructionTimeImpl implements XtaImpl {
    private Text m_value;
	private Combo m_units;
	private ButtonEnabler m_enabler;
	private Button m_define;

	protected Text m_references;
    protected Route m_route;
    protected Collection<Node> m_nodes;

	public boolean doAddDefine() {
		return m_define.getSelection();
    }

    public Composite createDialogArea(Composite parent, ButtonEnabler enabler, String valueStr, String unitsStr) {
    	m_enabler = enabler;
    	
    	Composite container = new Composite(parent, SWT.NONE);    	
	    XtaDialog.configureContainer(container);

	    m_references = XtaDialog.createReference(container, this);
	    m_value = XtaDialog.createValueControl(container, this, valueStr);	    
	    m_units = XtaDialog.createUnitsControl(container, this, unitsStr);
	    if (m_nodes != null || m_route != null) {
	    	m_define = XtaDialog.createDefineControl(container);	    
	    }

    	return container;
    }

    public void buttonPressed(int id) {
    	if (id == IDialogConstants.OK_ID) {
    		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
    		assert(timeUnit.isValid());

    		if (doAddDefine()) {
    			for (String reference : XtaDialog.getReferences(m_references.getText())) {
    				GuiEngine.getEngine().addInstructionTime(GuiEngine.s_guiSrc, reference, timeUnit);
    			}
    		}
    		if (m_nodes != null) {
        		for (Node node : m_nodes) {
   				    GuiEngine.getEngine().setInstructionTime(GuiEngine.s_guiSrc, node, timeUnit);
        		}    			
    		}
    		if (m_route != null) {
    			for (String reference : XtaDialog.getReferences(m_references.getText())) {
    				GuiEngine.getEngine().setInstructionTime(GuiEngine.s_guiSrc, m_route.getRouteId(), reference, timeUnit);
    			}
    		}    			
    	}
    }    
	
    public void updateButtonStatus() {
		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
		if (timeUnit.isValid()) {
			m_enabler.enable(XtaDialog.checkReferences(m_references.getText()));
		} else {
			m_enabler.enable(false);
		}    	
    }    
}

class InstructionTimeImplNode extends InstructionTimeImpl {
    public InstructionTimeImplNode(Collection<Node> nodes) {
    	if (nodes != null) {
    	    m_nodes = new ArrayList<Node>();
    	    m_nodes.addAll(nodes);
    	}
    }

    public Control createDialogArea(Composite parent, ButtonEnabler enabler) {
    	Node firstNode = m_nodes.iterator().next();
    	String valueStr = GuiEngine.getEngine().getInstructionTime(firstNode).getValueStr();	
	    String unitsStr = GuiEngine.getEngine().getInstructionTime(firstNode).getUnitsStr();
	    
		Composite container = createDialogArea(parent, enabler, valueStr, unitsStr);
		m_references.setText(XtaDialog.getReferencesText(m_nodes));
		m_references.setEditable(false);
    	return container;
    }
}

class InstructionTimeImplRoute extends InstructionTimeImpl {
    public InstructionTimeImplRoute(Route route) {
    	m_route = route;
    }

    public Control createDialogArea(Composite parent, ButtonEnabler enabler) {
		return createDialogArea(parent, enabler, "", "");
    }
}

class InstructionTimeImplDefine extends InstructionTimeImpl {
    public Control createDialogArea(Composite parent, ButtonEnabler enabler) {
    	return createDialogArea(parent, enabler, "", "");
    }
	
	public boolean doAddDefine() {
		return true;
	}
}
