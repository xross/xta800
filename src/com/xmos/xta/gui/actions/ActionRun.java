package com.xmos.xta.gui.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.Progress;
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

		} else if (event.getProperty().equals(Notifier.NOTIFY_RUN)) {
			String notifyArgument = GuiEngine.getRunMode();
			if (notifyArgument.equals(Notifier.RUN_MODE_FUNCTION)) {
				executeRunFunction();
			} else if (notifyArgument.equals(Notifier.RUN_MODE_PATH)) {
				executeRunPath();
			} else {
				assert(false);
			}
			
		}
	}

	public void run() {
		GuiEngine.broadcast(Notifier.NOTIFY_SET_RUN_MODE, Notifier.RUN_MODE_PATH);
		GuiEngine.broadcast(Notifier.NOTIFY_SET_GUI_COMMAND, "");
		GuiEngine.broadcast(Notifier.NOTIFY_RUN, "");
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

	public void executeRunFunction() {
		if (GuiEngine.getEngine().getProgress().getState() != Progress.State.IDLE) {
			return;
		}

		final String functionName = GuiEngine.getSelectedFunction();
		if (!functionName.equals("")) {			
			Job job = new Job("Analyse: " + functionName) {
				
				protected IStatus run(IProgressMonitor monitor) {
					Engine engine = GuiEngine.getEngine();
					engine.setMonitor(monitor);
					engine.analyseRoute(functionName);
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		} else {
			GuiEngine.broadcast(Notifier.NOTIFY_ERROR, "No function/path specified");
		}
	}
	
	public void executeRunPath() {
		if (GuiEngine.getEngine().getProgress().getState() != Progress.State.IDLE) {
			return;
		}
		
		Reference startPoint = GuiEngine.getStartPoint();
		Reference endPoint = GuiEngine.getEndPoint();
		if ((startPoint == null) || startPoint.getRef().equals("")) {
			GuiEngine.broadcast(Notifier.NOTIFY_ERROR, "Please specify a start reference");

		} else if ((endPoint == null) || endPoint.getRef().equals("")) {
			GuiEngine.broadcast(Notifier.NOTIFY_ERROR, "Please specify an end reference");

		} else {
			final String startRef = startPoint.getRef();
			final String endRef = endPoint.getRef();
			Job job = new Job("Analyse: " + startRef + " -> " + endRef) {

				protected IStatus run(IProgressMonitor monitor) {
					Engine engine = GuiEngine.getEngine();
					engine.setMonitor(monitor);
					engine.analyseRoute(startRef, endRef);
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
	}
}
