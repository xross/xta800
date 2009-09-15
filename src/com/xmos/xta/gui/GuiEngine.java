package com.xmos.xta.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.util.IPropertyChangeListener;

import com.xmos.xta.engine.CommandSource;
import com.xmos.xta.engine.Config;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.CommandSource.Type;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class GuiEngine {
	private static Engine s_engine;
	private static GuiNotifier s_notifier;	
	private static Broadcaster s_broadcaster;
	private static GuiLog s_log;
	private static String s_functionName;
	private static Reference s_startPoint;
	private static Reference s_endPoint;
	private static Reference s_codePoint;
	private static String s_runMode;
	private static List<Node> s_selectedNodes;
	private static Node s_focusNodes;
	private static Map<String, Map<Integer, GuiRouteProperties>> s_routeProperties;
	private static String s_commandLineXeFile;

	public static final CommandSource s_guiCommandSrc = new CommandSource(Type.GUI);  

	static {
        s_engine = new Engine(new Config());
        s_notifier = new GuiNotifier();
        s_broadcaster = new Broadcaster();        
        s_log = new GuiLog();
        s_selectedNodes = new ArrayList<Node>();
        s_focusNodes = null;
        s_commandLineXeFile = null;

        s_engine.setNotifier(s_notifier);
        s_routeProperties = new HashMap<String, Map<Integer, GuiRouteProperties>>();
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
		s_notifier.notify(s_guiCommandSrc, message, argument);
	}
	
	public static void runFunction(final String functionName) {
		if (!functionName.equals("")) {
			Job job = new Job("Analyse: " + functionName) {
				protected IStatus run(IProgressMonitor monitor) {
					s_engine.setMonitor(new GuiProgressMonitor(monitor));
					s_engine.analyseRoute(s_guiCommandSrc, functionName);
					s_engine.setMonitor(null);
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		} else {
			GuiEngine.notify(Notifier.NOTIFY_ERROR, "no function/path specified");
		}
	}
	
	public static void runPath() {
		if ((s_startPoint == null) || s_startPoint.getRef().equals("")) {
			GuiEngine.notify(Notifier.NOTIFY_ERROR, "please specify a start reference");

		} else if ((s_endPoint == null) || s_endPoint.getRef().equals("")) {
			GuiEngine.notify(Notifier.NOTIFY_ERROR, "please specify an end reference");

		} else {
			final String startRef = s_startPoint.getRef();
			final String endRef = s_endPoint.getRef();
			Job job = new Job("Analyse: " + startRef + " -> " + endRef) {
				protected IStatus run(IProgressMonitor monitor) {
					s_engine.setMonitor(new GuiProgressMonitor(monitor));
					s_engine.analyseRoute(s_guiCommandSrc, startRef, endRef);
					s_engine.setMonitor(null);
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
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

	public static void setCodePoint(Reference codePoint) {
		s_codePoint = codePoint;
	}
	
	public static List<Node> getSelectedNodes() {
		return s_selectedNodes;
	}	

	public static String getCommandLineXeFile() {
		return s_commandLineXeFile;
	}
	
	public static void setCommandLineXeFile(String xeFile) {
		s_commandLineXeFile = xeFile;
	}
	
	public static void setSelectedNodes(List<Node> selectedNodes) {
		assert(selectedNodes != null);
		boolean selectionChanged = false;
		if (selectedNodes.size() != s_selectedNodes.size()) {
			selectionChanged = true;
		} else {
			for (Node node : selectedNodes) {
				if (!s_selectedNodes.contains(node)) {
					selectionChanged = true;
					break;
				}
			}
		}
		
		if (selectionChanged) {
			s_selectedNodes = selectedNodes;
			broadcast(Broadcaster.BROADCAST_SELECTION_CHANGED, "");
		}
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
			Engine engine = GuiEngine.getEngine();
		    startPoint.resolve(engine, engine.getNodeId(), engine.getCoreId());
	    	s_startPoint = startPoint;

		} else if (message.equals(Notifier.NOTIFY_SET_END_POINT)) {
			Reference endPoint = ReferenceFactory.createReference(argument, ReferenceClass.ENDPOINT_REF);			
			Engine engine = GuiEngine.getEngine();
			endPoint.resolve(engine, engine.getNodeId(), engine.getCoreId());			
			s_endPoint = endPoint;
			
		} else if (message.equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			resetGuiState();
			refreshGuiRouteProperties();
			
		} else if (message.equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			resetGuiState();
						
		} else if (message.equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
			s_runMode = argument;
			
		} else if (message.equals(Notifier.NOTIFY_REMOVE_ROUTE)) {
			refreshGuiRouteProperties();
			
		}
	}
	
	private static void refreshGuiRouteProperties() {
		for (Map<Integer, GuiRouteProperties> routeIdMap : s_routeProperties.values()) {
			List<Integer> remove = new ArrayList<Integer>();
			for (Integer routeId : routeIdMap.keySet()) {
				if (s_engine.getRoute(routeId).size() == 0) {
					remove.add(routeId);
				}
			}
			for (Integer routeId : remove) {
				routeIdMap.remove(routeId);
			}
		}
	}

	public static GuiRouteProperties getRouteProperties(int routeId, String name) {
		Map<Integer, GuiRouteProperties> routeIdMap = getRouteIdMap(name);
		if (routeIdMap.containsKey(routeId)) {
			return routeIdMap.get(routeId);
			
		} else {
			GuiRouteProperties routeProperties = new GuiRouteProperties();
			routeIdMap.put(routeId, routeProperties);
			return routeProperties;
		}
	}
	
	private static Map<Integer, GuiRouteProperties> getRouteIdMap(String name) {
		if (s_routeProperties.containsKey(name)) {
			return s_routeProperties.get(name);
			
		} else {
			Map<Integer, GuiRouteProperties> routeIdMap = new HashMap<Integer, GuiRouteProperties>();
			s_routeProperties.put(name, routeIdMap);
			return routeIdMap;
		}
	}

	private static void resetGuiState() {
		s_codePoint = null;
		s_startPoint = null;
		s_endPoint = null;
		s_focusNodes = null;
		s_runMode = Notifier.RUN_MODE_FUNCTION;
	}

	public static void setFocus(Node node) {
		s_focusNodes = node;
		broadcast(Broadcaster.BROADCAST_SET_FOCUS, "");
	}

	public static Node getFocus() {
		return s_focusNodes;
	}
}
