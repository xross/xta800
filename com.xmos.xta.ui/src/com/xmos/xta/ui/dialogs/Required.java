package com.xmos.xta.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
	private Text m_value;
	private Combo m_units;
    
    public Required(Shell parent, Engine engine, Route route) {
    	super(parent);
    	m_engine = engine;
    	m_route = route;
    	m_routeId = m_route.getRouteId();
    	m_routeTiming = m_route.getRouteTiming();
    	open();
    }

    protected void configureShell(Shell newShell) {
    	super.configureShell(newShell);
    	configureShell(newShell, "Set timing requirements", 380, 120);
    }

    protected Control createDialogArea(Composite parent) {
	    Composite container = createContainer(parent);

	    m_value = createValueControl(container);
	    m_value.setText(m_routeTiming.getMaxTime().getValueStr());
	    
	    m_units = createUnitsControl(container);
	    m_units.setText(m_routeTiming.getMaxTime().getUnitsStr());

    	return container;
    }

    protected void buttonPressed(int id) {
    	if (id == IDialogConstants.OK_ID) {
    		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
    		assert (timeUnit.isValid());
    		m_engine.setRequiredTime(GuiEngine.s_guiCommandSrc, m_routeId, timeUnit);
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
