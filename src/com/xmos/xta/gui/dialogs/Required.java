package com.xmos.xta.gui.dialogs;

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

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.routes.RouteTiming;
import com.xmos.xta.gui.GuiEngine;

public class Required extends XtaDialog {
	Engine m_engine;
    Route m_route;
    RouteTiming m_routeTiming;
    int m_routeId;
    
    boolean m_valueChanged;
    boolean m_timingWasSet;
    double m_originalValue;
	
    Text m_ns;
    Text m_cycles;
    Text m_mhz;
    
    ModifyListener m_nsListener;
    ModifyListener m_cyclesListener;
    ModifyListener m_mhzListener;
    
    public Required(Shell parent, Engine engine, Route route) {
    	super(parent);
    	m_engine = engine;
    	m_route = route;
    	m_routeId = m_route.getRouteId();

    	m_valueChanged = false;
    	m_routeTiming = m_route.getRouteTiming();
		m_timingWasSet = m_routeTiming.isTimingSpecified();
		m_originalValue = m_routeTiming.getMaxTime();
    	open();
    }

    protected void configureShell(Shell newShell) {
    	super.configureShell(newShell);
    	configureShell(newShell, "Set timing requirements", 300, 120);
    }

    protected Control createDialogArea(Composite parent) {
    	Composite container = (Composite) super.createDialogArea(parent);

	    final GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 3;
	    container.setLayout(gridLayout);

	    createNsControl(container);
	    createCyclesControl(container);
	    createMhzControl(container);

		updateNsValue();
		updateCyclesValue();
		updateMhzValue();

    	return container;
    }

	private void createNsControl(Composite container) {
		Group nsGroup = createGroup(container, "ns", 1, 1);
		m_ns = new Text(nsGroup, SWT.SINGLE | SWT.CENTER);
		m_ns.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    
	    m_nsListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				try {
					m_engine.setRequirement(GuiEngine.s_guiCommandSrc, 
							m_routeId, Double.parseDouble(m_ns.getText()));
					m_valueChanged = true;
					updateCyclesValue();
					updateMhzValue();
				} catch (NumberFormatException exception) {
					// Do nothing
				}
			}
		};
	}

	private void createCyclesControl(Composite container) {
		Group cyclesGroup = createGroup(container, "cycles", 1, 1);
	    m_cycles = new Text(cyclesGroup, SWT.SINGLE | SWT.CENTER);
	    m_cycles.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    
	    m_cyclesListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				try {
					m_engine.setRequirement(GuiEngine.s_guiCommandSrc, 
							m_routeId, Integer.parseInt(m_cycles.getText()));
					m_valueChanged = true;
					updateNsValue();
					updateMhzValue();
				} catch (NumberFormatException exception) {
					// Do nothing
				}
			}
		};
	}

	private void createMhzControl(Composite container) {
		Group mhzGroup = createGroup(container, "mhz", 1, 1);
	    m_mhz = new Text(mhzGroup, SWT.SINGLE | SWT.CENTER);
	    m_mhz.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    
	    m_mhzListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				try {
					double value = Double.parseDouble(m_mhz.getText());
					m_engine.setRequirement(GuiEngine.s_guiCommandSrc, 
							m_routeId, ((float)1.0 / value) * 1000);
					m_valueChanged = true;
					updateNsValue();
					updateCyclesValue();
				} catch (NumberFormatException exception) {
					// Do nothing
				}
			}
		};
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
		m_ns.setText(Double.toString(m_routeTiming.getMaxTime()));
		m_ns.addModifyListener(m_nsListener);
	}			

	private void updateCyclesValue() {
		m_cycles.removeModifyListener(m_cyclesListener);
		m_cycles.setText(Integer.toString(m_routeTiming.getMaxCycles()));
		m_cycles.addModifyListener(m_cyclesListener);
	}			

	private void updateMhzValue() {
		m_mhz.removeModifyListener(m_mhzListener);
		Double ns = m_routeTiming.getMaxTime();
		m_mhz.setText(Double.toString(((float)1.0 / ns) * 1000));
		m_mhz.addModifyListener(m_mhzListener);
	}			

    protected void buttonPressed(int id) {
    	if (id == IDialogConstants.CANCEL_ID) {
    		if (m_valueChanged) {
    			if (!m_timingWasSet)
    				m_engine.unsetRequirement(GuiEngine.s_guiCommandSrc, m_routeId);
    			else
    				m_engine.setRequirement(GuiEngine.s_guiCommandSrc, m_routeId, m_originalValue);
    		}
    	}
    	super.buttonPressed(id);
    }
}
