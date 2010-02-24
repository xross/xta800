package com.xmos.xta.core.engine;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import com.xmos.libs.dwarfhandler.DebugEngine;
import com.xmos.libs.proginfoloader.ProgInfoEntry;
import com.xmos.libs.proginfoloader.ProgInfoLoader;
import com.xmos.libs.xeloader.MemoryImage;
import com.xmos.libs.xeloader.SymbolTable;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.libs.xnloader.XnLoader;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.controlflow.FunctionConstructor;
import com.xmos.xta.core.engine.analysis.controlflow.FunctionReferenceNodeAdder;
import com.xmos.xta.core.engine.analysis.controlflow.SuccessorAdder;
import com.xmos.xta.core.engine.analysis.controlflow.UnreachableCodeFinder;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteConstructor;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer;
import com.xmos.xta.core.engine.analysis.routes.RouteDefines;
import com.xmos.xta.core.engine.analysis.routes.RouteDescription;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming;
import com.xmos.xta.core.engine.analysis.routes.RouteConstructor.RouteError;
import com.xmos.xta.core.engine.analysis.structural.StructuralAnalysisEngine;
import com.xmos.xta.core.engine.analysis.structural.StructureGraph;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.Source;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.defines.Defines;
import com.xmos.xta.core.engine.defines.FunctionTimeInfo;
import com.xmos.xta.core.engine.defines.InstructionTimeInfo;
import com.xmos.xta.core.engine.defines.LoopInfo;
import com.xmos.xta.core.engine.defines.LoopPathInfo;
import com.xmos.xta.core.engine.defines.LoopScopeInfo;
import com.xmos.xta.core.engine.defines.PathTimeInfo;
import com.xmos.xta.core.engine.instructions.DataCollection;
import com.xmos.xta.core.engine.instructions.InstructionMnemonics;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.core.engine.scripter.Scripter;
import com.xmos.xta.core.engine.sections.BranchInfo;
import com.xmos.xta.core.engine.sections.CodePointInfo;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;
import com.xmos.xta.core.engine.sections.Sections;
import com.xmos.xta.core.engine.sections.SrcLabelTableInfo;
import com.xmos.xta.core.engine.sections.SrcLevelLoopInfo;
import com.xmos.xta.core.engine.sections.SrcStringInfo;
import com.xmos.xta.core.engine.sourcemapper.SourceFile;
import com.xmos.xta.core.engine.sourcemapper.SourceMapper;

public class Engine implements Notifier, Aborter {
	public static final int ROUTE_ID_LAST     = -1;
	public static final int ROUTE_ID_INVALID  = -2;
	public static final int ROUTE_ID_GLOBAL   = -3;
	public static final int ROUTE_ID_ALL      = -4;
	public static final int NODE_ID_TOP_LEVEL = -5;

	private final static boolean LAZY = true;

	private XeLoader m_xeLoader;
	private List<Route> m_routes;
	private List<RouteContainer> m_routeContainers;
	private int m_numRoutesLastAnalysed;
	private Collection<Reference> m_exclusions;
	private Route m_selectedRoute;
	private Config m_config;
	private File m_executableFile;
	private boolean m_wasReload;
	private int m_nodeNumber;
	private int m_coreNumber;
	private Notifier m_notifier;
	private ProgressMonitor m_progressMonitor;
	private String m_srcPaths;
	private Sections m_sections;
	private Defines m_defines;
	private Collection<String> m_functionsVisited;
	private int m_timeOut;
	private int m_numErrors;
	private long m_loadStartTime;
	private Map<Integer, Long> m_nodeHz;
	private NodeCoreCollection<Integer> m_coreThreads;
	private NodeCoreCollection<Boolean> m_structuralGraphUpToDate;
	private NodeCoreCollection<InstructionDecoder> m_instructionDecoder;
	private NodeCoreCollection<SourceMapper> m_sourceMapper;
	private NodeCoreCollection<StructureGraph> m_structuralGraph;
	private NodeCoreCollection<Map<String, List<InstructionNode>>> m_functionNameToInsts;
	private Collection<FileLocation> m_userSpecifedFileLocations;
	private DataCollection m_dataCollection;
	private Scripter m_scripter;
	private Reference m_fromEndPoint;
	private Reference m_toEndPoint;

	public Engine(Config config) {
		m_sourceMapper = new NodeCoreCollection<SourceMapper>();
		m_structuralGraph = new NodeCoreCollection<StructureGraph>();
		m_functionNameToInsts = new NodeCoreCollection<Map<String, List<InstructionNode>>>();
		m_structuralGraphUpToDate = new NodeCoreCollection<Boolean>();
		m_instructionDecoder = new NodeCoreCollection<InstructionDecoder>();
		m_nodeHz = new TreeMap<Integer, Long>();
		m_coreThreads = new NodeCoreCollection<Integer>();

		m_xeLoader = new XeLoader();
		m_routes = new LinkedList<Route>();
		m_routeContainers = new LinkedList<RouteContainer>();
		m_numRoutesLastAnalysed = 0;
		m_exclusions = new ArrayList<Reference>();
		m_config = config;
		m_executableFile = null;
		m_wasReload = false;
		m_nodeNumber = 0;
		m_coreNumber = 0;
		m_srcPaths = ".";
		m_progressMonitor = null;
		m_sections = new Sections();
		m_defines = new Defines();
		m_timeOut = -1;
		m_numErrors = 0;
		m_dataCollection = new DataCollection();
		m_scripter = new Scripter(this);
		m_userSpecifedFileLocations = new ArrayList<FileLocation>();
		ReferenceTypeFactory.setEngine(this);
	}

	public void notify(CommandSource src, String message, String argument) {
		if (m_notifier != null){
			m_notifier.notify(src, message, argument);
		}

		if (message.equals(NOTIFY_ERROR)) {
			m_numErrors++;
		}
	}

	public Config getConfig() {
		return m_config;
	}

	public List<Route> getRoutes() {
		return m_routes;
	}

	public List<RouteContainer> getRouteContainers() {
		return m_routeContainers;
	}

	public XeLoader getXeLoader() {
		return m_xeLoader;
	}

	public Vector<MemoryImage> getMemoryImages() {
		return m_xeLoader.getMemoryImages();
	}

	public MemoryImage getMemoryImage() {
		return m_xeLoader.getMemoryImage(m_nodeNumber, m_coreNumber);
	}

	public SymbolTable getSymbolTable() {
		return m_xeLoader.getSymbolTable(m_nodeNumber, m_coreNumber);
	}

	public SourceMapper getSourceMapper() {
		return m_sourceMapper.get(m_nodeNumber, m_coreNumber);
	}

	public StructureGraph getStructureGraph() {
		return m_structuralGraph.get(m_nodeNumber, m_coreNumber);
	}

	public InstructionDecoder getInstructionDecoder(){
		return m_instructionDecoder.get(m_nodeNumber, m_coreNumber);
	}

	public Set<String> getFunctionNames() {
		Map<String, List<InstructionNode>> functionNames = m_functionNameToInsts.get(m_nodeNumber, m_coreNumber);
		if (functionNames != null) {
			Set<String> names = new TreeSet<String>(functionNames.keySet());
			names.removeAll(getUnreachableFunctions());
		    return names;
	    }
		return new TreeSet<String>();
	}

	public long getFunctionPc(String functionName) {
		List<InstructionNode> insts = m_functionNameToInsts.get(m_nodeNumber, m_coreNumber).get(functionName);
		assert((insts != null) && !insts.isEmpty());
		return insts.get(0).getStartPc();
	}

