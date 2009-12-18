package com.xmos.xta.ui.dialogs;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.core.engine.sections.CodePointInfo;
import com.xmos.xta.ui.GuiEngine;

public class PathTime extends XtaDialog {
    public PathTime(Shell parent, Route route) {
    	super(parent, new PathTimeImpl(route), "Path Time");
    }
}

class PathTimeImpl implements XtaImpl {
	private Engine m_engine;
	private Route m_route;
	private Combo m_from;
	private Combo m_to;
	private Text m_value;
	private Combo m_units;
	private Button m_define;
	private ButtonEnabler m_enabler;
	
    public PathTimeImpl(Route route) {
    	m_engine = GuiEngine.getEngine();
    	m_route = route;    	
    }

    public Control createDialogArea(Composite parent, ButtonEnabler enabler) {
    	m_enabler = enabler;
    	
	    Composite container = new Composite(parent, SWT.NONE);
	    XtaDialog.configureContainer(container);

	    createFromControl(container);	    
	    createToControl(container);	    
	    m_value = XtaDialog.createValueControl(container, this, "");
	    m_units = XtaDialog.createUnitsControl(container, this, "");
	    m_define = XtaDialog.createDefineControl(container);

	    if (m_route == null) {
	    	m_define.setSelection(true);
	    	m_define.setEnabled(false);	    	
	    }	   	    
    	return container;
    }

    public void buttonPressed(int id) {
    	if (id == IDialogConstants.OK_ID) {
    		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
    		assert (timeUnit.isValid());
    		
    		if (m_define.getSelection()) {
    		    m_engine.addPathTime(GuiEngine.s_guiSrc, m_from.getText(), m_to.getText(), timeUnit);    			
    		}
    		if (m_route != null) {
    		    m_engine.setPathTime(GuiEngine.s_guiSrc, m_route.getRouteId(), m_from.getText(), m_to.getText(), timeUnit);
    		}
    	}
    }
    
    public void updateButtonStatus() {
		Reference fromRef = ReferenceFactory.createReference(
				m_from.getText(), ReferenceClass.ENDPOINT_REF);
		Reference toRef = ReferenceFactory.createReference(
				m_to.getText(), ReferenceClass.ENDPOINT_REF);    	
    	
		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
		if (timeUnit.isValid() && fromRef.resolve(m_engine) && toRef.resolve(m_engine)) {
			m_enabler.enable(true);
		} else {
			m_enabler.enable(false);
		}    	
    }    
        
    private void createFromControl(Composite container) {
		Group group = XtaDialog.createGroup(container, "from", 1, 2);
		m_from = new Combo(group, SWT.SINGLE);
		initialiseEndpointControl(m_from, GuiEngine.getStartPoint());
    }

    private void createToControl(Composite container) {
		Group group = XtaDialog.createGroup(container, "to", 1, 2);
		m_to = new Combo(group, SWT.SINGLE);
		initialiseEndpointControl(m_to, GuiEngine.getEndPoint());
    }
    
    private String[] getValidEndpoints() {
		Collection<String> validEndpoints = new HashSet<String>();
		for (CodePointInfo info : m_engine.getEndpoints()) {
			validEndpoints.add(info.getLabel());
		}
		for (CodePointInfo info : m_engine.getCalls()) {
			validEndpoints.add(info.getLabel());
		}
        return (String[])validEndpoints.toArray(new String[0]);    	
    }
    
    private void initialiseEndpointControl(Combo combo, Reference reference) {		
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.widthHint = 30;
		combo.setLayoutData(data);
		combo.setItems(getValidEndpoints());
		
		if (reference != null) {
			combo.setText(reference.toString());
		}		
		combo.setSelection(new Point(combo.getText().length(), combo.getText().length()));
		combo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateButtonStatus();
			}
		});    	
    }    
}
