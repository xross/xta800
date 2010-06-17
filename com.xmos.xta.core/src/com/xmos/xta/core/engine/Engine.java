package com.xmos.xta.core.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.xmos.libs.dwarfhandler.DebugEngine;
import com.xmos.libs.memorymonitor.MemoryMonitor;
import com.xmos.libs.memorymonitor.MemoryMonitorException;
import com.xmos.libs.proginfoloader.ProgInfoEntry;
import com.xmos.libs.proginfoloader.ProgInfoLoader;
import com.xmos.libs.util.IteratableMap;
import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.NonIteratableMap;
import com.xmos.libs.util.NonIteratableSet;
import com.xmos.libs.util.Pair;
import com.xmos.libs.xeloader.MemoryImage;
import com.xmos.libs.xeloader.SymbolTable;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.libs.xnloader.XmlHandler;
import com.xmos.libs.xnloader.XnLoader;
import com.xmos.xta.core.engine.analysis.Analyzer;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.controlflow.CallGraph;
import com.xmos.xta.core.engine.analysis.controlflow.FnopAdder;
import com.xmos.xta.core.engine.analysis.controlflow.FunctionConstructor;
import com.xmos.xta.core.engine.analysis.controlflow.FunctionContainer;
import com.xmos.xta.core.engine.analysis.controlflow.FunctionReferenceNodeAdder;
import com.xmos.xta.core.engine.analysis.controlflow.SuccessorAdder;
import com.xmos.xta.core.engine.analysis.controlflow.UnresolvedSetter;
import com.xmos.xta.core.engine.analysis.routes.PreanalysisReferenceMatcher;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer;
import com.xmos.xta.core.engine.analysis.routes.RouteDefines;
import com.xmos.xta.core.engine.analysis.routes.StackNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.Source;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.debugger.Debugger;
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
import com.xmos.xta.core.engine.utils.CoreCollection;
import com.xmos.xta.core.engine.utils.CoreConfig;
import com.xmos.xta.core.engine.utils.CoreId;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class Engine extends Notifier implements Aborter {
	public static final int ROUTE_ID_LAST     = -1;
	public static final int ROUTE_ID_INVALID  = -2;
	public static final int ROUTE_ID_ALL      = -3;
	public static final int NODE_ID_TOP_LEVEL = -4;

	private XeLoader m_xeLoader;
	private List<Route> m_routes;
	private List<RouteContainer> m_routeContainers;
	private Collection<Reference> m_exclusions;
	private Route m_selectedRoute;
	private Config m_config;
	private File m_executableFile;
	private long m_loadedFileLastModifiedTime;
	private Notifier m_notifier;
	private ProgressMonitor m_progressMonitor;
	private String m_srcPaths;
	private Sections m_sections;
	private Defines m_defines;
	private int m_timeOut;
	private int m_numErrors;
	private long m_loadStartTime;
	private Map<String, Integer> m_nodeHz;
	private CoreCollection<Integer> m_coreThreads;
	private CoreCollection<Boolean> m_structuralGraphUpToDate;
	private CoreCollection<SourceMapper> m_sourceMappers;
	private CoreCollection<InstructionDecoder> m_instructionDecoders;
	private CoreCollection<CallGraph> m_callGraphs;
	private Collection<FileLocation> m_userSpecifedFileLocations;
	private Scripter m_scripter;
	private Collection<CoreId> m_activeCores;
	private PathPoints m_pathPoints;
	private Debugger m_debugger;
	private MemoryMonitor m_memoryMonitor;

	public Engine(Config config) {
		super(config);
		m_sourceMappers = new CoreCollection<SourceMapper>();
		m_instructionDecoders = new CoreCollection<InstructionDecoder>();
		m_callGraphs = new CoreCollection<CallGraph>();
		m_structuralGraphUpToDate = new CoreCollection<Boolean>();
		m_nodeHz = new IteratableMap<String, Integer>();
		m_coreThreads = new CoreCollection<Integer>();
		m_xeLoader = new XeLoader();
		m_routes = new LinkedList<Route>();
		m_routeContainers = new LinkedList<RouteContainer>();
		m_exclusions = new ArrayList<Reference>();
		m_activeCores = new ArrayList<CoreId>();
		m_config = config;
		m_executableFile = null;
		m_loadedFileLastModifiedTime = 0;
		m_srcPaths = ".";
		m_progressMonitor = null;
		m_sections = new Sections();
		m_defines = new Defines();
		m_timeOut = -1;
		m_numErrors = 0;
		m_scripter = new Scripter(this);
		m_userSpecifedFileLocations = new ArrayList<FileLocation>();
		m_pathPoints = new PathPoints(this);
		m_debugger = new Debugger(this);
		m_memoryMonitor = new MemoryMonitor();
		ReferenceTypeFactory.setEngine(this);
	}

	public void notify(CommandSource src, String message, String argument) {
		if (m_notifier != null){
			m_notifier.notify(src, message, argument);
		}

		String level = getLevel(message);
		if (level.equals(NOTIFY_ERROR)) {
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

	public MemoryImage getMemoryImage(CoreId coreId) {
		return m_xeLoader.getMemoryImage(coreId.getNodeChainIndex(), coreId.getCoreNumber());
	}

	public SymbolTable getSymbolTable(CoreId coreId) {
		return m_xeLoader.getSymbolTable(coreId.getNodeChainIndex(), coreId.getCoreNumber());
	}

	public SourceMapper getSourceMapper(CoreId coreId) {
		return m_sourceMappers.get(coreId);
	}

	public CallGraph getCallGraph(CoreId coreId) {
		return m_callGraphs.get(coreId);
	}

	public PathPoints getPathPoints() {
		return m_pathPoints;
	}
	
	public Debugger getDebugger() {
		return m_debugger;
	}

	public MemoryMonitor getMemoryMonitor() {
		return m_memoryMonitor;
	}
	
	public InstructionDecoder getInstructionDecoder(CoreId coreId) {
		return m_instructionDecoders.get(coreId);
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

		if (!isReloaded(executableFile)) {
			m_userSpecifedFileLocations.clear();
		}

		boolean successfulInit = initControlFlowGraphs(src, xeLoader, sections);
		if (successfulInit){
			initialiseCoreStates(xeLoader);
		    initialiseActiveCores(xeLoader);		
	
			m_exclusions = new ArrayList<Reference>();
			m_executableFile = executableFile;
			m_loadedFileLastModifiedTime = executableFile.lastModified();
			m_xeLoader = xeLoader;
			m_sections = sections;
			m_routes.clear();
			m_routeContainers.clear();
			m_selectedRoute = null;
			m_defines.clear();
			m_pathPoints.clear();
			Route.resetRouteCount();
	
			runSrcLevelCommands(src, commandRunner);

			m_scripter.setEngineLoadAndRunDone();
			notify(src, Notifier.NOTIFY_LOAD_EXECUTABLE, "");
		}
		
		return successfulInit;
	}

	public boolean analyzeFunction(CommandSource src, String functionName) {
		Analyzer analyzer = new Analyzer(this, m_routes, m_routeContainers);
		return analyzer.analyzeFunction(src, functionName);
	}

	public boolean analyzeEndpoints(CommandSource src) {
		Analyzer analyzer = new Analyzer(this, m_routes, m_routeContainers);
		return analyzer.analyzeEndpoints(src, m_pathPoints);
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
		m_config.setCase(c);
		notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
	}

	public Case getCase() {
		return m_config.getCase();
	}

	public void setVerbosity(int verbosity) {
		m_config.setVerbosity(verbosity);
	}

	public int getVerbosity() {
		return m_config.getVerbosity();
	}

	public void setWarnAsErrors(boolean errorOn) {
		m_config.setWarningsAsErrors(errorOn);
	}

	public void setErrorsAsWarnings(boolean warningsOn) {
		m_config.setErrorsAsWarnings(warningsOn);
	}

	public void setTimingFailuresAsErrors(boolean errorsOn) {
		m_config.setTimingFailuresAsErrors(errorsOn);
	}

	public boolean canInstructionPause(int routeId, String refString) {		
		try {
			for (Route route : getRoute(routeId)) {				
				Reference reference = ReferenceFactory.createReference(refString, ReferenceClass.ENDPOINT, 
						route.getRouteCoreContainer().getCoreId());

				if (resolveReference(reference)) {
					if (route.getRouteUnknowns().canInstructionPause(reference)) {
					    return true;
					}
				}				
			}
		} catch (AbortedException e) { }
		return false;
	}

	public Collection<Route> getRoute(int routeId) {
		Collection<Route> routes = new ArrayList<Route>();
		assert(validateRouteCount());
		if (m_routes.isEmpty()){
			return routes;
		}
		if (routeId == ROUTE_ID_LAST) {
			assert(validateRouteCount());
			int toIndex = m_routes.size();
			int fromIndex = toIndex - getNumLastRoutesAnalyzed();
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

		} else if ((m_selectedRoute != null) && (m_selectedRoute.getRouteId() == routeId)) {
			this.setSelectedRoute(src, m_routes.get(m_routes.size() - 1));
			assert(validateRouteCount());
		}
		assert(validateRouteCount());
		notify(src, Notifier.NOTIFY_REMOVE_ROUTE, "");
		assert(validateRouteCount());
		return true;
	}

	public File getExecutable() {
		return m_executableFile;
	}

	public long getExecutableLastModifiedTime() {
		return m_loadedFileLastModifiedTime;
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

	public void updateSourceMappers(CommandSource src, Notifier notifier, String fileName, File file) {
		for (int nodeChainIndex : m_xeLoader.getNodeChainIndexes()) {
			for (int coreNumber : m_xeLoader.getCoreNumbers(nodeChainIndex)) {
				CoreId coreId = new CoreId(m_xeLoader.getXnLoader(), nodeChainIndex, coreNumber);
				SourceMapper sourceMapper = m_sourceMappers.get(coreId);
				if (sourceMapper != null) {
					updateSourceMapper(src, notifier, sourceMapper, fileName, file, coreId);
				}
				m_userSpecifedFileLocations.add(new FileLocation(nodeChainIndex, coreNumber, fileName, file));
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
	public void setFrequency(CommandSource src, String nodeId, double frequency) {
		if (checkFrequency(src, nodeId, frequency)) {
			m_nodeHz.put(nodeId, (int)(frequency * 1000000));
			notify(src, Notifier.NOTIFY_UPDATE_STATE, "");			
		}
	}

	public double getFrequency(String nodeId) {
		if (m_nodeHz.containsKey(nodeId)) {
			return (double)m_nodeHz.get(nodeId) / 1000000;
		} else {
			return CoreConfig.DEFAULT_FREQUENCY_MHZ;
		}
	}
	
	public Map<String, Double> getFrequencyMap() {
		Map<String, Double> frequencies = new NonIteratableMap<String, Double>();
		for (String nodeId : m_nodeHz.keySet()) {
			double frequency = (double)m_nodeHz.get(nodeId) / 1000000;
			frequencies.put(nodeId, frequency);
		}
		return frequencies;
	}
	
	// ===================================================================== //
	// Get/Set Num Threads.
	// ===================================================================== //		
	public void setNumThreads(CommandSource src, CoreId coreId, int numThreads) {
		if (checkNumThreads(src, numThreads)) {
   		    m_coreThreads.add(coreId, numThreads);
	    	notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
		}
	}

	public int getNumThreads(CoreId coreId) {
		Integer threads = m_coreThreads.get(coreId);
		if (threads == null) {
			return CoreConfig.DEFAULT_THREADS;
		}
		return threads;
	}

	public Map<CoreId, Integer> getNumThreadMap() {
		Map<CoreId, Integer> threads = new NonIteratableMap<CoreId, Integer>();
		for (CoreId coreId : m_coreThreads.getEntries()) {
			threads.put(coreId, m_coreThreads.get(coreId));
		}
		return threads;
	}

	// ===================================================================== //
	// Function Names/Pcs
	// ===================================================================== //
	public Set<String> getFunctionNames(CoreId coreId) {
		Set<FunctionContainer> functionContainers = m_callGraphs.get(coreId).getFunctionContainers();
		if (functionContainers != null) {
			Set<String> functionNames = new IteratableSet<String>();
			for(FunctionContainer fc : functionContainers){
				functionNames.add(fc.getFunctionName());
			}
			
		    return functionNames;
	    }
		return new NonIteratableSet<String>();
	}

	public Collection<String> getNonSystemFunctionNames() {
		List<String> functionNames = new LinkedList<String>();

		for (CoreId coreId : getActiveCores()) {
			if (!m_xeLoader.isLoaded(coreId.getNodeChainIndex(), coreId.getCoreNumber()))
				continue;

			List<String> coreFunctions = new LinkedList<String>();
			coreFunctions.addAll(getFunctionNames(coreId));
	
			Set<String> systemFunctions = new NonIteratableSet<String>();
			Collection<String> systems = m_sections.getSystems(coreId);
			for (String functionName : coreFunctions) {
				if (isSystemFunction(systems, functionName)) {
					systemFunctions.add(functionName);
				}
			}
			coreFunctions.removeAll(systemFunctions);
			functionNames.addAll(coreFunctions);
		}

		Set<String> uniqueNames = new IteratableSet<String>();
		uniqueNames.addAll(functionNames);
		functionNames.clear();
		functionNames.addAll(uniqueNames);
		Collections.sort(functionNames);
		return functionNames;
	}
	
	public List<Long> getFunctionStartPcs(String functionName, CoreId coreId) {
		List<Long> pcs = new LinkedList<Long>();
		for(FunctionContainer fc : m_callGraphs.get(coreId).getFunctionContainers()){
			if(functionName.equals(fc.getFunctionName())){
				pcs.add(fc.getStartInstruction().getPC());
			}
		}		
		return pcs;
	}
	
	// ===================================================================== //
	// Active Core Methods
	// ===================================================================== //
	public void addActiveCores(CommandSource src) {
		m_activeCores.clear();
		if (m_xeLoader.isLoaded()) {
			for (int nodeChainIndex : m_xeLoader.getNodeChainIndexes()) {
				for (int coreNumber : m_xeLoader.getCoreNumbers(nodeChainIndex)) {
					m_activeCores.add(new CoreId(m_xeLoader.getXnLoader(), nodeChainIndex, coreNumber));
				}
			}
		}
		m_pathPoints.refresh();
		notify(src, Notifier.NOTIFY_UPDATE_ACTIVE_CORES, "");
	}	

	public void addActiveCore(CommandSource src, CoreId activeCore) {		
		if (m_xeLoader.isLoaded(activeCore.getNodeChainIndex(), activeCore.getCoreNumber())) {
			if (!m_activeCores.contains(activeCore)) {
				m_activeCores.add(activeCore);
			}						
			m_pathPoints.refresh();
			notify(src, Notifier.NOTIFY_UPDATE_ACTIVE_CORES, "");
			
		} else {
			notify(src, Notifier.NOTIFY_ERROR, String.format(
					"node: %d, Core: %d not currently loaded", 
					activeCore.getNodeChainIndex(), activeCore.getCoreNumber()));
		}		
	}

	public void removeActiveCores(CommandSource src) {
		m_activeCores.clear();
		m_pathPoints.refresh();
		notify(src, Notifier.NOTIFY_UPDATE_ACTIVE_CORES, "");
	}

	public void removeActiveCore(CommandSource src, CoreId activeCore) {
		m_activeCores.remove(activeCore);
		m_pathPoints.refresh();
		notify(src, Notifier.NOTIFY_UPDATE_ACTIVE_CORES, "");
	}

	public Collection<CoreId> getActiveCores() {
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
		if (routeId == ROUTE_ID_INVALID) {
			setSelectedRoute(src, null);
			
		} else {
			Collection<Route> routes = getRoute(routeId);
			assert(routes.size() > 0);
			setSelectedRoute(src, routes.iterator().next());
		}
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
		for (CoreId activeCore : m_activeCores) {
			
			Reference functionRef = ReferenceFactory.createReference(functionName, 
					ReferenceClass.FUNCTION, activeCore);
			
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
		for (CoreId activeCore : m_activeCores) {
			
			Reference fromRef = ReferenceFactory.createReference(from, 
					ReferenceClass.ENDPOINT, activeCore);
			Reference toRef = ReferenceFactory.createReference(to, 
					ReferenceClass.ENDPOINT, activeCore);

			if (resolveReference(fromRef) && resolveReference(toRef)) {
			    m_defines.addPathTime(fromRef, toRef, timeUnit);
			    hit = true;
			}
		}
		if (hit) {	
		    notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
		} else {
		    notify(src, Notifier.NOTIFY_ERROR, "References not resolved on any active core");
		}		    
	}

	public void addInstructionTime(CommandSource src, String refString, final TimeUnit timeUnit) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.addInstructionTime(reference, timeUnit);
			}
		}, ReferenceClass.ANY);		
	}

	public void addLoop(CommandSource src, String refString, final long iterations) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.addLoop(reference, iterations);
			}
		}, ReferenceClass.ANY);
	}

	public void addLoopPath(CommandSource src, String refString, final long iterations) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.addLoopPath(reference, iterations);
			}
		}, ReferenceClass.ANY);
	}

	public void addLoopScope(CommandSource src, String refString, final boolean absolute) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.addLoopScope(reference, absolute);
			}
		}, ReferenceClass.ANY);
	}

	public void removeFunctionTime(CommandSource src, String functionName) {
		boolean hit = false;
		for (CoreId activeCore : m_activeCores) {

			Reference functionRef = ReferenceFactory.createReference(functionName, 
					ReferenceClass.FUNCTION_WITH_EVERYTHING, activeCore);
			
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
		for (CoreId activeCore : m_activeCores) {
			
			Reference fromRef = ReferenceFactory.createReference(from, 
					ReferenceClass.ENDPOINT_WITH_EVERYTHING, activeCore);
			Reference toRef = ReferenceFactory.createReference(to, 
					ReferenceClass.ENDPOINT_WITH_EVERYTHING, activeCore);

			if (resolveReference(fromRef) && resolveReference(toRef)) {
			    m_defines.removePathTime(fromRef, toRef);
			    hit = true;
			}
		}
		if (hit) {	
		    notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
		} else {
		    notify(src, Notifier.NOTIFY_ERROR, "References not resolved on any active core");
		}		    		
	}

	public void removeInstructionTime(CommandSource src, String refString) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.removeInstructionTime(reference);
			}
		}, ReferenceClass.ANY_WITH_EVERYTHING);
	}

	public void removeLoop(CommandSource src, String refString) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.removeLoop(reference);
			}
		}, ReferenceClass.ANY_WITH_EVERYTHING);
	}

	public void removeLoopPath(CommandSource src, String refString) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.removeLoopPath(reference);
			}
		}, ReferenceClass.ANY_WITH_EVERYTHING);
	}

	public void removeLoopScope(CommandSource src, String refString) {
		updateDefines(src, refString, new DefinesUpdate() {
			public void run(Reference reference) {
				m_defines.removeLoopScope(reference);
			}
		}, ReferenceClass.ANY_WITH_EVERYTHING);
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
	// Set Function Time Methods
	// ===================================================================== //
	public void setFunctionTime(CommandSource src, int routeId, String functionName, TimeUnit timeUnit) {		
		RouteDefines defines = new RouteDefines();
		
		boolean hit = false;
		try {
			for (Route route : getRoute(routeId)) {
				
				Reference functionRef = ReferenceFactory.createReference(functionName, 
						ReferenceClass.FUNCTION, route.getRouteCoreContainer().getCoreId());
				
				if (resolveReference(functionRef)) {
					FunctionTimeInfo info = new FunctionTimeInfo(functionRef, timeUnit);					
					defines.applyFunctionDefine(src, route, info, Source.SET);
					hit = true;
				}
			}
		} catch (AbortedException e) {	}
		
		if (hit) {
		    notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
		} else {
		    notify(src, Notifier.NOTIFY_ERROR, String.format("Reference: %s not resolved on any active core", functionName));			
		}
	}

	public void setFunctionTime(CommandSource src, Node node, TimeUnit timeUnit) {
		if (node.isFunctionNode()) {
			Route route = getSelectedRoute();
			route.getRouteUnknowns().setFixedTime(node, timeUnit, Type.FUNCTION_TIME, Source.SET);
		}
	}

	// ===================================================================== //
	// Set Path Time Methods
	// ===================================================================== //	
	public void setPathTime(CommandSource src, int routeId, String from, String to, TimeUnit timeUnit) {
		RouteDefines defines = new RouteDefines();
		
		boolean hit = false;		
		try {			
			for (Route route : getRoute(routeId)) {
				CoreId coreId = route.getRouteCoreContainer().getCoreId();

				Reference fromRef = ReferenceFactory.createReference(from, 
						ReferenceClass.ENDPOINT, coreId);
				Reference toRef = ReferenceFactory.createReference(to, 
						ReferenceClass.ENDPOINT, coreId);

				if (resolveReference(fromRef) && resolveReference(toRef)) {
					PathTimeInfo info = new PathTimeInfo(fromRef, toRef, timeUnit);					
					defines.applyPathDefine(this, src, route, info, Source.SET);
					hit = true;
				}
			}
		} catch (AbortedException e) { }

		if (hit) {
		    notify(src, Notifier.NOTIFY_ROUTE_CHANGED, String.format("%d", routeId));
		} else {
		    notify(src, Notifier.NOTIFY_ERROR, "References not resolved on any active core");
		}
	}

	// ===================================================================== //
	// Set Instruction Time Methods
	// ===================================================================== //
	public boolean setInstructionTime(CommandSource src, int routeId, String refString, TimeUnit timeUnit) {
		Collection<Route> routes = getRoute(routeId);
		if (routes.isEmpty()) {
			return false;
		}

		boolean hit = false;		
		try {
	    	Map<Route, Reference> refMap = new NonIteratableMap<Route, Reference>();
			for (Route route : routes) {
				Reference reference = ReferenceFactory.createReference(refString, 
						ReferenceClass.ENDPOINT,
				        route.getRouteCoreContainer().getCoreId());
								
				if (resolveReference(reference)) {
					if (!route.getRouteUnknowns().canInstructionPause(reference))
						return false;
					
					refMap.put(route, reference);
					hit = true;
				}				
			}
			for (Route route : routes) {
				if (refMap.containsKey(route)) {				
			        route.getRouteUnknowns().setFixedTime(refMap.get(route), timeUnit, Type.INSTRUCTION_TIME, Source.SET);
				}
			}
		} catch (AbortedException e) { 
			return false;			
		}
				
		if (hit) {
		    notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
		} else {
		    notify(src, Notifier.NOTIFY_ERROR, String.format("Reference: %s not resolved on any active core", refString));			
		}		
	    notify(src, Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
	    return true;
	}

	public void setInstructionTime(CommandSource src, Node node, TimeUnit timeUnit) {
		m_selectedRoute.getRouteUnknowns().setFixedTime(node, timeUnit, Type.INSTRUCTION_TIME, Source.SET);
		notify(src, Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
	}

	public TimeUnit getInstructionTime(Node node) {
		return m_selectedRoute.getRouteUnknowns().getFixedTime(node);
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

		boolean hit = false;		
		try {
			Map<Route, Reference> refMap = new NonIteratableMap<Route, Reference>();
			for (Route route : routes) {				
				Reference reference = ReferenceFactory.createReference(refString, 
						ReferenceClass.ANY,
				        route.getRouteCoreContainer().getCoreId());
				
				if (resolveReference(reference)) {
					route.getRouteUnknowns().checkSetLoopIterations(reference, iterations);
					refMap.put(route, reference);
					hit = true;
				}								
			}
			for (Route route : routes) {
				if (refMap.containsKey(route)) {
			        route.getRouteUnknowns().setLoopIterations(src, this,
			        	refMap.get(route), iterations, false, Source.SET);
				}
			}

		} catch (Exception e) {
			notify(src, Notifier.NOTIFY_ERROR, e.getMessage());
			return;
		}
		if (hit) {
		    notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
		} else {
		    notify(src, Notifier.NOTIFY_ERROR, String.format("Reference: %s not resolved on any active core", refString));			
		}		
	    notify(src, Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
	}

	public void setLoop(CommandSource src, Node node, long iterations) {
		try {
			m_selectedRoute.getRouteUnknowns().setLoopIterations(src, this, node, iterations, Source.SET);
			notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
		    notify(src, Notifier.NOTIFY_UPDATE_UNKNOWNS, "");

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
		
		boolean hit = false;				
		try {
			Map<Route, Reference> refMap = new NonIteratableMap<Route, Reference>();
			for (Route route : routes) {				
				Reference reference = ReferenceFactory.createReference(refString, 
						ReferenceClass.ANY,
				        route.getRouteCoreContainer().getCoreId());
				
				if (resolveReference(reference)) {
					route.getRouteUnknowns().checkSetLoopPathIterations(reference, iterations);
					refMap.put(route, reference);
					hit = true;
				}								
			}
			for (Route route : routes) {
				if (refMap.containsKey(route)) {
			        route.getRouteUnknowns().setLoopPathIterations(refMap.get(route), iterations, Source.SET);
				}
			}			
			
		} catch (Exception e) {
			notify(src, Notifier.NOTIFY_ERROR, e.getMessage());
			return;
		}
		if (hit) {
		    notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
		} else {
		    notify(src, Notifier.NOTIFY_ERROR, String.format("Reference: %s not resolved on any active core", refString));			
		}		
	}

	public void setLoopPath(CommandSource src, Node node, long iterations) {
		try {
			m_selectedRoute.getRouteUnknowns().setLoopPathIterations(node, iterations, Source.SET);
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
		
		boolean hit = false;				
		try {
			Map<Route, Reference> refMap = new NonIteratableMap<Route, Reference>();
			for (Route route : routes) {				
				Reference reference = ReferenceFactory.createReference(refString, 
						ReferenceClass.ANY,
				        route.getRouteCoreContainer().getCoreId());
				
				if (resolveReference(reference)) {
					route.getRouteUnknowns().checkSetLoopScope(reference, absolute);
					refMap.put(route, reference);
					hit = true;
				}								
			}
			for (Route route : routes) {
				if (refMap.containsKey(route)) {
			        route.getRouteUnknowns().setLoopScope(refMap.get(route), absolute, Source.SET);
				}
			}			
			
		} catch (Exception e) {
			notify(src, Notifier.NOTIFY_ERROR, e.getMessage());
			return;
		}
		if (hit) {
		    notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
		} else {
		    notify(src, Notifier.NOTIFY_ERROR, String.format("Reference: %s not resolved on any active core", refString));			
		}		
	}

	public void setLoopScope(CommandSource src, Node node, boolean absolute) {
		try {
		    m_selectedRoute.getRouteUnknowns().setLoopScope(node, absolute, Source.SET);
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
	// Set Exclusion Methods
	// ===================================================================== //
	public boolean setExclusion(CommandSource src, int routeId, String refString) {
		Collection<Route> routes = getRoute(routeId);
		if (routes.isEmpty()) {
			return false;
		}
		
		boolean hit = false;
	    try {
			for (Route route : routes) {
				Reference reference = ReferenceFactory.createReference(refString, 
						ReferenceClass.ANY,
				        route.getRouteCoreContainer().getCoreId());

				if (resolveReference(reference)) {
					Set<Node> nodes = route.getReferencedNodes(reference);

					boolean okToSetExclusion = true;
					for (Node node : nodes) {
						okToSetExclusion &= canSetExclusion(node);
					}
					if (okToSetExclusion && !nodes.isEmpty()) {
						hit = route.getRouteUnknowns().setExclusion(reference, Type.EXCLUSION, Source.SET);	
					}
				}
			}
			
		} catch (AbortedException e) { 
			return false;			
		}

	    if (hit) {
	    	notify(src, Notifier.NOTIFY_ROUTE_CHANGED, String.format("%d", routeId));
		} else {
			notify(src, Notifier.NOTIFY_ERROR, String.format("Unable to set exclusion for reference: %s", refString));
		}
	    return hit;
	}

	public void setExclusion(CommandSource src, Node node) {
		if (canSetExclusion(node)) {
			m_selectedRoute.getRouteUnknowns().setExclusion(node, Type.EXCLUSION, Source.SET);		
			notify(src, Notifier.NOTIFY_ROUTE_CHANGED, String.format("%d", getSelectedRouteId()));
			
		} else {
			notify(src, Notifier.NOTIFY_ERROR, String.format("Cannot remove all paths from route"));
		}		
	}
	
	public boolean canSetExclusion(Node node) {
		try {
			m_selectedRoute.getRouteUnknowns().checkExclusionWillLeaveAPath(node);
			return true;

		} catch (Exception e) {
			return false;
		}
	}
	
	// ===================================================================== //
	// Exclusion Methods
	// ===================================================================== //
	public boolean addExclusion(CommandSource src, String refString) {
		boolean hit = false;
		for (CoreId activeCore : m_activeCores) {
			Reference reference = ReferenceFactory.createReference(refString, 
					ReferenceClass.ANY, activeCore);
			
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
		for (CoreId activeCore : m_activeCores) {
			Reference reference = ReferenceFactory.createReference(refString, 
					ReferenceClass.ANY_WITH_EVERYTHING, activeCore);
			
			if (resolveReference(reference)) {
                Iterator<Reference> iter = m_exclusions.iterator();
                while (iter.hasNext()) {
                    Reference ref = iter.next();
                    if (ref.equals(reference) && 
                    	ref.getCoreId().getCoreReference().equals(reference.getCoreId().getCoreReference())) {
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
		for (CoreId activeCore : m_activeCores) {

			Reference fromRef = ReferenceFactory.createReference(fromRefString, 
					ReferenceClass.ANY, activeCore);
			boolean fromResolved = resolveReference(fromRef);
			
			if (fromResolved)			
			    checkInstructionIsBranch(src, fromRef, activeCore);
			
			Collection<Reference> toRefs = new ArrayList<Reference>();
			for (String toRefString : toRefStrings) {
				Reference ref = ReferenceFactory.createReference(toRefString, 
						ReferenceClass.ANY, activeCore);				
				toRefs.add(ref);
			}		
			boolean toResolved = true;
			for (Reference toRef : toRefs) {
				if (!resolveReference(toRef)) {
					toResolved = false;
				}
			}
			
			try {
				List<StackNode> stackNodes = getPathStackNodes(m_callGraphs.get(activeCore), fromRef);
				if(!stackNodes.isEmpty()) {
					if (fromResolved && toResolved) {
						if (m_sections.addToBranches(src, activeCore, fromRef, toRefs)) {
							m_structuralGraphUpToDate.add(activeCore, false);
							hit = true;
						}				
					}
				}
			} catch (AbortedException e) {
			} catch (MemoryMonitorException e) {
			}			
		}
		
		if (!hit) {
		    notify(src, Notifier.NOTIFY_ERROR, "References not resolved on any active core");
		} else {
			initControlFlowGraphs(src, m_xeLoader, m_sections);
		}		
		return hit;
	}
	
	public boolean removeBranch(CommandSource src, String fromRefString, Collection<String> toRefStrings) {
		boolean hit = false;
		for (CoreId activeCore : m_activeCores) {
			
			Reference fromRef = ReferenceFactory.createReference(fromRefString, 
					ReferenceClass.ANY_WITH_EVERYTHING, activeCore);
			boolean fromResolved = resolveReference(fromRef);
			
			if (fromResolved)
			    checkInstructionIsBranch(src, fromRef, activeCore);
			
			Collection<Reference> toRefs = new ArrayList<Reference>();
			for (String toRefString : toRefStrings) {
				Reference ref = ReferenceFactory.createReference(toRefString, 
						ReferenceClass.ANY_WITH_EVERYTHING, activeCore);				
				toRefs.add(ref);
			}		
			boolean toResolved = true;
			for (Reference toRef : toRefs) {
				if (!resolveReference(toRef)) {
					toResolved = false;
				}
			}
			
			if (fromResolved && toResolved) {
				if (m_sections.removeFromBranches(src, activeCore, fromRef, toRefs)) {
					m_structuralGraphUpToDate.add(activeCore, false);
					hit = true;
				}				
			}			
		}

		if (!hit) {
		    notify(src, Notifier.NOTIFY_ERROR, "References not resolved on any active core");
		}
		return hit;
	}
	
	public Map<Reference, List<Reference>> getUserBranches() {
		Map<Reference, List<Reference>> branches = new IteratableMap<Reference, List<Reference>>();
		
        for (int nodeChainIndex : m_xeLoader.getNodeChainIndexes()) {
            for (int coreNumber : m_xeLoader.getCoreNumbers(nodeChainIndex)) {
            	
        		Map<Reference, Set<BranchInfo>> currentBranches = getBranches(
        				new CoreId(m_xeLoader.getXnLoader(), nodeChainIndex, coreNumber));
        		
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

	public Map<Reference, Set<BranchInfo>> getBranches(CoreId coreId) {
		return m_sections.getBranches(coreId);
	}

	public Map<String, Set<Long>> getLabels(CoreId coreId) {
		return m_sections.getLabels(coreId);
	}

	public List<CodePointInfo> getSrcLabels(CoreId coreId) {
		return m_sections.getSrcLabels(coreId);
	}

	public Map<Long, SrcLabelTableInfo> getSrcLabelTable(CoreId coreId) {
		return m_sections.getSrcLabelTable(coreId);
	}

	public List<CodePointInfo> getEndpoints(CoreId coreId) {
		return m_sections.getEndpoints(coreId);
	}

	public Map<Long, CodePointTableInfo> getEndpointTable(CoreId coreId) {
		return m_sections.getEndpointTable(coreId);
	}

	public List<CodePointInfo> getCalls(CoreId coreId) {
		return m_sections.getCalls(coreId);
	}

	public Map<Long, CodePointTableInfo> getCallTable(CoreId coreId) {
		return m_sections.getCallTable(coreId);
	}

	public Collection<Long> getThreadStartPoints(CoreId coreId) {
		return m_sections.getThreadStartPoints(coreId);
	}

	public Collection<Long> getThreadStopPoints(CoreId coreId) {
		return m_sections.getThreadStopPoints(coreId);
	}

	public Collection<SrcStringInfo> getSrcLevelCommands(CoreId coreId) {
		return m_sections.getSrcLevelCommands(coreId);
	}

	public Collection<SrcLevelLoopInfo> getSrcLevelLoops(CoreId coreId) {
		return m_sections.getSrcLevelLoops(coreId);
	}

	public Collection<String> getSystems(CoreId coreId) {
		return m_sections.getSystems(coreId);
	}

    public CoreId getCoreId(CommandSource src, String coreReference) {
    	XnLoader xnLoader = m_xeLoader.getXnLoader();
		Pair<Integer,Integer> nodeCore = xnLoader.getNodeChainIndexCoreNumber(coreReference);
		if (nodeCore == null) {
    		notify(src, Notifier.NOTIFY_ERROR, String.format("core '%s' not found", coreReference));
    		return null;
		}
		int nodeChainIndex = nodeCore.first;
		int coreNumber = nodeCore.second;
		
    	if (m_xeLoader.getMemoryImage(nodeChainIndex, coreNumber) == null) {
    		notify(src, Notifier.NOTIFY_ERROR, String.format("core '%s' not found", coreReference));
    		return null;
    	}		
    	return new CoreId(xnLoader, nodeChainIndex, coreNumber);
    }

	public boolean updateControlFlowGraphs(CommandSource src, CoreId coreId) {
		assert(m_xeLoader.isLoaded(coreId.getNodeChainIndex(), coreId.getCoreNumber()));
	    if (!m_structuralGraphUpToDate.get(coreId)) {
			CoreCollection<CallGraph> callGraphs = new CoreCollection<CallGraph>(m_callGraphs);
			CoreCollection<SourceMapper> sourceMappers = new CoreCollection<SourceMapper>(m_sourceMappers);
			CoreCollection<InstructionDecoder> instructionDecoders = new CoreCollection<InstructionDecoder>(m_instructionDecoders);
			
			if (buildNodeCore(callGraphs, instructionDecoders, sourceMappers, m_xeLoader, m_sections, src, coreId)) {
				m_callGraphs = callGraphs;
				m_sourceMappers = sourceMappers;
				m_instructionDecoders = instructionDecoders;
			} else {
				return false;
			}
		}
	    return true;
	}
	
	private boolean initControlFlowGraphs(CommandSource src, XeLoader xeLoader, Sections sections){
		m_loadStartTime = System.currentTimeMillis();
		
		notify(src, Notifier.HOLD_MESSAGES, "");
		
		CoreCollection<CallGraph> callGraphs = new CoreCollection<CallGraph>();
		CoreCollection<Boolean> structuralGraphUpToDate = new CoreCollection<Boolean>();
		CoreCollection<SourceMapper> sourceMappers = new CoreCollection<SourceMapper>();
		CoreCollection<InstructionDecoder> instructionDecoders = new CoreCollection<InstructionDecoder>();
		
		for (int nodeChainIndex : xeLoader.getNodeChainIndexes()) {
			for (int coreNumber : xeLoader.getCoreNumbers(nodeChainIndex)) {
				CoreId coreId = new CoreId(xeLoader.getXnLoader(), nodeChainIndex, coreNumber);
				boolean success = buildNodeCore(callGraphs, instructionDecoders, sourceMappers, xeLoader, sections, src, coreId);
				if (success){
					structuralGraphUpToDate.add(coreId, true);
				} else {
					return false;
				}
			}
		}

		m_callGraphs = callGraphs;
		m_sourceMappers = sourceMappers;
		m_instructionDecoders = instructionDecoders;
		m_structuralGraphUpToDate = structuralGraphUpToDate;
		
		notify(src, Notifier.RELEASE_MESSAGES, "");
		return true;
	}
	
	private boolean buildNodeCore(CoreCollection<CallGraph> callGraphs,
			CoreCollection<InstructionDecoder> instructionDecoders,
			CoreCollection<SourceMapper> sourceMappers, 
			XeLoader xeLoader, Sections sections, CommandSource src, 
			CoreId coreId) {
		
		setAnalysisName(String.format("Processing %s", coreId.toString()));
		InstructionMnemonics mnenonics = new InstructionMnemonics(m_xeLoader.getArchitecture());
		
		//Binary Stage
		InstructionDecoder instructionDecoder = new InstructionDecoder(coreId, xeLoader);
		try {
			instructionDecoder.decode();
		} catch (Exception e) {
			notify(src, Notifier.NOTIFY_ERROR, e.getMessage());
			return false;
		}
		instructionDecoders.add(coreId, instructionDecoder);
		
		//Control Flow Stage
		FunctionConstructor fc = new FunctionConstructor(instructionDecoder, this, sections, coreId, src, 
				mnenonics, xeLoader.getSymbolTable(coreId.getNodeChainIndex(), coreId.getCoreNumber()));

		try {
			fc.process();
		} catch (Exception e) {
			notify(src, Notifier.NOTIFY_ERROR, e.getMessage());
			return false;
		}

		Set<InstructionNode> functionStartNodes = fc.getFunctionStarts();
		
		Set<InstructionNode> functionEndNodes = fc.getFunctionEnds();
		
		//add all the successors
		SuccessorAdder sa = new SuccessorAdder(coreId, instructionDecoder, sections, functionEndNodes, this, src, mnenonics);
		sa.process();

		UnresolvedSetter us = new UnresolvedSetter(functionStartNodes);
		us.process();
		
		//add function reference nodes
		FunctionReferenceNodeAdder frna = new FunctionReferenceNodeAdder(instructionDecoder, m_notifier, src, xeLoader, coreId, sections);
		frna.process(functionStartNodes);

		Set<FunctionContainer> functionContainers = frna.getFunctionContainers();
		
		FnopAdder fa = new FnopAdder(functionContainers);
		fa.process();
		
		ensureAllFunctionsAreNamed(xeLoader.getSymbolTable(coreId.getNodeChainIndex(), coreId.getCoreNumber()),
				functionContainers, sections.getLabels(coreId));

		setBranchTargets(instructionDecoder, functionContainers);

		linkFunctions(functionContainers);

		CallGraph cg = new CallGraph(functionContainers, coreId);
		callGraphs.add(coreId, cg);
		SourceMapper sourceMapper;
		try {
			sourceMapper = new SourceMapper(src, this, xeLoader, coreId, frna.getFunctionInstuctionToOriginal(),
					instructionDecoder, functionContainers, sections.getLabels(coreId));

		} catch (AbortedException e) {
			return false;
		}

		for (FileLocation fileLocation : m_userSpecifedFileLocations) {
			if ((fileLocation.m_nodeChainIndex == coreId.getNodeChainIndex()) &&
				(fileLocation.m_coreNumber == coreId.getCoreNumber())) {
				updateSourceMapper(src, m_notifier, sourceMapper,
						fileLocation.m_fileName, fileLocation.m_file, coreId);
			}
		}
		sourceMappers.add(coreId, sourceMapper);
		
		return true;
	}
	
	private void linkFunctions(Set<FunctionContainer> functionContainers) {
		Map<Long, FunctionContainer> pcToFunctionStartNode = new NonIteratableMap<Long, FunctionContainer>();
		for(FunctionContainer fc : functionContainers){
			pcToFunctionStartNode.put(fc.getStartInstruction().getPC(), fc);
		}
		for(FunctionContainer fc : functionContainers){
			for (FunctionReferenceNode g : fc.getFunctionReferenceNodes()){
				if(g.isKnownFunction()){
					FunctionContainer link = pcToFunctionStartNode.get(g.getFunctionStartPc());
					g.setFunction(link);
				}
			}
		}
	}

	private void setBranchTargets(InstructionDecoder id, Set<FunctionContainer> functionContainers) {
		for (InstructionNode inst : id.getInstructions()) {
			if (inst.getXtaInstruction().getBranchPcRelative()) {
				long branchTarget = inst.getXtaInstruction().getBranchPcRelativeTargetPc();
				inst.getXtaInstruction().setBranchPcRelativeTargetFunction("0x" + Long.toHexString(branchTarget));
				for(FunctionContainer fc : functionContainers){
					if(branchTarget == fc.getStartInstruction().getPC()){
						inst.getXtaInstruction().setBranchPcRelativeTargetFunction(fc.getFunctionName());
						break;
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

	private void ensureAllFunctionsAreNamed( SymbolTable symbolTable, 
			Set<FunctionContainer> functionContainers, Map<String, Set<Long>> labelToPc) {

		Map<Long, String> pcToLabel = new NonIteratableMap<Long, String>();
		for (String label : labelToPc.keySet()) {
			for (Long pc : labelToPc.get(label)) {
				pcToLabel.put(pc, label);
			}
		}
		Set<String> seen = new NonIteratableSet<String>();
		for(FunctionContainer fc : functionContainers){
			InstructionNode inst = fc.getStartInstruction();
			String functionName = symbolTable.functionName(inst.getStartPc());
			if (seen.contains(functionName)) {
				String label = pcToLabel.get(inst.getXtaInstruction().getPc());
				if(label != null){
					functionName = label;
				}
			}			
			fc.setFunctionName(functionName);
			seen.add(functionName);
		}
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
    		String fileName, File file, CoreId coreId) {

        DebugEngine debugEngine = m_xeLoader.getDebugInfo(coreId.getNodeChainIndex(), coreId.getCoreNumber()).getDebugEngine();
        SourceFile sf = sourceMapper.getSourceFile(fileName);
        sourceMapper.setFile(src, notifier, debugEngine, sf, file);
    }

	private void updateDefines(CommandSource src, String refString, DefinesUpdate update, ReferenceClass refClass) {		
		boolean hit = false;
		for (CoreId activeCore : m_activeCores) {
			
			Reference reference = ReferenceFactory.createReference(refString, refClass, activeCore);
			
			if (resolveReference(reference)) {
				update.run(reference);
				hit = true;
			}
		}
		if (hit) {	
		    notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
		} else {
		    notify(src, Notifier.NOTIFY_ERROR, "References not resolved on any active core");
		}
	}

	private static interface DefinesUpdate {
		void run(Reference reference);
	}

	private void runSrcLevelCommands(CommandSource src, CommandRunner commandRunner) {
		if (commandRunner != null) {
			int commandNum = 0;
			
			// All the source level commands are present on each core, 
			// so make sure we only run them once..
			assert(!m_activeCores.isEmpty());
			CoreId activeCore = m_activeCores.iterator().next();
			
			Collection<SrcStringInfo> srcStringInfos = getSrcLevelCommands(activeCore);
			startAnalysis("Running source level commands", srcStringInfos.size());
			
		    for (SrcStringInfo cmdInfo : srcStringInfos) {
	            CommandSource commandSource = new CommandSource(CommandSource.Type.SRC_LEVEL_COMMAND,
	            		cmdInfo.getFileName(), cmdInfo.getLineNumber());

	            String command = cmdInfo.getString();
		        setAnalysisName("Running command: '" + command + "'");
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
		m_nodeHz = new IteratableMap<String, Integer>();
		m_coreThreads = new CoreCollection<Integer>();

		XnLoader xnLoader = xeLoader.getXnLoader();
		ProgInfoLoader progInfoLoader = xeLoader.getProgInfoLoader();
		if ((xnLoader == null) || (progInfoLoader == null)) {
			return;
		}
		
		for (int nodeChainIndex : xeLoader.getNodeChainIndexes()) {
			for (int coreNumber : xeLoader.getCoreNumbers(nodeChainIndex)) {				
				int coreThreads = getCoreThreads(xnLoader, progInfoLoader, nodeChainIndex, coreNumber);
				CoreId coreId = new CoreId(xeLoader.getXnLoader(), nodeChainIndex, coreNumber);
				m_coreThreads.add(coreId, coreThreads);
			}
		}

		// Initialise the frequencies with the default for each node..
		for (int nodeChainIndex : xeLoader.getNodeChainIndexes()) {
		    String nodeId = xnLoader.getNodeId(nodeChainIndex);			
		    m_nodeHz.put(nodeId, (int)XmlHandler.DEFAULT_FREQUENCY_HZ);
		}

		// Sets the frequencies with the ones actually used (from the proginfo sector)..
		for (Integer nodeRoutingId : progInfoLoader.getNodeFrequencies().keySet()) {			
			int nodeChainIndex = xnLoader.getNodeChainIndex(nodeRoutingId);			
			String nodeId = xnLoader.getNodeId(nodeChainIndex);			
			m_nodeHz.put(nodeId, progInfoLoader.getNodeFrequencies().get(nodeRoutingId));
		}		
	}

	private int getCoreThreads(XnLoader xnLoader, ProgInfoLoader progInfoLoader, int nodeChainIndex, int coreNumber) {
		int nodeRoutingId = xnLoader.getNodeRoutingId(nodeChainIndex);
		for (ProgInfoEntry entry : progInfoLoader.getProgInfos())  {
			if ((entry.getNodeRoutingId() == nodeRoutingId) &&
				(entry.getCoreNumber() == coreNumber) &&
				entry.getType().getCategory().equals(ProgInfoLoader.PI_CATEGORY_THREADS) &&
				entry.getType().getLabel().equals(ProgInfoLoader.PI_LABEL_USED)) {

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

	private boolean checkFrequency(CommandSource src, String nodeId, double frequency) {
		if (!m_nodeHz.containsKey(nodeId)) {
			notify(src, Notifier.NOTIFY_ERROR, String.format("Invalid node id: %s", nodeId));
			return false;
		}
		if (frequency <= 0) {
			notify(src, Notifier.NOTIFY_ERROR, String.format("Frequency '%.1f' invalid", frequency));
			return false;
		}
		return true;
	}
	
	private int getNumLastRoutesAnalyzed() {
		if (m_routeContainers.isEmpty()) { 
			return 0;
		}
		return m_routeContainers.get(m_routeContainers.size() -1).getNumExistingRoutes();
	}
	
	private void initialiseActiveCores(XeLoader xeLoader) {
		m_activeCores.clear();
		for (int nodeChainIndex : xeLoader.getNodeChainIndexes()) {
			for (int coreNumber : xeLoader.getCoreNumbers(nodeChainIndex)) {
				m_activeCores.add(new CoreId(xeLoader.getXnLoader(), nodeChainIndex, coreNumber));
			}
		}
	}

	private boolean validateRouteCount(){
		return m_routes.size() >= getNumLastRoutesAnalyzed();
	}
	
	private List<StackNode> getPathStackNodes(CallGraph callGraph, Reference reference) 
	    throws AbortedException, MemoryMonitorException {
		
		PreanalysisReferenceMatcher prm = new PreanalysisReferenceMatcher();
		return prm.matchReference(callGraph, reference, this);
	}
	
	private void checkInstructionIsBranch(CommandSource src, Reference ref, CoreId activeCore) {
		for (long pc : ref.getPcs()) {
			InstructionNode node = m_instructionDecoders.get(activeCore).getNode(pc);
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
}

class FileLocation {
	int m_nodeChainIndex;
	int m_coreNumber;
	String m_fileName;
	File m_file;

	FileLocation(int nodeChainIndex, int coreNumber, String fileName, File file) {
		m_nodeChainIndex = nodeChainIndex;
		m_coreNumber = coreNumber;
		m_fileName = fileName;
		m_file = file;
	}
}
