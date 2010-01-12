package com.xmos.xta.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.Scripter;
import com.xmos.xta.core.engine.CommandSource.Type;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.ui.editors.script.InputScript;
import com.xmos.xta.ui.editors.script.ScriptEditor;
import com.xmos.xta.ui.input.InputDisassembly;
import com.xmos.xta.ui.views.console.ConsoleView;

public class GuiEngine {
	private static Engine s_engine;
	private static GuiNotifier s_notifier;	
	private static Broadcaster s_broadcaster;
	private static String s_functionName;
	private static Reference s_startPoint;
	private static Reference s_endPoint;
	private static Reference s_codePoint;
	private static String s_runMode;
	private static Map<Integer, List<Node>> s_selectedNodes;
	private static Node s_focusNodes;
	private static Map<String, Map<Integer, GuiRouteProperties>> s_routeProperties;
	private static String s_commandLineXeFile;
	
	/* Ensure there is only one editor at once */
	private static Map<String, InputDisassembly> s_disassemblyInputs;
	private static Map<String, InputScript> s_scriptInputs;

	private static Set<Long> s_sourcePcs;
	private static boolean s_showPcPosition;
	
	private static String m_scriptFileName;
	
	public static final CommandSource s_guiSrc           = new CommandSource(Type.GUI);  
	public static final CommandSource s_consoleViaGuiSrc = new CommandSource(Type.CONSOLE_VIA_GUI);  

	static {
        s_engine = new Engine(new Config());
        s_notifier = new GuiNotifier();
        s_broadcaster = new Broadcaster();        
        s_selectedNodes = new HashMap<Integer, List<Node>>();
        s_sourcePcs = new TreeSet<Long>();
        s_showPcPosition = false;
        s_focusNodes = null;
        s_commandLineXeFile = null;
        s_engine.setNotifier(s_notifier);
        s_routeProperties = new HashMap<String, Map<Integer, GuiRouteProperties>>();        
        s_disassemblyInputs = new HashMap<String, InputDisassembly>();
        s_scriptInputs = new HashMap<String, InputScript>();
        
        m_scriptFileName = "script.xta";
	}

	public static Engine getEngine() {
		assert(s_engine != null);
		return s_engine;		
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
		s_notifier.notify(s_guiSrc, message, argument);
	}
	
