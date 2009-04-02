package com.xmos.xta.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.runtime.IProgressMonitor;

import com.xmos.elfhandler.ElfHandler;
import com.xmos.elfhandler.Symbol;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeloader.XeLoader;
import com.xmos.xeobject.AssemblyLine;
import com.xmos.xeobject.FileFinder;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.engine.Route.RouteStatus;
import com.xmos.xta.engine.constraint.Constraint;
import com.xmos.xta.engine.constraint.ConstraintFactory;
import com.xmos.xta.engine.maps.BranchMap;
import com.xmos.xta.engine.maps.EndpointTableInfo;
import com.xmos.xta.engine.maps.EndpointTableMap;
import com.xmos.xta.engine.maps.LabelMap;
import com.xmos.xta.engine.maps.CodePointMap;
import com.xmos.xta.engine.maps.CodePointInfo;
import com.xmos.xta.engine.maps.SrcLabelTableInfo;
import com.xmos.xta.engine.maps.SrcLabelTableMap;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.ReferenceFactory.ReferenceClass;
import com.xmos.xta.engine.unknowns.Unknown;
import com.xmos.xta.rcp.XtaNotifier;

public class Engine {

	public static int ROUTE_ID_LAST = -1;
	
	public enum ReferenceMode {
		FILTERED, PROHIBITED, REQUIRED
	}

	private XeLoader m_xeLoader;
	private List<Route> m_routes;
	private Route m_selectedRoute;
	private Config m_config;
	private File m_executableFile;
	private int m_nodeId;
	private int m_coreId;
	private Vector<Notifier> m_notifiers;
	private NodeCoreInstructionCaches m_nodeCoreInstructionCache;
	private Progress m_progress;
	private IProgressMonitor m_jobMonitor;
	private String m_srcPaths;
	private ArrayList<XeObject> m_xeObjects;
	private ArrayList<BranchMap> m_branches;
	private ArrayList<LabelMap> m_labels;
	private ArrayList<CodePointMap> m_srcLabels;
	private ArrayList<CodePointMap> m_endpoints;
	private ArrayList<SrcLabelTableMap> m_srcLabelTables;
	private ArrayList<EndpointTableMap> m_endpointTables;
	private AnalysisEngine m_ae;
	private Constraint m_constraint;
	private InstructionMnemonics m_mnenonics;

	public Engine(Config config) {
		m_xeObjects = new ArrayList<XeObject>();
		m_branches  = new ArrayList<BranchMap>();
		m_labels    = new ArrayList<LabelMap>();
		m_srcLabels = new ArrayList<CodePointMap>();
		m_endpoints = new ArrayList<CodePointMap>();
		m_srcLabelTables = new ArrayList<SrcLabelTableMap>();
		m_endpointTables = new ArrayList<EndpointTableMap>();
		m_xeLoader = new XeLoader();
		m_routes = new LinkedList<Route>();
		m_config = config;
		m_executableFile = null;
		m_nodeId = 0;
		m_coreId = 0;
		m_srcPaths = ".";
		m_notifiers = new Vector<Notifier>();
		m_progress = new Progress(Progress.State.IDLE, 0);
		m_jobMonitor = null;
		m_constraint = ConstraintFactory.createConstraint(this, "");
		m_mnenonics = new InstructionMnemonics(m_xeLoader.getArchitecture());

		ReferenceFactory.setEngine(this);
	}

	public void notify(String message, String argument) {
		for (Notifier notifier : m_notifiers)
			notifier.notify(message, argument);
	}

	public Config getConfig() {
		return m_config;
	}

	public List<Route> getRoutes() {
		return m_routes;
	}

	public Route getSelectedRoute() {
		return m_selectedRoute;
	}

	public int getSelectedRouteId() {
		if (m_selectedRoute != null)
			return m_selectedRoute.getRouteId();
		else
			return Route.INVALID_ROUTE_ID;
	}

	public void setSelectedRoute(Route route) {
		if (route != null) {
			RouteTiming routeTiming = route.getRouteTiming();
			int nodeId = routeTiming.getNodeId();
			if (nodeId != m_nodeId)
				setNodeId(nodeId);
	
			int coreId = routeTiming.getCoreId();
			if (coreId != m_coreId)
				setCoreId(coreId);
		}		
		m_selectedRoute = route;
		XtaNotifier.getInstance().notify(Notifier.NOTIFY_SELECT_ROUTE, "");
	}