	public Collection<String> getNonSystemFunctionNames() {
		if (!m_xeLoader.isLoaded(m_nodeNumber, m_coreNumber))
			return new HashSet<String>();

		List<String> functionNames = new ArrayList<String>();
		functionNames.addAll(getFunctionNames());
		Collections.sort(functionNames);

		Set<String> systemFunctions = new HashSet<String>();
		Collection<String> systems = m_sections.getSystems(m_nodeNumber, m_coreNumber);
		for (String functionName : functionNames) {
			if (isSystemFunction(systems, functionName)) {
				systemFunctions.add(functionName);
			}
		}
		functionNames.removeAll(systemFunctions);
		return functionNames;
	}

	public String getSrcPaths() {
		if (m_srcPaths != null) {
			return m_srcPaths;
		} else {
			return ".";
		}
	}

	public void setSrcPaths(String srcPaths) {
		m_srcPaths = srcPaths;
		m_srcPaths += ";.";
	}

	public Collection<Route> getRoute(int routeId) {
		Collection<Route> routes = new ArrayList<Route>();

		if(m_routes.isEmpty()){
			return routes;
		}

		if (routeId == ROUTE_ID_LAST) {

			assert(m_routes.size() >= m_numRoutesLastAnalysed);
			int toIndex = m_routes.size();
			int fromIndex = toIndex - m_numRoutesLastAnalysed;
			routes.addAll(m_routes.subList(fromIndex, toIndex));

		} else if (routeId == ROUTE_ID_ALL) {
		    for (Route route : m_routes) {
				routes.add(route);
		    }

		} else {
		    for (Route route : m_routes) {
				if (route.getRouteId() == routeId) {
					routes.add(route);
					break;
				}
		    }
		}
		return routes;
	}

	public boolean loadExecutable(CommandSource src, File executableFile, CommandRunner commandRunner) {
		notify(src, Notifier.CLEAR_LOG, "");

		try {
    		executableFile = executableFile.getCanonicalFile();
		} catch (IOException e) {
			notify(src, Notifier.NOTIFY_ERROR, String.format("failed to resolve executable path: %s", executableFile.getName()));
			return false;
		}

		setAnalysisName("Loading XE");
		XeLoader xeLoader = new XeLoader();
		if (!xeLoader.loadExecutable(executableFile)) {
			notify(src, Notifier.NOTIFY_ERROR, "the file '" + executableFile + "' could not be loaded.");
			return false;
		}

		setAnalysisName("Loading sections");
		Sections sections = new Sections();
		if (!sections.load(src, xeLoader, this)) {
			return false;
		}

		m_wasReload = isReloaded(executableFile);
		if (!m_wasReload) {
			m_userSpecifedFileLocations.clear();
		}

		m_structuralGraphUpToDate = new NodeCoreCollection<Boolean>();
		if (LAZY){
			if (!initialiseGlobalStructure(src, xeLoader, sections, false, 0, 0))
			    return false;

		} else {
			for (int nodeNumber : xeLoader.getNodeNumbers()){
				for (int coreNumber : xeLoader.getCoreNumbers(nodeNumber)){
					initialiseGlobalStructure(src, xeLoader, sections, false, nodeNumber, coreNumber);
				}
			}
		}

		initialiseCoreStates(xeLoader);

		m_exclusions = new ArrayList<Reference>();
		m_executableFile = executableFile;
		m_xeLoader = xeLoader;
		m_sections = sections;
		m_nodeNumber = -1; // Ensure setNodeNumber does something
		setNodeNumber(src, 0, false);
		m_coreNumber = -1; // Ensure setCoreNumber does something
		setCoreNumber(src, 0, false);
		m_routes.clear();
		m_routeContainers.clear();
		m_numRoutesLastAnalysed = 0;
		m_selectedRoute = null;
		m_defines.clear();
		Route.resetRouteCount();
		m_fromEndPoint = null;
		m_toEndPoint = null;

		notify(src, Notifier.NOTIFY_LOAD_EXECUTABLE, "");

		runSrcLevelCommands(src, commandRunner);

		m_scripter.setEngineLoadAndRunDone();
		return true;
	}

	public void printInstructionReport(){
		m_dataCollection.report();
	}

	public boolean analyseRoute(CommandSource src, String functionName, boolean temporaryRoute) {
		Reference functionRef = ReferenceFactory.createReference(functionName, ReferenceClass.FUNCTION_REF);

		if (!m_xeLoader.isLoaded(m_nodeNumber, m_coreNumber)) {
			notify(src, Notifier.NOTIFY_ERROR, String.format(
					"node: %d, Core: %d image not found in file: %s", m_nodeNumber,
					m_coreNumber, m_executableFile));
			return false;

		} else if (!resolveOrError(src, functionRef)) {
			return false;

		} else {
			RouteDescription routeDescription = new RouteDescription(functionRef, this,
					m_nodeNumber, m_coreNumber, m_exclusions, m_defines, m_config);
			return analyseRouteSafely(src, routeDescription, temporaryRoute);
		}
	}

	public void analysePath(CommandSource src) {
		if ((m_fromEndPoint == null) || m_fromEndPoint.getRef().equals("")) {
			notify(src, Notifier.NOTIFY_ERROR, "please specify the from endpoint");

		} else if ((m_toEndPoint == null) || m_toEndPoint.getRef().equals("")) {
			notify(src, Notifier.NOTIFY_ERROR, "please specify the to endpoint");

		} else {
			RouteDescription routeDescription = new RouteDescription(
					m_fromEndPoint, m_toEndPoint, this, m_nodeNumber, m_coreNumber,
					m_exclusions, m_defines, m_config);

			analyseRouteSafely(src, routeDescription, false);
		}
	}

	public boolean setFromEndPoint(CommandSource src, String ref) {
		Reference fromEndPoint = ReferenceFactory.createReference(ref, ReferenceClass.ENDPOINT_REF);
		if (!fromEndPoint.resolve(this)) {
			notify(src, Notifier.NOTIFY_ERROR, String.format("'%s' is not a valid endpoint", ref));
			return false;

		} else {
			m_fromEndPoint = fromEndPoint;
			notify(src, Notifier.NOTIFY_FROM_END_POINT_SET, "");
			return true;
		}
	}

	public void removeFromEndPoint(CommandSource src) {
		if (m_fromEndPoint != null) {
			m_fromEndPoint = null;
			notify(src, Notifier.NOTIFY_FROM_END_POINT_SET, "");
		}
	}

	public boolean setToEndPoint(CommandSource src, String ref) {
		Reference toEndPoint = ReferenceFactory.createReference(ref, ReferenceClass.ENDPOINT_REF);
		if (!toEndPoint.resolve(this)) {
			notify(src, Notifier.NOTIFY_ERROR, String.format("'%s' is not a valid endpoint", ref));
			return false;

		} else {
			m_toEndPoint = toEndPoint;
			notify(src, Notifier.NOTIFY_TO_END_POINT_SET, "");
			return true;
		}
	}

	public void removeToEndPoint(CommandSource src) {
		if (m_toEndPoint != null) {
			m_toEndPoint = null;
			notify(src, Notifier.NOTIFY_TO_END_POINT_SET, "");
		}
	}

	public Reference getFromEndPoint() {
		return m_fromEndPoint;
	}

	public Reference getToEndPoint() {
		return m_toEndPoint;
	}

	public Notifier getNotifier() {
		return m_notifier;
	}

	public void setNotifier(Notifier notifier) {
		m_notifier = notifier;
	}

	public void getAbort() throws AbortedException {
		if (m_timeOut > 0 && (System.currentTimeMillis() - m_loadStartTime)/1000 > m_timeOut) {
			throw new AbortedException();
		}
		if (m_progressMonitor != null) {
			if (m_progressMonitor.isCanceled()) {
				throw new AbortedException();
			}
		}
	}

