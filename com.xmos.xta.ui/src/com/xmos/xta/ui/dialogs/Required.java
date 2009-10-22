package com.xmos.xta.ui.dialogs;

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

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming;
import com.xmos.xta.ui.GuiEngine;

public class Required extends XtaDialog {
	private Engine m_engine;
	private Route m_route;
	private RouteTiming m_routeTiming;
	private int m_routeId;    
	private boolean m_valueChanged;
	private boolean m_timingWasSet;
	private TimeUnit m_originalValue;	
	private Text m_ns;
	private Text m_cycles;
	private Text m_mhz;    
	private ModifyListener m_nsListener;
	private ModifyListener m_cyclesListener;
	private ModifyListener m_mhzListener;
    
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
    	configureShell(newShell, "Set timing requirements", 380, 120);
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
				TimeUnit timeUnit = new TimeUnit(m_ns.getText(), "ns");
				if (timeUnit.isValid()) {
					m_engine.setRequiredTime(GuiEngine.s_guiCommandSrc, m_routeId, timeUnit);
					m_valueChanged = true;
					updateCyclesValue();
					updateMhzValue();					
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
				TimeUnit timeUnit = new TimeUnit(m_cycles.getText(), "cycles");
				if (timeUnit.isValid()) {
					m_engine.setRequiredTime(GuiEngine.s_guiCommandSrc, m_routeId, timeUnit);
					m_valueChanged = true;
					updateNsValue();
					updateMhzValue();					
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
				TimeUnit timeUnit = new TimeUnit(m_mhz.getText(), "mhz");
				if (timeUnit.isValid()) {
					m_engine.setRequiredTime(GuiEngine.s_guiCommandSrc, m_routeId, timeUnit);
					m_valueChanged = true;
					updateNsValue();
					updateCyclesValue();
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
		m_ns.setText(Double.toString(m_routeTiming.getMaxTime().getNs()));
		m_ns.addModifyListener(m_nsListener);
	}			

	private void updateCyclesValue() {
		m_cycles.removeModifyListener(m_cyclesListener);
		m_cycles.setText(Long.toString(m_routeTiming.getMaxTime().getCycles()));
		m_cycles.addModifyListener(m_cyclesListener);
	}			

	private void updateMhzValue() {
		m_mhz.removeModifyListener(m_mhzListener);
		m_mhz.setText(Double.toString(m_routeTiming.getMaxTime().getMhz()));
		m_mhz.addModifyListener(m_mhzListener);
	}			

    protected void buttonPressed(int id) {
    	if (id == IDialogConstants.CANCEL_ID) {
    		if (m_valueChanged) {
    			if (!m_timingWasSet)
    				m_engine.unsetRequirement(GuiEngine.s_guiCommandSrc, m_routeId);
    			else
    				m_engine.setRequiredTime(GuiEngine.s_guiCommandSrc, m_routeId, m_originalValue);
    		}
    	}
    	super.buttonPressed(id);
    }
}
