package com.xmos.xta.gui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.gui.GuiEngine;

public class ActionRun extends ActionDelegate implements IPropertyChangeListener,
                                                         IWorkbenchWindowActionDelegate {
	private static int s_numInstances = 0;

	private static final String DEFAULT_TOOL_TIP = "First set start/end points";

    private Reference m_startPoint;
    private Reference m_endPoint;
	private IAction m_action;
	private int m_instanceId;

	public ActionRun() {
		GuiEngine.addBroadcastListener(this);
		m_instanceId = s_numInstances;
		++s_numInstances;
	}

	public void init(IAction action) {
		m_action = action;
		
	    // Note: This is a really nasty hack to get around the fact that the eclipse
	    // platform seems to create another instance of this at exit.
	    // This needs further investigation.
	    if (m_instanceId == 0) {
	        m_action.setEnabled(false);
	    }		
	}
	
	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		GuiEngine.runPath();
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {			
	        updateState();
	        m_action.setEnabled(false);
	        
		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_START_POINT)) {
			m_startPoint = GuiEngine.getStartPoint();					
			updateText(m_startPoint);

		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_END_POINT)) {
			m_endPoint = GuiEngine.getEndPoint();					
			updateText(m_endPoint);
		}
	}

	private void updateText(Reference point) {
		if (point != null) {
			Engine engine = GuiEngine.getEngine();
			point.resolve(engine, engine.getNodeId(), engine.getCoreId());
			if (!point.getResolved()) {		
		     	point = null;
		    }
		}		

		if (m_startPoint == null && m_endPoint == null) { 
			m_action.setToolTipText(DEFAULT_TOOL_TIP);
			m_action.setEnabled(false);
		} else {
			String startText = "";
			String endText = "";
			
			if (m_startPoint != null) 
				startText = m_startPoint.getRef();
			if (m_endPoint != null)
				endText = m_endPoint.getRef();
				
			m_action.setToolTipText(startText + " -> " + endText);
			
			if (!startText.equals("") && !endText.equals("")) {
				m_action.setEnabled(true);
			} else {
				m_action.setEnabled(false);
			}
		}
	}	

	private void updateState() {
	    m_startPoint = null;
	    m_endPoint = null;
    	
	    m_action.setToolTipText(DEFAULT_TOOL_TIP);
	}
}
