package com.xmos.xta.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import com.xmos.xta.ui.GuiEngine;

public class FunctionTime extends XtaDialog {
	private Engine m_engine;
	private Route m_route;
	private int m_routeId;    
	private Combo m_function;
	private Text m_value;
	private Combo m_units;
	private Button m_assert;

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
	    Composite container = createContainer(parent);
	    
	    createFunctionControl(container);	    
	    m_value = createValueControl(container);
	    m_units = createUnitsControl(container);
	    m_assert = createAssertControl(container);

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

    		if (m_assert.getSelection()) {
    		    m_engine.assertFunction(GuiEngine.s_guiCommandSrc, m_function.getText(), timeUnit);    			
    		} else {
    		    m_engine.setFunction(GuiEngine.s_guiCommandSrc, m_routeId, m_function.getText(), timeUnit);
    		}
    	}
    	super.buttonPressed(id);
    }
    
    void checkButtonStatus() {
		Reference functionRef = ReferenceFactory.createReference(
				m_function.getText(), ReferenceClass.FUNCTION_REF);

		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
		if (timeUnit.isValid() && functionRef.resolve(m_engine)) {
			getButton(IDialogConstants.OK_ID).setEnabled(true);
		} else {
			getButton(IDialogConstants.OK_ID).setEnabled(false);					
		}    	
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
}