	public void setSelectedRoute(int routeId) {
		 setSelectedRoute(getRoute(routeId));
	}

	public XeLoader getXeLoader() {
		return m_xeLoader;
	}
	
	public Vector<MemoryImage> getMemoryImages() {
		return m_xeLoader.getMemoryImages();
	}

	public Vector<SymbolTable> getSymbolTables() {
		return m_xeLoader.getSymbolTables();
	}

	public Vector<DebugInfo> getDebugInfos() {
		return m_xeLoader.getDebugInfos();
	}
	
	public MemoryImage getMemoryImage() {
		return m_xeLoader.getMemoryImage(m_nodeId, m_coreId);
	}

	public SymbolTable getSymbolTable() {
		return m_xeLoader.getSymbolTable(m_nodeId, m_coreId);
	}

	public DebugInfo getDebugInfo() {
		return m_xeLoader.getDebugInfo(m_nodeId, m_coreId);
	}

	public XeObject getXeObject() {
		return getXeObject(m_nodeId, m_coreId);
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

	public Route getRoute(int routeId) {
		if (routeId == ROUTE_ID_LAST) {
			if (m_routes.size() > 0)
				return m_routes.get(m_routes.size() - 1);
			else 
				return null;
		}

		for (Route route : m_routes) {
			if (route.getRouteId() == routeId) {
				return route;
			}
		}		
		return null;
	}
	
	public boolean loadExecutable(File executableFile, FileFinder fileFinder) {
		XeLoader newLoader = new XeLoader();

		if (!newLoader.loadExecutable(executableFile)) {
			notify(Notifier.NOTIFY_ERROR, "The file '" + executableFile + "' could not be loaded.");
			return false;			
		}
		
		m_nodeId = 0;
		m_coreId = 0;

		m_xeLoader = newLoader;
		m_executableFile = executableFile;
		initialiseLists(fileFinder);
		m_nodeCoreInstructionCache = new NodeCoreInstructionCaches(this,
				m_xeLoader, m_config);

		try {
			decodeInstructions();
		} catch (Exception e) {
			notify(Notifier.NOTIFY_ERROR, "Some of the pc's refered to in the executable could not be found");
			return false;
		}

		if (!loadBranchMaps()) 
			return false;		
		if (!loadLabelMaps())
		    return false;		
		if (!loadCodePointMaps(m_srcLabels))
		    return false;		
		if (!loadCodePointMaps(m_endpoints))
		    return false;		
		if (!loadSrcLabelTables())
		    return false;		
		if (!loadEndpointTables())
		    return false;		

		m_ae = new AnalysisEngine(m_xeLoader, m_nodeCoreInstructionCache,
				m_xeObjects, m_branches);

		runAnalysisEngine();

		m_routes.clear();
		setSelectedRoute(null);
		Route.resetRouteCount();
		setConstraint(ConstraintFactory.createConstraint(this, ""));

		// Need to update as the executable might be of a different architecture
		m_mnenonics = new InstructionMnemonics(m_xeLoader.getArchitecture());

		notify(Notifier.NOTIFY_LOAD_EXECUTABLE, executableFile.getAbsolutePath());
		return true;
	}

	public void runAnalysisEngine() {
		m_ae.run();
	}

	private void decodeInstructions() throws Exception {
		for (int nodeId : m_xeLoader.getNodeIds()) {
			for (int coreId : m_xeLoader.getCoreIds(nodeId)) {
				XeObject xeObject = getXeObject(nodeId, coreId);
				for (AssemblyLine al : xeObject.getInstructions()) {
					int pc = al.getPc();
					m_nodeCoreInstructionCache.decode(getOpcode(pc, nodeId,
							coreId), pc, nodeId, coreId);
				}
			}
		}
	}

	public Progress getProgress() {
		if (m_progress != null) {
			return m_progress;
		} else {
			return new Progress(Progress.State.IDLE, 0);
		}
	}

	public boolean analyseRoute(String functionName) {
		if (!m_xeLoader.isLoaded(m_nodeId, m_coreId)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Node: %d, Core: %d image not found in file: %s", m_nodeId,
					m_coreId, m_executableFile));
			return false;
		} else if (!functionExists(functionName)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Cannot find function: %s in file: %s, node: %d, core: %d",
					functionName, m_executableFile, m_nodeId, m_coreId));
			return false;
		} else {
			notify(Notifier.NOTIFY_SELECT_FUNCTION, functionName);
			notify(Notifier.NOTIFY_SET_RUN_MODE, Notifier.RUN_MODE_FUNCTION);

			TreeSet<Integer> endPcs = m_ae.getFunctionRetuns(functionName, m_nodeId, m_coreId);
			int startPc = m_ae.getFunctionStartPc(functionName, m_nodeId, m_coreId);
			
			RouteDescription routeDescription = new RouteDescription(functionName);
			return analyseRouteSafely(startPc, endPcs, routeDescription);
		}
	}

	public boolean analyseRoute(String from, String to) {
		Reference fromRef = ReferenceFactory.createReference(
				from, ReferenceClass.ENDPOINT_REF);
		Reference toRef = ReferenceFactory.createReference(
				to, ReferenceClass.ENDPOINT_REF);
		
		if (!m_xeLoader.isLoaded(m_nodeId, m_coreId)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Node: %d, Core: %d image not found in file: %s", m_nodeId,
					m_coreId, m_executableFile));
			return false;
		} else if (!resolveRefernece(fromRef, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Unresolved reference: %s", from));
			return false;
		} else if (!resolveRefernece(toRef, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Unresolved reference: %s", to));
			return false;
		} else {
			TreeSet<Integer> endPc = new TreeSet<Integer>();
			endPc.add(toRef.getPc());
			RouteDescription routeDescription = new RouteDescription(fromRef, toRef);
			boolean success = analyseRouteSafely(fromRef.getPc(), endPc, routeDescription);
			if (success) {
				notify(Notifier.NOTIFY_SET_RUN_MODE, Notifier.RUN_MODE_PATH);
				notify(Notifier.NOTIFY_SET_START_POINT, from);
				notify(Notifier.NOTIFY_SET_END_POINT, to);
			}
			return success;
		}
	}

	public void registerNotifier(Notifier notifier) {
		assert (m_notifiers.contains(notifier) == false);
		m_notifiers.add(notifier);
	}

	public void removeNotifier(Notifier notifier) {
		assert (m_notifiers.contains(notifier));
		m_notifiers.remove(notifier);
	}

	public boolean getAbort() {
		if (m_jobMonitor != null)
			return m_jobMonitor.isCanceled();
		return false;
	}

	public void setNodeId(int nodeId) {
		m_nodeId = nodeId;
		setConstraint(ConstraintFactory.createConstraint(this, ""));
		m_selectedRoute = null;
		notify(Notifier.NOTIFY_SET_NODE_CORE, "");
	}

	public int getNodeId() {
		return m_nodeId;
	}

	public void setCoreId(int coreId) {
		m_coreId = coreId;
		setConstraint(ConstraintFactory.createConstraint(this, ""));
		m_selectedRoute = null;
		notify(Notifier.NOTIFY_SET_NODE_CORE, "");
	}

	public int getCoreId() {
		return m_coreId;
	}

	public void setFrequency(int frequency) {
		m_config.m_frequency = frequency;
		invalidateRouteStats();
		notify(Notifier.NOTIFY_UPDATE_STATE, Notifier.STATE_FREQUENCY);
	}

	public int getFrequency() {
		return m_config.m_frequency;
	}

	public void setNumThreads(int numThreads) {
		m_config.m_numThreads = numThreads;
		invalidateRouteStats();
		notify(Notifier.NOTIFY_UPDATE_STATE, Notifier.STATE_NUM_THREADS);
	}

	public int getNumThreads() {
		return m_config.m_numThreads;
	}
	
	public boolean setLoopIterations(int routeId, int pc, long iterations) {
		Route route = getRoute(routeId);
		if (route == null)		
		    return false;
		
		if (route.setLoopIterations(pc, new Long(iterations))) {
			notifyLoopsUpdated();
			return true;
		}
		return false;
	}

	public boolean setLoopMaxIterations(int routeId, int pc, long iterations) {				
		Route route = getRoute(routeId);						
		if (route == null) 
			return false;
			
		if (route.setLoopMaxIterations(pc, new Long(iterations))) {
			notifyLoopsUpdated();
			return true;
		}
		return false;
	}
	
	public boolean setLoopScope(int routeId, int pc, boolean absolute) {
		Route route = getRoute(routeId);		
		if (route == null) 
			return false;
		
		if (route.setLoopScope(pc, absolute)) {
			notifyLoopsUpdated();
			return true;
		}
		return false;
	}
	
	public boolean unsetLoopIterations(int routeId, int pc) {
		Route route = getRoute(routeId);
		if (route == null)		
		    return false;
		
		if (route.setLoopIterations(pc, null)) {
			notifyLoopsUpdated();
			return true;
		}
		return false;
	}

	public boolean unsetLoopMaxIterations(int routeId, int pc) {				
		Route route = getRoute(routeId);						
		if (route == null) 
			return false;
			
		if (route.setLoopMaxIterations(pc, null)) {
			notifyLoopsUpdated();
			return true;
		}
		return false;
	}
	
	public boolean setInstructionTimeTaken(Reference ref, double timeTaken) {
		if (m_nodeCoreInstructionCache == null) {
			notify(Notifier.NOTIFY_ERROR, "No executable loaded");
			return false;
		}

		if (!resolveRefernece(ref, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Unresolved reference: %s", ref.getRef()));
			return false;
		}

		if (m_nodeCoreInstructionCache.setInstructionTimeTaken(
				this, ref.getPc(), timeTaken, m_nodeId, m_coreId)) {
			invalidateRouteStats();
			notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
			return true;
		}
		return false;
	}

	public double getInstructionTimeTaken(int pc) {
		if (m_nodeCoreInstructionCache == null) {
			notify(Notifier.NOTIFY_ERROR, "No executable loaded");
			return 0;
		}
		return m_nodeCoreInstructionCache.getInstructionTimeTaken(pc, m_nodeId, m_coreId);
	}

	public boolean setInstructionNumCycles(Reference ref, long numCycles) {
		if (m_nodeCoreInstructionCache == null) {
			notify(Notifier.NOTIFY_ERROR, "No executable loaded");
			return false;
		}

		if (!resolveRefernece(ref, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Unresolved reference: %s", ref.getRef()));
			return false;
		}

		if (m_nodeCoreInstructionCache.setInstructionNumCycles(
				this, ref.getPc(), numCycles, m_nodeId, m_coreId)) {
			invalidateRouteStats();
			notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
            return true;			
		}		
		return false;
	}

	public long getInstructionNumCycles(int pc) {
		if (m_nodeCoreInstructionCache == null) {
			notify(Notifier.NOTIFY_ERROR, "No executable loaded");
			return 0;
		}
		return m_nodeCoreInstructionCache.getInstructionNumCycles(pc, m_nodeId, m_coreId);
	}

	public void unsetInstructionTime(int pc) {
		if (m_nodeCoreInstructionCache == null) {
			notify(Notifier.NOTIFY_ERROR, "No executable loaded");
			return;
		}
		if (m_nodeCoreInstructionCache.unsetInstructionTime(pc, m_nodeId, m_coreId)) {
			invalidateRouteStats();
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
   			notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
		} else {
			notify(Notifier.NOTIFY_ERROR, String.format("Unable to unset time for: 0x%0x", pc));
		}
	}

	public void unsetInstructionTime(Reference ref, HashSet<Unknown> unknowns) {
		if (m_nodeCoreInstructionCache == null) {
			notify(Notifier.NOTIFY_ERROR, "No executable loaded");
			return;
		}

		if (m_nodeCoreInstructionCache.unsetInstructionTime(ref, m_nodeId, m_coreId, unknowns)) {
			invalidateRouteStats();
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
   			notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
		} else {
			notify(Notifier.NOTIFY_ERROR, String.format("Unable to unset time for: %s", ref.getRef()));
		}
	}

	public boolean canInstructionPause(int pc) {
		if (m_nodeCoreInstructionCache == null) {
			notify(Notifier.NOTIFY_ERROR, "No executable loaded");
			return false;
		}
		return m_nodeCoreInstructionCache.getInstructionDecodeCache(m_nodeId,
				m_coreId).getInstruction(pc).canPause();
	}
	
	public boolean setRouteMaxTime(int routeId, double time) {
		Route route = getRoute(routeId);
		if (route != null) {
			route.getRouteTiming().setMaxTime(time);
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
			return true;
		}
		return false;
	}

	public boolean setRouteMaxCycles(int routeId, int cycles) {
		Route route = getRoute(routeId);
		if (route != null) {
			route.getRouteTiming().setMaxCycles(cycles);
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
			return true;
		}
		return false;		
	}
	
	public boolean unsetRouteMax(int routeId) {
		Route route = getRoute(routeId);
		if (route != null) {
			route.getRouteTiming().resetMaxTime();
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
			return true;
		}
		return false;		
	}	
	
	public boolean removeRoute(int routeId) {
		Route route = getRoute(routeId);
		if (route != null) {
			m_routes.remove(route);			
			if (m_routes.size() == 0) {
				Route.resetRouteCount();
				setSelectedRoute(null);
			} else if (m_selectedRoute.getRouteId() == routeId) {
				m_selectedRoute = m_routes.get(m_routes.size() - 1);
			}
			notify(Notifier.NOTIFY_REMOVE_ROUTE, "");
			return true;
		}
		return false;
	}

	public void removeAllRoutes() {
		m_routes.clear();
		setSelectedRoute(null);
		Route.resetRouteCount();
		notify(Notifier.NOTIFY_REMOVE_ROUTE, "");
	}	
		
	public File getExecutable() {
		return m_executableFile;
	}

	public void setConstraint(Constraint constraint) {
		assert(constraint != null);
		if (constraint == null){
			m_constraint = ConstraintFactory.createConstraint(this, "");
		} else {
			m_constraint = constraint;
		}
		notify(Notifier.NOTIFY_SET_CONSTRAINT, "");
	}

	public Constraint getConstraint() {
		return m_constraint;
	}
			
	public boolean addBranch(Reference fromRef, Reference toRef) {
		return addToBranchMap(fromRef, toRef);
	}

	public void removeBranch(Reference fromRef) {
		removeFromBranchMap(fromRef);
	}

	public Map<Integer, TreeSet<Integer>> getBranches() {
		return getBranchMap();
	}

	public boolean addLabel(String label, Reference ref) {
		return addToLabelMap(label, ref);
	}

	public void removeLabel(String label) {
		removeFromLabelMap(label);
	}

	public Map<String, Integer> getLabels() {
		return getLabelMap();
	}
	
	public boolean addSrcLabel(String label, Reference ref) {
		return addToCodePointMap(m_srcLabels, label, ref);
	}

	public void removeSrcLabel(String label) {
		removeFromCodePointMap(m_srcLabels, label);
	}

	public Map<String, CodePointInfo> getSrcLabels() {
		return getCodePointMap(m_srcLabels);
	}

	public Map<Integer, SrcLabelTableInfo> getSrcLabelTable() {
		return getSrcLabelTableMap();
	}

	public boolean addEndpoint(String endpoint, Reference ref) {
		return addToCodePointMap(m_endpoints, endpoint, ref);
	}

	public void removeEndpoint(String endpoint) {
		removeFromCodePointMap(m_endpoints, endpoint);
	}
	
	public Map<String, CodePointInfo> getEndpoints() {
		return getCodePointMap(m_endpoints);
	}
		
	public Map<Integer, EndpointTableInfo> getEndpointTable() {
		return getEndpointTableMap();
	}
	
	private boolean analyseRouteSafely(int startPc, TreeSet<Integer> endPcs, RouteDescription routeDescription) {
		boolean success = false;
		try {
			success = doRouteAnalysis(startPc, endPcs, routeDescription);
			m_progress.setState(Progress.State.IDLE);
		} catch (final java.lang.OutOfMemoryError e) {
			m_progress.setState(Progress.State.IDLE);
			System.gc();
			notify(Notifier.NOTIFY_ERROR, "Out of Memory (Heap)");
			return false;
		} catch (final java.lang.StackOverflowError e) {
			m_progress.setState(Progress.State.IDLE);
			System.gc();
			notify(Notifier.NOTIFY_ERROR, "Out of Memory (Stack)");
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			m_progress.setState(Progress.State.IDLE);
			notify(Notifier.NOTIFY_ERROR, "Unknown error detected");
			return false;
		}
		return success;
	}

	private boolean doRouteAnalysis(int startPc, TreeSet<Integer> endPcs, 
			RouteDescription routeDescription) throws Exception {
		 		
		if (m_progress.getState() != Progress.State.IDLE) {
			notify(Notifier.NOTIFY_ERROR, "Engine busy");
			return false;
		}

		if (endPcs.size() == 0){
            notify(Notifier.NOTIFY_ERROR, "No terminating PC" );
            return false;
        }
		
		if (!m_xeLoader.isLoaded(m_nodeId, m_coreId)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Node: %d, Core: %d image not found in file: %s",
					m_nodeId, m_coreId, m_executableFile));
			return false;
		}
		
 		Route route = new Route(m_config, this, startPc, endPcs, m_ae.getNodeArray(m_nodeId, m_coreId), 
 				m_nodeCoreInstructionCache.getInstructionDecodeCache(m_nodeId, m_coreId),
 				m_mnenonics, routeDescription);
													
		route.setConstrained(false);
			
		notify(Notifier.NOTIFY_ANALYSIS_BEGIN, "");
		m_progress.resetState(Progress.State.PROCESSING_PATHS);
		RouteStatus status = route.analyse(routeDescription.getMode());
		switch (status) {
		case ABORTED:
			notify(Notifier.NOTIFY_INFO, "Abort detected");
			return false;

		case ERROR:
			notify(Notifier.NOTIFY_ERROR, "Cannot analyse path");
			return false;
		}
		int numUnresolvedPaths = route.getUnresolvedPaths().size();
		if (numUnresolvedPaths > 0) {
			String plural = (numUnresolvedPaths == 1) ? "" : "s";
			notify(Notifier.NOTIFY_ERROR, String.format("Found "
					+ numUnresolvedPaths + " unresolved path" + plural));	
			
		} else if (route.getCompletedPaths().size() == 0) {
			if (route.wasConstrained()) {
				notify(Notifier.NOTIFY_ERROR, "No valid paths of execution (constraints applied)");
			} else {
				notify(Notifier.NOTIFY_ERROR, "No valid paths of execution");
			}
		}
		m_routes.add(route);
		setSelectedRoute(route);

		notify(Notifier.NOTIFY_ANALYSIS_COMPLETE, Integer.toString(route.getRouteId()));

		return true;
	}

	private int getOpcode(int pc, int nodeId, int coreId) throws Exception {
		return m_xeLoader.getMemoryImage(nodeId, coreId)
				.readInstructionWord(pc);
	}
	
	private boolean functionExists(String functionName) {
		SymbolTable symbolTable = m_xeLoader.getSymbolTable(m_nodeId, m_coreId);
		if (symbolTable != null) {
			ElfHandler elfHandler = symbolTable.getElfHandler();

			Symbol symbol = elfHandler.getSymbolsNameKeyed().get(functionName);
			if (symbol != null) {
				if (symbol.isFunction()) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

	private boolean resolveRefernece(Reference ref, Engine engine) {
		return ref.resolve(engine, m_nodeId, m_coreId);
	}

	private XeObject getXeObject(int nodeId, int coreId) {
		for (XeObject xeObject : m_xeObjects) {
			if ((xeObject.getNodeId() == nodeId)
					&& (xeObject.getCoreId() == coreId)) {
				return xeObject;
			}
		}
		assert (false);
		return null;
	}
	
	private void notifyLoopsUpdated() {
    	invalidateRouteStats();
	    notify(Notifier.NOTIFY_UPDATE_STATE, "");
	    notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
    }
	
	private void invalidateRouteStats() {
		for (Route route : m_routes) {
			route.invalidateAllBlocks();
		}		
	}
	
	private void initialiseLists(FileFinder fileFinder) {
		m_branches.clear();
		m_labels.clear();
		m_srcLabels.clear();
		m_srcLabelTables.clear();
		m_endpoints.clear();
		m_endpointTables.clear();
		m_xeObjects.clear();

		for (int node : m_xeLoader.getNodeIds()) {
			for (int core : m_xeLoader.getCoreIds(node)) {
				m_branches.add(new BranchMap(node, core));
				m_labels.add(new LabelMap(node, core));
				m_srcLabels.add(new CodePointMap(node, core, ".xtalabel", this, "label"));
				m_endpoints.add(new CodePointMap(node, core, ".xtaendpoint", this, "endpoint"));
				m_srcLabelTables.add(new SrcLabelTableMap(node, core, ".xtalabeltable"));
				m_endpointTables.add(new EndpointTableMap(node, core, ".xtaendpointtable"));
				m_xeObjects.add(new XeObject(m_xeLoader, node, core, fileFinder));
			}
		}
	}

	private boolean loadBranchMaps() {
		for (BranchMap branchMap : m_branches) {
			if (!branchMap.load(this, m_xeLoader)) {
			    return false;
			}
		}
		return true;
	}
	
	private boolean loadLabelMaps() {
		for (LabelMap labelMap : m_labels) {
			if (!labelMap.load(this, m_xeLoader)) {
				return false;
			}			
		}	
		return true;
	}

	private boolean loadCodePointMaps(List<CodePointMap> codePointMaps) {
		for (CodePointMap codePointMap : codePointMaps) {
			if (!codePointMap.load(this, m_xeLoader)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean loadSrcLabelTables() {
		for (SrcLabelTableMap tableMap : m_srcLabelTables) {
			if (!tableMap.load(this, m_xeLoader)) {
				return false;
			}
		}
		return true;
	}	
		
	private boolean loadEndpointTables() {
		for (EndpointTableMap tableMap : m_endpointTables) {
			if (!tableMap.load(this, m_xeLoader)) {
				return false;
			}
		}
		return true;
	}	
	
	private boolean addToBranchMap(Reference fromRef, Reference toRef) {
		if (!resolveRefernece(fromRef, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Unresolved reference: %s", fromRef.getRef()));
			return false;
		} else if (!resolveRefernece(toRef, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Unresolved reference: %s", toRef.getRef()));
			return false;
		}

		BranchMap branchMap = getBranchMapNull(m_nodeId, m_coreId);
		if (branchMap != null) {
			branchMap.add(fromRef.getPc(), toRef.getPc());
			return true;
		}
		return false;
	}

	private void removeFromBranchMap(Reference fromRef) {
		if (!resolveRefernece(fromRef, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Unresolved reference: %s", fromRef.getRef()));
			return;
		}

		BranchMap branchMap = getBranchMapNull(m_nodeId, m_coreId);
		if (branchMap != null) {
			if (branchMap.getMap().containsKey(fromRef.getPc())) {
				branchMap.remove(fromRef.getPc());
			} else {
				notify(Notifier.NOTIFY_ERROR, String.format(
						"Unable to remove branch: 0x%x", fromRef.getPc()));
			}
		}
	}	
	
	private boolean addToLabelMap(String label, Reference ref) {
		if (!resolveRefernece(ref, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Unresolved reference: %s", ref.getRef()));
			return false;
		}
		
		if (ReferenceFactory.isPcString(label)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Cannot use PC as a label: %s", label));
			return false;
		}		

		LabelMap labelMap = getLabelMapNull(m_nodeId, m_coreId);
		if (labelMap != null) {
			if (!labelMap.getMap().containsKey(label)) {										
				labelMap.getMap().put(label, ref.getPc());
			} else {
				notify(Notifier.NOTIFY_ERROR, String.format(
						"Label already exists: %s", label));
			}
			return true;
		}
		return false;
	}

	private void removeFromLabelMap(String label) {
		LabelMap labelMap = getLabelMapNull(m_nodeId, m_coreId);
		if (labelMap != null) {
			List<String> matches = new ArrayList<String>();

			for (String mapEntry : labelMap.getMap().keySet()) {
				try {
					if (mapEntry.matches(label)) {
						matches.add(mapEntry);
					}
				} catch (PatternSyntaxException e) {
				}
			}

			if (matches.size() > 0) {
				for (String toRemove : matches)
					labelMap.getMap().remove(toRemove);
			} else {
				notify(Notifier.NOTIFY_ERROR, String.format(
						"Unable to remove label: %s", label));
			}
		}
	}
	
	private boolean addToCodePointMap(List<CodePointMap> codePointMaps, String label, Reference ref) {
		if (!resolveRefernece(ref, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Unresolved reference: %s", ref.getRef()));
			return false;
		}

		if (ReferenceFactory.isPcString(label)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Cannot use PC as a label: %s", label));
			return false;
		}
		
		CodePointMap labelMap = getCodePointMapNull(codePointMaps, m_nodeId, m_coreId);
		if (labelMap != null) {
			if (!labelMap.getMap().containsKey(label)) {										
				labelMap.getMap().put(label, new CodePointInfo(ref.getPc(), label, "", "", 0));
			} else {
				notify(Notifier.NOTIFY_ERROR, String.format(
						"Label already exists: %s", label));
			}
			return true;
		}
		return false;
	}

	private void removeFromCodePointMap(List<CodePointMap> codePointMaps, String label) {
		CodePointMap labelMap = getCodePointMapNull(codePointMaps, m_nodeId, m_coreId);
		if (labelMap != null) {
			List<String> matches = new ArrayList<String>();

			for (String mapEntry : labelMap.getMap().keySet()) {
				try {
					if (mapEntry.matches(label)) {
						matches.add(mapEntry);
					}
				} catch (PatternSyntaxException e) {
				}
			}

			if (matches.size() > 0) {
				for (String toRemove : matches)
					labelMap.getMap().remove(toRemove);
			} else {
				notify(Notifier.NOTIFY_ERROR, String.format(
						"Unable to remove label: %s", label));
			}
		}
	}
	
	private Map<Integer, TreeSet<Integer>> getBranchMap() {
		BranchMap branchMap = getBranchMapNull(m_nodeId, m_coreId);
		if (branchMap != null) {
			return branchMap.getMap();
		}
		return new HashMap<Integer, TreeSet<Integer>>();
	}

	private Map<String, Integer> getLabelMap() {
	    LabelMap labelMap = getLabelMapNull(m_nodeId, m_coreId);
	    if (labelMap != null) {
		    return labelMap.getMap();
	    }
	    return new HashMap<String, Integer>();
	}

	private Map<String, CodePointInfo> getCodePointMap(List<CodePointMap> codePointMaps) {
	    CodePointMap labelMap = getCodePointMapNull(codePointMaps, m_nodeId, m_coreId);
	    if (labelMap != null) {
		    return labelMap.getMap();
	    }
	    return new HashMap<String, CodePointInfo>();
	}
	
	private Map<Integer, SrcLabelTableInfo> getSrcLabelTableMap() {
		SrcLabelTableMap tableMap = getSrcLabelTableMapNull(m_nodeId, m_coreId);
		if (tableMap != null) {
			return tableMap.getMap();
		}
		return new HashMap<Integer, SrcLabelTableInfo>();
	}

	private Map<Integer, EndpointTableInfo> getEndpointTableMap() {
		EndpointTableMap tableMap = getEndpointTableMapNull(m_nodeId, m_coreId);
		if (tableMap != null) {
			return tableMap.getMap();
		}
		return new HashMap<Integer, EndpointTableInfo>();
	}
	
	private BranchMap getBranchMapNull(int nodeId, int coreId) {
		for (BranchMap branches : m_branches) {
			if ((branches.getNodeId() == nodeId) &&
				(branches.getCoreId() == coreId)) {
				return branches;
			}
		}
		return null;
	}

	private LabelMap getLabelMapNull(int nodeId, int coreId) {
		for (LabelMap labels : m_labels) {
			if ((labels.getNodeId() == nodeId) &&
				(labels.getCoreId() == coreId)) {
				return labels;
			}
		}
		return null;
	}
	
	private CodePointMap getCodePointMapNull(List<CodePointMap> codePoints, int nodeId, int coreId) {
		for (CodePointMap codePointMap : codePoints) {
			if ((codePointMap.getNodeId() == nodeId) &&
				(codePointMap.getCoreId() == coreId)) {
				return codePointMap;
			}
		}
		return null;
	}

	private SrcLabelTableMap getSrcLabelTableMapNull(int nodeId, int coreId) {
		for (SrcLabelTableMap tableMap : m_srcLabelTables) {
			if ((tableMap.getNodeId() == nodeId) &&
				(tableMap.getCoreId() == coreId)) {
				return tableMap;
			}
		}
		return null;
	}

	private EndpointTableMap getEndpointTableMapNull(int nodeId, int coreId) {
		for (EndpointTableMap tableMap : m_endpointTables) {
			if ((tableMap.getNodeId() == nodeId) &&
				(tableMap.getCoreId() == coreId)) {
				return tableMap;
			}
		}
		return null;
	}

	public void setMonitor(IProgressMonitor monitor) {
		m_jobMonitor = monitor;
	}
}