	public static void runFunction(final String functionName) {
		if (!functionName.equals("")) {
			Job job = new Job("Analyse: " + functionName) {
				protected IStatus run(IProgressMonitor monitor) {
					s_engine.setMonitor(new GuiProgressMonitor(monitor));
					s_engine.analyseRoute(s_guiSrc, functionName, false);
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
					s_engine.analyseRoute(s_guiSrc, startRef, endRef, false);
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
		int routeId = s_engine.getSelectedRouteId();
		if (s_selectedNodes.containsKey(routeId)) {
			return s_selectedNodes.get(routeId);
		}
		return new ArrayList<Node>();
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
		int routeId = s_engine.getSelectedRouteId();
		
		if (!s_selectedNodes.containsKey(routeId)) {
			selectionChanged = true;
		} else {
			List<Node> currentSelection = s_selectedNodes.get(routeId);
			if (selectedNodes.size() != currentSelection.size()) {
				selectionChanged = true;
			} else {
				for (Node node : selectedNodes) {
					if (!currentSelection.contains(node)) {
						selectionChanged = true;
						break;
					}
				}
			}
		}
		
		if (selectionChanged) {
			s_selectedNodes.put(routeId, selectedNodes);
			broadcast(Broadcaster.BROADCAST_SELECTION_CHANGED, "");
		}
	}
	
	public static void updateGuiState(String message, String argument) {
		if (message.equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			resetGuiState();
			refreshGuiRouteProperties();
			s_disassemblyInputs.clear();				
			
		} else if (message.equals(Notifier.NOTIFY_SET_FUNCTION_NAME)) {
			s_functionName = argument;
			
		} else if (message.equals(Notifier.NOTIFY_SET_START_POINT)) {
			Reference startPoint = ReferenceFactory.createReference(argument, ReferenceClass.ENDPOINT_REF);			
			Engine engine = GuiEngine.getEngine();
		    startPoint.resolve(engine);
	    	s_startPoint = startPoint;

		} else if (message.equals(Notifier.NOTIFY_SET_END_POINT)) {
			Reference endPoint = ReferenceFactory.createReference(argument, ReferenceClass.ENDPOINT_REF);			
			Engine engine = GuiEngine.getEngine();
			endPoint.resolve(engine);			
			s_endPoint = endPoint;
						
		} else if (message.equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			resetGuiState();
						
		} else if (message.equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
			s_runMode = argument;
			
		} else if (message.equals(Notifier.NOTIFY_REMOVE_ROUTE)) {
			refreshGuiRouteProperties();			

		} else if (message.equals(Notifier.NOTIFY_ROUTE_CHANGED)) {
			int routeId = Integer.parseInt(argument);
			clearGuiRouteProperties(routeId);
		}
	}
	
	private static void refreshGuiRouteProperties() {
		List<Integer> remove = new ArrayList<Integer>();
		for (Map<Integer, GuiRouteProperties> routeIdMap : s_routeProperties.values()) {
			for (Integer routeId : routeIdMap.keySet()) {
				if (s_engine.getRoute(routeId).isEmpty()) {
					remove.add(routeId);
				}
			}
		}
		
		for (Integer routeId : remove) {
			clearGuiRouteProperties(routeId);
		}
	}

	private static void clearGuiRouteProperties(int routeId) {
		for (Map<Integer, GuiRouteProperties> routeIdMap : s_routeProperties.values()) {
			routeIdMap.remove(routeId);
		}
		s_selectedNodes.remove(routeId);
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
	
	public static void setFocus(Node node) {
		s_focusNodes = node;
		broadcast(Broadcaster.BROADCAST_SET_FOCUS, "");
	}

	public static Node getFocus() {
		return s_focusNodes;
	}

	public static InputDisassembly getDisassemblyInput() {
		String nodeCoreId = String.format("%d:%d", s_engine.getNodeId(), s_engine.getCoreId());
		InputDisassembly input = s_disassemblyInputs.get(nodeCoreId);
		if (input == null) {
			input = new InputDisassembly();
			s_disassemblyInputs.put(nodeCoreId, input);
		}
		return input;
	}

	public static IEditorInput getScriptInput(String fileName) {
		InputScript input = s_scriptInputs.get(fileName);
		if (input == null) {
			input = new InputScript(fileName);
			s_scriptInputs.put(fileName, input);
		}
		return input;
	}

	public static String getScriptFileName() {
		return m_scriptFileName;
	}

	public static void setSourcePcs(Set<Long> pcs, boolean showPosition) {
		if (!pcs.equals(s_sourcePcs) || showPosition) {
			s_sourcePcs = pcs;
			s_showPcPosition = showPosition;
			broadcast(Broadcaster.BROADCAST_SET_SOURCE_PCS, "");
		}
	}

	public static Set<Long> getSourcePcs() {
		return s_sourcePcs;
	}

	public static boolean showSourcePcsPosition() {
		return s_showPcPosition;
	}

	public static void generateAndOpenScript() {
		String scriptFileName = chooseScriptFileName();
		if (scriptFileName == null) {
			return;
		}

		IEditorPart scriptEditor = getScriptEditor(scriptFileName);
		if ((scriptEditor != null) && scriptEditor.isDirty()) {
			if (!loseScriptChanges()) {
				return;
			}
		}

		if (!generateScriptFile(scriptFileName)) {
			return;
		}
		openScriptFile(scriptFileName);
	}
	
	public static boolean openScriptFile(String scriptFileName) {
		boolean success = true;
		IEditorInput input = getScriptInput(scriptFileName);
		
		IEditorPart scriptEditor = getScriptEditor(scriptFileName);
		if (scriptEditor != null) {
			((ScriptEditor)scriptEditor).setInput(input);
		} else {

			try {
				final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				page.openEditor(input, ScriptEditor.ID);
			    m_scriptFileName = scriptFileName;
			} catch (PartInitException e) {
				success = false;
			}
		}
		return success;
	}
	
	/**
	 * Prompt the user to see whether they are happy to lose their unsaved script changes.
	 * 
	 * Returns true if ok to lose changes, false otherwise.
	 */
	private static boolean loseScriptChanges() {
		Display display = PlatformUI.getWorkbench().getDisplay();
		Shell shell = new Shell(display);

		MessageDialog dialog = new MessageDialog(shell, "Script not saved",
				null, "Are you sure you want to lose changes?",
				MessageDialog.WARNING, new String[] {"Yes", "No"}, 0);
		int result = dialog.open();
		if (result == 0) {
			return true;
		}
		return false;
	}

	private static String chooseScriptFileName() {
		Display display = PlatformUI.getWorkbench().getDisplay();
		Shell parent = new Shell(display);
		FileDialog dialog = new FileDialog(parent, SWT.SAVE);
		dialog.setFilterNames(new String[] { "XTA script files", "All Files" });
		dialog.setFilterExtensions(new String[] { "*.xta", "*.*" });
		dialog.setFileName(m_scriptFileName);
		String scriptFileName = dialog.open();
		return scriptFileName;
	}

	/**
	 * Returns true if the script has been successfully created.
	 */
	private static boolean generateScriptFile(String scriptFileName) {
		try {
		    FileWriter fstream = new FileWriter(scriptFileName);
		    BufferedWriter out = new BufferedWriter(fstream);

		    Scripter scripter = new Scripter(GuiEngine.getEngine());
		    out.write(scripter.createScript());
		    out.close();
		    return true;
		    
		} catch (Exception e) {
			GuiEngine.getEngine().notify(GuiEngine.s_guiSrc, Notifier.NOTIFY_ERROR,
					String.format("Failed to generate to '%s'", scriptFileName));
		}
		return false;
	}

	private static IEditorPart getScriptEditor(String scriptFileName) {
		File file = new File(scriptFileName);
		String fileName = file.getName();
		
		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorReference[] editorReferences = page.getEditorReferences();
		for (IEditorReference editorRef : editorReferences) {
			if (editorRef.getPartName().equals(fileName)) {
				return editorRef.getEditor(true);
			}
		}
		return null;
	}

	public static void closeDisassembly() {
		s_disassemblyInputs.clear();
	}

	public static void runCommandInConsole(String command) {
		runCommandInConsole(command, s_consoleViaGuiSrc);
	}

	public static void runCommandInConsole(String command, CommandSource src) {
		ConsoleView console = (ConsoleView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
		        getActivePage().findView(ConsoleView.ID);
	
	    try {
		    PlatformUI.getWorkbench().getActiveWorkbenchWindow().
		        getActivePage().showView(ConsoleView.ID);
	    } catch (PartInitException e) {}
	
	    console.runCommand(command, src);
	}
	
	private static Console tmpConsole; 
	public static Console getConsole() {
		tmpConsole = null;
		
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {      
				ConsoleView consoleView = (ConsoleView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				getActivePage().findView(ConsoleView.ID);

				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView(ConsoleView.ID);
				} catch (PartInitException e) {}

				tmpConsole = consoleView.getConsole(); 
			}
		});
		
		return tmpConsole;
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

}
