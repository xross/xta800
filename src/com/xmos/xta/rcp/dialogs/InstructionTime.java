package com.xmos.xta.rcp.dialogs;

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
import com.xmos.xta.engine.references.Reference;

public class InstructionTime extends XtaDialog {
    Engine m_engine;
    List<Reference> m_refs;
    
    boolean m_valueChanged;
    List<Integer> m_originalCycles;
	
    Text m_ns;
    Text m_cycles;
    
    ModifyListener m_nsListener;
    ModifyListener m_cyclesListener;
    
    public InstructionTime(Shell parent, final Engine engine, Reference codeReference) {
    	super(parent);
    	m_engine = engine;
    	m_refs = new ArrayList<Reference>();
    	m_refs.add(codeReference);

    	m_valueChanged = false;
    	m_originalCycles = new ArrayList<Integer>();
    	for (Reference ref : m_refs) {
    		m_originalCycles.add(m_engine.getInstructionNumCycles(ref.getPc()));
    	}
    	open();
    }

    public InstructionTime(Shell parent, final Engine engine, List<Reference> codeReferences) {
    	super(parent);
    	assert(codeReferences.size() > 0);
    	
    	m_engine = engine;
    	m_refs = codeReferences;

    	m_valueChanged = false;
    	m_originalCycles = new ArrayList<Integer>();
    	for (Reference ref : m_refs) {
    		m_originalCycles.add(m_engine.getInstructionNumCycles(ref.getPc()));
    	}
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
		String labelText = "";
		for (Reference ref : m_refs) {
			labelText += " " + ref.getRef();
		}
		label.setText(labelText);

		Group nsGroup = createGroup(container, "ns", 1, 1);
		m_ns = new Text(nsGroup, SWT.SINGLE | SWT.CENTER);
		m_ns.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    
	    m_nsListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				try {
					for (Reference ref : m_refs) {
						m_engine.setInstructionTimeTaken(ref, Double.parseDouble(m_ns.getText()));
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
					for (Reference ref : m_refs) {
						m_engine.setInstructionNumCycles(ref, Integer.parseInt(m_cycles.getText()));
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
		m_ns.setText(Double.toString(m_engine.getInstructionTimeTaken(m_refs.get(0).getPc())));
		m_ns.addModifyListener(m_nsListener);
	}			

	private void updateCyclesValue() {
		m_cycles.removeModifyListener(m_cyclesListener);
		m_cycles.setText(Integer.toString(m_engine.getInstructionNumCycles(m_refs.get(0).getPc())));
		m_cycles.addModifyListener(m_cyclesListener);
	}			

    protected void buttonPressed(int id) {
    	if (id == IDialogConstants.CANCEL_ID) {
    		if (m_valueChanged) {
    			int index = 0;
    			for (Reference ref : m_refs) {
    				m_engine.setInstructionNumCycles(ref, m_originalCycles.get(index));
    				m_engine.unsetInstructionTime(ref.getPc());
    				index++;
    			}
    		}
    	} else if (id == IDialogConstants.OK_ID) {
    		int index = 0;
    		for (Reference ref : m_refs) {
    			m_engine.setInstructionNumCycles(ref, m_originalCycles.get(index));
				index++;
    		}
    	}
    	super.buttonPressed(id);
    }
}
