package com.xmos.xta.ui.dialogs;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.ui.GuiEngine;

public class LoopPath extends XtaDialog {
    public LoopPath(Shell parent, Collection<Node> nodes) {
    	super(parent, new LoopPathImplNode(nodes), "Loop Path Iterations");
    }

    public LoopPath(Shell parent, Route route) {
    	super(parent, new LoopPathImplRoute(route), "Loop Path Iterations");
    }
}

abstract class LoopPathImpl implements XtaImpl {
	private long m_iterations;
	private int m_minIterations;
	private ButtonEnabler m_enabler;
	private Button m_define;

	protected Text m_references;
	protected Route m_route;
	protected Collection<Node> m_nodes;

    public abstract Control createDialogArea(Composite parent, ButtonEnabler enabler);

	protected Composite createDialogArea(Composite parent, ButtonEnabler enabler, long iterations, int minIterations) {
    	Composite container = new Composite(parent, SWT.NONE);
    	XtaDialog.configureContainer(container);
    	m_enabler = enabler;
		m_references = XtaDialog.createReference(container, this);
		createNumIterationsControl(container, iterations, minIterations);

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
    				GuiEngine.getEngine().addLoopPath(GuiEngine.s_guiSrc, reference, m_iterations);
    			}
    		}
    		if (m_nodes != null) {
    			for (Node node : m_nodes) {
    				GuiEngine.getEngine().setLoopPath(GuiEngine.s_guiSrc, node, m_iterations);
    			}
    		}
    		if (m_route != null) {
    			for (String reference : XtaDialog.getReferences(m_references.getText())) {
    				GuiEngine.getEngine().setLoopPath(GuiEngine.s_guiSrc, m_route.getRouteId(), reference, m_iterations);
    			}
    		}
    	}
    }

	public void updateButtonStatus() {
		m_enabler.enable(XtaDialog.checkReferences(m_references.getText()));
	}

    protected void createNumIterationsControl(Composite parent, long iterations, int minIterations) {
    	m_iterations = iterations;
    	m_minIterations = minIterations;

    	Group group = XtaDialog.createGroup(parent, "num iterations", 1, 2);
    	final Spinner spinner = new Spinner(group, SWT.BORDER);
    	spinner.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
    	spinner.setMinimum(m_minIterations);
    	spinner.setMaximum(Integer.MAX_VALUE);
    	spinner.setEnabled(true);
    	spinner.setSelection((int)m_iterations);
    	spinner.setIncrement(1);
    	spinner.setPageIncrement(10);
    	spinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (spinner.getSelection() >= m_minIterations) {
					m_iterations = spinner.getSelection();
					m_enabler.enable(true);
				} else {
					m_enabler.enable(false);
				}
			}
		});
    }
}

class LoopPathImplNode extends LoopPathImpl {
    public LoopPathImplNode(Collection<Node> nodes) {
    	if (nodes != null) {
    	    m_nodes = new ArrayList<Node>();
    	    m_nodes.addAll(nodes);
    	}
    }

    public Control createDialogArea(Composite parent, ButtonEnabler enabler) {
    	Node firstNode = m_nodes.iterator().next();
    	long iterations = GuiEngine.getEngine().getSetLoop(firstNode);        	

    	int minIterations = 1;
    	for (Node node : m_nodes) {
    		Node loopNode = node.getEnclosingLoopNode();
    		assert(loopNode != null);
    		if (!loopNode.isSelfLoopNode()) {
    			minIterations = 0;
    			break;
    		}
    	}

		Composite container = createDialogArea(parent, enabler, iterations, minIterations);
		m_references.setText(XtaDialog.getReferencesText(m_nodes));
		m_references.setEditable(false);
    	return container;
    }
}

class LoopPathImplRoute extends LoopPathImpl {
    public LoopPathImplRoute(Route route) {
    	m_route = route;
    }

    public Control createDialogArea(Composite parent, ButtonEnabler enabler) {
		return createDialogArea(parent, enabler, 0, 0);
    }
}

class LoopPathImplDefine extends LoopPathImpl {
    public Control createDialogArea(Composite parent, ButtonEnabler enabler) {
    	return createDialogArea(parent, enabler, 0, 0);
    }
	
	public boolean doAddDefine() {
		return true;
	}
}
