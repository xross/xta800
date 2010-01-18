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
import com.xmos.libs.xeloader.MemoryImage;
import com.xmos.libs.xeloader.SymbolTable;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.libs.xnloader.XnLoader;
import com.xmos.xta.core.engine.analysis.controlflow.ControlFlowGraph;
import com.xmos.xta.core.engine.analysis.controlflow.FunctionHandler;
import com.xmos.xta.core.engine.analysis.controlflow.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.controlflow.SuccessorAdder;
import com.xmos.xta.core.engine.analysis.controlflow.ThreadHandler;
import com.xmos.xta.core.engine.analysis.controlflow.UnreachableCodeFinder;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteDefines;
import com.xmos.xta.core.engine.analysis.routes.RouteConstructor;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer;
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
	private XnLoader m_xnLoader;
	private List<Route> m_routes;
	private List<RouteContainer> m_routeContainers;
	private int m_numRoutesLastAnalysed;
	private Collection<Reference> m_exclusions;
	private Route m_selectedRoute;
	private Config m_config;
	private File m_executableFile;
	private boolean m_wasReload;
	private int m_nodeId;
	private int m_coreId;
	private Notifier m_notifier;	
	private ProgressMonitor m_progressMonitor;
	private String m_srcPaths;
	private Sections m_sections;
	private Defines m_defines;
	private Collection<String> m_functionsVisited;
	private int m_timeOut;
	private int m_numErrors;
	private long m_loadStartTime;
	private NodeCoreCollection<Boolean> m_structuralGraphUpToDate;
	private NodeCoreCollection<SourceMapper> m_sourceMapper;
	private NodeCoreCollection<StructureGraph> m_structuralGraph;
	private NodeCoreCollection<Map<String, List<InstructionNode>>> m_functionNameToInsts;
	private Collection<FileLocation> m_userSpecifedFileLocations;	
	private DataCollection m_dataCollection;
	private Scripter m_scripter;
	
	public Engine(Config config) {		
		m_sourceMapper = new NodeCoreCollection<SourceMapper>();
		m_structuralGraph = new NodeCoreCollection<StructureGraph>();
		m_functionNameToInsts = new NodeCoreCollection<Map<String, List<InstructionNode>>>();
		m_structuralGraphUpToDate = new NodeCoreCollection<Boolean>();
		m_xeLoader = new XeLoader();
		m_xnLoader = new XnLoader();
		m_routes = new LinkedList<Route>();
		m_routeContainers = new LinkedList<RouteContainer>();
		m_numRoutesLastAnalysed = 0;
		m_exclusions = new ArrayList<Reference>();
		m_config = config;
		m_executableFile = null;
		m_wasReload = false;
		m_nodeId = 0;
		m_coreId = 0;
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
		return m_xeLoader.getMemoryImage(m_nodeId, m_coreId);
	}

	public SymbolTable getSymbolTable() {
		return m_xeLoader.getSymbolTable(m_nodeId, m_coreId);
	}
		
	public SourceMapper getSourceMapper() {
		return m_sourceMapper.get(m_nodeId, m_coreId);
	}
	
	public StructureGraph getStructureGraph() {
		return m_structuralGraph.get(m_nodeId, m_coreId);
	}
	
	public Set<String> getFunctionNames() {
		Map<String, List<InstructionNode>> functionNames = m_functionNameToInsts.get(m_nodeId, m_coreId);
		if (functionNames != null) {
			Set<String> names = new TreeSet<String>(functionNames.keySet());
			names.removeAll(getUnreachableFunctions());
		    return names;
	    }
		return new TreeSet<String>();
	}

	public long getFunctionPc(String functionName) {
		List<InstructionNode> insts = m_functionNameToInsts.get(m_nodeId, m_coreId).get(functionName);
		assert((insts != null) && !insts.isEmpty());
		return insts.get(0).getStartPc();
	}

	public Collection<String> getNonSystemFunctionNames() {
		if (!m_xeLoader.isLoaded(m_nodeId, m_coreId))
			return new HashSet<String>();
		
		List<String> functionNames = new ArrayList<String>();
		functionNames.addAll(getFunctionNames());
		Collections.sort(functionNames);
		
		Set<String> systemFunctions = new HashSet<String>();
		Collection<String> systems = m_sections.getSystems(m_nodeId, m_coreId);
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

		setAnalysisName("Loading XN");
		XnLoader xnLoader = new XnLoader();
		if (!xnLoader.loadExecutable(executableFile)) {
			notify(src, Notifier.NOTIFY_ERROR, "the file '" + executableFile + "' did not contain a valid XN.");
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
			for(int n : xeLoader.getNodeIds()){
				for(int c : xeLoader.getCoreIds(n)){
					initialiseGlobalStructure(src, xeLoader, sections, false, n, c);
				}
			}
		}
		
		m_exclusions = new ArrayList<Reference>();
		m_executableFile = executableFile;
		m_xeLoader = xeLoader;
		m_xnLoader = xnLoader;
		m_sections = sections;
		m_nodeId = -1; // Ensure setNodeId does something
		m_coreId = 0;
		setNodeId(src, 0, false);
		m_routes.clear();
		m_routeContainers.clear();
		m_numRoutesLastAnalysed = 0;
		m_selectedRoute = null;
		m_defines.clear();
		Route.resetRouteCount();
		
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

		if (!m_xeLoader.isLoaded(m_nodeId, m_coreId)) {
			notify(src, Notifier.NOTIFY_ERROR, String.format(
					"node: %d, Core: %d image not found in file: %s", m_nodeId,
					m_coreId, m_executableFile));
			return false;

		} else if (!resolveOrError(src, functionRef)) {
			return false;
						
		} else {
			RouteDescription routeDescription = new RouteDescription(functionRef, this, 
					m_nodeId, m_coreId, m_exclusions, m_defines, m_config);			
			return analyseRouteSafely(src, routeDescription, temporaryRoute);
		}
	}

	public boolean analyseRoute(CommandSource src, String from, String to, boolean temporaryRoute) {
		Reference fromRef = ReferenceFactory.createReference(from, ReferenceClass.ENDPOINT_REF);
		Reference toRef = ReferenceFactory.createReference(to, ReferenceClass.ENDPOINT_REF);
		
		if (!m_xeLoader.isLoaded(m_nodeId, m_coreId)) {
			notify(src, Notifier.NOTIFY_ERROR, String.format(
					"node: %d, Core: %d image not found in file: %s", m_nodeId,
					m_coreId, m_executableFile));
			return false;
			
		} else if (!resolveOrError(src, fromRef)) {
			return false;
			
		} else if (!resolveOrError(src, toRef)) {
			return false;
			
		} else {		
			RouteDescription routeDescription = new RouteDescription(fromRef, toRef, this,
					m_nodeId, m_coreId, m_exclusions, m_defines, m_config);
			return analyseRouteSafely(src, routeDescription, temporaryRoute);
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

	public void setNodeId(CommandSource src, int nodeId, boolean doNotify) {
		if (nodeId != m_nodeId) {
			if (initialiseGlobalStructure(src, m_xeLoader, m_sections, false, nodeId, m_coreId)) {
				m_nodeId = nodeId;
				m_config.m_frequency = (double)m_xnLoader.getFrequencyHz(m_nodeId) / 1000000;
				m_selectedRoute = null;

				if (doNotify) {
					notify(src, Notifier.NOTIFY_SET_NODE_CORE, "");
				}
				
			} else {
				notify(src, Notifier.NOTIFY_ERROR, String.format("Error initialising node: %d", nodeId));
			}
		}
	}

	public int getNodeId() {
		return m_nodeId;
	}

	public void setCoreId(CommandSource src, int coreId) {
		if (m_coreId != coreId) {
			if (initialiseGlobalStructure(src, m_xeLoader, m_sections, false, m_nodeId, coreId)) {
				m_coreId = coreId;
				m_selectedRoute = null;
				notify(src, Notifier.NOTIFY_SET_NODE_CORE, "");		
				
			} else {
				notify(src, Notifier.NOTIFY_ERROR, String.format("Error initialising core: %d", coreId));
			}
		}
	}

	public int getCoreId() {
		return m_coreId;
	}

	public void setFrequency(CommandSource src, double frequency) {
		m_config.m_frequency = frequency;
		notify(src, Notifier.NOTIFY_UPDATE_STATE, Notifier.STATE_FREQUENCY);
	}

	public double getFrequency() {
		return m_config.m_frequency;
	}

	public void setNumThreads(CommandSource src, int numThreads) {
		m_config.m_numThreads = numThreads;
		notify(src, Notifier.NOTIFY_UPDATE_STATE, Notifier.STATE_NUM_THREADS);
	}

	public int getNumThreads() {
		return m_config.m_numThreads;
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
		
	public void updateSourceMappers(String fileName, File file) {
		for (int nodeId : m_xeLoader.getNodeIds()){
			for (int coreId : m_xeLoader.getCoreIds(nodeId)) {
				SourceMapper sourceMapper = m_sourceMapper.get(nodeId, coreId);		
				if (sourceMapper != null) {
					updateSourceMapper(sourceMapper, fileName, file, nodeId, coreId);
				}
				m_userSpecifedFileLocations.add(new FileLocation(nodeId, coreId, fileName, file));
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
			int nodeId = routeTiming.getNodeId();
			if (nodeId != m_nodeId)
				setNodeId(src, nodeId, true);
	
			int coreId = routeTiming.getCoreId();
			if (coreId != m_coreId)
				setCoreId(src, coreId);
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
		m_defines.addFunctionTime(m_nodeId, m_coreId, functionRef, timeUnit);
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

		m_defines.addPathTime(m_nodeId, m_coreId, fromRef, toRef, timeUnit);
		notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
	}	

	public void addInstructionTime(CommandSource src, String refString, final TimeUnit timeUnit) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.addInstructionTime(m_nodeId, m_coreId, reference, timeUnit);
			}			
		});
	}
	
	public void addLoop(CommandSource src, String refString, final long iterations) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.addLoop(m_nodeId, m_coreId, reference, iterations);
			}			
		});
	}

	public void addLoopPath(CommandSource src, String refString, final long iterations) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.addLoopPath(m_nodeId, m_coreId, reference, iterations);
			}			
		});
	}

	public void addLoopScope(CommandSource src, String refString, final boolean absolute) {		
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.addLoopScope(m_nodeId, m_coreId, reference, absolute);
			}			
		});
	}

	public void removeFunctionTime(CommandSource src, String functionName) {
		Reference functionRef = ReferenceFactory.createReference(functionName, ReferenceClass.FUNCTION_REF);
		if (!resolveOrError(src, functionRef)) {
			return;
		}
		m_defines.removeFunctionTime(m_nodeId, m_coreId, functionRef);
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

		m_defines.removePathTime(m_nodeId, m_coreId, fromRef, toRef);
		notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
	}	

	public void removeInstructionTime(CommandSource src, String refString) {	
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.removeInstructionTime(m_nodeId, m_coreId, reference);
			}			
		});
	}
	
	public void removeLoop(CommandSource src, String refString) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.removeLoop(m_nodeId, m_coreId, reference);
			}			
		});
	}

	public void removeLoopPath(CommandSource src, String refString) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.removeLoopPath(m_nodeId, m_coreId, reference);
			}			
		});
	}

	public void removeLoopScope(CommandSource src, String refString) {		
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.removeLoopScope(m_nodeId, m_coreId, reference);
			}			
		});
	}
	
	public Collection<FunctionTimeInfo> getFunctionTimeDefines() {
		return m_defines.getFunctionTimes(m_nodeId, m_coreId);
	}

	public Collection<PathTimeInfo> getPathTimeDefines() {
		return m_defines.getPathTimes(m_nodeId, m_coreId);
	}

	public Collection<InstructionTimeInfo> getInstructionTimeDefines() {
		return m_defines.getInstructionTimes(m_nodeId, m_coreId);
	}
	
	public Collection<LoopInfo> getLoopDefines() {
		return m_defines.getLoops(m_nodeId, m_coreId);
	}

	public Collection<LoopPathInfo> getLoopPathDefines() {
		return m_defines.getLoopPaths(m_nodeId, m_coreId);
	}

	public Collection<LoopScopeInfo> getLoopScopeDefines() {
		return m_defines.getLoopScopes(m_nodeId, m_coreId);
	}
	
	Defines getDefines() {
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
		
		if (m_sections.addToBranches(src, m_nodeId, m_coreId, fromRef, toRefs)) {
			m_structuralGraphUpToDate.add(m_nodeId, m_coreId, false);			
			return true;			
		}		
		return false;
	}

	private void checkInstructionIsBranch(CommandSource src, Reference ref) {
		for (long pc : ref.getPcs()) {
			InstructionNode node = m_sourceMapper.get(m_nodeId, m_coreId).getInstructionNode(pc);
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
		
		if (m_sections.removeFromBranches(src, m_nodeId, m_coreId, fromRef, toRefs)) {
			m_structuralGraphUpToDate.add(m_nodeId, m_coreId, false);
			return true;
		}
		return false;
	}

	public Map<Reference, List<Reference>> getUserBranches() {
		Map<Reference, List<Reference>> branches = new HashMap<Reference, List<Reference>>();
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
		return m_sections.getBranches(m_nodeId, m_coreId);
	}
	
	public Map<String, Set<Long>> getLabels() {
		return m_sections.getLabels(m_nodeId, m_coreId);
	}
	
	public List<CodePointInfo> getSrcLabels() {
		return m_sections.getSrcLabels(m_nodeId, m_coreId);
	}

	public Map<Long, SrcLabelTableInfo> getSrcLabelTable() {
		return m_sections.getSrcLabelTable(m_nodeId, m_coreId);
	}
	
	public List<CodePointInfo> getEndpoints() {
		return m_sections.getEndpoints(m_nodeId, m_coreId);
	}
		
	public Map<Long, CodePointTableInfo> getEndpointTable() {
		return m_sections.getEndpointTable(m_nodeId, m_coreId);
	}
	
	public List<CodePointInfo> getCalls() {
		return m_sections.getCalls(m_nodeId, m_coreId);
	}
		
	public Map<Long, CodePointTableInfo> getCallTable() { 
		return m_sections.getCallTable(m_nodeId, m_coreId);
	}
	
	public Collection<Long> getThreadStartPoints() {
		return m_sections.getThreadStartPoints(m_nodeId, m_coreId);
	}	

	public Collection<Long> getThreadStopPoints() {
		return m_sections.getThreadStopPoints(m_nodeId, m_coreId);
	}	
	
	public Collection<SrcStringInfo> getSrcLevelCommands() {
		return m_sections.getSrcLevelCommands(m_nodeId, m_coreId);
	}

	
	public Collection<SrcLevelLoopInfo> getSrcLevelLoops() {
		return m_sections.getSrcLevelLoops(m_nodeId, m_coreId);
	}
	
	public Collection<String> getSystems() {
		return m_sections.getSystems(m_nodeId, m_coreId);
	}

	public Collection<String> getRecursiveFunctions() {
		return m_sections.getRecursiveFunctions(m_nodeId, m_coreId);
	}

	
	public Collection<String> getUnreachableFunctions() {
		return m_sections.getUnreachableFunctions(m_nodeId, m_coreId);
	}
	
	// ===================================================================== //
	// Private Methods
	// ===================================================================== //
	private boolean initialiseGlobalStructure(CommandSource src, XeLoader xeLoader, Sections sections,
			boolean clearPrevious, int nodeId, int coreId) {

		if (clearPrevious) {
			m_structuralGraph = null;
			m_functionNameToInsts = null;
			m_sourceMapper = null;	
		}
		
		m_loadStartTime = System.currentTimeMillis();
		notify(src, Notifier.HOLD_MESSAGES, "");
		NodeCoreCollection<StructureGraph> newStructuralGraph =
			new NodeCoreCollection<StructureGraph>(m_structuralGraph);
		NodeCoreCollection<Map<String, List<InstructionNode>>> newFunctionNames =
			new NodeCoreCollection<Map<String, List<InstructionNode>>>(m_functionNameToInsts);
		NodeCoreCollection<SourceMapper> newSourceMappers =
			new NodeCoreCollection<SourceMapper>(m_sourceMapper);
		
		if (m_structuralGraphUpToDate.get(nodeId, coreId) != null && 
			m_structuralGraphUpToDate.get(nodeId, coreId)) {
			//System.out.println("node " + nodeId + " core " + coreId + " is up to date");
			
		} else {
			setAnalysisName(String.format("Processing node: %d, core: %d", nodeId, coreId));
			
			InstructionDecoder id = new InstructionDecoder(nodeId, coreId, xeLoader, m_config, m_dataCollection);
			try {
				id.decode();
			} catch (Exception e) {
				notify(src, Notifier.NOTIFY_ERROR, e.getMessage());								
				return false;
			} 
			
			newFunctionNames.add(nodeId, coreId, new TreeMap<String, List<InstructionNode>>());
			
			ThreadHandler th = new ThreadHandler(id, this);

			Set<InstructionNode> threadStartNodes = 
				th.getThreadNodes(src, sections.getThreadStartPoints(nodeId, coreId), "thread start");
			Set<InstructionNode> threadStopNodes = 
				th.getThreadNodes(src, sections.getThreadStopPoints(nodeId, coreId), "thread stop");
			
			threadStartNodes = th.ensureThreadStartExists(src, threadStartNodes);

			SuccessorAdder sa = new SuccessorAdder(nodeId, coreId, id, sections, threadStopNodes, xeLoader, this);
			if (!sa.process(src))
				return false;
			warnAboutUnsetBranches(xeLoader, sections, src, id, nodeId, coreId);
			
			FunctionHandler fh = new FunctionHandler(id);
			if (!fh.addFunctions()) 
				return false;
			
			Map<Long, Set<Node>> pcToEndPoints = th.addTerminators(threadStartNodes);
			pcToEndPoints.putAll(fh.getFunctionsToEndPoints());
			
			Set<InstructionNode> starts = new TreeSet<InstructionNode>();
			starts.addAll(fh.getKnownFuncStarts());
			starts.addAll(threadStartNodes);
			
			ensureAllFunctionsAreNamed(xeLoader.getSymbolTable(nodeId, coreId), 
					newFunctionNames.get(nodeId, coreId), fh.getKnownFuncStarts(),
					threadStartNodes, sections.getLabels(nodeId, coreId));
			
			setBranchTargets(id, newFunctionNames.get(nodeId, coreId));

			SourceMapper sourceMapper;
			try {
				sourceMapper = new SourceMapper(this, xeLoader, nodeId, coreId, starts, 
						id, newFunctionNames.get(nodeId, coreId), sections.getLabels(nodeId, coreId));
			} catch (AbortedException e) {
				return false;
			}
			
			for (FileLocation fileLocation : m_userSpecifedFileLocations) {
				if ((fileLocation.m_nodeId == nodeId) && (fileLocation.m_coreId == coreId)) {
					updateSourceMapper(sourceMapper, fileLocation.m_fileName, fileLocation.m_file, nodeId, coreId);
				}
			}
			
			newSourceMappers.add(nodeId, coreId, sourceMapper);
			
			UnreachableCodeFinder uca = new UnreachableCodeFinder();
			uca.analyseReachable(id, threadStartNodes, fh.getKnownFuncStarts()); 
			
			setAnalysisName("Creating control flow graph");				
			ControlFlowGraph cfg = new ControlFlowGraph(threadStartNodes, fh.getKnownFuncStarts());
			
			StructuralAnalysisEngine sae = new StructuralAnalysisEngine(cfg, xeLoader);
			
			try {
				if (!sae.analyse(src, this, newFunctionNames.get(nodeId, coreId),
						pcToEndPoints, sections, nodeId, coreId)) {
					return false;
				}
			} catch (AbortedException e) {
				return false;
			}

			newStructuralGraph.add(nodeId, coreId, sae.getStructureGraph());
		}
		
		m_structuralGraphUpToDate.add(nodeId, coreId, true);
		m_structuralGraph = newStructuralGraph;
		m_functionNameToInsts = newFunctionNames;
		m_sourceMapper = newSourceMappers;
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
			return false;
		}
		return success;
	}

	private boolean doRouteAnalysis(CommandSource src, RouteDescription routeDescription, boolean temporaryRoute) throws Exception {	
		if (!m_xeLoader.isLoaded(m_nodeId, m_coreId)) {
			notify(src, Notifier.NOTIFY_ERROR, String.format(
					"node: %d, Core: %d image not found in file: %s",
					m_nodeId, m_coreId, m_executableFile));
			return false;
		}

		if (!temporaryRoute) {
		    notify(src, Notifier.NOTIFY_ANALYSIS_BEGIN, "");
		}
		
		if (!m_structuralGraphUpToDate.get(m_nodeId, m_coreId)) {
			if (!initialiseGlobalStructure(src, m_xeLoader, m_sections, false, m_nodeId, m_coreId))
				return false;
		}

        RouteContainer routeContainer = new RouteContainer(src, routeDescription); 
		RouteConstructor routeConstructor = new RouteConstructor(this, m_structuralGraph.get(m_nodeId, m_coreId), routeDescription, routeContainer);				
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
	
	private void warnAboutUnsetBranches(XeLoader xeLoader, Sections sections, CommandSource src, InstructionDecoder id,
			int nodeId, int coreId) {
		SymbolTable symbolTable = xeLoader.getSymbolTable(nodeId, coreId);
		Collection<String> systemFunctions = sections.getSystems(nodeId, coreId);

		for (InstructionNode inst : id.getInstructions()) {
			if (inst.getXtaInstruction().isBranch() && (inst.getNextNodesSize() == 0) 
					&& !inst.getXtaInstruction().getReturn() && !inst.getXtaInstruction().getBranchPcRelative()) {
				
				String functionName = symbolTable.functionName(inst.getXtaInstruction().getPc());
				if (!isSystemFunction(systemFunctions, functionName)) {
					notify(src, Notifier.NOTIFY_WARNING, "No branch targets set on instruction " + inst.getXtaInstruction());
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
			Collection<InstructionNode> funcStarts, Set<InstructionNode> threadStarts,
			Map<String, Set<Long>> labelToPc) {
		
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
		
		for (InstructionNode inst : threadStarts) {
			String name = symbolTable.functionName(inst.getStartPc());
			if (treeMap.containsKey(name)) {
				name = "unnamed_thread_at_0x" + Long.toHexString(inst.getStartPc());
				addToMap(treeMap, inst, name);
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
	
    private void updateSourceMapper(SourceMapper sourceMapper, String fileName, File file, int nodeId, int coreId) {
        DebugEngine debugEngine = m_xeLoader.getDebugInfo(nodeId, coreId).getDebugEngine();					
        SourceFile sf = sourceMapper.getSourceFile(fileName);
        sourceMapper.setFile(debugEngine, sf, file);    	
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
}

class FileLocation {
	int m_nodeId;
	int m_coreId;
	String m_fileName;
	File m_file;
	
	FileLocation(int nodeId, int coreId, String fileName, File file) {
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_fileName = fileName;		
		m_file = file;
	}
}
