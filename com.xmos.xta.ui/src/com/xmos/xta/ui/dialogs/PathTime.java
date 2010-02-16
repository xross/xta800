package com.xmos.xta.ui.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

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
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.core.engine.sections.CodePointInfo;
import com.xmos.xta.ui.GuiEngine;

public class PathTime extends XtaDialog {
    public PathTime(Shell parent, Collection<Node> nodes) {
    	super(parent, new PathTimeImplNode(nodes), "Path Time");
    }

    public PathTime(Shell parent, Route route) {
    	super(parent, new PathTimeImplRoute(route), "Path Time");
    }
}

abstract class PathTimeImpl implements XtaImpl {
	private Engine m_engine;
	private Combo m_from;
	private Combo m_to;
	private Text m_value;
	private Combo m_units;
	private Button m_define;
	private ButtonEnabler m_enabler;

	protected Collection<Node> m_nodes;
	protected Route m_route;

    public PathTimeImpl() {
    	m_engine = GuiEngine.getEngine();
    }

    public boolean doAddDefine() {
		return m_define.getSelection();
    }

    public Control createDialogArea(Composite parent, ButtonEnabler enabler) {
    	String from = "";
    	Engine engine = GuiEngine.getEngine();
    	Reference fromRef = engine.getFromEndPoint();
    	if (fromRef != null) {
    		from = fromRef.getRef();
    	}
    	
    	String to = "";
    	Reference toRef = engine.getToEndPoint();
    	if (toRef != null) {
    		to = toRef.getRef();
    	}
    	return createDialogArea(parent, enabler, from, to);
    }
    
    public Control createDialogArea(Composite parent, ButtonEnabler enabler, String from, String to) {
    	m_enabler = enabler;
    	
	    Composite container = new Composite(parent, SWT.NONE);
	    XtaDialog.configureContainer(container);

	    createFromControl(container, from);	    
	    createToControl(container, to);
	    m_value = XtaDialog.createValueControl(container, this, "");
	    m_units = XtaDialog.createUnitsControl(container, this, "");
	    if (m_nodes != null || m_route != null) {
	    	m_define = XtaDialog.createDefineControl(container);
	    }

	    return container;
    }

    public void buttonPressed(int id) {
    	if (id == IDialogConstants.OK_ID) {
    		TimeUnit timeUnit = new TimeUnit(m_value.getText(), m_units.getText());
    		assert (timeUnit.isValid());
    		
    		if (doAddDefine()) {
    		    m_engine.addPathTime(GuiEngine.s_guiSrc,
    		    		m_from.getText(), m_to.getText(), timeUnit);    			
    		}

    		if (m_route != null) {
    		    m_engine.setPathTime(GuiEngine.s_guiSrc, m_route.getRouteId(),
    		    		m_from.getText(), m_to.getText(), timeUnit);
    		}

    		if (m_nodes != null) {
    			m_engine.setPathTime(GuiEngine.s_guiSrc, m_engine.getSelectedRouteId(),
    					m_from.getText(), m_to.getText(), timeUnit);
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
        
    private void createFromControl(Composite container, String from) {
		Group group = XtaDialog.createGroup(container, "from", 1, 2);
		m_from = new Combo(group, SWT.SINGLE);
		initialiseEndpointControl(m_from, from);
    }

    private void createToControl(Composite container, String to) {
		Group group = XtaDialog.createGroup(container, "to", 1, 2);
		m_to = new Combo(group, SWT.SINGLE);
		initialiseEndpointControl(m_to, to);
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
    
    private void initialiseEndpointControl(Combo combo, String string) {		
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.widthHint = 30;
		combo.setLayoutData(data);
		combo.setItems(getValidEndpoints());
		combo.setText(string);
		combo.setSelection(new Point(combo.getText().length(), combo.getText().length()));
		combo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateButtonStatus();
			}
		});    	
    }    
}

class PathTimeImplNode extends PathTimeImpl {
    public PathTimeImplNode(Collection<Node> nodes) {
    	if (nodes != null) {
    	    m_nodes = new ArrayList<Node>();
    	    m_nodes.addAll(nodes);
    	}
    }

    public Composite createDialogArea(Composite parent, ButtonEnabler enabler) {
    	Iterator<Node> iter = m_nodes.iterator();
    	String fromStr = iter.next().getReferenceStr();
    	String toStr = iter.next().getReferenceStr();
		return (Composite) createDialogArea(parent, enabler, fromStr, toStr);
    }
}

class PathTimeImplRoute extends PathTimeImpl {
    public PathTimeImplRoute(Route route) {
    	m_route = route;
    }

    public Control createDialogArea(Composite parent, ButtonEnabler enabler) {
		return super.createDialogArea(parent, enabler);
    }
}

class PathTimeImplDefine extends PathTimeImpl {
    public Control createDialogArea(Composite parent, ButtonEnabler enabler) {
    	return super.createDialogArea(parent, enabler);
    }
	
	public boolean doAddDefine() {
		return true;
	}
}
