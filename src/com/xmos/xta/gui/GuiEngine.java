package com.xmos.xta.gui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelection;

import com.xmos.xta.engine.Config;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.Progress;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.ReferenceFactory.ReferenceClass;

public class GuiEngine {
	private static Engine m_engine;
	private static GuiNotifier m_notifier;	
	private static Broadcaster m_broadcaster;
	private static GuiLog m_log;
	private static GuiRouteInfo m_routeInfo;
	private static ISelection m_focus;
	private static String m_functionName;
	private static Reference m_startPoint;
	private static Reference m_endPoint;
	private static Reference m_codePoint;
	private static String m_runMode;

	public static void initialise() {
        m_engine = new Engine(new Config());
        m_notifier = new GuiNotifier();
        m_broadcaster = new Broadcaster();        
        m_routeInfo = new GuiRouteInfo(m_engine);
        m_log = new GuiLog();

        m_engine.setNotifier(m_notifier);
	}
	
	public static Engine getEngine() {
		assert(m_engine != null);
		return m_engine;		
	}
	
	public static GuiLog getLog() {
		assert(m_log != null);
		return m_log;		
	}	
	
	public static void addBroadcastListener(final IPropertyChangeListener listener) {
		m_broadcaster.addListener(listener);
	}

	public static void removeBroadcastListener(final IPropertyChangeListener listener) {
		m_broadcaster.removeListener(listener);
	}
	
	public static void broadcast(String message, String argument) {
		m_broadcaster.broadcast(message, argument);
	}
	
	public static void notify(String message, String argument) {
		m_notifier.notify(message, argument);
	}
	
	public static void runFunction(final String functionName) {
		if (GuiEngine.getEngine().getProgress().getState() != Progress.State.IDLE) {
			return;
		}

		if (!functionName.equals("")) {
			Job job = new Job("Analyse: " + functionName) {
				protected IStatus run(IProgressMonitor monitor) {
					m_engine.setMonitor(monitor);
					m_engine.analyseRoute(functionName);
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		} else {
			GuiEngine.notify(Notifier.NOTIFY_ERROR, "No function/path specified");
		}
	}
	
	public static void runPath() {
		if (GuiEngine.getEngine().getProgress().getState() != Progress.State.IDLE) {
			return;
		}
		
		if ((m_startPoint == null) || m_startPoint.getRef().equals("")) {
			GuiEngine.notify(Notifier.NOTIFY_ERROR, "Please specify a start reference");

		} else if ((m_endPoint == null) || m_endPoint.getRef().equals("")) {
			GuiEngine.notify(Notifier.NOTIFY_ERROR, "Please specify an end reference");

		} else {
			final String startRef = m_startPoint.getRef();
			final String endRef = m_endPoint.getRef();
			Job job = new Job("Analyse: " + startRef + " -> " + endRef) {
				protected IStatus run(IProgressMonitor monitor) {
					m_engine.setMonitor(monitor);
					m_engine.analyseRoute(startRef, endRef);
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
	}
	
	public static GuiRouteInfo getRouteInfo() {
		return m_routeInfo;
	}
	
	public static ISelection getFocus() {
		return m_focus;
	}
	
	public static String getFunctionName() {
		return m_functionName;
	}
	
	public static Reference getStartPoint() {
		return m_startPoint;
	}
	
	public static Reference getEndPoint() {
		return m_endPoint;
	}

	public static Reference getCodePoint() {
		return m_codePoint;
	}
		
	public static String getRunMode() {
		return m_runMode;
	}

	public static void setFocus(ISelection selection) {
		m_focus = selection;
	}	

	public static void setCodePoint(Reference codePoint) {
		m_codePoint = codePoint;
	}
	
	public static void updateGuiState(String message, String argument) {
		if (message.equals(Notifier.NOTIFY_ERROR)) {
			m_log.add(GuiLog.Level.ERROR, argument);
			
		} else if (message.equals(Notifier.NOTIFY_WARNING)) {
			m_log.add(GuiLog.Level.WARNING, argument);
			
		} else if (message.equals(Notifier.NOTIFY_INFO)) {
			m_log.add(GuiLog.Level.INFO, argument);
			
		} else if (message.equals(Notifier.NOTIFY_SET_FUNCTION_NAME)) {
			m_functionName = argument;
			
		} else if (message.equals(Notifier.NOTIFY_SET_START_POINT)) {
			Reference startPoint = ReferenceFactory.createReference(argument, ReferenceClass.ENDPOINT_REF);			
			if (startPoint != null) {
				Engine engine = GuiEngine.getEngine();
			    startPoint.resolve(engine, engine.getNodeId(), engine.getCoreId());
			}
	    	m_startPoint = startPoint;

		} else if (message.equals(Notifier.NOTIFY_SET_END_POINT)) {
			Reference endPoint = ReferenceFactory.createReference(argument, ReferenceClass.ENDPOINT_REF);			
			if (endPoint != null) {
				Engine engine = GuiEngine.getEngine();
				endPoint.resolve(engine, engine.getNodeId(), engine.getCoreId());			
			}
			m_endPoint = endPoint;
			
		} else if (message.equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			resetGuiState(true);
			
		} else if (message.equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			resetGuiState(false);
						
		} else if (message.equals(Notifier.NOTIFY_SELECT_ROUTE) ||
				   message.equals(Notifier.NOTIFY_REMOVE_ROUTE)) {
			m_routeInfo.update();
			
		} else if (message.equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
			m_runMode = argument;						
		}
	}
	
	private static void resetGuiState(boolean isLoad) {
		m_codePoint = null;
		m_startPoint = null;
		m_endPoint = null;
		m_runMode = Notifier.RUN_MODE_FUNCTION;

		if (isLoad) {
			m_routeInfo.resetState();
		}
	}	
}
