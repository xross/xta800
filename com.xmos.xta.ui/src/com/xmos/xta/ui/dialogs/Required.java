package com.xmos.xta.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming;
import com.xmos.xta.ui.GuiEngine;

public class Required extends XtaDialog {
    public Required(Shell parent, Route route) {
    	super(parent, new RequiredImpl(route), "Requirement");
    }
}

class RequiredImpl implements XtaImpl {
	private Route m_route;
	private RouteTiming m_routeTiming;
	private int m_routeId;    
	private Text m_value;
	private Combo m_units;
	private ButtonEnabler m_enabler;
	
    public RequiredImpl(Route route) {
    	m_route = route;
    	m_routeId = m_route.getRouteId();
    	m_routeTiming = m_route.getRouteTiming();
    }

    public Control createDialogArea(Composite parent, ButtonEnabler enabler) {
    	m_enabler = enabler;
    	
    	Composite container = new Composite(parent, SWT.NONE);
	    XtaDialog.configureContainer(container);
	    
	    m_value = XtaDialog.createValueControl(container, this, m_routeTiming.getMaxTime().getValueStr());
	    m_units = XtaDialog.createUnitsControl(container, this, m_routeTiming.getMaxTime().getUnitsStr());

    	return container;
    }

    public void buttonPressed(int id) {
    	if (id == IDialogConstants.OK_ID) {
    		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
    		assert (timeUnit.isValid());
    		GuiEngine.getEngine().setRequiredTime(GuiEngine.s_guiSrc, m_routeId, timeUnit);
    	}
    }    
    
    public void updateButtonStatus() {
    	if (m_value != null && m_units != null) {
			TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
			if (timeUnit.isValid()) {
				m_enabler.enable(true);
			} else {
				m_enabler.enable(false);
			}
    	} else {
    		m_enabler.enable(false);
    	}
    } 
}
