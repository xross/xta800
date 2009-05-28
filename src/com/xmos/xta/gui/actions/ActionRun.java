package com.xmos.xta.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.icons.Icons;

public class ActionRun extends Action implements IPropertyChangeListener {

	private static final String DEFAULT_TOOL_TIP = "First set start/end points";

    private Reference m_startPoint;
    private Reference m_endPoint;

	public ActionRun() {
		GuiEngine.addBroadcastListener(this);
		setImageDescriptor(Icons.getIconDescriptor("run"));
		setToolTipText("Open Xe File");
		setEnabled(false);
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {			
	        updateState();
	        setEnabled(false);
	        
		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_START_POINT)) {
			m_startPoint = GuiEngine.getStartPoint();					
			updateText(m_startPoint);

		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_END_POINT)) {
			m_endPoint = GuiEngine.getEndPoint();					
			updateText(m_endPoint);
		}
	}

	public void run() {
		GuiEngine.runPath();
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
			setToolTipText(DEFAULT_TOOL_TIP);
			setEnabled(false);
		} else {
			String startText = "";
			String endText = "";
			
			if (m_startPoint != null) 
				startText = m_startPoint.getRef();
			if (m_endPoint != null)
				endText = m_endPoint.getRef();
				
			setToolTipText(startText + " -> " + endText);
			
			if (!startText.equals("") && !endText.equals("")) {
				setEnabled(true);
			} else {
				setEnabled(false);
			}
		}
	}	

	private void updateState() {
	    m_startPoint = null;
	    m_endPoint = null;
    	
	    setToolTipText(DEFAULT_TOOL_TIP);
	}
}
