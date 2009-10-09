package com.xmos.xta.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Unknown.State;
import com.xmos.xta.ui.GuiEngine;

public class InstructionTime extends XtaDialog {
    private List<Node> m_nodes;    
    private boolean m_valueChanged;
    private List<Boolean> m_wasSet;
    private List<Long> m_originalCycles;
    private Text m_ns;
    private Text m_cycles;    
    private ModifyListener m_nsListener;
    private ModifyListener m_cyclesListener;

	public InstructionTime(Shell parent, List<Node> nodes) {
    	super(parent);
    	assert(nodes.size() > 0);
    	m_nodes = new ArrayList<Node>();
    	m_nodes.addAll(nodes);
    	initialise();
    }

	private void initialise() {
    	m_valueChanged = false;    	
    	m_wasSet = new ArrayList<Boolean>();
    	m_originalCycles = new ArrayList<Long>();
    	for (Node node : m_nodes) {
    		m_wasSet.add(node.getAttributes().getUnknowns().get(0).getState() == State.UNKNOWN ? false : true);
    		m_originalCycles.add(node.getAttributes().getInstructionCycles());
    	}    	
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

		Group nsGroup = createGroup(container, "ns", 1, 1);
		m_ns = new Text(nsGroup, SWT.SINGLE | SWT.CENTER);
		m_ns.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    
	    m_nsListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				try {
					for (Node node : m_nodes) {
						GuiEngine.getEngine().setInstructionNs(GuiEngine.s_guiCommandSrc, 
								node, Double.parseDouble(m_ns.getText()));
					}
					m_valueChanged = true;
					updateCyclesValue();
				} catch (NumberFormatException exception) {
					// Do nothing
				}
			}
		};

		Group cyclesGroup = createGroup(container, "cycles", 1, 1);
	    m_cycles = new Text(cyclesGroup, SWT.SINGLE | SWT.CENTER);
	    m_cycles.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    
	    m_cyclesListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				try {
					for (Node node : m_nodes) {
						GuiEngine.getEngine().setInstructionCycles(GuiEngine.s_guiCommandSrc, 
								node, Integer.parseInt(m_cycles.getText()));
					}
					m_valueChanged = true;
					updateNsValue();
				} catch (NumberFormatException exception) {
					// Do nothing
				}
			}
		};

		updateNsValue();
		updateCyclesValue();
    	return container;
    }

	private Group createGroup(Composite parent, String description, int numColumns, int span) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
	    GridLayout layout = new GridLayout();
	    layout.numColumns = numColumns;
	    group.setLayout(layout);
	    group.setText(description);
	    group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, span, 1));
	    return group;
	}

	private void updateNsValue() {
		m_ns.removeModifyListener(m_nsListener);
	    m_ns.setText(Double.toString(GuiEngine.getEngine().getInstructionNs(m_nodes.get(0))));
		m_ns.addModifyListener(m_nsListener);
	}			

	private void updateCyclesValue() {
		m_cycles.removeModifyListener(m_cyclesListener);
		m_cycles.setText(Long.toString(GuiEngine.getEngine().getInstructionCycles(m_nodes.get(0))));
		m_cycles.addModifyListener(m_cyclesListener);
	}			

    protected void buttonPressed(int id) {
    	if (id == IDialogConstants.CANCEL_ID) {
    		if (m_valueChanged) {
    			int index = 0;    			
    			for (Node node : m_nodes) {
    				GuiEngine.getEngine().setInstructionCycles(GuiEngine.s_guiCommandSrc, 
    						node, m_originalCycles.get(index));
    				
    				if (!m_wasSet.get(index)) {
    					GuiEngine.getEngine().unsetInstructionTime(GuiEngine.s_guiCommandSrc, node);
    				}
    				index++;
    			}
    		}
    	} else if (id == IDialogConstants.OK_ID) {
    		if (!m_valueChanged) {
	    		int index = 0;
	    		for (Node node : m_nodes) {
    				GuiEngine.getEngine().setInstructionCycles(GuiEngine.s_guiCommandSrc, 
    						node, m_originalCycles.get(index));
					index++;
	    		}
    		}
    	}
    	super.buttonPressed(id);
    }
}
