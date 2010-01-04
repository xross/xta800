package com.xmos.xta.ui.dialogs;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
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
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.ui.GuiEngine;

public class FunctionTime extends XtaDialog {
    public FunctionTime(Shell parent, Collection<Node> nodes) {
    	super(parent, new FunctionTimeImplNode(nodes), "Function Time");
    }
    
    public FunctionTime(Shell parent, Route route) {
    	super(parent, new FunctionTimeImplRoute(route), "Function Time");
    }
}

abstract class FunctionTimeImpl implements XtaImpl {
	static private String s_filterString = "";
	
	private Engine m_engine;
	private Combo m_units;
	private ButtonEnabler m_enabler;
	private Text m_value;
	
	protected FilteredTree m_function;
	protected Route m_route;
	protected Collection<Node> m_nodes;
	protected Button m_define;

	public abstract Control createDialogArea(Composite parent, ButtonEnabler enabler);
	
    public Composite createDialogArea(Composite parent, ButtonEnabler enabler, String function) {
    	m_engine = GuiEngine.getEngine();
    	m_enabler = enabler;
    	
	    Composite container = new Composite(parent, SWT.NONE);
	    XtaDialog.configureContainer(container);
	    
	    createFunctionControl(container);	    
	    m_value = XtaDialog.createValueControl(container, this, "");
	    m_units = XtaDialog.createUnitsControl(container, this, "");
	    if (m_nodes != null || m_route != null) {
	    	m_define = XtaDialog.createDefineControl(container);
	    }

m_function.getFilterControl().setText(function);
	    m_function.getFilterControl().addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateButtonStatus();
			}
		});

    	return container;
    }
    
	public boolean doAddDefine() {
		return m_define.getSelection();
    }
	
	public void buttonPressed(int id) {
    	if (id == IDialogConstants.OK_ID) {
    		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
    		assert (timeUnit.isValid());

			Text filterControl = m_function.getFilterControl();
			if (filterControl != null) {
				s_filterString = filterControl.getText();
			}

			String functionNames = "";
			for (Object object : ((IStructuredSelection)m_function.getViewer().getSelection()).toArray()) {
				if (object instanceof String) {
					if (functionNames.length() > 0) {
						functionNames += XtaDialog.SEPARATOR_CHAR;
					}
					functionNames += (String)object;
				}
			}
			
			if (functionNames.equals("")) {
				functionNames = s_filterString;
			}

			for (String functionName : XtaDialog.getReferences(functionNames)) {
	    		if (doAddDefine()) {
	    		    m_engine.addFunctionTime(GuiEngine.s_guiSrc, functionName, timeUnit);
	    		}
	    		if (m_route != null) {
	    		    m_engine.setFunctionTime(GuiEngine.s_guiSrc, m_route.getRouteId(), functionName, timeUnit);
	    		}
			}
			
			if (m_nodes != null) {
    			for (Node node : m_nodes) {
    				m_engine.setFunctionTime(GuiEngine.s_guiSrc, node, timeUnit);
    			}
    		}
    	}
    }
      
    public void updateButtonStatus() {
		m_enabler.enable(true);

		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
		if (!timeUnit.isValid()) {
			m_enabler.enable(false);
			return;
		}

		if (m_function.getViewer().getSelection().isEmpty()) {
			boolean allValid = true;
			boolean found = false;
			for (String reference : XtaDialog.getReferences(m_function.getFilterControl().getText())) {
		    	Reference functionRef = ReferenceFactory.createReference(reference, ReferenceClass.FUNCTION_REF);
		    	if (!functionRef.resolve(m_engine)) {
		    		allValid = false;
		    		break;
		    	} else {
		    		found = true;
		    	}
			}
			m_enabler.enable(allValid && found);
		}
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

class FunctionTimeImplNode extends FunctionTimeImpl {
    public FunctionTimeImplNode(Collection<Node> nodes) {
    	if (nodes != null) {
    	    m_nodes = new ArrayList<Node>();
    	    m_nodes.addAll(nodes);
    	}
    }

    public Composite createDialogArea(Composite parent, ButtonEnabler enabler) {
    	String functions = XtaDialog.getReferencesText(m_nodes);
		Composite container = createDialogArea(parent, enabler, functions);
    	m_function.getFilterControl().setEditable(false);
    	m_function.setEnabled(false);
    	return container;
    }
}

class FunctionTimeImplRoute extends FunctionTimeImpl {
    public FunctionTimeImplRoute(Route route) {
    	m_route = route;
    }

    public Control createDialogArea(Composite parent, ButtonEnabler enabler) {
		return super.createDialogArea(parent, enabler, "");
    }
}

class FunctionTimeImplDefine extends FunctionTimeImpl {
    public Control createDialogArea(Composite parent, ButtonEnabler enabler) {
    	return super.createDialogArea(parent, enabler, "");
    }
	
	public boolean doAddDefine() {
		return true;
	}
}
