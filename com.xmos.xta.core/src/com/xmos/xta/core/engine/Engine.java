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
import com.xmos.libs.util.Pair;
import com.xmos.libs.xeloader.MemoryImage;
import com.xmos.libs.xeloader.SymbolTable;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.libs.xnloader.XnLoader;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.controlflow.FunctionConstructor;
import com.xmos.xta.core.engine.analysis.controlflow.FunctionReferenceNodeAdder;
import com.xmos.xta.core.engine.analysis.controlflow.SuccessorAdder;
import com.xmos.xta.core.engine.analysis.controlflow.TerminatorAdder;
import com.xmos.xta.core.engine.analysis.controlflow.UnreachableCodeFinder;
import com.xmos.xta.core.engine.analysis.controlflow.UnresolvedSetter;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteConstructor;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer;
import com.xmos.xta.core.engine.analysis.routes.RouteCoreContainer;
import com.xmos.xta.core.engine.analysis.routes.RouteDefines;
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
import com.xmos.xta.core.engine.utils.CoreConfig;
import com.xmos.xta.core.engine.utils.NodeCoreCollection;
import com.xmos.xta.core.engine.utils.NodeCorePair;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class Engine implements Notifier, Aborter {
	public static final int ROUTE_ID_LAST     = -1;
	public static final int ROUTE_ID_INVALID  = -2;
	public static final int ROUTE_ID_GLOBAL   = -3;
	public static final int ROUTE_ID_ALL      = -4;
	public static final int NODE_ID_TOP_LEVEL = -5;

	private XeLoader m_xeLoader;
	private List<Route> m_routes;
	private List<RouteContainer> m_routeContainers;
	private Collection<Reference> m_exclusions;
	private Route m_selectedRoute;
	private Config m_config;
	private File m_executableFile;
	private boolean m_wasReload;
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
	private Scripter m_scripter;
	private String m_fromEndpointName;
	private String m_toEndpointName;	
	private Map<NodeCorePair, Reference> m_fromEndPoints;
	private Map<NodeCorePair, Reference> m_toEndPoints;
	private Collection<NodeCorePair> m_activeCores;

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
		m_exclusions = new ArrayList<Reference>();
		m_activeCores = new ArrayList<NodeCorePair>();
		m_config = config;
		m_executableFile = null;
		m_wasReload = false;
		m_srcPaths = ".";
		m_progressMonitor = null;
		m_sections = new Sections();
		m_defines = new Defines();
		m_timeOut = -1;
		m_numErrors = 0;
		m_scripter = new Scripter(this);
		m_userSpecifedFileLocations = new ArrayList<FileLocation>();
		m_fromEndPoints = new HashMap<NodeCorePair, Reference>();
		m_toEndPoints = new HashMap<NodeCorePair, Reference>();
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

	public MemoryImage getMemoryImage(int nodeNumber, int coreNumber) {
		return m_xeLoader.getMemoryImage(nodeNumber, coreNumber);
	}

	public SymbolTable getSymbolTable(int nodeNumber, int coreNumber) {
		return m_xeLoader.getSymbolTable(nodeNumber, coreNumber);
	}

	public SourceMapper getSourceMapper(int nodeNumber, int coreNumber) {
		return m_sourceMapper.get(nodeNumber, coreNumber);
	}

	public StructureGraph getStructureGraph(int nodeNumber, int coreNumber) {
		return m_structuralGraph.get(nodeNumber, coreNumber);
	}

	public InstructionDecoder getInstructionDecoder(int nodeNumber, int coreNumber){
		return m_instructionDecoder.get(nodeNumber, coreNumber);
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
		assert(validateRouteCount());
		if(m_routes.isEmpty()){
			return routes;
		}
		if (routeId == ROUTE_ID_LAST) {
			assert(validateRouteCount());
			int toIndex = m_routes.size();
			int fromIndex = toIndex - getNumLastRoutesAnalysed();
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
		for (int nodeNumber : xeLoader.getNodeNumbers()){
			for (int coreNumber : xeLoader.getCoreNumbers(nodeNumber)){
				initialiseGlobalStructure(src, xeLoader, sections, false, nodeNumber, coreNumber);
			}
		}

		initialiseCoreStates(xeLoader);

		m_exclusions = new ArrayList<Reference>();
		m_executableFile = executableFile;
		m_xeLoader = xeLoader;
		m_sections = sections;
	    initialiseActiveCores(xeLoader);		
		m_routes.clear();
		m_routeContainers.clear();
		m_selectedRoute = null;
		m_defines.clear();
		Route.resetRouteCount();
		m_fromEndPoints.clear();
		m_toEndPoints.clear();

		notify(src, Notifier.NOTIFY_LOAD_EXECUTABLE, "");

		runSrcLevelCommands(src, commandRunner);

		m_scripter.setEngineLoadAndRunDone();
		return true;
	}

	public boolean analyseFunction(CommandSource src, String functionName) {
        RouteContainer routeContainer = new RouteContainer(functionName, this, src);		
		
		boolean routeFound = false;
		for (NodeCorePair activeCore : m_activeCores) {			
			Reference functionRef = ReferenceFactory.createReference(functionName, 
					ReferenceClass.FUNCTION_REF, activeCore.getNode(), activeCore.getCore());

			if (resolveReference(functionRef)) {
				RouteCoreContainer routeCoreContainer = new RouteCoreContainer(functionRef, 
						routeContainer, this, activeCore.getNode(), activeCore.getCore());
				
				analyseRouteSafely(src, routeContainer, routeCoreContainer, activeCore.getNode(), activeCore.getCore());
				routeFound = true;
			}		
		}
	    if (!routeFound) {
		    notify(src, Notifier.NOTIFY_ERROR, String.format("Reference: %s not resolved on any active core", functionName));
		    return false;
	    }
	    return true;
	}

	public void analysePath(CommandSource src) {
        RouteContainer routeContainer = new RouteContainer(m_fromEndpointName, m_toEndpointName, this, src);
		
		boolean routeFound = false;
		for (NodeCorePair activeCore : m_activeCores) {

			boolean fromFound = true;
			Reference fromEndpoint = m_fromEndPoints.get(activeCore);
		    if ((fromEndpoint == null) || fromEndpoint.getRef().equals("")) {
		    	fromFound = false;
		    }

			boolean toFound = true;
			Reference toEndpoint = m_toEndPoints.get(activeCore);
		    if ((toEndpoint == null) || toEndpoint.getRef().equals("")) {
		    	fromFound = false;
		    }

		    if (fromFound && toFound) {
				RouteCoreContainer routeCoreContainer = new RouteCoreContainer(fromEndpoint, toEndpoint,
						routeContainer, this, activeCore.getNode(), activeCore.getCore());
				
				analyseRouteSafely(src, routeContainer, routeCoreContainer, activeCore.getNode(), activeCore.getCore());
				routeFound = true;
    		}			
		}

	    if (!routeFound) {
		    notify(src, Notifier.NOTIFY_ERROR, String.format("References not resolved on any active core"));
	    }
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
		try {
			for (Route route : getRoute(routeId)) {				
				Reference reference = ReferenceFactory.createReference(refString, ReferenceClass.ENDPOINT_REF, 
						route.getRouteCoreContainer().getNodeNumber(),
						route.getRouteCoreContainer().getCoreNumber());

				if (resolveReference(reference)) {
					if (route.getRouteUnknowns().canInstructionPause(reference)) {
					    return true;
					}
				}				
			}
		} catch (AbortedException e) { }
		return false;
	}

	public boolean removeRoute(CommandSource src, int routeId) {
		Collection<Route> routes = getRoute(routeId);
		assert(validateRouteCount());
		
		if (routes.size() == 0)
			return false;
		
		assert(validateRouteCount());

		for (Route route : routes) {
			route.getRouteCoreContainer().remove(route);
		}
		m_routes.removeAll(routes);

		assert(validateRouteCount());
		
		if (m_routes.size() == 0) {
			Route.resetRouteCount();
			assert(validateRouteCount());
			setSelectedRoute(src, null);
			assert(validateRouteCount());

		} else if (m_selectedRoute.getRouteId() == routeId) {
			this.setSelectedRoute(src, m_routes.get(m_routes.size() - 1));
			assert(validateRouteCount());
		}
		assert(validateRouteCount());
		notify(src, Notifier.NOTIFY_REMOVE_ROUTE, "");
		assert(validateRouteCount());
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
	// Get/Set Frequency.
	// ===================================================================== //			
	public void setFrequency(CommandSource src, int nodeNumber, double frequency) {
		if (checkFrequency(src, frequency)) {
			m_nodeHz.put(nodeNumber, (long)(frequency * 1000000));
			notify(src, Notifier.NOTIFY_UPDATE_STATE, Notifier.STATE_FREQUENCY);			
		}
	}

	public double getFrequency(int nodeNumber) {
		if (m_nodeHz.containsKey(nodeNumber)) {
			return (double)m_nodeHz.get(nodeNumber) / 1000000;
		} else {
			return CoreConfig.DEFAULT_FREQUENCY_MHZ;
		}
	}
	
	public Map<Integer, Double> getFrequencyMap() {
		Map<Integer, Double> frequencies = new HashMap<Integer, Double>();
		for (Integer nodeNumber : m_nodeHz.keySet()) {
			double frequency = (double)m_nodeHz.get(nodeNumber) / 1000000;
			frequencies.put(nodeNumber, frequency);
		}
		return frequencies;
	}
	
	// ===================================================================== //
	// Get/Set Num Threads.
	// ===================================================================== //		
	public void setNumThreads(CommandSource src, int nodeNumber, int coreNumber, int numThreads) {
		if (checkNumThreads(src, numThreads)) {
   		    m_coreThreads.add(nodeNumber, coreNumber, numThreads);
	    	notify(src, Notifier.NOTIFY_UPDATE_STATE, Notifier.STATE_NUM_THREADS);
		}
	}

	public int getNumThreads(int nodeNumber, int coreNumber) {
		Integer threads = m_coreThreads.get(nodeNumber, coreNumber);
		if (threads == null) {
			return CoreConfig.DEFAULT_THREADS;
		}
		return threads;
	}

	public Map<NodeCorePair, Integer> getNumThreadMap() {
		Map<NodeCorePair, Integer> threads = new HashMap<NodeCorePair, Integer>();
		for (Pair<Integer, Integer> nodeCore : m_coreThreads.getEntries()) {
			threads.put(new NodeCorePair(nodeCore.first, nodeCore.second), 
					m_coreThreads.get(nodeCore.first, nodeCore.second));
		}
		return threads;
	}

	// ===================================================================== //
	// From/To Endpoints.
	// ===================================================================== //	
	public boolean setFromEndPoint(CommandSource src, String ref) {
		m_fromEndpointName = ref;			
		
		boolean hit = false;		
		for (NodeCorePair activeCore : m_activeCores) {
			Reference fromEndPoint = ReferenceFactory.createReference(ref, 
					ReferenceClass.ENDPOINT_REF, activeCore.getNode(), activeCore.getCore());
			
			if (resolveReference(fromEndPoint)) {
				m_fromEndPoints.put(activeCore, fromEndPoint);
				hit = true;
			}
		}
		if (!hit) {
			notify(src, Notifier.NOTIFY_ERROR, String.format("'%s' is not a valid endpoint", ref));
			return false;
		}
		notify(src, Notifier.NOTIFY_FROM_END_POINT_SET, "");
		return true;
	}

	public void removeFromEndPoint(CommandSource src) {
		m_fromEndPoints.clear();
		notify(src, Notifier.NOTIFY_FROM_END_POINT_SET, "");
	}

	public boolean setToEndPoint(CommandSource src, String ref) {
		m_toEndpointName = ref;
		
		boolean hit = false;		
		for (NodeCorePair activeCore : m_activeCores) {
			Reference toEndPoint = ReferenceFactory.createReference(ref, 
					ReferenceClass.ENDPOINT_REF, activeCore.getNode(), activeCore.getCore());
			
			if (resolveReference(toEndPoint)) {
				m_toEndPoints.put(activeCore, toEndPoint);
				hit = true;
			}
		}
		if (!hit) {
			notify(src, Notifier.NOTIFY_ERROR, String.format("'%s' is not a valid endpoint", ref));
			return false;
		}
		notify(src, Notifier.NOTIFY_FROM_END_POINT_SET, "");
		return true;		
	}

	public void removeToEndPoint(CommandSource src) {
		m_toEndPoints.clear();
		notify(src, Notifier.NOTIFY_TO_END_POINT_SET, "");
	}

	public Map<NodeCorePair, Reference> getFromEndPoints() {
		return m_fromEndPoints;
	}

	public Map<NodeCorePair, Reference> getToEndPoints() {
		return m_toEndPoints;
	}
	
	// ===================================================================== //
	// Function Names/Pcs
	// ===================================================================== //
	public Set<String> getFunctionNames(int nodeNumber, int coreNumber) {
		Map<String, List<InstructionNode>> functionNames = m_functionNameToInsts.get(nodeNumber, coreNumber);
		if (functionNames != null) {
			Set<String> names = new TreeSet<String>(functionNames.keySet());
			names.removeAll(getUnreachableFunctions(nodeNumber, coreNumber));
		    return names;
	    }
		return new TreeSet<String>();
	}

	public Collection<String> getNonSystemFunctionNames() {
		List<String> functionNames = new LinkedList<String>();

		for (NodeCorePair activeCore : getActiveCores()) {
			int nodeNumber = activeCore.getNode();
			int coreNumber = activeCore.getCore();

			if (!m_xeLoader.isLoaded(nodeNumber, coreNumber))
				continue;

			List<String> nodeCoreFunctions = new LinkedList<String>();
			nodeCoreFunctions.addAll(getFunctionNames(nodeNumber, coreNumber));
	
			Set<String> systemFunctions = new HashSet<String>();
			Collection<String> systems = m_sections.getSystems(nodeNumber, coreNumber);
			for (String functionName : functionNames) {
				if (isSystemFunction(systems, functionName)) {
					systemFunctions.add(functionName);
				}
			}
			nodeCoreFunctions.removeAll(systemFunctions);
			functionNames.addAll(nodeCoreFunctions);
		}

		Collections.sort(functionNames);
		Set<String> uniqueNames = new HashSet<String>();
		uniqueNames.addAll(functionNames);
		return uniqueNames;
	}
	
	public List<Long> getFunctionPcs(String functionName, int nodeNumber, int coreNumber) {		
		List<Long> pcs = new LinkedList<Long>();		
		List<InstructionNode> insts = m_functionNameToInsts.get(nodeNumber, coreNumber).get(functionName);		
		assert((insts != null) && !insts.isEmpty());
		
		for(InstructionNode inst : insts){
			pcs.add(inst.getPC());
		}		
		return pcs;
	}
	
	// ===================================================================== //
	// Active Core Methods
	// ===================================================================== //
	public void addActiveCore(CommandSource src, NodeCorePair activeCore) {		
		if (m_xeLoader.isLoaded(activeCore.getNode(), activeCore.getCore())) {
			if (!m_activeCores.contains(activeCore)) {
				m_activeCores.add(activeCore);
			}						
		} else {
			notify(src, Notifier.NOTIFY_ERROR, String.format(
					"node: %d, Core: %d not currently loaded", 
					activeCore.getNode(), activeCore.getCore()));
		}		
	}

	public void removeActiveCore(CommandSource src, NodeCorePair activeCore) {
		m_activeCores.remove(activeCore);
	}

	public Collection<NodeCorePair> getActiveCores() {
		return m_activeCores;
	}

	// ===================================================================== //
	// Route Selection Methods
	// ===================================================================== //
	public Route getSelectedRoute() {
		return m_selectedRoute;
	}

	public void setSelectedRoute(CommandSource src, Route route) {
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
		boolean hit = false;
		for (NodeCorePair activeCore : m_activeCores) {
			
			Reference functionRef = ReferenceFactory.createReference(functionName, 
					ReferenceClass.FUNCTION_REF, activeCore.getNode(), activeCore.getCore());
			
			if (resolveReference(functionRef)) {
				m_defines.addFunctionTime(functionRef, timeUnit);
				hit = true;
			}
		}
		if (hit) {	
		    notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
		} else {
		    notify(src, Notifier.NOTIFY_ERROR, String.format("Reference: %s not resolved on any active core", functionName));
		}		    
	}

	public void addPathTime(CommandSource src, String from, String to, TimeUnit timeUnit) {
		boolean hit = false;
		for (NodeCorePair activeCore : m_activeCores) {
			
			Reference fromRef = ReferenceFactory.createReference(from, 
					ReferenceClass.ENDPOINT_REF, activeCore.getNode(), activeCore.getCore());
			Reference toRef = ReferenceFactory.createReference(to, 
					ReferenceClass.ENDPOINT_REF, activeCore.getNode(), activeCore.getCore());

			if (resolveReference(fromRef) && resolveReference(toRef)) {
			    m_defines.addPathTime(fromRef, toRef, timeUnit);
			    hit = true;
			}
		}
		if (hit) {	
		    notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
		} else {
		    notify(src, Notifier.NOTIFY_ERROR, String.format("References not resolved on any active core"));
		}		    
	}

	public void addInstructionTime(CommandSource src, String refString, final TimeUnit timeUnit) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.addInstructionTime(reference, timeUnit);
			}
		}, ReferenceClass.ANY_REF);		
	}

	public void addLoop(CommandSource src, String refString, final long iterations) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.addLoop(reference, iterations);
			}
		}, ReferenceClass.ANY_REF);
	}

	public void addLoopPath(CommandSource src, String refString, final long iterations) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.addLoopPath(reference, iterations);
			}
		}, ReferenceClass.ANY_REF);
	}

	public void addLoopScope(CommandSource src, String refString, final boolean absolute) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.addLoopScope(reference, absolute);
			}
		}, ReferenceClass.ANY_REF);
	}

	public void removeFunctionTime(CommandSource src, String functionName) {
		boolean hit = false;
		for (NodeCorePair activeCore : m_activeCores) {

			Reference functionRef = ReferenceFactory.createReference(functionName, 
					ReferenceClass.FUNCTION_REF_WITH_EVERYTHING, activeCore.getNode(), activeCore.getCore());
			
			if (resolveReference(functionRef)) {
				m_defines.removeFunctionTime(functionRef);
				hit = true;
			}			
		}
		if (hit) {	
		    notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
		} else {
		    notify(src, Notifier.NOTIFY_ERROR, String.format("Reference: %s not resolved on any active core", functionName));
		}		    
	}

	public void removePathTime(CommandSource src, String from, String to) {
		boolean hit = false;
		for (NodeCorePair activeCore : m_activeCores) {
			
			Reference fromRef = ReferenceFactory.createReference(from, 
					ReferenceClass.ENDPOINT_REF, activeCore.getNode(), activeCore.getCore());
			Reference toRef = ReferenceFactory.createReference(to, 
					ReferenceClass.ENDPOINT_REF, activeCore.getNode(), activeCore.getCore());

			if (resolveReference(fromRef) && resolveReference(toRef)) {
			    m_defines.removePathTime(fromRef, toRef);
			    hit = true;
			}
		}
		if (hit) {	
		    notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
		} else {
		    notify(src, Notifier.NOTIFY_ERROR, String.format("References not resolved on any active core"));
		}		    		
	}

	public void removeInstructionTime(CommandSource src, String refString) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.removeInstructionTime(reference);
			}
		}, ReferenceClass.ANY_REF_WITH_EVERYTHING);
	}

	public void removeLoop(CommandSource src, String refString) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.removeLoop(reference);
			}
		}, ReferenceClass.ANY_REF_WITH_EVERYTHING);
	}

	public void removeLoopPath(CommandSource src, String refString) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.removeLoopPath(reference);
			}
		}, ReferenceClass.ANY_REF_WITH_EVERYTHING);
	}

	public void removeLoopScope(CommandSource src, String refString) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.removeLoopScope(reference);
			}
		}, ReferenceClass.ANY_REF_WITH_EVERYTHING);
	}

	public Collection<FunctionTimeInfo> getFunctionTimeDefines() {
		return m_defines.getFunctionTimes();
	}

	public Collection<PathTimeInfo> getPathTimeDefines() {
		return m_defines.getPathTimes();
	}

	public Collection<InstructionTimeInfo> getInstructionTimeDefines() {
		return m_defines.getInstructionTimes();
	}

	public Collection<LoopInfo> getLoopDefines() {
		return m_defines.getLoops();
	}

	public Collection<LoopPathInfo> getLoopPathDefines() {
		return m_defines.getLoopPaths();
	}

	public Collection<LoopScopeInfo> getLoopScopeDefines() {
		return m_defines.getLoopScopes();
	}

	public Defines getDefines() {
		return m_defines;
	}

	// ===================================================================== //
	// Set Function Methods
	// ===================================================================== //
	public void setFunctionTime(CommandSource src, int routeId, String functionName, TimeUnit timeUnit) {		
		RouteDefines defines = new RouteDefines();
		
		boolean hit = false;
		try {
			for (Route route : getRoute(routeId)) {
				
				Reference functionRef = ReferenceFactory.createReference(functionName, 
						ReferenceClass.FUNCTION_REF,
						route.getRouteCoreContainer().getNodeNumber(),
						route.getRouteCoreContainer().getCoreNumber());
				
				if (resolveReference(functionRef)) {
					FunctionTimeInfo info = new FunctionTimeInfo(functionRef, timeUnit);					
					defines.applyFunctionDefine(src, route, info, Source.USER);
					hit = true;
				}
			}
		} catch (AbortedException e) {	}
		
		if (!hit) {
		    notify(src, Notifier.NOTIFY_ERROR, String.format("Reference: %s not resolved on any active core", functionName));			
		}
	}

	public void setFunctionTime(CommandSource src, Node node, TimeUnit timeUnit) {
		if (node.isFunctionNode()) {
			Route route = getSelectedRoute();
			route.getRouteUnknowns().setFixedTime(node, timeUnit, Type.FUNCTION_TIME, Source.USER);
		}
	}

	// ===================================================================== //
	// Set Path Methods
	// ===================================================================== //	
	public void setPathTime(CommandSource src, int routeId, String from, String to, TimeUnit timeUnit) {
		RouteDefines defines = new RouteDefines();
		
		boolean hit = false;		
		try {			
			for (Route route : getRoute(routeId)) {
				int node = route.getRouteCoreContainer().getNodeNumber();
				int core = route.getRouteCoreContainer().getCoreNumber();

				Reference fromRef = ReferenceFactory.createReference(from, 
						ReferenceClass.ENDPOINT_REF, node, core);
				Reference toRef = ReferenceFactory.createReference(to, 
						ReferenceClass.ENDPOINT_REF, node, core);

				if (resolveReference(fromRef) && resolveReference(toRef)) {
					PathTimeInfo info = new PathTimeInfo(fromRef, toRef, timeUnit);					
					defines.applyPathDefine(this, src, route, info, Source.USER);
					hit = true;
				}
			}
		} catch (AbortedException e) { }

		if (hit) {
		    notify(src, Notifier.NOTIFY_ROUTE_CHANGED, String.format("%d", routeId));
		} else {
		    notify(src, Notifier.NOTIFY_ERROR, String.format("References not resolved on any active core"));			
		}
	}

	// ===================================================================== //
	// Set Loop Methods
	// ===================================================================== //
	public void setLoop(CommandSource src, int routeId, String refString, long iterations) {
		Collection<Route> routes = getRoute(routeId);
		if (routes.size() == 0) {
			notify(src, Notifier.NOTIFY_ERROR, String.format("Failed to find route: %d", routeId));
			return;
		}
		try {
			Map<Route, Reference> refMap = new HashMap<Route, Reference>();
			for (Route route : routes) {				
				Reference reference = ReferenceFactory.createReference(refString, 
						ReferenceClass.ANY_REF,
				        route.getRouteCoreContainer().getNodeNumber(),
				        route.getRouteCoreContainer().getCoreNumber());
				
				if (resolveReference(reference)) {
					route.getRouteUnknowns().checkSetLoopIterations(reference, iterations);
					refMap.put(route, reference);
				}								
			}
			for (Route route : routes) {
				if (refMap.containsKey(route)) {
			        route.getRouteUnknowns().setLoopIterations(src, this,
			        	refMap.get(route), iterations, false, Source.USER);
				}
			}

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

	// ===================================================================== //
	// Set Loop Path Methods
	// ===================================================================== //
	public void setLoopPath(CommandSource src, int routeId, String refString, long iterations) {
		Collection<Route> routes = getRoute(routeId);
		if (routes.size() == 0) {
			notify(src, Notifier.NOTIFY_ERROR, String.format("Failed to find route: %d", routeId));
			return;
		}
		try {
			Map<Route, Reference> refMap = new HashMap<Route, Reference>();
			for (Route route : routes) {				
				Reference reference = ReferenceFactory.createReference(refString, 
						ReferenceClass.ANY_REF,
				        route.getRouteCoreContainer().getNodeNumber(),
				        route.getRouteCoreContainer().getCoreNumber());
				
				if (resolveReference(reference)) {
					route.getRouteUnknowns().checkSetLoopPathIterations(reference, iterations);
					refMap.put(route, reference);
				}								
			}
			for (Route route : routes) {
				if (refMap.containsKey(route)) {
			        route.getRouteUnknowns().setLoopPathIterations(refMap.get(route), iterations, Source.USER);
				}
			}			
			
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

	// ===================================================================== //
	// Set Loop Scope Methods
	// ===================================================================== //
	public void setLoopScope(CommandSource src, int routeId, String refString, boolean absolute) {
		Collection<Route> routes = getRoute(routeId);
		if (routes.size() == 0) {
			notify(src, Notifier.NOTIFY_ERROR, String.format("Failed to find route: %d", routeId));
			return;
		}
		try {
			Map<Route, Reference> refMap = new HashMap<Route, Reference>();
			for (Route route : routes) {				
				Reference reference = ReferenceFactory.createReference(refString, 
						ReferenceClass.ANY_REF,
				        route.getRouteCoreContainer().getNodeNumber(),
				        route.getRouteCoreContainer().getCoreNumber());
				
				if (resolveReference(reference)) {
					route.getRouteUnknowns().checkSetLoopScope(reference, absolute);
					refMap.put(route, reference);
				}								
			}
			for (Route route : routes) {
				if (refMap.containsKey(route)) {
			        route.getRouteUnknowns().setLoopScope(refMap.get(route), absolute, Source.USER);
				}
			}			
			
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

	// ===================================================================== //
	// Instruction Time Methods
	// ===================================================================== //
	public boolean setInstructionTime(CommandSource src, int routeId, String refString, TimeUnit timeUnit) {
		Collection<Route> routes = getRoute(routeId);
		if (routes.isEmpty()) {
			return false;
		}
	    try {
	    	Map<Route, Reference> refMap = new HashMap<Route, Reference>();
			for (Route route : routes) {
				Reference reference = ReferenceFactory.createReference(refString, 
						ReferenceClass.ENDPOINT_REF,
				        route.getRouteCoreContainer().getNodeNumber(),
				        route.getRouteCoreContainer().getCoreNumber());
								
				if (resolveReference(reference)) {
					if (!route.getRouteUnknowns().canInstructionPause(reference))
						return false;
					
					refMap.put(route, reference);
				}				
			}
			for (Route route : routes) {
				if (refMap.containsKey(route)) {				
			        route.getRouteUnknowns().setFixedTime(refMap.get(route), timeUnit, Type.INSTRUCTION_TIME, Source.USER);
				}
			}
		} catch (AbortedException e) { 
			return false;			
		}
				
	    notify(src, Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
	    return true;
	}

	public void setInstructionTime(CommandSource src, Node node, TimeUnit timeUnit) {
		m_selectedRoute.getRouteUnknowns().setFixedTime(node, timeUnit, Type.INSTRUCTION_TIME, Source.USER);
		notify(src, Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
	}

	public TimeUnit getInstructionTime(Node node) {
		return m_selectedRoute.getRouteUnknowns().getFixedTime(node);
	}

	// ===================================================================== //
	// Exclusion Methods
	// ===================================================================== //
	public boolean addExclusion(CommandSource src, String refString) {
		boolean hit = false;
		for (NodeCorePair activeCore : m_activeCores) {
			Reference reference = ReferenceFactory.createReference(refString, 
					ReferenceClass.ANY_REF, activeCore.getNode(), activeCore.getCore());
			
			if (alreadyExcluded(reference)) {
				notify(src, Notifier.NOTIFY_ERROR, String.format("cannot add duplicate exclusion for ref: %s", reference.getRef()));
				
			} else if (resolveReference(reference)) {
				m_exclusions.add(reference);
                hit = true;				
			}
		}
		
		if (hit) {
			notify(src, Notifier.NOTIFY_UPDATE_EXCLUSIONS, "");
		} else {
			notify(src, Notifier.NOTIFY_ERROR, String.format("Reference: %s not resolved on any active core", refString));			
		}
		return hit;
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
		boolean hit = false;
		for (NodeCorePair activeCore : m_activeCores) {
			Reference reference = ReferenceFactory.createReference(refString, 
					ReferenceClass.ANY_REF_WITH_EVERYTHING, activeCore.getNode(), activeCore.getCore());
			
			if (resolveReference(reference)) {
                Iterator<Reference> iter = m_exclusions.iterator();
                while (iter.hasNext()) {
                    Reference ref = iter.next();
                    if (ref.equals(reference)) {
                        iter.remove();
                    }
                }				
				hit = true;
			}
		}
		
		if (hit) {
			notify(src, Notifier.NOTIFY_UPDATE_EXCLUSIONS, "");
		} else {
			notify(src, Notifier.NOTIFY_ERROR, String.format("Reference: %s not resolved on any active core", refString));			
		}
		return hit;
	}

	public Collection<Reference> getExclusions() {
		return m_exclusions;
	}

	// ===================================================================== //
	// Section Methods
	// ===================================================================== //
	public boolean addBranch(CommandSource src, String fromRefString, Collection<String> toRefStrings) {
		boolean hit = false;
		for (NodeCorePair activeCore : m_activeCores) {

			Reference fromRef = ReferenceFactory.createReference(fromRefString, 
					ReferenceClass.LABEL_REF, activeCore.getNode(), activeCore.getCore());
			boolean fromResolved = resolveReference(fromRef);
			
			checkInstructionIsBranch(src, fromRef, activeCore);
			
			Collection<Reference> toRefs = new ArrayList<Reference>();
			for (String toRefString : toRefStrings) {
				Reference ref = ReferenceFactory.createReference(toRefString, 
						ReferenceClass.LABEL_REF, activeCore.getNode(), activeCore.getCore());				
				toRefs.add(ref);
			}		
			boolean toResolved = true;
			for (Reference toRef : toRefs) {
				if (!resolveReference(toRef)) {
					toResolved = false;
				}
			}
			
			if (fromResolved && toResolved) {
				if (m_sections.addToBranches(src, activeCore.getNode(), activeCore.getCore(), fromRef, toRefs)) {
					m_structuralGraphUpToDate.add(activeCore.getNode(), activeCore.getCore(), false);
					hit = true;
				}				
			}
		}
		
		if (!hit) {
			notify(src, Notifier.NOTIFY_ERROR, String.format("References not resolved on any active core"));			
		}
		return hit;
	}

	private void checkInstructionIsBranch(CommandSource src, Reference ref, NodeCorePair activeCore) {
		for (long pc : ref.getPcs()) {
			InstructionNode node = m_sourceMapper.get(activeCore.getNode(), activeCore.getCore()).getInstructionNode(pc);
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

	public boolean removeBranch(CommandSource src, String fromRefString, Collection<String> toRefStrings) {
		boolean hit = false;
		for (NodeCorePair activeCore : m_activeCores) {
			
			Reference fromRef = ReferenceFactory.createReference(fromRefString, 
					ReferenceClass.LABEL_REF_WITH_EVERYTHING, activeCore.getNode(), activeCore.getCore());
			boolean fromResolved = resolveReference(fromRef);
			
			checkInstructionIsBranch(src, fromRef, activeCore);
			
			Collection<Reference> toRefs = new ArrayList<Reference>();
			for (String toRefString : toRefStrings) {
				Reference ref = ReferenceFactory.createReference(toRefString, 
						ReferenceClass.LABEL_REF_WITH_EVERYTHING, activeCore.getNode(), activeCore.getCore());				
				toRefs.add(ref);
			}		
			boolean toResolved = true;
			for (Reference toRef : toRefs) {
				if (!resolveReference(toRef)) {
					toResolved = false;
				}
			}
			
			if (fromResolved && toResolved) {
				if (m_sections.removeFromBranches(src, activeCore.getNode(), activeCore.getCore(), fromRef, toRefs)) {
					m_structuralGraphUpToDate.add(activeCore.getNode(), activeCore.getCore(), false);
					hit = true;
				}				
			}			
		}

		if (!hit) {
			notify(src, Notifier.NOTIFY_ERROR, String.format("References not resolved on any active core"));			
		}
		return hit;
	}

	public Map<Reference, List<Reference>> getUserBranches() {
		Map<Reference, List<Reference>> branches = new TreeMap<Reference, List<Reference>>();
		
        for (int nodeNumber : m_xeLoader.getNodeNumbers()){
            for (int coreNumber : m_xeLoader.getCoreNumbers(nodeNumber)){
            	
        		Map<Reference, Set<BranchInfo>> currentBranches = getBranches(nodeNumber, coreNumber);
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
            }
        }		
		return branches;
	}

	public Map<Reference, Set<BranchInfo>> getBranches(int nodeNumber, int coreNumber) {
		return m_sections.getBranches(nodeNumber, coreNumber);
	}

	public Map<String, Set<Long>> getLabels(int nodeNumber, int coreNumber) {
		return m_sections.getLabels(nodeNumber, coreNumber);
	}

	public List<CodePointInfo> getSrcLabels(int nodeNumber, int coreNumber) {
		return m_sections.getSrcLabels(nodeNumber, coreNumber);
	}

	public Map<Long, SrcLabelTableInfo> getSrcLabelTable(int nodeNumber, int coreNumber) {
		return m_sections.getSrcLabelTable(nodeNumber, coreNumber);
	}

	public List<CodePointInfo> getEndpoints(int nodeNumber, int coreNumber) {
		return m_sections.getEndpoints(nodeNumber, coreNumber);
	}

	public Map<Long, CodePointTableInfo> getEndpointTable(int nodeNumber, int coreNumber) {
		return m_sections.getEndpointTable(nodeNumber, coreNumber);
	}

	public List<CodePointInfo> getCalls(int nodeNumber, int coreNumber) {
		return m_sections.getCalls(nodeNumber, coreNumber);
	}

	public Map<Long, CodePointTableInfo> getCallTable(int nodeNumber, int coreNumber) {
		return m_sections.getCallTable(nodeNumber, coreNumber);
	}

	public Collection<Long> getThreadStartPoints(int nodeNumber, int coreNumber) {
		return m_sections.getThreadStartPoints(nodeNumber, coreNumber);
	}

	public Collection<Long> getThreadStopPoints(int nodeNumber, int coreNumber) {
		return m_sections.getThreadStopPoints(nodeNumber, coreNumber);
	}

	public Collection<SrcStringInfo> getSrcLevelCommands(int nodeNumber, int coreNumber) {
		return m_sections.getSrcLevelCommands(nodeNumber, coreNumber);
	}

	public Collection<SrcLevelLoopInfo> getSrcLevelLoops(int nodeNumber, int coreNumber) {
		return m_sections.getSrcLevelLoops(nodeNumber, coreNumber);
	}

	public Collection<String> getSystems(int nodeNumber, int coreNumber) {
		return m_sections.getSystems(nodeNumber, coreNumber);
	}

	public Collection<String> getRecursiveFunctions(int nodeNumber, int coreNumber) {
		return m_sections.getRecursiveFunctions(nodeNumber, coreNumber);
	}

	public Collection<String> getUnreachableFunctions(int nodeNumber, int coreNumber) {
		return m_sections.getUnreachableFunctions(nodeNumber, coreNumber);
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
			InstructionDecoder id = new InstructionDecoder(nodeNumber, coreNumber, xeLoader, m_config);

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

			StructuralAnalysisEngine sae = new StructuralAnalysisEngine();
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

	private boolean analyseRouteSafely(CommandSource src, RouteContainer routeContainer,
			RouteCoreContainer routeCoreContainer, int nodeNumber, int coreNumber) {
		
		boolean success = false;
		try {
			success = doRouteAnalysis(src, routeContainer, routeCoreContainer, nodeNumber, coreNumber);

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

	private boolean doRouteAnalysis(CommandSource src, RouteContainer routeContainer,
			RouteCoreContainer routeCoreContainer, int nodeNumber, int coreNumber) throws Exception {
				
		assert(m_xeLoader.isLoaded(nodeNumber, coreNumber));
	    notify(src, Notifier.NOTIFY_ANALYSIS_BEGIN, "");
	    
		if (!m_structuralGraphUpToDate.get(nodeNumber, coreNumber)) {
			if (!initialiseGlobalStructure(src, m_xeLoader, m_sections, false, nodeNumber, coreNumber))
				return false;
		}

		RouteConstructor routeConstructor = new RouteConstructor(this, m_structuralGraph.get(nodeNumber, coreNumber), routeCoreContainer);
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
			for (Route route : routeConstructor.getRoutes()) {
				m_routes.add(route);
			}
			if (!m_routes.isEmpty() && !routeConstructor.getRoutes().isEmpty()) {				
				routeCoreContainer.add(routeConstructor.getRoutes());
				routeContainer.addRouteCoreContainer(routeCoreContainer);				
    			m_routeContainers.add(routeContainer);

				setSelectedRoute(src, m_routes.get(currentSize));
			    notify(src, Notifier.NOTIFY_ANALYSIS_COMPLETE, Integer.toString(m_routes.get(currentSize).getRouteId()));

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
			SymbolTable symbolTable, Map<String, List<InstructionNode>> functionNameToInstructions,
			Set<InstructionNode> funcStarts, Map<String, Set<Long>> labelToPc) {

		Map<Long, String> pcToLabel = new TreeMap<Long, String>();
		for (String label : labelToPc.keySet()) {
			for (Long pc : labelToPc.get(label)) {
				pcToLabel.put(pc, label);
			}
		}
		
		for(InstructionNode inst : funcStarts){
			String functionName = symbolTable.functionName(inst.getStartPc());
			
			if (functionNameToInstructions.containsKey(functionName)) {
				String label = pcToLabel.get(inst.getXtaInstruction().getPc());
				if(label != null){
					functionName = label;
				}
			}			
			addToMap(functionNameToInstructions, inst, functionName);
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

	private void updateDefines(CommandSource src, String refString, DefinesUpdate update, ReferenceClass ref) {		
		boolean hit = false;
		for (NodeCorePair activeCore : m_activeCores) {
			
			Reference reference = ReferenceFactory.createReference(refString, 
					ReferenceClass.ANY_REF, activeCore.getNode(), activeCore.getCore());
			
			if (resolveReference(reference)) {
				update.run(reference);
				hit = true;
			}
		}
		if (hit) {	
		    notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
		} else {
		    notify(src, Notifier.NOTIFY_ERROR, String.format("References not resolved on any active core"));
		}
	}

	private static interface DefinesUpdate {
		void run(Reference reference);
	}

	private void runSrcLevelCommands(CommandSource src, CommandRunner commandRunner) {
		if (commandRunner != null) {
			int commandNum = 0;
			
			for (NodeCorePair activeCore : m_activeCores) {
				Collection<SrcStringInfo> srcStringInfos = getSrcLevelCommands(activeCore.getNode(), activeCore.getCore());

				String desc = String.format("Running commands: Node: %d, Core: %d", activeCore.getNode(), activeCore.getCore());
				startAnalysis(desc, srcStringInfos.size());

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
					return CoreConfig.DEFAULT_THREADS;
				}
			}
		}
		return CoreConfig.DEFAULT_THREADS;
	}

	private boolean checkNumThreads(CommandSource src, int numThreads) {
		if ((numThreads <= 0) || (numThreads > CoreConfig.MAX_THREADS)) {
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
	
	private int getNumLastRoutesAnalysed() {
		if (m_routeContainers.isEmpty()) { 
			return 0;
		}
		return m_routeContainers.get(m_routeContainers.size() -1).getNumExistingRoutes();
	}
	
	private void initialiseActiveCores(XeLoader xeLoader) {
		m_activeCores.clear();
		for (int nodeNumber : xeLoader.getNodeNumbers()) {
			for (int coreNumber : xeLoader.getCoreNumbers(nodeNumber)) {
				m_activeCores.add(new NodeCorePair(nodeNumber, coreNumber));
			}
		}
	}

	private boolean validateRouteCount(){
		return m_routes.size() >= getNumLastRoutesAnalysed();
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
