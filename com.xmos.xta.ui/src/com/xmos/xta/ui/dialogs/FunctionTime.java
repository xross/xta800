package com.xmos.xta.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.ui.GuiEngine;

public class FunctionTime extends XtaDialog {
	static private String s_filterString = "";
	
	private Engine m_engine;
	private Route m_route;
	private int m_routeId;    
	private FilteredTree m_function;	
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

			for (Object object : ((IStructuredSelection)m_function.getViewer().getSelection()).toArray()) {
				if (object instanceof String) {
		    		if (m_assert.getSelection()) {
		    		    m_engine.assertFunction(GuiEngine.s_guiCommandSrc, (String)object, timeUnit);    			
		    		} else {
		    		    m_engine.setFunction(GuiEngine.s_guiCommandSrc, m_routeId, (String)object, timeUnit);
		    		}					
				}
			}
			Text filterControl = m_function.getFilterControl();
			if (filterControl != null) {
				s_filterString = filterControl.getText();
			}    		
    	}
    	super.buttonPressed(id);
    }
    
    void checkButtonStatus() {
		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
		if (!timeUnit.isValid()) {
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			return;
		}

		if (m_function.getViewer().getSelection().isEmpty()) {
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			return;			
		}
		
		boolean allValid = true;
		for (Object object : ((IStructuredSelection)m_function.getViewer().getSelection()).toArray()) {
			if (object instanceof String) {
		    	Reference functionRef = ReferenceFactory.createReference(
		    			(String)object, ReferenceClass.FUNCTION_REF);
				
		    	if (!functionRef.resolve(m_engine)) {
		    		allValid = false;
		    		break;
		    	}
			}
		}    	
		getButton(IDialogConstants.OK_ID).setEnabled(allValid);
    }
    
    private void createFunctionControl(Composite container) {    	
		Group group = createGroup(container, "function", 1, 2);
    	
    	m_function = new FilteredTree(group, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL, new PatternFilter());
	    TreeViewer viewer = m_function.getViewer();
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

	    viewer.setContentProvider(new FunctionsContentProvider());			
	    viewer.setLabelProvider(new FunctionsLabelProvider());
	    
	    viewer.setInput(GuiEngine.getEngine());
	    m_function.getFilterControl().setText(s_filterString);
	    
	    m_function.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				checkButtonStatus();
			}
	    });			
    }    
}
