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
    public FunctionTime(Shell parent, Route route) {
    	super(parent, new FunctionTimeImpl(route), "Function Time");
    }
}

class FunctionTimeImpl implements XtaImpl {
	static private String s_filterString = "";
	
	private Engine m_engine;
	private Route m_route;
	private FilteredTree m_function;	
	private Text m_value;
	private Combo m_units;
	private Button m_define;
	private ButtonEnabler m_enabler;

    public FunctionTimeImpl(Route route) {
    	m_engine = GuiEngine.getEngine();
    	m_route = route;
    }

    public Control createDialogArea(Composite parent, ButtonEnabler enabler) {
    	m_enabler = enabler;
    	
	    Composite container = new Composite(parent, SWT.NONE);
	    XtaDialog.configureContainer(container);
	    
	    createFunctionControl(container);	    
	    m_value = XtaDialog.createValueControl(container, this);
	    m_units = XtaDialog.createUnitsControl(container, this);
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

			for (Object object : ((IStructuredSelection)m_function.getViewer().getSelection()).toArray()) {
				if (object instanceof String) {
		    		if (m_define.getSelection()) {
		    		    m_engine.addFunctionTime(GuiEngine.s_guiSrc, (String)object, timeUnit);
		    		}
		    		if (m_route != null) {
		    		    m_engine.setFunctionTime(GuiEngine.s_guiSrc, m_route.getRouteId(), (String)object, timeUnit);
		    		}					
				}
			}
			Text filterControl = m_function.getFilterControl();
			if (filterControl != null) {
				s_filterString = filterControl.getText();
			}
    	}
    }
      
    public void updateButtonStatus() {
		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
		if (!timeUnit.isValid()) {
			m_enabler.enable(false);
			return;
		}

		if (m_function.getViewer().getSelection().isEmpty()) {
			m_enabler.enable(false);
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
		m_enabler.enable(allValid);
    }
    
    private void createFunctionControl(Composite container) {    	
		Group group = XtaDialog.createGroup(container, "function", 1, 2);
    	
    	m_function = new FilteredTree(group, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL, new PatternFilter());
	    TreeViewer viewer = m_function.getViewer();
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

	    viewer.setContentProvider(new FunctionsContentProvider());			
	    viewer.setLabelProvider(new FunctionsLabelProvider());
	    
	    viewer.setInput(GuiEngine.getEngine());
	    m_function.getFilterControl().setText(s_filterString);
	    
	    m_function.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				updateButtonStatus();
			}
	    });			
    }    
}
