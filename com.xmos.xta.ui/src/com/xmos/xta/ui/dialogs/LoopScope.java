package com.xmos.xta.ui.dialogs;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.ui.GuiEngine;

public class LoopScope extends XtaDialog {
    public LoopScope(Shell parent, Collection<Node> nodes) {
    	super(parent, new LoopScopeImplNode(nodes), "Loop Scope");
    }

    public LoopScope(Shell parent, Route route) {
    	super(parent, new LoopScopeImplRoute(route), "Loop Scope");
    }
}

abstract class LoopScopeImpl implements XtaImpl {
	private boolean m_scope;
	private ButtonEnabler m_enabler;
	private Button m_define;

	protected Text m_references;
	protected Route m_route;
	protected Collection<Node> m_nodes;

    public abstract Control createDialogArea(Composite parent, ButtonEnabler enabler);

	protected Composite createDialogArea(Composite parent, ButtonEnabler enabler, boolean scope) {
    	Composite container = new Composite(parent, SWT.NONE);
    	XtaDialog.configureContainer(container);
    	m_enabler = enabler;
		m_references = XtaDialog.createReference(container, this);
		createNumIterationsControl(container, scope);

		if (m_nodes != null || m_route != null) {
			m_define = XtaDialog.createDefineControl(container);
		}
		return container;
	}

	public boolean doAddDefine() {
		return m_define.getSelection();
    }
	
    public void buttonPressed(int id) {
    	if (id == IDialogConstants.OK_ID) {
    		if (doAddDefine()) {
    			for (String reference : XtaDialog.getReferences(m_references.getText())) {
    				GuiEngine.getEngine().addLoopScope(GuiEngine.s_guiSrc, reference, m_scope);
    			}
    		}
    		if (m_nodes != null) {
    			for (Node node : m_nodes) {
    				GuiEngine.getEngine().setLoopScope(GuiEngine.s_guiSrc, node, m_scope);
    			}
    		}
    		if (m_route != null) {
    			for (String reference : XtaDialog.getReferences(m_references.getText())) {
    				GuiEngine.getEngine().setLoopScope(GuiEngine.s_guiSrc, m_route.getRouteId(), reference, m_scope);
    			}
    		}
    	}
    }

	public void updateButtonStatus() {
		m_enabler.enable(XtaDialog.checkReferences(m_references.getText()));
	}

    protected void createNumIterationsControl(Composite parent, boolean scope) {
    	m_scope = scope;
    	Group group = XtaDialog.createGroup(parent, "make scope absolute", 1, 1);
    	
		final Button button = new Button(group, SWT.CHECK);
		button.setSelection(m_scope);		
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_scope = button.getSelection();
				updateButtonStatus();
			}			
		});
    }
}

class LoopScopeImplNode extends LoopScopeImpl {
    public LoopScopeImplNode(Collection<Node> nodes) {
    	if (nodes != null) {
    	    m_nodes = new ArrayList<Node>();
    	    m_nodes.addAll(nodes);
    	}
    }

    public Control createDialogArea(Composite parent, ButtonEnabler enabler) {
    	Node firstNode = m_nodes.iterator().next();
	    boolean scope = GuiEngine.getEngine().getSetLoopScope(firstNode);

		Composite container = createDialogArea(parent, enabler, scope);
		m_references.setText(XtaDialog.getReferencesText(m_nodes));
		m_references.setEditable(false);
    	return container;
    }
}

class LoopScopeImplRoute extends LoopScopeImpl {
    public LoopScopeImplRoute(Route route) {
    	m_route = route;
    }

    public Control createDialogArea(Composite parent, ButtonEnabler enabler) {
		return createDialogArea(parent, enabler, false);
    }
}

class LoopScopeImplDefine extends LoopScopeImpl {
    public Control createDialogArea(Composite parent, ButtonEnabler enabler) {
    	return createDialogArea(parent, enabler, false);
    }
	
	public boolean doAddDefine() {
		return true;
	}
}
