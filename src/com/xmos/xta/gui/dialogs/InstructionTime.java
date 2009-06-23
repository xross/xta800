package com.xmos.xta.gui.dialogs;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.gui.GuiEngine;

public class InstructionTime extends XtaDialog {
    List<Node> m_nodes;    
    boolean m_valueChanged;
    Text m_ns;
    Text m_cycles;    
    ModifyListener m_nsListener;
    ModifyListener m_cyclesListener;
    
    public InstructionTime(Shell parent, Node node) {
    	super(parent);
    	m_nodes = new ArrayList<Node>();
    	m_nodes.add(node);
    	initialise();
    }

	public InstructionTime(Shell parent, List<Node> nodes) {
    	super(parent);
    	assert(nodes.size() > 0);
    	// Create a copy since the original list can be emptied by this dialog
    	m_nodes = new ArrayList<Node>();
    	m_nodes.addAll(nodes);
    	initialise();
    }

	private void initialise() {
    	m_valueChanged = false;
    	open();
	}

    protected void configureShell(Shell newShell) {
    	super.configureShell(newShell);
    	configureShell(newShell, "Set Instruction Time", 250,  180);
    }

    protected Control createDialogArea(Composite parent) {
    	Composite container = (Composite) super.createDialogArea(parent);

	    final GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 2;
	    container.setLayout(gridLayout);

		Group engineGroup = createGroup(container, "PC", 1, 2);
		Label label = new Label(engineGroup, SWT.SHADOW_NONE | SWT.CENTER);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		String labelText = "Hello";
/* TODO		for (Reference ref : m_refs) {
			labelText += " " + ref.getRef();
		}*/
		label.setText(labelText);

		Group nsGroup = createGroup(container, "ns", 1, 1);
		m_ns = new Text(nsGroup, SWT.SINGLE | SWT.CENTER);
		m_ns.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    
	    m_nsListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				try {
					for (Node node : m_nodes) {
						GuiEngine.getEngine().setInstructionTimeTaken(node, Double.parseDouble(m_ns.getText()));
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
						GuiEngine.getEngine().setInstructionNumCycles(node, Integer.parseInt(m_cycles.getText()));
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
		// TODO m_ns.setText(Double.toString(GuiEngine.getEngine().getInstructionTimeTaken(m_nodes.get(0))));
		m_ns.addModifyListener(m_nsListener);
	}			

	private void updateCyclesValue() {
		m_cycles.removeModifyListener(m_cyclesListener);
		// TODO m_cycles.setText(Long.toString(GuiEngine.getEngine().getInstructionNumCycles(m_nodes.get(0))));
		m_cycles.addModifyListener(m_cyclesListener);
	}			

    protected void buttonPressed(int id) {
    	if (id == IDialogConstants.CANCEL_ID) {
    		if (m_valueChanged) {
    			int index = 0;
/* TODO    			
    			for (Node node : m_nodes) {
    				int selectedRouteId = GuiEngine.getEngine().getSelectedRouteId();
    				GuiEngine.getEngine().setInstructionNumCycles(
    						selectedRouteId, node, m_originalCycles.get(index));
    				
    				if (!m_wasSet.get(index)) {
    					GuiEngine.getEngine().unsetInstructionTime(
    							selectedRouteId, ref.getRef());
    				}
    				index++;
    			} */
    		}
    	} else if (id == IDialogConstants.OK_ID) {
    		if (!m_valueChanged) {
	    		int index = 0;
/* TODO	    		
	    		for (Reference ref : m_refs) {
    				int selectedRouteId = GuiEngine.getEngine().getSelectedRouteId();
    				GuiEngine.getEngine().setInstructionNumCycles(
    						selectedRouteId, ref.getRef(), m_originalCycles.get(index));
					index++;
	    		}*/
    		}
    	}
    	super.buttonPressed(id);
    }
}