	public Scripter getScripter() {
		return m_scripter;
	}

	public void setNodeNumber(CommandSource src, int nodeNumber, boolean doNotify) {
		if (nodeNumber != m_nodeNumber) {
			if (initialiseGlobalStructure(src, m_xeLoader, m_sections, false, nodeNumber, m_coreNumber)) {
				m_nodeNumber = nodeNumber;
				m_config.m_frequency = getFrequency(m_nodeNumber);
				m_selectedRoute = null;

				if (doNotify) {
					notify(src, Notifier.NOTIFY_SET_NODE_CORE, "");
				}

			} else {
				notify(src, Notifier.NOTIFY_ERROR, String.format("Error initialising node: %d", nodeNumber));
			}
		}
	}

	public int getNodeNumber() {
		return m_nodeNumber;
	}

	public void setCoreNumber(CommandSource src, int coreNumber, boolean doNotify) {
		if (m_coreNumber != coreNumber) {
			if (initialiseGlobalStructure(src, m_xeLoader, m_sections, false, m_nodeNumber, coreNumber)) {
				m_coreNumber = coreNumber;
				m_config.m_numThreads = getNumThreads(m_nodeNumber, coreNumber);
				m_selectedRoute = null;

				if (doNotify) {
					notify(src, Notifier.NOTIFY_SET_NODE_CORE, "");
				}

			} else {
				notify(src, Notifier.NOTIFY_ERROR, String.format("Error initialising core: %d", coreNumber));
			}
		}
	}

	public long getNodeHz(int nodeNumber) {
		if (m_nodeHz.containsKey(nodeNumber)) {
			return m_nodeHz.get(nodeNumber);
		} else {
			return com.xmos.libs.xnloader.XmlHandler.DEFAULT_FREQUENCY_HZ;
		}
	}

	public int getCoreNumber() {
		return m_coreNumber;
	}

	public void setFrequency(CommandSource src, double frequency) {
		if (!checkFrequency(src, frequency)) {
			return;
		}

		m_config.m_frequency = frequency;
		m_nodeHz.put(m_nodeNumber, (long)(frequency * 1000000));
		notify(src, Notifier.NOTIFY_UPDATE_STATE, Notifier.STATE_FREQUENCY);
	}

	public void setFrequency(CommandSource src, int nodeNumber, double frequency) {
		if (!checkFrequency(src, frequency)) {
			return;
		}

		if (m_nodeNumber == nodeNumber) {
			setFrequency(src, frequency);
		} else {
			m_nodeHz.put(nodeNumber, (long)(frequency * 1000000));
		}
	}

	public double getFrequency() {
		return m_config.m_frequency;
	}

	public double getFrequency(int nodeNumber) {
		if (m_nodeHz.containsKey(nodeNumber)) {
			return (double)m_nodeHz.get(nodeNumber) / 1000000;
		} else {
			return Config.DEFAULT_MHZ;
		}
	}

	public void setNumThreads(CommandSource src, int numThreads) {
		if (!checkNumThreads(src, numThreads)) {
			return;
		}

		m_config.m_numThreads = numThreads;
		m_coreThreads.add(m_nodeNumber, m_coreNumber, numThreads);
		notify(src, Notifier.NOTIFY_UPDATE_STATE, Notifier.STATE_NUM_THREADS);
	}

	public void setNumThreads(CommandSource src, int nodeNumber, int coreNumber, int numThreads) {
		if (!checkNumThreads(src, numThreads)) {
			return;
		}

		if ((m_nodeNumber == nodeNumber) && (m_coreNumber == coreNumber)) {
			setNumThreads(src, numThreads);
		} else {
			m_coreThreads.add(nodeNumber, coreNumber, numThreads);
		}
	}

	public int getNumThreads() {
		return m_config.m_numThreads;
	}

	public int getNumThreads(int nodeNumber, int coreNumber) {
		Integer threads = m_coreThreads.get(nodeNumber, coreNumber);
		if (threads == null) {
			return Config.DEFAULT_THREADS;
		}
		return threads;
	}

	public void setCase(CommandSource src, Case c) {
		m_config.m_case = c;
		notify(src, Notifier.NOTIFY_UPDATE_STATE, Notifier.STATE_CASE);
	}

	public Case getCase() {
		return m_config.m_case;
	}

	public void setVerbosity(int verbosity) {
		m_config.m_verbosity = verbosity;
	}

	public int getVerbosity() {
		return m_config.m_verbosity;
	}

	public void setWarnAsErrors(boolean errorOn) {
		m_config.m_warnAsErrors = errorOn;
	}

	public void setTimingFailuresAsErrors(boolean errorsOn) {
		m_config.m_timingFailuresAsErrors = errorsOn;
	}

	public boolean canInstructionPause(int routeId, String refString) {
		Reference reference = ReferenceFactory.createReference(refString, ReferenceClass.ENDPOINT_REF);
		if (!resolveReference(reference)) {
			return false;
		}
		try {
			for (Route route : getRoute(routeId)) {
					if (route.getRouteUnknowns().canInstructionPause(reference))
						 return true;
			}
		} catch (AbortedException e) { }
		return false;
	}

	public boolean removeRoute(CommandSource src, int routeId) {
		Collection<Route> routes = getRoute(routeId);
		if (routes.size() == 0)
			return false;

		for (Route route : routes) {
			route.getRouteContainer().remove(route);
		}

		m_routes.removeAll(routes);
		if (m_routes.size() == 0) {
			Route.resetRouteCount();
			setSelectedRoute(src, null);

		} else if (m_selectedRoute.getRouteId() == routeId) {
			this.setSelectedRoute(src, m_routes.get(m_routes.size() - 1));
		}
		notify(src, Notifier.NOTIFY_REMOVE_ROUTE, "");
		return true;
	}

	public void removeAllRoutes(CommandSource src) {
		m_routes.clear();
		setSelectedRoute(src, null);
		Route.resetRouteCount();
		notify(src, Notifier.NOTIFY_REMOVE_ROUTE, "");
	}

	public File getExecutable() {
		return m_executableFile;
	}

	public void setMonitor(ProgressMonitor monitor) {
		m_progressMonitor = monitor;
	}

	public void provideFeedback(String feedback) {
		if (m_progressMonitor != null) {
			m_progressMonitor.subTask(feedback);
		}
	}

	public void startAnalysis(String task, int workToDo) {
		if (m_progressMonitor != null) {
			m_progressMonitor.beginTask(task, workToDo);
		}
	}

	public void setAnalysisName(String name) {
		if (m_progressMonitor != null) {
			m_progressMonitor.setTaskName(name);
		}
	}

	public void provideWorkDone(int work) {
		if (m_progressMonitor != null) {
			m_progressMonitor.worked(work);
		}
	}

	public boolean isBusy() {
		return m_progressMonitor != null;
	}

	public Collection<String> getFunctionsVisited() {
		return m_functionsVisited;
	}

	public boolean wasReload() {
		return m_wasReload;
	}

	public void updateSourceMappers(CommandSource src, Notifier notifier, String fileName, File file) {
		for (int nodeNumber : m_xeLoader.getNodeNumbers()) {
			for (int coreNumber : m_xeLoader.getCoreNumbers(nodeNumber)) {
				SourceMapper sourceMapper = m_sourceMapper.get(nodeNumber, coreNumber);
				if (sourceMapper != null) {
					updateSourceMapper(src, notifier, sourceMapper, fileName, file, nodeNumber, coreNumber);
				}
				m_userSpecifedFileLocations.add(new FileLocation(nodeNumber, coreNumber, fileName, file));
			}
		}
	}

	public void setAnalysisTimeOut(int time) {
		m_timeOut = time;
	}

	public int getNumErrors() {
		return m_numErrors;
	}

