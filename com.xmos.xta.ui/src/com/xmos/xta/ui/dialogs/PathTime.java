package com.xmos.xta.ui.dialogs;

import java.util.Collection;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
import com.xmos.xta.ui.GuiEngine;

public class PathTime extends XtaDialog {
	private Engine m_engine;
	private Route m_route;
	private int m_routeId;    
	private Combo m_from;
	private Combo m_to;
	private Text m_value;
	private Combo m_units;
    
    public PathTime(Shell parent, Engine engine, Route route) {
    	super(parent);
    	m_engine = engine;
    	m_route = route;
    	m_routeId = m_route.getRouteId();
    	open();
    }

    protected void configureShell(Shell newShell) {
    	super.configureShell(newShell);
    	configureShell(newShell, "Set path timing", 380, 120);    	
    }

    protected Control createDialogArea(Composite parent) {
    	Composite container = (Composite) super.createDialogArea(parent);

	    final GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 2;
	    container.setLayout(gridLayout);

	    createFromControl(container);	    
	    createToControl(container);	    
	    createValueControl(container);
	    createUnitsControl(container);

    	return container;
    }

    protected void createButtonsForButtonBar(Composite parent) {
    	super.createButtonsForButtonBar(parent);
    	getButton(IDialogConstants.OK_ID).setEnabled(false);
    }
    
    protected void buttonPressed(int id) {
    	if (id == IDialogConstants.OK_ID) {
    		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
    		assert (timeUnit.isValid());
    		m_engine.setPath(GuiEngine.s_guiCommandSrc, m_routeId, m_from.getText(), m_to.getText(), timeUnit);
    	}
    	super.buttonPressed(id);
    }
    
    private void createFromControl(Composite container) {
		Group group = createGroup(container, "from", 1, 2);
		m_from = new Combo(group, SWT.SINGLE);
		initialiseEndpointControl(m_from, GuiEngine.getStartPoint());
    }

    private void createToControl(Composite container) {
		Group group = createGroup(container, "to", 1, 2);
		m_to = new Combo(group, SWT.SINGLE);
		initialiseEndpointControl(m_to, GuiEngine.getEndPoint());
    }
    
	private void createValueControl(Composite container) {
		Group group = createGroup(container, "value", 1, 1);
		m_value = new Text(group, SWT.SINGLE | SWT.CENTER | SWT.BORDER);
		m_value.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		m_value.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				checkButtonStatus();
			}
		});		
	}

	private void createUnitsControl(Composite container) {
		Group group = createGroup(container, "units", 1, 1);
		m_units = new Combo(group, SWT.SINGLE | SWT.CENTER);
		m_units.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		m_units.setItems(TimeUnit.getUnits());
		m_units.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				checkButtonStatus();
			}
		});		
	}

    private void checkButtonStatus() {
		Reference fromRef = ReferenceFactory.createReference(
				m_from.getText(), ReferenceClass.ENDPOINT_REF);
		Reference toRef = ReferenceFactory.createReference(
				m_to.getText(), ReferenceClass.ENDPOINT_REF);    	
    	
		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
		if (timeUnit.isValid() && fromRef.resolve(m_engine) && toRef.resolve(m_engine)) {
			getButton(IDialogConstants.OK_ID).setEnabled(true);
		} else {
			getButton(IDialogConstants.OK_ID).setEnabled(false);					
		}    	
    }

    private String[] getValidEndpoints() {
		Collection<String> validEndpoints = m_engine.getEndpoints().keySet();
		validEndpoints.addAll(m_engine.getCalls().keySet());
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
				checkButtonStatus();
			}
		});    	
    }    
}
