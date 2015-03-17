package com.xmos.xta.core.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.xmos.libs.core.controlflow.Branch;
import com.xmos.libs.core.controlflow.ControlFlow;
import com.xmos.libs.core.controlflow.ControlFlows;
import com.xmos.libs.core.controlflow.FunctionControlFlow;
import com.xmos.libs.core.controlflow.Label;
import com.xmos.libs.core.controlflow.functionmapper.FunctionMapper;
import com.xmos.libs.core.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.libs.core.instructiondecoder.InstructionDecoder;
import com.xmos.libs.core.instructiondecoder.InstructionDecoders;
import com.xmos.libs.core.memorymonitor.MemoryMonitor;
import com.xmos.libs.core.memorymonitor.MemoryMonitorException;
import com.xmos.libs.core.proginfoloader.ProgInfoEntry;
import com.xmos.libs.core.proginfoloader.ProgInfoLoader;
import com.xmos.libs.core.utils.CoreCollection;
import com.xmos.libs.core.utils.CoreId;
import com.xmos.libs.core.utils.IteratableMap;
import com.xmos.libs.core.utils.IteratableSet;
import com.xmos.libs.core.utils.NonIteratableMap;
import com.xmos.libs.core.utils.NonIteratableSet;
import com.xmos.libs.core.utils.Pair;
import com.xmos.libs.core.utils.ProgressMonitor;
import com.xmos.libs.core.utils.WarningNotifier;
import com.xmos.libs.core.xeloader.MemoryImage;
import com.xmos.libs.core.xeloader.SymbolTable;
import com.xmos.libs.core.xeloader.XeLoader;
import com.xmos.libs.core.xnloader.XmlHandler;
import com.xmos.libs.core.xnloader.XnLoader;
import com.xmos.xta.core.engine.Config.ConfigStartMode;
import com.xmos.xta.core.engine.analysis.Analyzer;
import com.xmos.xta.core.engine.analysis.Analyzer.Status;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteConstructor;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer;
import com.xmos.xta.core.engine.analysis.routes.RouteDefines;
import com.xmos.xta.core.engine.analysis.routes.RouteLoopConversion;
import com.xmos.xta.core.engine.analysis.routes.BackTrails;
import com.xmos.xta.core.engine.analysis.structural.CachedFunctionMap;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.Source;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.debugger.Debugger;
import com.xmos.xta.core.engine.defines.Defines;
import com.xmos.xta.core.engine.defines.ExclusionInfo;
import com.xmos.xta.core.engine.defines.FunctionTimeInfo;
import com.xmos.xta.core.engine.defines.InstructionTimeInfo;
import com.xmos.xta.core.engine.defines.LoopInfo;
import com.xmos.xta.core.engine.defines.LoopPathInfo;
import com.xmos.xta.core.engine.defines.LoopScopeInfo;
import com.xmos.xta.core.engine.defines.PathTimeInfo;
import com.xmos.xta.core.engine.points.LoopPoints;
import com.xmos.xta.core.engine.points.PathPoints;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceType.Result;
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
import com.xmos.xta.core.engine.sourcemapper.SourceMapper;
import com.xmos.xta.core.engine.utils.CoreConfig;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class Engine extends Notifier implements Aborter {

    private static final int WORK_UNITS_PER_ANALYSIS = 100;

    public static final String ROUTE_ID_LAST_STR = "-";
    public static final String ROUTE_ID_ALL_STR = "*";

    public static final int ROUTE_ID_LAST     = -1;
    public static final int ROUTE_ID_INVALID  = -2;
    public static final int ROUTE_ID_ALL      = -3;
    public static final int NODE_ID_TOP_LEVEL = -4;

    private XeLoader m_xeLoader;
    private SourceMapper m_sourceMapper;
    private InstructionDecoders m_instructionDecoders;
    private ControlFlows m_controlFlows;
    private List<Route> m_routes;
    private List<RouteContainer> m_routeContainers;
    private Status m_lastAnalysisStatus;
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
    private CoreCollection<CachedFunctionMap> m_cachedFunctions;
    private Scripter m_scripter;
    private Collection<CoreId> m_activeCores;
    private PathPoints m_pathPoints;
    private LoopPoints m_loopPoints;
    private Debugger m_debugger;
    private MemoryMonitor m_memoryMonitor;
    private String m_workspacePath;

    public Engine(Config config) {
        super(config);
        m_nodeHz = new IteratableMap<String, Integer>();
        m_coreThreads = new CoreCollection<Integer>();
        m_cachedFunctions = new CoreCollection<CachedFunctionMap>();
        m_xeLoader = new XeLoader();
        m_sourceMapper = null;
        m_instructionDecoders = new InstructionDecoders();
        m_controlFlows = new ControlFlows();
        m_routes = new LinkedList<Route>();
        m_routeContainers = new LinkedList<RouteContainer>();
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
        m_pathPoints = new PathPoints(this);
        m_loopPoints = new LoopPoints(this);
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

    public SourceMapper getSourceMapper() {
        return m_sourceMapper;
    }

    public Vector<MemoryImage> getMemoryImages() {
        return m_xeLoader.getMemoryImages();
    }

    public MemoryImage getMemoryImage(CoreId coreId) {
        return m_xeLoader.getMemoryImage(coreId);
    }

    public SymbolTable getSymbolTable(CoreId coreId) {
        return m_xeLoader.getSymbolTable(coreId);
    }

    public ControlFlow getControlFlow(CoreId coreId) {
        return m_controlFlows.getControlFlow(coreId);
    }

    public FunctionMapper getFunctionMapper(CoreId coreId) {
        return m_controlFlows.getControlFlow(coreId).getFunctionMapper();
    }

    public InstructionDecoder getInstructionDecoder(CoreId coreId) {
        return m_instructionDecoders.getInstructionDecoder(coreId);
    }

    public CachedFunctionMap getCachedFunctionMap(CoreId coreId) {
        return m_cachedFunctions.get(coreId);
    }

    public PathPoints getPathPoints() {
        return m_pathPoints;
    }

    public LoopPoints getLoopPoints() {
        return m_loopPoints;
    }

    public Debugger getDebugger() {
        return m_debugger;
    }

    public MemoryMonitor getMemoryMonitor() {
        return m_memoryMonitor;
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

    public void setLastAnalysisStatus(Status status) {
        m_lastAnalysisStatus = status;
    }

    public Status getLastAnalysisStatus() {
        return m_lastAnalysisStatus;
    }

    public boolean loadExecutable(CommandSource src, File executableFile, CommandRunner commandRunner) {
        try {
            m_loadStartTime = System.currentTimeMillis();

            notify(src, Notifier.CLEAR_LOG, "");
            try {
                executableFile = executableFile.getCanonicalFile();
            } catch (IOException e) {
                notify(src, Notifier.NOTIFY_ERROR, String.format("failed to resolve executable path: %s", executableFile.getName()));
                return false;
            }

            setAnalysisName("Loading Executable");
            XeLoader xeLoader = new XeLoader();
            if (!xeLoader.loadExecutable(executableFile)) {
                notify(src, Notifier.NOTIFY_ERROR, "the file '" + executableFile + "' could not be loaded: " +
                        xeLoader.getErrorMessage() + ".");
                return false;
            }

            setAnalysisName("Loading Sections");
            Sections sections = new Sections();
            if (!sections.load(src, xeLoader, this)) {
                return false;
            }

            setAnalysisName("Decoding Instructions");
            try {
                m_instructionDecoders.initialise(xeLoader);

            } catch (Exception e) {
                notify(src, Notifier.NOTIFY_ERROR, e.getMessage());
                return false;
            }

            if (!isReloaded(executableFile)) {
                SourceMapper.clearUserSpecifedFileLocations();
            }

            setAnalysisName("Analysing Control Flow");
            initialiseControlFlows(xeLoader, sections, src);

            setAnalysisName("Determining Source To Assembly Mapping");
            try {
                   m_sourceMapper = new SourceMapper(src, this, xeLoader, sections, m_instructionDecoders);
            } catch (AbortedException e) {
                return false;
            }

            initialiseCoreStates(xeLoader);
            initialiseActiveCores(xeLoader);
            initialiseCachedFunctions(xeLoader);

            m_executableFile = executableFile;
            m_loadedFileLastModifiedTime = executableFile.lastModified();
            m_xeLoader = xeLoader;
            m_sections = sections;
            m_routes.clear();
            m_routeContainers.clear();
            m_selectedRoute = null;
            m_defines.clear();
            m_pathPoints.clear();
            m_loopPoints.clear();
            Route.resetRouteCount();

            runSrcLevelCommands(src, commandRunner);

            m_scripter.setEngineLoadAndRunDone();
            notify(src, Notifier.NOTIFY_LOAD_EXECUTABLE, "");

        } catch (final java.lang.OutOfMemoryError e) {
            notify(src, Notifier.NOTIFY_ERROR, "out of memory (heap)");
            return false;

         } catch (final java.lang.StackOverflowError e) {
            notify(src, Notifier.NOTIFY_ERROR, "out of memory (stack)");
            return false;
         }
        return true;
    }

    public void analyzeFunction(CommandSource src, String functionName) {
        startAnalysis("Analyzing Function: " + functionName, WORK_UNITS_PER_ANALYSIS);
        Analyzer analyzer = new Analyzer(this, m_routes, m_routeContainers);
        m_lastAnalysisStatus = analyzer.analyzeFunction(src, functionName);
    }

    public void analyzeEndpoints(CommandSource src) {
        startAnalysis("Analyzing Endpoints", WORK_UNITS_PER_ANALYSIS);
        Analyzer analyzer = new Analyzer(this, m_routes, m_routeContainers);
        m_lastAnalysisStatus = analyzer.analyzeEndpoints(src, m_pathPoints);
    }

    public void analyzeLoop(CommandSource src) {
        startAnalysis("Analyzing Loop", WORK_UNITS_PER_ANALYSIS);
        Analyzer analyzer = new Analyzer(this, m_routes, m_routeContainers);
        m_lastAnalysisStatus = analyzer.analyzeLoop(src, m_loopPoints);
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

    public void setStartMode(ConfigStartMode mode) {
        m_config.setStartMode(mode);
    }

    public ConfigStartMode getStartMode() {
        return m_config.getStartMode();
    }

    public boolean canInstructionPause(int routeId, String refString) {
        try {
            for (Route route : getRoute(routeId)) {
                Reference reference = ReferenceFactory.createReference(refString, ReferenceClass.ENDPOINT,
                        route.parentRouteCoreContainer().getCoreId());

                if (resolveReference(reference) == Result.RESOLVED) {
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

        if (routes.size() == 0) {
            notify(src, Notifier.NOTIFY_ERROR, String.format("Failed to find route: %d", routeId));
            return false;
        }

        assert(validateRouteCount());

        for (Route route : routes) {
            route.parentRouteCoreContainer().remove(route);
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

    public void provideFeedback(String feedback) throws AbortedException {
        if (m_progressMonitor != null) {
            m_progressMonitor.subTask(feedback);
            if (m_progressMonitor.isCanceled()) {
                throw new AbortedException();
            }
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
        Set<String> functionNames = new IteratableSet<String>();
        for(FunctionControlFlow fc : m_controlFlows.getControlFlow(coreId)){
            functionNames.add(fc.getFunctionName());
        }
        return functionNames;
    }

    public Collection<String> getNonSystemFunctionNames() {
        List<String> functionNames = new LinkedList<String>();

        for (CoreId coreId : getActiveCores()) {
            if (!m_xeLoader.isLoaded(coreId))
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
        for (FunctionControlFlow fc : m_controlFlows.getControlFlow(coreId)){
            if (functionName.equals(fc.getFunctionName())){
                pcs.add(fc.getStartInstructionNode().getPc());
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
            for (CoreId coreId : m_xeLoader.getCoreIds()) {
                m_activeCores.add(new CoreId(coreId));
            }
        }
        m_pathPoints.refresh();
        m_loopPoints.refresh();
        notify(src, Notifier.NOTIFY_UPDATE_ACTIVE_CORES, "");
    }

    public void addActiveCore(CommandSource src, CoreId activeCore) {
        if (m_xeLoader.isLoaded(activeCore)) {
            if (!m_activeCores.contains(activeCore)) {
                m_activeCores.add(activeCore);
            }
            m_pathPoints.refresh();
            m_loopPoints.refresh();
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
        m_loopPoints.refresh();
        notify(src, Notifier.NOTIFY_UPDATE_ACTIVE_CORES, "");
    }

    public void removeActiveCore(CommandSource src, CoreId activeCore) {
        m_activeCores.remove(activeCore);
        m_pathPoints.refresh();
        m_loopPoints.refresh();
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
        if (routes.isEmpty()) {
            notify(src, Notifier.NOTIFY_ERROR, String.format("Failed to find route: %d", routeId));
            return false;
        }

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

            Result result = resolveReference(functionRef);
            switch (result) {
            case RESOLVED:
                m_defines.addFunctionTime(functionRef, timeUnit);
                hit = true;
                break;
            case UNRESOLVED:
            case REMOVED:
                break;
            }
        }
        if (hit) {
            notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
        } else {
            notify(src, Notifier.NOTIFY_ERROR, String.format("Reference: %s not resolved on any active tile", functionName));
        }
    }

    public void addPathTime(CommandSource src, String from, String to, TimeUnit timeUnit) {
        boolean hit = false;
        boolean removed = false;
        String refString = "";

        for (CoreId activeCore : m_activeCores) {

            Reference fromRef = ReferenceFactory.createReference(from,
                    ReferenceClass.ENDPOINT, activeCore);
            Reference toRef = ReferenceFactory.createReference(to,
                    ReferenceClass.ENDPOINT, activeCore);

            Result fromResult = resolveReference(fromRef);
            Result toResult   = resolveReference(toRef);

            if ((fromResult == Result.RESOLVED) && (toResult == Result.RESOLVED)) {
                m_defines.addPathTime(fromRef, toRef, timeUnit);
                hit = true;

            } else if (fromResult == Result.REMOVED) {
                refString = from;
                removed = true;
                break;

            } else if (toResult == Result.REMOVED) {
                refString = to;
                removed = true;
                break;
            }
        }
        if (hit) {
            notify(src, Notifier.NOTIFY_UPDATE_STATE, "");

        } else if (removed) {
            if (m_config.getWarningEnabled(Config.WARNING_REMOVED)) {
                notify(src, Notifier.NOTIFY_WARNING, String.format("Reference: %s has been removed", refString));
            }

        } else {
            notify(src, Notifier.NOTIFY_ERROR, "References not resolved on any active tile");
        }
    }

    public void addInstructionTime(CommandSource src, String refString, final TimeUnit timeUnit) {
        updateDefines(src, refString, new DefinesUpdate() {
            public void run(Reference reference) {
                m_defines.addInstructionTime(reference, timeUnit);
            }
        }, ReferenceClass.ENDPOINT, false);
    }

    public void addLoop(CommandSource src, String refString, final long iterations) {
        updateDefines(src, refString, new DefinesUpdate() {
            public void run(Reference reference) {
                m_defines.addLoop(reference, iterations);
            }
        }, ReferenceClass.LOOP, true);
    }

    public void addLoopPath(CommandSource src, String refString, final long iterations) {
        updateDefines(src, refString, new DefinesUpdate() {
            public void run(Reference reference) {
                m_defines.addLoopPath(reference, iterations);
            }
        }, ReferenceClass.ANY, true);
    }

    public void addLoopScope(CommandSource src, String refString, final boolean absolute) {
        updateDefines(src, refString, new DefinesUpdate() {
            public void run(Reference reference) {
                m_defines.addLoopScope(reference, absolute);
            }
        }, ReferenceClass.LOOP, true);
    }

    public void addExclusion(CommandSource src, String refString) {
        updateDefines(src, refString, new DefinesUpdate() {
            public void run(Reference reference) {
                m_defines.addExclusion(reference);
            }
        }, ReferenceClass.ANY, false);
    }

    public void removeFunctionTime(CommandSource src, String functionName) {
        boolean hit = false;
        for (CoreId activeCore : m_activeCores) {

            Reference functionRef = ReferenceFactory.createReference(functionName,
                    ReferenceClass.FUNCTION_WITH_EVERYTHING, activeCore);

            Result result = resolveReference(functionRef);
            switch (result) {
            case RESOLVED:
                m_defines.removeFunctionTime(functionRef);
                hit = true;
                break;
            case UNRESOLVED:
            case REMOVED:
                break;
            }
        }
        if (hit) {
            notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
        } else {
            notify(src, Notifier.NOTIFY_ERROR, String.format("Reference: %s not resolved on any active tile", functionName));
        }
    }

    public void removePathTime(CommandSource src, String from, String to) {
        boolean hit = false;
        boolean removed = false;
        String refString = "";

        for (CoreId activeCore : m_activeCores) {

            Reference fromRef = ReferenceFactory.createReference(from,
                    ReferenceClass.ENDPOINT_WITH_EVERYTHING, activeCore);
            Reference toRef = ReferenceFactory.createReference(to,
                    ReferenceClass.ENDPOINT_WITH_EVERYTHING, activeCore);

            Result fromResult = resolveReference(fromRef);
            Result toResult   = resolveReference(toRef);

            if ((fromResult == Result.RESOLVED) &&
                (toResult == Result.RESOLVED)) {
                m_defines.removePathTime(fromRef, toRef);
                hit = true;

            } else if (fromResult == Result.REMOVED) {
                refString = from;
                removed = true;
                break;

            } else if (toResult == Result.REMOVED) {
                refString = to;
                removed = true;
                break;
            }
        }
        if (hit) {
            notify(src, Notifier.NOTIFY_UPDATE_STATE, "");

        } else if (removed) {
            if (m_config.getWarningEnabled(Config.WARNING_REMOVED)) {
                notify(src, Notifier.NOTIFY_WARNING, String.format("Reference: %s has been removed", refString));
            }

        } else {
            notify(src, Notifier.NOTIFY_ERROR, "References not resolved on any active tile");
        }
    }

    public void removeInstructionTime(CommandSource src, String refString) {
        updateDefines(src, refString, new DefinesUpdate() {
            public void run(Reference reference) {
                m_defines.removeInstructionTime(reference);
            }
        }, ReferenceClass.ENDPOINT_WITH_EVERYTHING, false);
    }

    public void removeLoop(CommandSource src, String refString) {
        updateDefines(src, refString, new DefinesUpdate() {
            public void run(Reference reference) {
                m_defines.removeLoop(reference);
            }
        }, ReferenceClass.LOOP_WITH_EVERYTHING, true);
    }

    public void removeLoopPath(CommandSource src, String refString) {
        updateDefines(src, refString, new DefinesUpdate() {
            public void run(Reference reference) {
                m_defines.removeLoopPath(reference);
            }
        }, ReferenceClass.ANY_WITH_EVERYTHING, true);
    }

    public void removeLoopScope(CommandSource src, String refString) {
        updateDefines(src, refString, new DefinesUpdate() {
            public void run(Reference reference) {
                m_defines.removeLoopScope(reference);
            }
        }, ReferenceClass.LOOP_WITH_EVERYTHING, true);
    }

    public void removeExclusion(CommandSource src, String refString) {
        updateDefines(src, refString, new DefinesUpdate() {
            public void run(Reference reference) {
                m_defines.removeExclusion(reference);
            }
        }, ReferenceClass.ANY_WITH_EVERYTHING, false);
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

    public Collection<ExclusionInfo> getExclusions() {
        return m_defines.getExclusions();
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
                        ReferenceClass.FUNCTION, route.parentRouteCoreContainer().getCoreId());

                Result result = resolveReference(functionRef);
                switch (result) {
                case RESOLVED:
                    FunctionTimeInfo info = new FunctionTimeInfo(functionRef, timeUnit);
                    defines.applyFunctionDefine(src, route, info, Source.SET);
                    hit = true;
                    break;
                case UNRESOLVED:
                case REMOVED:
                    break;
                }
            }
        } catch (AbortedException e) {    }

        if (hit) {
            notify(src, Notifier.NOTIFY_UPDATE_STATE, "");
        } else {
            notify(src, Notifier.NOTIFY_ERROR, String.format("Reference: %s not resolved on any active tile", functionName));
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
        boolean removed = false;
        String refString = "";
        try {
            for (Route route : getRoute(routeId)) {
                CoreId coreId = route.parentRouteCoreContainer().getCoreId();

                Reference fromRef = ReferenceFactory.createReference(from,
                        ReferenceClass.ENDPOINT, coreId);
                Reference toRef = ReferenceFactory.createReference(to,
                        ReferenceClass.ENDPOINT, coreId);

                Result fromResult = resolveReference(fromRef);
                Result toResult   = resolveReference(toRef);

                if ((fromResult == Result.RESOLVED) &&
                    (toResult == Result.RESOLVED)) {
                    PathTimeInfo info = new PathTimeInfo(fromRef, toRef, timeUnit);
                    defines.applyPathDefine(this, src, route, info, Source.SET);
                    hit = true;

                } else if (fromResult == Result.REMOVED) {
                    refString = from;
                    removed = true;
                    break;

                } else if (toResult == Result.REMOVED) {
                    refString = to;
                    removed = true;
                    break;
                }
            }
        } catch (AbortedException e) { }

        if (hit) {
            notify(src, Notifier.NOTIFY_ROUTE_CHANGED, String.format("%d", routeId));

        } else if (removed) {
            if (m_config.getWarningEnabled(Config.WARNING_REMOVED)) {
                notify(src, Notifier.NOTIFY_WARNING, String.format("Reference: %s has been removed", refString));
            }

        } else {
            notify(src, Notifier.NOTIFY_ERROR, "References not resolved on any active tile");
        }
    }

    // ===================================================================== //
    // Set Instruction Time Methods
    // ===================================================================== //
    public boolean setInstructionTime(CommandSource src, int routeId, String refString, TimeUnit timeUnit) {
        Collection<Route> routes = getRoute(routeId);
        boolean hit = false;
        boolean removed = false;
        try {
            Map<Route, Reference> refMap = new NonIteratableMap<Route, Reference>();
            for (Route route : routes) {
                Reference reference = ReferenceFactory.createReference(refString,
                        ReferenceClass.ENDPOINT,
                        route.parentRouteCoreContainer().getCoreId());

                Result result = resolveReference(reference);
                switch (result) {
                case RESOLVED:
                    if (!route.getRouteUnknowns().canInstructionPause(reference)) {
                        notify(src, Notifier.NOTIFY_ERROR, String.format(
                                "failed to set instruction time on reference: %s " +
                                "(instruction referenced is non-pausing)", refString));

                        return false;
                    }

                    refMap.put(route, reference);
                    hit = true;
                    break;

                case REMOVED:
                    removed = true;
                    break;

                case UNRESOLVED:
                    break;
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

        } else if (removed) {
            if (m_config.getWarningEnabled(Config.WARNING_REMOVED)) {
                notify(src, Notifier.NOTIFY_WARNING, String.format("Reference: %s has been removed", refString));
            }

        } else {
            notify(src, Notifier.NOTIFY_ERROR, String.format("Reference: %s not resolved on any active tile", refString));
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
    public void setLoop(CommandSource src, int routeId, String refString, final long iterations) {
        updateLoops(src, routeId, refString, ReferenceClass.LOOP, new LoopsUpdate() {
            public void check(Route route, Reference reference, CommandSource src) throws Exception {
                route.getRouteUnknowns().checkSetLoopIterations(reference, iterations, Engine.this, src);
            }
            public void run(Route route, Reference reference, CommandSource src) throws AbortedException {
                route.getRouteUnknowns().setLoopIterations(src, Engine.this, reference, iterations, false, Source.SET);
            }
        });
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
    public void setLoopPath(CommandSource src, int routeId, String refString, final long iterations) {
        updateLoops(src, routeId, refString, ReferenceClass.ANY, new LoopsUpdate() {
            public void check(Route route, Reference reference, CommandSource src) throws Exception {
                route.getRouteUnknowns().checkSetLoopPathIterations(reference, iterations, Engine.this, src);
            }
            public void run(Route route, Reference reference, CommandSource src) throws AbortedException {
                route.getRouteUnknowns().setLoopPathIterations(reference, iterations, Source.SET);
            }
        });
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
    public void setLoopScope(CommandSource src, int routeId, String refString, final boolean absolute) {
        updateLoops(src, routeId, refString, ReferenceClass.LOOP, new LoopsUpdate() {
            public void check(Route route, Reference reference, CommandSource src) throws Exception {
                route.getRouteUnknowns().checkSetLoopScope(reference, absolute, Engine.this, src);
            }
            public void run(Route route, Reference reference, CommandSource src) throws AbortedException {
                route.getRouteUnknowns().setLoopScope(reference, absolute, Source.SET);
            }
        });
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
        boolean hit = false;
        boolean removed = false;
        try {
            for (Route route : getRoute(routeId)) {
                Reference reference = ReferenceFactory.createReference(refString,
                        ReferenceClass.ANY,
                        route.parentRouteCoreContainer().getCoreId());

                Result result = resolveReference(reference);
                switch (result) {
                case RESOLVED:
                    Set<Node> nodes = route.getReferencedNodes(reference);

                    boolean okToSetExclusion = true;
                    for (Node node : nodes) {
                        okToSetExclusion &= canSetExclusion(node);
                    }
                    if (okToSetExclusion && !nodes.isEmpty()) {
                        hit = route.getRouteUnknowns().setExclusion(src, this, reference, Type.EXCLUSION, Source.SET);
                    }
                    break;

                case REMOVED:
                    removed = true;
                    break;

                case UNRESOLVED:
                    break;
                }
            }

        } catch (AbortedException e) {
            return false;
        }

        if (hit) {
            notify(src, Notifier.NOTIFY_ROUTE_CHANGED, String.format("%d", routeId));
            notify(src, Notifier.NOTIFY_UPDATE_UNKNOWNS, "");

        } else if (removed) {
            if (m_config.getWarningEnabled(Config.WARNING_REMOVED)) {
                notify(src, Notifier.NOTIFY_WARNING, String.format("Reference: %s has been removed", refString));
            }

        } else {
            notify(src, Notifier.NOTIFY_ERROR, String.format("Unable to set exclusion for reference: %s", refString));
        }
        return hit;
    }

    public void setExclusion(CommandSource src, Node node) {
        if (canSetExclusion(node)) {
            m_selectedRoute.getRouteUnknowns().setExclusion(src, this, node, Type.EXCLUSION, Source.SET);
            notify(src, Notifier.NOTIFY_ROUTE_CHANGED, String.format("%d", getSelectedRouteId()));
            notify(src, Notifier.NOTIFY_UPDATE_UNKNOWNS, "");

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
    // Section Methods
    // ===================================================================== //
    public boolean addBranch(CommandSource src, String fromRefString, Collection<String> toRefStrings) {
        boolean resolved = false;
        Collection<String> removedRefStrings = new ArrayList<String>();

        for (CoreId activeCore : m_activeCores) {

            Reference fromRef = ReferenceFactory.createReference(fromRefString,
                    ReferenceClass.ANY, activeCore);
            Result fromResolved = resolveReference(fromRef);
            switch (fromResolved) {
            case RESOLVED:
                checkInstructionIsBranch(src, fromRef, activeCore);
                break;

            case REMOVED:
                removedRefStrings.add(fromRefString);
                break;

            case UNRESOLVED:
                break;
            }

            Collection<Reference> toRefs = new ArrayList<Reference>();
            for (String toRefString : toRefStrings) {
                Reference ref = ReferenceFactory.createReference(toRefString,
                        ReferenceClass.ANY, activeCore);
                toRefs.add(ref);
            }
            boolean allToRefsResolved = true;
            for (Reference toRef : toRefs) {
                Result toResolved = resolveReference(toRef);
                switch (toResolved) {
                case UNRESOLVED:
                    allToRefsResolved = false;
                    break;

                case REMOVED:
                    removedRefStrings.add(toRef.getRef());
                    break;

                case RESOLVED:
                    break;
                }
            }

            try {
                List<BackTrails> backTrailsList = RouteConstructor.getPathBackTrails(this, activeCore, fromRef);
                if (!backTrailsList.isEmpty()) {
                    if ((fromResolved == Result.RESOLVED) && allToRefsResolved) {
                        if (m_sections.addToBranches(src, activeCore, fromRef, toRefs)) {
                            resolved = true;
                        }
                    }
                }
            } catch (AbortedException e) {
            } catch (MemoryMonitorException e) {
            }
        }

        if (!resolved) {
            notify(src, Notifier.NOTIFY_ERROR, "References not resolved on any active tile");

        } else if (!removedRefStrings.isEmpty()) {
            if (m_config.getWarningEnabled(Config.WARNING_REMOVED)) {
                for (String removedRefString : removedRefStrings) {
                    notify(src, Notifier.NOTIFY_WARNING, String.format("Reference: %s has been removed", removedRefString));
                }
            }

        } else {
            initialiseControlFlows(m_xeLoader, m_sections, src);
            initialiseCachedFunctions(m_xeLoader);
        }
        return resolved && removedRefStrings.isEmpty();
    }

    public boolean removeBranch(CommandSource src, String fromRefString, Collection<String> toRefStrings) {
        boolean resolved = false;
        Collection<String> removedRefStrings = new ArrayList<String>();

        for (CoreId activeCore : m_activeCores) {

            Reference fromRef = ReferenceFactory.createReference(fromRefString,
                    ReferenceClass.ANY_WITH_EVERYTHING, activeCore);
            Result fromResolved = resolveReference(fromRef);
            switch (fromResolved) {
            case RESOLVED:
                checkInstructionIsBranch(src, fromRef, activeCore);
                break;

            case REMOVED:
                removedRefStrings.add(fromRefString);
                break;

            case UNRESOLVED:
                break;
            }

            Collection<Reference> toRefs = new ArrayList<Reference>();
            for (String toRefString : toRefStrings) {
                Reference ref = ReferenceFactory.createReference(toRefString,
                        ReferenceClass.ANY_WITH_EVERYTHING, activeCore);
                toRefs.add(ref);
            }
            boolean allToRefsResolved = true;
            for (Reference toRef : toRefs) {
                Result toResolved = resolveReference(toRef);
                switch (toResolved) {
                case UNRESOLVED:
                    allToRefsResolved = false;
                    break;

                case REMOVED:
                    removedRefStrings.add(toRef.getRef());
                    break;

                case RESOLVED:
                    break;
                }
            }

            if ((fromResolved == Result.RESOLVED) && allToRefsResolved) {
                if (m_sections.removeFromBranches(src, activeCore, fromRef, toRefs)) {
                    resolved = true;
                }
            }
        }

        if (!resolved) {
            notify(src, Notifier.NOTIFY_ERROR, "References not resolved on any active tile");

        } else if (!removedRefStrings.isEmpty()) {
            if (m_config.getWarningEnabled(Config.WARNING_REMOVED)) {
                for (String removedRefString : removedRefStrings) {
                    notify(src, Notifier.NOTIFY_WARNING, String.format("Reference: %s has been removed", removedRefString));
                }
            }

        } else {
            initialiseControlFlows(m_xeLoader, m_sections, src);
            initialiseCachedFunctions(m_xeLoader);
        }
        return resolved && removedRefStrings.isEmpty();
    }

    public Map<Reference, List<Reference>> getUserBranches() {
        Map<Reference, List<Reference>> branches = new IteratableMap<Reference, List<Reference>>();

        for (CoreId coreId : m_xeLoader.getCoreIds()) {
            Map<Reference, Set<BranchInfo>> currentBranches = getBranches(coreId);

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

    public IteratableSet<SrcLabelTableInfo> getSrcLabelTable(CoreId coreId) {
        return m_sections.getSrcLabelTable(coreId);
    }

    public List<CodePointInfo> getLoopLabels(CoreId coreId) {
        return m_sections.getLoopLabels(coreId);
    }

    public IteratableSet<SrcLabelTableInfo> getLoopLabelTable(CoreId coreId) {
        return m_sections.getLoopLabelTable(coreId);
    }

    public List<CodePointInfo> getEndpoints(CoreId coreId) {
        return m_sections.getEndpoints(coreId);
    }

    public IteratableSet< CodePointTableInfo> getEndpointTable(CoreId coreId) {
        return m_sections.getEndpointTable(coreId);
    }

    public List<CodePointInfo> getCalls(CoreId coreId) {
        return m_sections.getCalls(coreId);
    }

    public IteratableSet< CodePointTableInfo> getCallTable(CoreId coreId) {
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

            // Handle backward compatibility when using stdcore[n] references.
            if (coreReference.matches("stdcore.*")) {
                coreReference = coreReference.replaceAll("stdcore", "tile");
                nodeCore = xnLoader.getNodeChainIndexCoreNumber(coreReference);
            }
            if (nodeCore == null) {
                notify(src, Notifier.NOTIFY_ERROR, String.format("core '%s' not found", coreReference));
                return null;
            }
        }
        int nodeChainIndex = nodeCore.first;
        int coreNumber = nodeCore.second;
        CoreId coreId = new CoreId(xnLoader, nodeChainIndex, coreNumber);

        if (!m_xeLoader.isLoaded(coreId)) {
            notify(src, Notifier.NOTIFY_ERROR, String.format("core '%s' not found", coreReference));
            return null;
        }
        return coreId;
    }

    public boolean isWithinLoop(String ref) {
        for (CoreId activeCore : m_activeCores) {
            Reference loopRef = ReferenceFactory.createReference(ref, ReferenceClass.LOOP, activeCore);
            if (resolveReference(loopRef) == Result.RESOLVED &&
                    RouteLoopConversion.isWithinLoop(this, activeCore, loopRef))
                return true;
        }
        return false;
    }

    public void setWorkspacePath(String workspacePath) {
        m_workspacePath = workspacePath;
    }

    public String getWorkspacePath() {
        return m_workspacePath;
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

    private Result resolveReference(Reference ref) {
        return ref.resolve(this);
    }

    private boolean isReloaded(File executableFile) {
        if ((m_executableFile != null) &&
            m_executableFile.getAbsolutePath().equals(executableFile.getAbsolutePath())) {
            return true;
        }
        return false;
    }

    private void updateDefines(CommandSource src, String refString, DefinesUpdate update,
            ReferenceClass refClass, boolean isLoop) {

        boolean hit = false;
        boolean removed = false;
        for (CoreId activeCore : m_activeCores) {
            Reference reference = ReferenceFactory.createReference(refString, refClass, activeCore);

            Result result = resolveReference(reference);
            switch (result) {
            case RESOLVED:
                update.run(reference);
                hit = true;
                break;

            case REMOVED:
                removed = true;
                break;

            case UNRESOLVED:
                break;
            }
        }
        if (hit) {
            notify(src, Notifier.NOTIFY_UPDATE_STATE, "");

        } else if (removed) {
            if (m_config.getWarningEnabled(Config.WARNING_REMOVED)) {
                notify(src, Notifier.NOTIFY_WARNING, String.format("Reference: %s has been removed", refString));
            }

        } else {
            if (isLoop) {
                notify(src, Notifier.NOTIFY_ERROR, String.format("Reference: %s not within a loop on any active tile", refString));
            } else {
                notify(src, Notifier.NOTIFY_ERROR, "References not resolved on any active tile");
            }
        }
    }

    private static interface DefinesUpdate {
        void run(Reference reference);
    }

    public void updateLoops(CommandSource src, int routeId, String refString,
            ReferenceClass referenceClass, LoopsUpdate update) {

        Collection<Route> routes = getRoute(routeId);

        boolean hit = false;
        boolean removed = false;
        try {
            Map<Route, Reference> refMap = new NonIteratableMap<Route, Reference>();
            for (Route route : routes) {
                Reference reference = ReferenceFactory.createReference(refString,
                        referenceClass, route.parentRouteCoreContainer().getCoreId());

                Result result = resolveReference(reference);
                switch (result) {
                case RESOLVED:
                    update.check(route, reference, src);
                    refMap.put(route, reference);
                    hit = true;
                    break;

                case REMOVED:
                    removed = true;
                    break;

                case UNRESOLVED:
                    break;
                }
            }
            for (Route route : routes) {
                if (refMap.containsKey(route)) {
                    update.run(route, refMap.get(route), src);
                }
            }

        } catch (Exception e) {
            notify(src, Notifier.NOTIFY_ERROR, e.getMessage());
            return;
        }
        if (hit) {
            notify(src, Notifier.NOTIFY_UPDATE_STATE, "");

        } else if (removed) {
            if (m_config.getWarningEnabled(Config.WARNING_REMOVED)) {
                notify(src, Notifier.NOTIFY_WARNING, String.format("Reference: %s has been removed", refString));
            }

        } else {
            notify(src, Notifier.NOTIFY_ERROR, String.format("Reference: %s not within a loop on any active tile", refString));
        }
        notify(src, Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
    }

    private static interface LoopsUpdate {
        void check(Route route, Reference reference, CommandSource src) throws Exception;
        void run(Route route, Reference reference, CommandSource src) throws Exception;
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

        for (CoreId coreId : xeLoader.getCoreIds()) {
            int coreThreads = getCoreThreads(progInfoLoader, coreId);
            m_coreThreads.add(coreId, coreThreads);
        }

        // Initialise the frequencies with the default for each node..
        for (int nodeChainIndex : xeLoader.getNodeChainIndexes()) {
            String nodeId = xnLoader.getNodeId(nodeChainIndex);
            m_nodeHz.put(nodeId, (int)XmlHandler.DEFAULT_FREQUENCY_HZ);
        }

        // Sets the frequencies with the ones actually used (from the proginfo sector)..
        for (Integer nodeChainIndex : progInfoLoader.getNodeFrequencies().keySet()) {
            String nodeId = xnLoader.getNodeId(nodeChainIndex);
            m_nodeHz.put(nodeId, progInfoLoader.getNodeFrequencies().get(nodeChainIndex));
        }
    }

    private void initialiseActiveCores(XeLoader xeLoader) {
        m_activeCores.clear();
        for (CoreId coreId : xeLoader.getCoreIds()) {
            m_activeCores.add(coreId);
        }
    }

    private void initialiseCachedFunctions(XeLoader xeLoader) {
        CoreCollection<CachedFunctionMap> cachedFunctions = new CoreCollection<CachedFunctionMap>();
        for (CoreId coreId : xeLoader.getCoreIds()) {
            cachedFunctions.add(coreId, new CachedFunctionMap(this));
        }
        m_cachedFunctions = cachedFunctions;
    }

    private void initialiseControlFlows(XeLoader xeLoader, Sections sections, final CommandSource src) {
        notify(src, Notifier.HOLD_MESSAGES, "");

        m_controlFlows.initialise(xeLoader, m_instructionDecoders,
            getBranchesForControlFlow(xeLoader, sections),
            getLabelsForControlFlow(xeLoader, sections),
            getThreadStartsForControlFlow(xeLoader, sections),
            getThreadStopsForControlFlow(xeLoader, sections),
            new WarningNotifier() {
                public void warn(String message) {
                    m_notifier.notify(src, Notifier.NOTIFY_WARNING, message);
                }
            }
        );
        notify(src, Notifier.RELEASE_MESSAGES, "");
    }

    private int getCoreThreads(ProgInfoLoader progInfoLoader, CoreId coreId) {
        int nodeChainIndex = coreId.getNodeChainIndex();
        for (ProgInfoEntry entry : progInfoLoader.getProgInfos())  {
            if ((entry.getNodeChainIndex() == nodeChainIndex) &&
                (entry.getCoreNumber() == coreId.getCoreNumber()) &&
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
        if (m_lastAnalysisStatus != Status.COMPLETED) {
            return 0;
        }
        return m_routeContainers.get(m_routeContainers.size() -1).getNumExistingRoutes();
    }

    private boolean validateRouteCount(){
        return m_routes.size() >= getNumLastRoutesAnalyzed();
    }

    private void checkInstructionIsBranch(CommandSource src, Reference ref, CoreId activeCore) {
        for (long pc : ref.getPcs()) {
            CfgInstructionNode node = m_instructionDecoders.getInstructionDecoder(activeCore).getNode(pc);
            if (node == null) {
                notify(src, Notifier.NOTIFY_WARNING,
                        String.format("adding branch from non-existant instruction at %s",
                        ref.toString()));
                return;
            }

            Instruction inst = node.getInstruction();
            if (!inst.isBranch()) {
                notify(src, Notifier.NOTIFY_WARNING,
                        String.format("adding branch from instruction which is not a branch: %s",
                        inst.toString()));
                return;
            }
        }
    }

    private CoreCollection<Collection<Branch>> getBranchesForControlFlow(XeLoader xeLoader, Sections sections) {
        CoreCollection<Collection<Branch>> branches = new CoreCollection<Collection<Branch>>();

        for (CoreId coreId : xeLoader.getCoreIds()) {
            List<Branch> branchList = new ArrayList<Branch>();
            Map<Reference, Set<BranchInfo>> branchesMap = sections.getBranches(coreId);

            for (Reference fromRef : branchesMap.keySet()) {
                for (long fromPc : fromRef.getPcs()) {
                    List<Long> toPcs = new ArrayList<Long>();
                    for (BranchInfo branchInfo : branchesMap.get(fromRef)) {
                        for (long toPc : branchInfo.getRef().getPcs()) {
                            toPcs.add(toPc);
                        }
                    }
                    branchList.add(new Branch(fromPc, toPcs));
                }
            }
            branches.add(coreId, branchList);
        }
        return branches;
    }

    private CoreCollection<Collection<Label>> getLabelsForControlFlow(XeLoader xeLoader, Sections sections) {
        CoreCollection<Collection<Label>> labels = new CoreCollection<Collection<Label>>();

        for (CoreId coreId : xeLoader.getCoreIds()) {
            List<Label> labelList = new ArrayList<Label>();
            Map<String, Set<Long>> labelsMap = sections.getLabels(coreId);

            for (String name : labelsMap.keySet()) {
                List<Long> toPcs = new ArrayList<Long>();
                for (long toPc : labelsMap.get(name)) {
                    toPcs.add(toPc);
                }
                labelList.add(new Label(name, toPcs));
            }
            labels.add(coreId, labelList);
        }
        return labels;
    }

    private CoreCollection<Collection<Long>> getThreadStartsForControlFlow(XeLoader xeLoader, Sections sections) {
        CoreCollection<Collection<Long>> threadPoints = new CoreCollection<Collection<Long>>();
        for (CoreId coreId : xeLoader.getCoreIds()) {
            threadPoints.add(coreId, sections.getThreadStartPoints(coreId));
        }
        return threadPoints;
    }

    private CoreCollection<Collection<Long>> getThreadStopsForControlFlow(XeLoader xeLoader, Sections sections) {
        CoreCollection<Collection<Long>> threadPoints = new CoreCollection<Collection<Long>>();
        for (CoreId coreId : xeLoader.getCoreIds()) {
            threadPoints.add(coreId, sections.getThreadStopPoints(coreId));
        }
        return threadPoints;
    }
}