	// ===================================================================== //
	// Route Selection Methods
	// ===================================================================== //
	public Route getSelectedRoute() {
		return m_selectedRoute;
	}

	public void setSelectedRoute(CommandSource src, Route route) {
		if (route != null) {
			RouteTiming routeTiming = route.getRouteTiming();
			int nodeNumber = routeTiming.getNodeNumber();
			if (nodeNumber != m_nodeNumber)
				setNodeNumber(src, nodeNumber, true);

			int coreNumber = routeTiming.getCoreNumber();
			if (coreNumber != m_coreNumber)
				setCoreNumber(src, coreNumber, true);
		}
		m_selectedRoute = route;
		notify(src, Notifier.NOTIFY_SELECT_ROUTE, "");
	}

	public int getSelectedRouteId() {
		if (m_selectedRoute != null)
			return m_selectedRoute.getRouteId();
		else
			return ROUTE_ID_INVALID;
	}

	public void setSelectedRouteId(CommandSource src, int routeId) {
		assert((routeId != ROUTE_ID_INVALID) || (routeId != ROUTE_ID_LAST));
		Collection<Route> routes = getRoute(routeId);
		assert(routes.size() == 1);
		setSelectedRoute(src, routes.iterator().next());
	}

	// ===================================================================== //
	// Requirements Methods
	// ===================================================================== //
	public boolean setRequiredTime(CommandSource src, int routeId, TimeUnit timeUnit) {
		Collection<Route> routes = getRoute(routeId);
		if (routes.isEmpty())
			return false;

		for (Route route : routes) {
			route.getRouteTiming().setMaxTime(src, timeUnit);
		}
		notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
		return true;
	}

	// ===================================================================== //
	// Defines Methods
	// ===================================================================== //
	public void addFunctionTime(CommandSource src, String functionName, TimeUnit timeUnit) {
		Reference functionRef = ReferenceFactory.createReference(functionName, ReferenceClass.FUNCTION_REF);
		if (!resolveOrError(src, functionRef)) {
			return;
		}
		m_defines.addFunctionTime(m_nodeNumber, m_coreNumber, functionRef, timeUnit);
		notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
	}

	public void addPathTime(CommandSource src, String from, String to, TimeUnit timeUnit) {
		Reference fromRef = ReferenceFactory.createReference(from, ReferenceClass.ENDPOINT_REF);
		if (!resolveOrError(src, fromRef)) {
			return;
		}
		Reference toRef = ReferenceFactory.createReference(to, ReferenceClass.ENDPOINT_REF);
		if (!resolveOrError(src, toRef)) {
			return;
		}

		m_defines.addPathTime(m_nodeNumber, m_coreNumber, fromRef, toRef, timeUnit);
		notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
	}

