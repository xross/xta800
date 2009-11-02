package com.xmos.xta.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

public class FunctionTime extends XtaDialog {
	private Engine m_engine;
	private Route m_route;
	private int m_routeId;    
	private Combo m_function;
	private Text m_value;
	private Combo m_units;

    public FunctionTime(Shell parent, Engine engine, Route route) {
    	super(parent);
    	m_engine = engine;
    	m_route = route;
    	m_routeId = m_route.getRouteId();
    	open();
    }

    protected void configureShell(Shell newShell) {
    	super.configureShell(newShell);
    	configureShell(newShell, "Set function timing", 380, 120);
    }

    protected Control createDialogArea(Composite parent) {
    	Composite container = (Composite) super.createDialogArea(parent);

	    final GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 2;
	    container.setLayout(gridLayout);

	    createFunctionControl(container);	    
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
    		m_engine.setFunction(GuiEngine.s_guiCommandSrc, m_routeId, m_function.getText(), timeUnit);
    	}
    	super.buttonPressed(id);
    }
    
    private void createFunctionControl(Composite container) {
		Group group = createGroup(container, "function", 1, 2);
		m_function = new Combo(group, SWT.SINGLE);
		m_function.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));		
		m_function.setItems((String[])m_engine.getNonSystemFunctionNames().toArray(new String[0]));
		m_function.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				checkButtonStatus();
			}
		});
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
		Reference functionRef = ReferenceFactory.createReference(
				m_function.getText(), ReferenceClass.FUNCTION_REF);

		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
		if (timeUnit.isValid() && functionRef.resolve(m_engine)) {
			getButton(IDialogConstants.OK_ID).setEnabled(true);
		} else {
			getButton(IDialogConstants.OK_ID).setEnabled(false);					
		}    	
    }
}
