package com.xmos.xta.rcp.actions.control;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.Progress;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.rcp.XtaEngine;
import com.xmos.xta.rcp.XtaNotifier;
import com.xmos.xta.rcp.icons.Icons;

public class ActionRun extends Action implements IPropertyChangeListener {

	private static final String DEFAULT_TOOL_TIP = "First set start/end points";

    private Reference m_startPoint;
    private Reference m_endPoint;

	public ActionRun() {
		XtaNotifier.getInstance().addPropertyChangeListener(this);
		setImageDescriptor(Icons.getIconDescriptor("run"));
		setToolTipText("Open Xe File");
		setEnabled(false);
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {			
	        updateState();
	        
		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_START_POINT)) {
			m_startPoint = XtaNotifier.getInstance().getStartPoint();					
			updateText(m_startPoint);

		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_END_POINT)) {
			m_endPoint = XtaNotifier.getInstance().getEndPoint();					
			updateText(m_endPoint);

		} else if (event.getProperty().equals(Notifier.NOTIFY_RUN)) {
			String notifyArgument = XtaNotifier.getInstance().getRunMode();
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
		XtaNotifier notifier = XtaNotifier.getInstance();
		notifier.notify(Notifier.NOTIFY_SET_RUN_MODE, Notifier.RUN_MODE_PATH);
		notifier.notify(Notifier.NOTIFY_SET_GUI_COMMAND, "");
		notifier.notify(Notifier.NOTIFY_RUN, "");
	}

	private void updateText(Reference point) {
		if (point != null) {
			Engine engine = XtaEngine.getEngine();
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
		if (XtaEngine.getEngine().getProgress().getState() != Progress.State.IDLE) {
			return;
		}

		XtaNotifier notifier = XtaNotifier.getInstance();
		String functionName = notifier.getSelectedFunction();
		if (!functionName.equals("")) {
			RunEngineMethodFunc run = new RunEngineMethodFunc(functionName);
			run.start();
		} else {
			notifier.notify(Notifier.NOTIFY_ERROR, "No function/path specified");
		}
	}
	
	public void executeRunPath() {
		if (XtaEngine.getEngine().getProgress().getState() != Progress.State.IDLE) {
			return;
		}
		
		XtaNotifier notifier = XtaNotifier.getInstance();
		Reference startPoint = notifier.getStartPoint();
		Reference endPoint = notifier.getEndPoint();
		if ((startPoint == null) || startPoint.getRef().equals("")) {
			notifier.notify(Notifier.NOTIFY_ERROR, "Please specify a start reference");

		} else if ((endPoint == null) || endPoint.getRef().equals("")) {
			notifier.notify(Notifier.NOTIFY_ERROR, "Please specify an end reference");

		} else {
			RunEngineMethodPath run = new RunEngineMethodPath(startPoint.getRef(), endPoint.getRef());
			run.start();
		}
	}
	
	class RunEngineMethodFunc extends Thread {
        private String m_functionName;

        RunEngineMethodFunc(String functionName) {
            m_functionName = functionName;
        }

        public void run() {
            XtaEngine.getEngine().analyseRoute(m_functionName);
        }
    }

    class RunEngineMethodPath extends Thread {
        private String m_startRef;
        private String m_endRef;

        RunEngineMethodPath(String startRef, String endRef) {
        	m_startRef = startRef;
        	m_endRef = endRef;
        }

        public void run() {
        	XtaEngine.getEngine().analyseRoute(m_startRef, m_endRef);
        }
    }
}