	public void addInstructionTime(CommandSource src, String refString, final TimeUnit timeUnit) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.addInstructionTime(m_nodeNumber, m_coreNumber, reference, timeUnit);
			}
		});
	}

	public void addLoop(CommandSource src, String refString, final long iterations) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.addLoop(m_nodeNumber, m_coreNumber, reference, iterations);
			}
		});
	}

	public void addLoopPath(CommandSource src, String refString, final long iterations) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.addLoopPath(m_nodeNumber, m_coreNumber, reference, iterations);
			}
		});
	}

	public void addLoopScope(CommandSource src, String refString, final boolean absolute) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.addLoopScope(m_nodeNumber, m_coreNumber, reference, absolute);
			}
		});
	}

	public void removeFunctionTime(CommandSource src, String functionName) {
		Reference functionRef = ReferenceFactory.createReference(functionName, ReferenceClass.FUNCTION_REF);
		if (!resolveOrError(src, functionRef)) {
			return;
		}
		m_defines.removeFunctionTime(m_nodeNumber, m_coreNumber, functionRef);
		notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
	}

	public void removePathTime(CommandSource src, String from, String to) {
		Reference fromRef = ReferenceFactory.createReference(from, ReferenceClass.ENDPOINT_REF);
		if (!resolveOrError(src, fromRef)) {
			return;
		}
		Reference toRef = ReferenceFactory.createReference(to, ReferenceClass.ENDPOINT_REF);
		if (!resolveOrError(src, toRef)) {
			return;
		}

		m_defines.removePathTime(m_nodeNumber, m_coreNumber, fromRef, toRef);
		notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
	}

	public void removeInstructionTime(CommandSource src, String refString) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.removeInstructionTime(m_nodeNumber, m_coreNumber, reference);
			}
		});
	}

	public void removeLoop(CommandSource src, String refString) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.removeLoop(m_nodeNumber, m_coreNumber, reference);
			}
		});
	}

	public void removeLoopPath(CommandSource src, String refString) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.removeLoopPath(m_nodeNumber, m_coreNumber, reference);
			}
		});
	}

	public void removeLoopScope(CommandSource src, String refString) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.removeLoopScope(m_nodeNumber, m_coreNumber, reference);
			}
		});
	}

	public Collection<FunctionTimeInfo> getFunctionTimeDefines() {
		return m_defines.getFunctionTimes(m_nodeNumber, m_coreNumber);
	}

	public Collection<PathTimeInfo> getPathTimeDefines() {
		return m_defines.getPathTimes(m_nodeNumber, m_coreNumber);
	}

	public Collection<InstructionTimeInfo> getInstructionTimeDefines() {
		return m_defines.getInstructionTimes(m_nodeNumber, m_coreNumber);
	}

	public Collection<LoopInfo> getLoopDefines() {
		return m_defines.getLoops(m_nodeNumber, m_coreNumber);
	}

	public Collection<LoopPathInfo> getLoopPathDefines() {
		return m_defines.getLoopPaths(m_nodeNumber, m_coreNumber);
	}

	public Collection<LoopScopeInfo> getLoopScopeDefines() {
		return m_defines.getLoopScopes(m_nodeNumber, m_coreNumber);
	}

	public Defines getDefines() {
		return m_defines;
	}

	// ===================================================================== //
	// Set Function/Path Methods
	// ===================================================================== //
	public void setFunctionTime(CommandSource src, int routeId, String functionName, TimeUnit timeUnit) {
		Reference functionRef = ReferenceFactory.createReference(functionName, ReferenceClass.FUNCTION_REF);
		if (!resolveOrError(src, functionRef)) {
			return;
		}
		FunctionTimeInfo info = new FunctionTimeInfo(functionRef, timeUnit);
		RouteDefines defines = new RouteDefines();
		try {
			for (Route route : getRoute(routeId)) {
				defines.applyFunctionDefine(src, route, info, Source.USER);
			}
		} catch (AbortedException e) {	}
	}

	public void setFunctionTime(CommandSource src, Node node, TimeUnit timeUnit) {
		if (node.isFunctionNode()) {
			Route route = getSelectedRoute();
			route.getRouteUnknowns().setFixedTime(node, timeUnit, Type.FUNCTION_TIME, Source.USER);
		}
	}

	public void setPathTime(CommandSource src, int routeId, String from, String to, TimeUnit timeUnit) {
		Reference fromRef = ReferenceFactory.createReference(from, ReferenceClass.ENDPOINT_REF);
		if (!resolveOrError(src, fromRef)) {
			return;
		}
		Reference toRef = ReferenceFactory.createReference(to, ReferenceClass.ENDPOINT_REF);
		if (!resolveOrError(src, toRef)) {
			return;
		}

		PathTimeInfo info = new PathTimeInfo(fromRef, toRef, timeUnit);
		RouteDefines defines = new RouteDefines();
		try {
			for (Route route : getRoute(routeId)) {
				defines.applyPathDefine(this, src, route, info, Source.USER);
			}
		} catch (AbortedException e) { }

		notify(src, Notifier.NOTIFY_ROUTE_CHANGED, String.format("%d", routeId));
	}

	// ===================================================================== //
	// Set Loop Methods
	// ===================================================================== //
	public void setLoop(CommandSource src, int routeId, String refString, long iterations) {
		Reference reference = ReferenceFactory.createReference(refString, ReferenceClass.ANY_REF);
		if (!resolveOrError(src, reference)) {
			return;
		}
		Collection<Route> routes = getRoute(routeId);
		if (routes.size() == 0) {
			notify(src, Notifier.NOTIFY_ERROR, String.format("Failed to find route: %d", routeId));
			return;
		}
		try {
			applyLoopToRoutes(src, routes, new LoopInfo(reference, iterations), Source.USER);
		} catch (Exception e) {
			notify(src, Notifier.NOTIFY_ERROR, e.getMessage());
			return;
		}
	    notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
	}

	public void setLoop(CommandSource src, Node node, long iterations) {
		try {
			m_selectedRoute.getRouteUnknowns().setLoopIterations(src, this, node, iterations, Source.USER);
			notify(src, Notifier.NOTIFY_UPDATE_STATE, "");

		} catch (Exception e) {
			notify(src, Notifier.NOTIFY_ERROR, e.getMessage());
		}
	}

	public boolean canSetLoop(Node node) {
		try {
			m_selectedRoute.getRouteUnknowns().checkSetLoopIterations(node);
			return true;

		} catch (Exception e) {
			return false;
		}
	}

	public long getSetLoop(Node node) {
		return m_selectedRoute.getRouteUnknowns().getSetLoopIterations(node);
	}

	public void applyLoopToRoutes(CommandSource src, Collection<Route> routes,
			LoopInfo info, Source source) throws Exception {

		for (Route route : routes) {
			route.getRouteUnknowns().checkSetLoopIterations(
					info.getReference(), info.getIterations());
		}
		for (Route route : routes) {
		    route.getRouteUnknowns().setLoopIterations(src, this,
		    		info.getReference(), info.getIterations(), false, source);
		}
	}

	// ===================================================================== //
	// Set Loop Path Methods
	// ===================================================================== //
	public void setLoopPath(CommandSource src, int routeId, String refString, long iterations) {
		Reference reference = ReferenceFactory.createReference(refString, ReferenceClass.ANY_REF);
		if (!resolveOrError(src, reference)) {
			return;
		}
		Collection<Route> routes = getRoute(routeId);
		if (routes.size() == 0) {
			notify(src, Notifier.NOTIFY_ERROR, String.format("Failed to find route: %d", routeId));
			return;
		}
		try {
			applyLoopPathToRoutes(routes, new LoopPathInfo(reference, iterations), Source.USER);
		} catch (Exception e) {
			notify(src, Notifier.NOTIFY_ERROR, e.getMessage());
			return;
		}
		notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
	}

	public void setLoopPath(CommandSource src, Node node, long iterations) {
		try {
			m_selectedRoute.getRouteUnknowns().setLoopPathIterations(node, iterations, Source.USER);
			notify(src, Notifier.NOTIFY_UPDATE_STATE, "");

		} catch (Exception e) {
			notify(src, Notifier.NOTIFY_ERROR, e.getMessage());
		}
	}

	public boolean canSetLoopPath(Node node) {
		try {
			m_selectedRoute.getRouteUnknowns().checkSetLoopPathIterations(node);
			return true;

		} catch (Exception e) {
			return false;
		}
	}

	public long getSetLoopPath(Node node) {
		return m_selectedRoute.getRouteUnknowns().getSetLoopPathIterations(node);
	}

	public void applyLoopPathToRoutes(Collection<Route> routes,
			LoopPathInfo info, Source source) throws Exception {

		for (Route route : routes) {
			route.getRouteUnknowns().checkSetLoopPathIterations(
					info.getReference(), info.getIterations());
		}
		for (Route route : routes) {
		    route.getRouteUnknowns().setLoopPathIterations(
		    		info.getReference(), info.getIterations(), source);
		}
	}

	// ===================================================================== //
	// Set Loop Scope Methods
	// ===================================================================== //
	public void setLoopScope(CommandSource src, int routeId, String refString, boolean absolute) {
		Reference reference = ReferenceFactory.createReference(refString, ReferenceClass.ANY_REF);
		if (!resolveOrError(src, reference)) {
			return;
		}
		Collection<Route> routes = getRoute(routeId);
		if (routes.size() == 0) {
			notify(src, Notifier.NOTIFY_ERROR, String.format("Failed to find route: %d", routeId));
			return;
		}
		try {
			applyLoopScopeToRoutes(routes, new LoopScopeInfo(reference, absolute), Source.USER);
		} catch (Exception e) {
			notify(src, Notifier.NOTIFY_ERROR, e.getMessage());
			return;
		}
		notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
	}

	public void setLoopScope(CommandSource src, Node node, boolean absolute) {
		try {
		    m_selectedRoute.getRouteUnknowns().setLoopScope(node, absolute, Source.USER);
			notify(src, Notifier.NOTIFY_UPDATE_STATE, "");

    	} catch (Exception e) {
	    	notify(src, Notifier.NOTIFY_ERROR, e.getMessage());
	    }
	}

	public boolean canSetLoopScope(Node node) {
		try {
			m_selectedRoute.getRouteUnknowns().checkSetLoopScope(node);
			return true;

		} catch (Exception e) {
			return false;
		}
	}

	public boolean getSetLoopScope(Node node) {
		return m_selectedRoute.getRouteUnknowns().getSetLoopScope(node);
	}

	public void applyLoopScopeToRoutes(Collection<Route> routes,
			LoopScopeInfo info, Source source) throws Exception {

		for (Route route : routes) {
			route.getRouteUnknowns().checkSetLoopScope(
					info.getReference(), info.getAbsolute());
		}
		for (Route route : routes) {
		    route.getRouteUnknowns().setLoopScope(
		    		info.getReference(), info.getAbsolute(), source);
		}
	}

	// ===================================================================== //
	// Instruction Time Methods
	// ===================================================================== //
	public boolean setInstructionTime(CommandSource src, int routeId, String refString, TimeUnit timeUnit) {
		Reference reference = ReferenceFactory.createReference(refString, ReferenceClass.ENDPOINT_REF);
		if (!resolveReference(reference)) {
			return false;
		}
		Collection<Route> routes = getRoute(routeId);
		if (routes.isEmpty()) {
			return false;
		}
		if (applyInstructionTimeToRoutes(routes, new InstructionTimeInfo(reference, timeUnit), Source.USER)) {
		    notify(src, Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
		    return true;
		}
		return false;
	}

	public void setInstructionTime(CommandSource src, Node node, TimeUnit timeUnit) {
		m_selectedRoute.getRouteUnknowns().setFixedTime(node, timeUnit, Type.INSTRUCTION_TIME, Source.USER);
		notify(src, Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
	}

	public TimeUnit getInstructionTime(Node node) {
		return m_selectedRoute.getRouteUnknowns().getFixedTime(node);
	}

	public boolean applyInstructionTimeToRoutes(Collection<Route> routes,
			InstructionTimeInfo info, Source source) {

	    try {
			for (Route route : routes) {
					if (!route.getRouteUnknowns().canInstructionPause(info.getReference()))
						return false;
			}
			for (Route route : routes) {
			    route.getRouteUnknowns().setFixedTime(info.getReference(), info.getTimeUnit(), Type.INSTRUCTION_TIME, source);
			}
		} catch (AbortedException e) { }
		return true;
	}

	// ===================================================================== //
	// Exclusion Methods
	// ===================================================================== //
	public boolean addExclusion(CommandSource src, String refString) {
		Reference reference = ReferenceFactory.createReference(refString, ReferenceClass.ANY_REF);
		if (!resolveOrError(src, reference)) {
			return false;
		}
		if (alreadyExcluded(reference)) {
			notify(src, Notifier.NOTIFY_ERROR, String.format("cannot add duplicate exclusion for ref: %s", reference.getRef()));
			return false;
		}
		m_exclusions.add(reference);
		notify(src, Notifier.NOTIFY_UPDATE_EXCLUSIONS, "");
		return true;
	}

	private boolean alreadyExcluded(Reference ref) {
		for (Reference exclusion : m_exclusions) {
			if (exclusion.equals(ref)) {
				return true;
			}
		}
		return false;
	}

	public boolean removeExclusion(CommandSource src, String refString) {
		Reference reference = ReferenceFactory.createReference(refString, ReferenceClass.ANY_REF);
		if (!resolveOrError(src, reference)) {
			return false;
		}
		Iterator<Reference> iter = m_exclusions.iterator();
		while (iter.hasNext()) {
			Reference ref = iter.next();
			if (ref.equals(reference)) {
				iter.remove();
			}
		}
		notify(src, Notifier.NOTIFY_UPDATE_EXCLUSIONS, "");
		return true;
	}

	public Collection<Reference> getExclusions() {
		return m_exclusions;
	}

	// ===================================================================== //
	// Section Methods
	// ===================================================================== //
	public boolean addBranch(CommandSource src, Reference fromRef, Collection<Reference> toRefs) {
		if (!resolveOrError(src, fromRef)) {
			return false;
		}
		checkInstructionIsBranch(src, fromRef);

		for (Reference toRef : toRefs) {
			if (!resolveOrError(src, toRef)) {
				return false;
			}
		}

		if (m_sections.addToBranches(src, m_nodeNumber, m_coreNumber, fromRef, toRefs)) {
			m_structuralGraphUpToDate.add(m_nodeNumber, m_coreNumber, false);
			return true;
		}
		return false;
	}

	private void checkInstructionIsBranch(CommandSource src, Reference ref) {
		for (long pc : ref.getPcs()) {
			InstructionNode node = m_sourceMapper.get(m_nodeNumber, m_coreNumber).getInstructionNode(pc);
			if (node == null) {
				notify(src, Notifier.NOTIFY_WARNING,
						String.format("adding branch from non-existant instruction at %s",
						ref.toString()));
				return;
			}

			XtaInstruction inst = node.getXtaInstruction();
			InstructionMnemonics mnenonics = new InstructionMnemonics(m_xeLoader.getArchitecture());
			if (!inst.isBranch() && !mnenonics.isWait(inst)) {
				notify(src, Notifier.NOTIFY_WARNING,
						String.format("adding branch from instruction which is not a branch: %s",
						inst.toString()));
				return;
			}
		}
	}

	public boolean removeBranch(CommandSource src, Reference fromRef, Collection<Reference> toRefs) {
		if (!resolveOrError(src, fromRef))
			return false;

		for (Reference toRef : toRefs) {
			if (!resolveOrError(src, toRef)) {
				return false;
			}
		}

		if (m_sections.removeFromBranches(src, m_nodeNumber, m_coreNumber, fromRef, toRefs)) {
			m_structuralGraphUpToDate.add(m_nodeNumber, m_coreNumber, false);
			return true;
		}
		return false;
	}

	public Map<Reference, List<Reference>> getUserBranches() {
		Map<Reference, List<Reference>> branches = new TreeMap<Reference, List<Reference>>();
		Map<Reference, Set<BranchInfo>> currentBranches = getBranches();

		for (Reference from : currentBranches.keySet()) {
			Set<BranchInfo> to = currentBranches.get(from);
			List<Reference> toRefs = new ArrayList<Reference>();
			for (BranchInfo info : to) {
				if (info.isUser()) {
					toRefs.add(info.getRef());
				}
			}
			if (toRefs.size() > 0) {
				branches.put(from, toRefs);
			}
		}
		return branches;
	}

	public Map<Reference, Set<BranchInfo>> getBranches() {
		return m_sections.getBranches(m_nodeNumber, m_coreNumber);
	}

	public Map<String, Set<Long>> getLabels() {
		return m_sections.getLabels(m_nodeNumber, m_coreNumber);
	}

	public List<CodePointInfo> getSrcLabels() {
		return m_sections.getSrcLabels(m_nodeNumber, m_coreNumber);
	}

	public Map<Long, SrcLabelTableInfo> getSrcLabelTable() {
		return m_sections.getSrcLabelTable(m_nodeNumber, m_coreNumber);
	}

	public List<CodePointInfo> getEndpoints() {
		return m_sections.getEndpoints(m_nodeNumber, m_coreNumber);
	}

	public Map<Long, CodePointTableInfo> getEndpointTable() {
		return m_sections.getEndpointTable(m_nodeNumber, m_coreNumber);
	}

	public List<CodePointInfo> getCalls() {
		return m_sections.getCalls(m_nodeNumber, m_coreNumber);
	}

	public Map<Long, CodePointTableInfo> getCallTable() {
		return m_sections.getCallTable(m_nodeNumber, m_coreNumber);
	}

	public Collection<Long> getThreadStartPoints() {
		return m_sections.getThreadStartPoints(m_nodeNumber, m_coreNumber);
	}

	public Collection<Long> getThreadStopPoints() {
		return m_sections.getThreadStopPoints(m_nodeNumber, m_coreNumber);
	}

	public Collection<SrcStringInfo> getSrcLevelCommands() {
		return m_sections.getSrcLevelCommands(m_nodeNumber, m_coreNumber);
	}

	public Collection<SrcLevelLoopInfo> getSrcLevelLoops() {
		return m_sections.getSrcLevelLoops(m_nodeNumber, m_coreNumber);
	}

	public Collection<String> getSystems() {
		return m_sections.getSystems(m_nodeNumber, m_coreNumber);
	}

	public Collection<String> getRecursiveFunctions() {
		return m_sections.getRecursiveFunctions(m_nodeNumber, m_coreNumber);
	}


	public Collection<String> getUnreachableFunctions() {
		return m_sections.getUnreachableFunctions(m_nodeNumber, m_coreNumber);
	}

	// ===================================================================== //
	// Private Methods
	// ===================================================================== //
	private boolean initialiseGlobalStructure(CommandSource src, XeLoader xeLoader, Sections sections,
			boolean clearPrevious, int nodeNumber, int coreNumber) {

		if (clearPrevious) {
			m_structuralGraph = null;
			m_functionNameToInsts = null;
			m_sourceMapper = null;
		}

		m_loadStartTime = System.currentTimeMillis();
		notify(src, Notifier.HOLD_MESSAGES, "");
		NodeCoreCollection<StructureGraph> newStructuralGraph =
			new NodeCoreCollection<StructureGraph>(m_structuralGraph);
		NodeCoreCollection<InstructionDecoder> newInstructionDecoder =
			new NodeCoreCollection<InstructionDecoder>(m_instructionDecoder);
		NodeCoreCollection<Map<String, List<InstructionNode>>> newFunctionNames =
			new NodeCoreCollection<Map<String, List<InstructionNode>>>(m_functionNameToInsts);
		NodeCoreCollection<SourceMapper> newSourceMappers =
			new NodeCoreCollection<SourceMapper>(m_sourceMapper);

		if (m_structuralGraphUpToDate.get(nodeNumber, coreNumber) != null &&
			m_structuralGraphUpToDate.get(nodeNumber, coreNumber)) {
			//System.out.println("node " + nodeNumber + " core " + coreNumber + " is up to date");

		} else {

			setAnalysisName(String.format("Processing node: %d, core: %d", nodeNumber, coreNumber));
			InstructionMnemonics mnenonics = new InstructionMnemonics(m_xeLoader.getArchitecture());

			//Binary Stage
			InstructionDecoder id = new InstructionDecoder(nodeNumber, coreNumber, xeLoader, m_config, m_dataCollection);

			try {
				id.decode();
			} catch (Exception e) {
				notify(src, Notifier.NOTIFY_ERROR, e.getMessage());
				return false;
			}

			newInstructionDecoder.add(nodeNumber, coreNumber, id);
			//Control Flow Stage

			newFunctionNames.add(nodeNumber, coreNumber, new TreeMap<String, List<InstructionNode>>());

			//collect the thread starts
			//collect the function targets
			//collect the thread ends
			//collect the function ends
			FunctionConstructor fc = new FunctionConstructor(id, this, sections, nodeNumber, coreNumber, src, mnenonics);

			try {
				fc.process();
			} catch (Exception e) {
				notify(src, Notifier.NOTIFY_ERROR, e.getMessage());
				return false;
			}


			Set<InstructionNode> functionStartNodes = fc.getFunctionStarts();
			Set<InstructionNode> functionEndNodes = fc.getFunctionEnds();

			//add all the successors
			SuccessorAdder sa = new SuccessorAdder(nodeNumber, coreNumber, id, sections, functionEndNodes, this, src, mnenonics);
			sa.process();

			//add function reference nodes
			FunctionReferenceNodeAdder frna = new FunctionReferenceNodeAdder(id, m_notifier, src, xeLoader, nodeNumber, coreNumber);
			frna.process();

			//add terminators(null nodes) to each of the functions
			TerminatorAdder ta = new TerminatorAdder(functionStartNodes);
			ta.process();

			UnresolvedSetter us = new UnresolvedSetter(functionStartNodes);

			us.process();

			//name all the functions
			ensureAllFunctionsAreNamed(xeLoader.getSymbolTable(nodeNumber, coreNumber),
					newFunctionNames.get(nodeNumber, coreNumber), functionStartNodes, sections.getLabels(nodeNumber, coreNumber));

			setBranchTargets(id, newFunctionNames.get(nodeNumber, coreNumber));

			UnreachableCodeFinder uca = new UnreachableCodeFinder();
			uca.analyseReachable(id, functionStartNodes);

			//Structural Stage

			StructuralAnalysisEngine sae = new StructuralAnalysisEngine(xeLoader);
			try {
				if (!sae.analyse(this, functionStartNodes, newFunctionNames.get(nodeNumber, coreNumber), sections, nodeNumber, coreNumber)) {
					return false;
				}
			} catch (AbortedException e) {
				System.err.println("problem");
				return false;
			}


			newStructuralGraph.add(nodeNumber, coreNumber, sae.getStructureGraph());

			//Source Mapping Stage

			SourceMapper sourceMapper;
			try {
				sourceMapper = new SourceMapper(src, this, xeLoader, nodeNumber, coreNumber, functionStartNodes,
						id, newFunctionNames.get(nodeNumber, coreNumber), sections.getLabels(nodeNumber, coreNumber));

			} catch (AbortedException e) {
				return false;
			}

			for (FileLocation fileLocation : m_userSpecifedFileLocations) {
				if ((fileLocation.m_nodeNumber == nodeNumber) && (fileLocation.m_coreNumber == coreNumber)) {
					updateSourceMapper(src, m_notifier, sourceMapper,
							fileLocation.m_fileName, fileLocation.m_file, nodeNumber, coreNumber);
				}
			}

			newSourceMappers.add(nodeNumber, coreNumber, sourceMapper);
		}

		m_structuralGraphUpToDate.add(nodeNumber, coreNumber, true);
		m_structuralGraph = newStructuralGraph;
		m_functionNameToInsts = newFunctionNames;
		m_sourceMapper = newSourceMappers;
		m_instructionDecoder = newInstructionDecoder;
		notify(src, Notifier.RELEASE_MESSAGES, "");
		return true;
	}

	private boolean analyseRouteSafely(CommandSource src, RouteDescription routeDescription, boolean temporaryRoute) {
		boolean success = false;
		try {
			success = doRouteAnalysis(src, routeDescription, temporaryRoute);

		} catch (final java.lang.OutOfMemoryError e) {
			notify(src, Notifier.NOTIFY_ERROR, "out of memory (heap)");
			return false;

		} catch (final java.lang.StackOverflowError e) {
			notify(src, Notifier.NOTIFY_ERROR, "out of memory (stack)");
			return false;

		} catch (AbortedException e) {
			return false;

		} catch (Exception e) {
			notify(src, Notifier.NOTIFY_ERROR, String.format("internal error detected (%s)", e.getMessage()));
			e.printStackTrace();
			return false;
		}
		return success;
	}

	private boolean doRouteAnalysis(CommandSource src, RouteDescription routeDescription, boolean temporaryRoute) throws Exception {
		if (!m_xeLoader.isLoaded(m_nodeNumber, m_coreNumber)) {
			notify(src, Notifier.NOTIFY_ERROR, String.format(
					"node: %d, Core: %d image not found in file: %s",
					m_nodeNumber, m_coreNumber, m_executableFile));
			return false;
		}

		if (!temporaryRoute) {
		    notify(src, Notifier.NOTIFY_ANALYSIS_BEGIN, "");
		}

		if (!m_structuralGraphUpToDate.get(m_nodeNumber, m_coreNumber)) {
			if (!initialiseGlobalStructure(src, m_xeLoader, m_sections, false, m_nodeNumber, m_coreNumber))
				return false;
		}

        RouteContainer routeContainer = new RouteContainer(src, routeDescription);
		RouteConstructor routeConstructor = new RouteConstructor(this, m_structuralGraph.get(m_nodeNumber, m_coreNumber), routeDescription, routeContainer);
		RouteError error = routeConstructor.analyse(src);

		if (error == RouteError.NO_ENDPOINTS_FOUND ) {
			notify(src, Notifier.NOTIFY_ERROR, "no paths of execution could reach the endpoint(s)");
			return false;

		} else if (error == RouteError.COULD_NOT_FIND_ENDPOINT ) {
			notify(src, Notifier.NOTIFY_ERROR, "could not find any end points");
			return false;

		} else if (error == RouteError.COULD_NOT_FIND_STARTPOINT ) {
			notify(src, Notifier.NOTIFY_ERROR,"could not find any start points");
			return false;

		} else if (error == RouteError.ENDPOINTS_MISSING ) {
			notify(src, Notifier.NOTIFY_ERROR, "could not determine the endpoint(s)");
			return false;

		} else if (error == RouteError.STARTPOINT_MISSING ) {
			notify(src, Notifier.NOTIFY_ERROR, "could not determine the startpoint");
			return false;

		} else if (error == RouteError.ABORTED ) {
			System.gc();
			return false;

		} else {
			int currentSize = m_routes.size();
			m_numRoutesLastAnalysed = 0;
			for (Route route : routeConstructor.getRoutes()) {
				m_routes.add(route);
				++m_numRoutesLastAnalysed;
			}
			if (!m_routes.isEmpty() && !routeConstructor.getRoutes().isEmpty()) {
				routeContainer.add(routeConstructor.getRoutes());
    			m_routeContainers.add(routeContainer);

				if (!temporaryRoute) {
					setSelectedRoute(src, m_routes.get(currentSize));
				    notify(src, Notifier.NOTIFY_ANALYSIS_COMPLETE, Integer.toString(m_routes.get(currentSize).getRouteId()));
				}
			} else {
				notify(src, Notifier.NOTIFY_WARNING, "no valid paths of execution");
			}
			return true;
		}
	}

	private void setBranchTargets(InstructionDecoder id, Map<String, List<InstructionNode>> treeMap) {

		for (InstructionNode inst : id.getInstructions()) {
			if (inst.getXtaInstruction().getBranchPcRelative()) {
				long branchTarget = inst.getXtaInstruction().getBranchPcRelativeTargetPc();
				inst.getXtaInstruction().setBranchPcRelativeTargetFunction("0x" + Long.toHexString(branchTarget));
				for (String fn : treeMap.keySet()) {
					for (InstructionNode fnInst : treeMap.get(fn)) {
						if (fnInst.getStartPc() == branchTarget){
							inst.getXtaInstruction().setBranchPcRelativeTargetFunction(fn);
							break;
						}
					}
				}
			}
		}
	}

	private boolean isSystemFunction(Collection<String> systems, String functionName) {
		if (functionName.length() == 0) {
			return false;
		} else if (functionName.charAt(0) == '_') {
			return true;
		} else if (systems.contains(functionName)) {
			return true;
		}

		return false;
	}


	private void ensureAllFunctionsAreNamed(
			SymbolTable symbolTable, Map<String, List<InstructionNode>> treeMap,
			Set<InstructionNode> funcStarts, Map<String, Set<Long>> labelToPc) {

		Map<Long, String> pcToLabel = new HashMap<Long, String>();
		for (String label : labelToPc.keySet()) {
			for (Long pc : labelToPc.get(label)) {
				pcToLabel.put(pc, label);
			}
		}

		for (InstructionNode inst : funcStarts) {
			String name = symbolTable.functionName(inst.getStartPc());
			String label = pcToLabel.get(inst.getXtaInstruction().getPc());

			if (treeMap.containsKey(name)) {
				//the one with the lower pc will stay in the set and the other will take an auto-generated name.

				LinkedList<InstructionNode> instNodes = new LinkedList<InstructionNode>(treeMap.get(name));
				for (InstructionNode instNode : instNodes) {
					if (instNode.getStartPc() < inst.getStartPc()) {
						if (label != null) {
							addToMap(treeMap, inst, label);
						} else {
							name = "unnamed_function_at_0x" + Long.toHexString(inst.getStartPc());
							addToMap(treeMap, inst, name);
						}

					} else if (instNode.getStartPc() > inst.getStartPc()) {
						String newName = "unnamed_function_at_0x" + Long.toHexString(instNode.getStartPc());
						addToMap(treeMap, instNode, newName);
						addToMap(treeMap, inst, name);
					} else {
						assert(false);
					}
				}
			} else {
				assert(!treeMap.containsKey(name));
				addToMap(treeMap, inst, name);
			}
		}
	}

	private void addToMap(Map<String, List<InstructionNode>> treeMap,
			InstructionNode inst, String name) {
		List<InstructionNode> list = treeMap.get(name);
		if (list == null){
			list = new LinkedList<InstructionNode>();
		}
		list.add(inst);
		treeMap.put(name, list);
	}

	private boolean resolveReference(Reference ref) {
		return ref.resolve(this);
	}

	private boolean resolveOrError(CommandSource src, Reference ref) {
		if (!resolveReference(ref)) {
			notify(src, Notifier.NOTIFY_ERROR, String.format("unresolved reference: %s", ref.getRef()));
			return false;
		}
		return true;
	}

	private boolean isReloaded(File executableFile) {
		if ((m_executableFile != null) &&
			m_executableFile.getAbsolutePath().equals(executableFile.getAbsolutePath())) {
			return true;
		}
		return false;
	}

    private void updateSourceMapper(CommandSource src, Notifier notifier, SourceMapper sourceMapper,
    		String fileName, File file, int nodeNumber, int coreNumber) {

        DebugEngine debugEngine = m_xeLoader.getDebugInfo(nodeNumber, coreNumber).getDebugEngine();
        SourceFile sf = sourceMapper.getSourceFile(fileName);
        sourceMapper.setFile(src, notifier, debugEngine, sf, file);
    }

	private void updateDefines(CommandSource src, String refString, DefinesUpdate update) {
		Reference reference = ReferenceFactory.createReference(refString, ReferenceClass.ANY_REF);
		if (resolveOrError(src, reference)) {
			update.run(reference);
			notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
		}
	}

	private static interface DefinesUpdate {
		void run(Reference reference);
	}

	private void runSrcLevelCommands(CommandSource src, CommandRunner commandRunner) {
		if (commandRunner != null) {
			int commandNum = 0;
			Collection<SrcStringInfo> srcStringInfos = getSrcLevelCommands();

			startAnalysis("Running commands", srcStringInfos.size());

		    for (SrcStringInfo cmdInfo : srcStringInfos) {
	            CommandSource commandSource = new CommandSource(CommandSource.Type.SRC_LEVEL_COMMAND,
	            		cmdInfo.getFileName(), cmdInfo.getLineNumber());
	            String command = cmdInfo.getString();
	            setAnalysisName("Running commands: '" + command + "'");
	            commandRunner.run(command, commandSource, commandNum);
	            ++commandNum;
	            provideWorkDone(1);

	            try {
					getAbort();
				} catch (AbortedException e) {
					break;
				}
		    }
		}
	}

	private void initialiseCoreStates(XeLoader xeLoader) {
		m_nodeHz = new TreeMap<Integer, Long>();
		m_coreThreads = new NodeCoreCollection<Integer>();

		XnLoader xnLoader = xeLoader.getXnLoader();
		ProgInfoLoader progInfoLoader = xeLoader.getProgInfoLoader();
		if ((xnLoader == null) || (progInfoLoader == null)) {
			return;
		}

		for (int nodeNumber : xeLoader.getNodeNumbers()){
			m_nodeHz.put(nodeNumber, xnLoader.getFrequencyHz(nodeNumber));
			for (int coreNumber : xeLoader.getCoreNumbers(nodeNumber)){
				int coreThreads = getCoreThreads(xnLoader, progInfoLoader, nodeNumber, coreNumber);
				m_coreThreads.add(nodeNumber, coreNumber, coreThreads);
			}
		}
	}

	private int getCoreThreads(XnLoader xnLoader, ProgInfoLoader progInfoLoader, int nodeNumber, int coreNumber) {
		int nodeRoutingId = xnLoader.getNodeRoutingId(nodeNumber);
		for (ProgInfoEntry entry : progInfoLoader.getProgInfos())  {
			if ((entry.getNodeRoutingId() == nodeRoutingId) && (entry.getCoreNumber() == coreNumber) &&
				(entry.getName().equals("thread"))) {

				int worstCase = entry.getWorstCase();
				if (worstCase > 0) {
					return worstCase;
				} else {
					return Config.DEFAULT_THREADS;
				}
			}
		}
		return Config.DEFAULT_THREADS;
	}

	private boolean checkNumThreads(CommandSource src, int numThreads) {
		if ((numThreads <= 0) || (numThreads > Config.MAX_THREADS)) {
			notify(src, Notifier.NOTIFY_ERROR, String.format("Num threads '%d' invalid", numThreads));
			return false;
		}
		return true;
	}

	private boolean checkFrequency(CommandSource src, double frequency) {
		if (frequency <= 0) {
			notify(src, Notifier.NOTIFY_ERROR, String.format("Frequency '%.1f' invalid", frequency));
			return false;
		}
		return true;
	}
}

class FileLocation {
	int m_nodeNumber;
	int m_coreNumber;
	String m_fileName;
	File m_file;

	FileLocation(int nodeNumber, int coreNumber, String fileName, File file) {
		m_nodeNumber = nodeNumber;
		m_coreNumber = coreNumber;
		m_fileName = fileName;
		m_file = file;
	}
}
