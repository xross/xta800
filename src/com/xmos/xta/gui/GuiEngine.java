package com.xmos.xta.gui;

import java.util.List;

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
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class GuiEngine {
	private static Engine s_engine;
	private static GuiNotifier s_notifier;	
	private static Broadcaster s_broadcaster;
	private static GuiLog s_log;
	private static ISelection s_focus;
	private static String s_functionName;
	private static Reference s_startPoint;
	private static Reference s_endPoint;
	private static Reference s_codePoint;
	private static String s_runMode;
	private static List<Node> s_selectedNodes;

	public static void initialise() {
        s_engine = new Engine(new Config());
        s_notifier = new GuiNotifier();
        s_broadcaster = new Broadcaster();        
        s_log = new GuiLog();

        s_engine.setNotifier(s_notifier);
	}
	
	public static Engine getEngine() {
		assert(s_engine != null);
		return s_engine;		
	}
	
	public static GuiLog getLog() {
		assert(s_log != null);
		return s_log;		
	}	
	
	public static void addBroadcastListener(final IPropertyChangeListener listener) {
		s_broadcaster.addListener(listener);
	}

	public static void removeBroadcastListener(final IPropertyChangeListener listener) {
		s_broadcaster.removeListener(listener);
	}
	
	public static void broadcast(String message, String argument) {
		s_broadcaster.broadcast(message, argument);
	}
	
	public static void notify(String message, String argument) {
		s_notifier.notify(message, argument);
	}
	
	public static void runFunction(final String functionName) {
		if (GuiEngine.getEngine().getProgress().getState() != Progress.State.IDLE) {
			return;
		}

		if (!functionName.equals("")) {
			Job job = new Job("Analyse: " + functionName) {
				protected IStatus run(IProgressMonitor monitor) {
					s_engine.setMonitor(monitor);
					s_engine.analyseRoute(functionName);
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
		
		if ((s_startPoint == null) || s_startPoint.getRef().equals("")) {
			GuiEngine.notify(Notifier.NOTIFY_ERROR, "Please specify a start reference");

		} else if ((s_endPoint == null) || s_endPoint.getRef().equals("")) {
			GuiEngine.notify(Notifier.NOTIFY_ERROR, "Please specify an end reference");

		} else {
			final String startRef = s_startPoint.getRef();
			final String endRef = s_endPoint.getRef();
			Job job = new Job("Analyse: " + startRef + " -> " + endRef) {
				protected IStatus run(IProgressMonitor monitor) {
					s_engine.setMonitor(monitor);
					s_engine.analyseRoute(startRef, endRef);
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
	}
	
	public static ISelection getFocus() {
		return s_focus;
	}
	
	public static String getFunctionName() {
		return s_functionName;
	}
	
	public static Reference getStartPoint() {
		return s_startPoint;
	}
	
	public static Reference getEndPoint() {
		return s_endPoint;
	}

	public static Reference getCodePoint() {
		return s_codePoint;
	}
		
	public static String getRunMode() {
		return s_runMode;
	}

	public static void setFocus(ISelection selection) {
		s_focus = selection;
	}	

	public static void setCodePoint(Reference codePoint) {
		s_codePoint = codePoint;
	}
	
	public static List<Node> getSelectedNodes() {
		return s_selectedNodes;
	}	

	public static void setSelectedNodes(List<Node> selectedNodes) {
		s_selectedNodes = selectedNodes;
	}
	
	public static void updateGuiState(String message, String argument) {
		if (message.equals(Notifier.NOTIFY_ERROR)) {
			s_log.add(GuiLog.Level.ERROR, argument);
			
		} else if (message.equals(Notifier.NOTIFY_WARNING)) {
			s_log.add(GuiLog.Level.WARNING, argument);
			
		} else if (message.equals(Notifier.NOTIFY_INFO)) {
			s_log.add(GuiLog.Level.INFO, argument);
			
		} else if (message.equals(Notifier.NOTIFY_SET_FUNCTION_NAME)) {
			s_functionName = argument;
			
		} else if (message.equals(Notifier.NOTIFY_SET_START_POINT)) {
			Reference startPoint = ReferenceFactory.createReference(argument, ReferenceClass.ENDPOINT_REF);			
			if (startPoint != null) {
				Engine engine = GuiEngine.getEngine();
			    startPoint.resolve(engine, engine.getNodeId(), engine.getCoreId());
			}
	    	s_startPoint = startPoint;

		} else if (message.equals(Notifier.NOTIFY_SET_END_POINT)) {
			Reference endPoint = ReferenceFactory.createReference(argument, ReferenceClass.ENDPOINT_REF);			
			if (endPoint != null) {
				Engine engine = GuiEngine.getEngine();
				endPoint.resolve(engine, engine.getNodeId(), engine.getCoreId());			
			}
			s_endPoint = endPoint;
			
		} else if (message.equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			resetGuiState();
			
		} else if (message.equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			resetGuiState();
						
		} else if (message.equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
			s_runMode = argument;						
		}
	}
	
	private static void resetGuiState() {
		s_codePoint = null;
		s_startPoint = null;
		s_endPoint = null;
		s_runMode = Notifier.RUN_MODE_FUNCTION;
	}	
}
