package com.xmos.xta.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.PatternSyntaxException;
import com.xmos.elfhandler.ElfHandler;
import com.xmos.elfhandler.Symbol;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeloader.XeLoader;
import com.xmos.xeobject.AssemblyLine;
import com.xmos.xeobject.FileFinder;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.engine.Route.RouteMode;
import com.xmos.xta.engine.Route.RouteStatus;
import com.xmos.xta.engine.constraint.Constraint;
import com.xmos.xta.engine.constraint.ConstraintFactory;
import com.xmos.xta.engine.maps.BranchMap;
import com.xmos.xta.engine.maps.LabelMap;
import com.xmos.xta.engine.maps.SrcLabelMap;
import com.xmos.xta.engine.maps.SrcLabelInfo;
import com.xmos.xta.engine.maps.SrcTableInfo;
import com.xmos.xta.engine.maps.SrcTableMap;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.ReferenceFactory.ReferenceClass;

public class Engine {

	public enum ReferenceMode {
		FILTERED, PROHIBITED, REQUIRED
	}

	private XeLoader m_xeLoader;
	private List<Route> m_routes;
	private Config m_config;
	private File m_executableFile;
	private int m_nodeId;
	private int m_coreId;
	private Vector<Notifier> m_notifiers;
	private NodeCoreInstructionCaches m_nodeCoreInstructionCache;
	private boolean m_abort;
	private Progress m_progress;
	private String m_srcPaths;
	private ArrayList<XeObject> m_xeObjects;
	private ArrayList<BranchMap> m_branches;
	private ArrayList<LabelMap> m_labels;
	private ArrayList<SrcLabelMap> m_srcLabels;
	private ArrayList<SrcTableMap> m_srcLabelTables;
	private ArrayList<SrcLabelMap> m_endpoints;
	private ArrayList<SrcTableMap> m_endpointTables;
	private AnalysisEngine m_ae;
	private Constraint m_constraint;
	private InstructionMnemonics m_mnenonics;

	public Engine(Config config) {
		m_xeObjects = new ArrayList<XeObject>();
		m_branches = new ArrayList<BranchMap>();
		m_labels = new ArrayList<LabelMap>();
		m_srcLabels = new ArrayList<SrcLabelMap>();
		m_srcLabelTables = new ArrayList<SrcTableMap>();
		m_endpoints = new ArrayList<SrcLabelMap>();
		m_endpointTables = new ArrayList<SrcTableMap>();
		m_xeLoader = new XeLoader();
		m_routes = new LinkedList<Route>();
		m_config = config;
		m_executableFile = null;
		m_nodeId = 0;
		m_coreId = 0;
		m_srcPaths = ".";
		m_notifiers = new Vector<Notifier>();
		m_abort = false;
		m_progress = new Progress(Progress.State.IDLE, 0, 0);
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
		if (routeId == -1)
			return m_routes.get(m_routes.size() - 1);

		for (Route route : m_routes) {
			if (route.getRouteId() == routeId) {
				return route;
			}
		}		
		return null;
	}
	
	public boolean loadExecutable(File executableFile, FileFinder fileFinder) {
		XeLoader newLoader = new XeLoader();

		boolean success = newLoader.loadExecutable(executableFile);
		if (success) {
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
				notify(Notifier.NOTIFY_ERROR,
						"Some of the pc's refered to in the executable could not be found");
				success = false;
			}

			loadBranchMaps();
			loadLabelMaps();
			loadSrcLabelMaps(m_srcLabels);
			loadSrcLabelTables(m_srcLabelTables);
			loadSrcLabelMaps(m_endpoints);
			loadSrcLabelTables(m_endpointTables);

			m_ae = new AnalysisEngine(m_xeLoader, m_nodeCoreInstructionCache,
					m_xeObjects, m_branches);

			runAnalysisEngine();

			m_routes.clear();
			Route.resetRouteCount();

			notify(Notifier.NOTIFY_LOAD_EXECUTABLE, executableFile
					.getAbsolutePath());
		} else {
			notify(Notifier.NOTIFY_ERROR, "The file '" + executableFile
					+ "' could not be loaded.");
		}
		return success;
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
			return new Progress(Progress.State.IDLE, 0, 0);
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
			TreeSet<Integer> endPcs = m_ae.getFunctionRetuns(functionName, m_nodeId, m_coreId);
			int startPc = m_ae.getFunctionStartPc(functionName, m_nodeId, m_coreId);
			String routeDescription = String.format("function: %s", functionName);
			return analyseRoute(startPc, endPcs, routeDescription, RouteMode.FUNCTION);
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
			String routeDescription = String.format("path: %s to %s", from, to);
			

			boolean success = analyseRoute(fromRef.getPc(), endPc, routeDescription, RouteMode.ABSLOUTE);
			if (success) {
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

	public void setAbort(boolean value) {
		m_abort = value;
	}

	public boolean getAbort() {
		return m_abort;
	}

	public void setNodeId(int nodeId) {
		m_nodeId = nodeId;
		notify(Notifier.NOTIFY_LOAD_EXECUTABLE, Notifier.STATE_NODE_ID);
	}

	public int getNodeId() {
		return m_nodeId;
	}

	public void setCoreId(int coreId) {
		m_coreId = coreId;
		notify(Notifier.NOTIFY_LOAD_EXECUTABLE, Notifier.STATE_NODE_ID);
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

	public boolean setLoopIterations(int routeId, int pathId, boolean absolute, int iterations) {
		Route route = getRoute(routeId);		
		if (route != null) {
			boolean hit = route.setLoopIterations(pathId, absolute, iterations);
			if (hit) {
				invalidateRouteStats();
				notify(Notifier.NOTIFY_UPDATE_STATE, "");
			}
			return hit;
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

		invalidateRouteStats();

		if (m_nodeCoreInstructionCache.setInstructionTimeTaken(
				this, ref.getPc(), timeTaken, m_nodeId, m_coreId)) {
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

	public boolean setInstructionNumCycles(Reference ref, int numCycles) {
		if (m_nodeCoreInstructionCache == null) {
			notify(Notifier.NOTIFY_ERROR, "No executable loaded");
			return false;
		}

		if (!resolveRefernece(ref, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Unresolved reference: %s", ref.getRef()));
			return false;
		}

		invalidateRouteStats();

		if (m_nodeCoreInstructionCache.setInstructionNumCycles(
				this, ref.getPc(), numCycles, m_nodeId, m_coreId)) {
			notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
            return true;			
		}		
		return false;
	}

	public ArrayList<Integer> getPcsWithTimeSet() {
		if (m_nodeCoreInstructionCache == null) {
			notify(Notifier.NOTIFY_ERROR, "No executable loaded");
			return new ArrayList<Integer>();
		}
		return m_nodeCoreInstructionCache.getPcsWithTimeSet(m_nodeId, m_coreId);
	}

	public int getInstructionNumCycles(int pc) {
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
		m_nodeCoreInstructionCache.unsetInstructionTime(pc, m_nodeId, m_coreId);
	}

	public void unsetInstructionTime(Reference ref) {
		if (m_nodeCoreInstructionCache == null) {
			notify(Notifier.NOTIFY_ERROR, "No executable loaded");
			return;
		}

		if (!m_nodeCoreInstructionCache.unsetInstructionTime(ref, m_nodeId,
				m_coreId)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Unable to unset time for: %s", ref.getRef()));
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
			return true;
		}
		return false;
	}

	public boolean setRouteMaxCycles(int routeId, int cycles) {
		Route route = getRoute(routeId);
		if (route != null) {
			route.getRouteTiming().setMaxCycles(cycles);
			return true;
		}
		return false;		
	}
	
	public boolean unsetRouteMax(int routeId) {
		Route route = getRoute(routeId);
		if (route != null) {
			route.getRouteTiming().resetMaxTime();
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
			}			
			return true;
		}
		return false;				
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
		return addToSrcLabelMap(m_srcLabels, m_srcLabelTables, label, ref);
	}

	public void removeSrcLabel(String label) {
		removeFromSrcLabelMap(m_srcLabels, label);
	}

	public Map<String, SrcLabelInfo> getSrcLabels() {
		return getSrcLabelMap(m_srcLabels);
	}

	public Map<Integer, SrcTableInfo> getSrcLabelTable() {
		return getSrcLabelTableMap(m_srcLabelTables);
	}
	
	public boolean addEndpoint(String endpoint, Reference ref) {
		return addToSrcLabelMap(m_endpoints, m_endpointTables, endpoint, ref);
	}

	public void removeEndpoint(String endpoint) {
		removeFromSrcLabelMap(m_endpoints, endpoint);
	}
	
	public Map<String, SrcLabelInfo> getEndpoints() {
		return getSrcLabelMap(m_endpoints);
	}
	
	public SrcTableInfo getEndpoint(int pc) {
		Map<Integer, SrcTableInfo> endPointTable = getEndpointTable();
		if (endPointTable.containsKey(pc)) {
			return endPointTable.get(pc);
		}
		return null;
	}
	
	public Map<Integer, SrcTableInfo> getEndpointTable() {
		return getSrcLabelTableMap(m_endpointTables);
	}
	
	public File getExecutable() {
		return m_executableFile;
	}

	public void setConstraint(Constraint constraint) {
		assert(constraint != null);
		if (constraint==null){
			m_constraint = ConstraintFactory.createConstraint(this, "");
		} else {
			m_constraint = constraint;
		}
		notify(Notifier.NOTIFY_SET_CONSTRAINT, "");
	}

	public Constraint getConstraint() {
		return m_constraint;
	}
		
	private boolean analyseRoute(int startPc, TreeSet<Integer> endPcs, String routeDescription, RouteMode mode) {
		boolean reachable = false;
		for (int end : endPcs) {
			int dist = m_ae.determineReachability(startPc, end, m_nodeId,
					m_coreId);
			if (dist != Integer.MAX_VALUE) {
				reachable = true;
				break;
			}
		}
		String endString = "";
		if (!reachable) {
			for (int end : endPcs) {
				endString += "0x" + Integer.toHexString(end) + ", ";
			}
			if(endString.length() > 2){
				endString = endString.substring(0, endString.length() - 2);
				notify(Notifier.NOTIFY_ERROR, "No valid of execution from 0x"
						+ Integer.toHexString(startPc) + " to " + endString);
			} else {
				notify(Notifier.NOTIFY_ERROR, "No valid of execution from 0x"
						+ Integer.toHexString(startPc) );
			}			
			return false;
		}
		
		Route route = new Route(m_config, this, startPc, endPcs, m_ae.getNodeArray(m_nodeId, m_coreId), 
				m_nodeCoreInstructionCache.getInstructionDecodeCache(m_nodeId, m_coreId),
				m_mnenonics, routeDescription);

		boolean success = analyseRouteSafley(route, mode);
		if (success) {
			m_routes.add(route);
		}
		return success;
	}
	
	private boolean analyseRouteSafley(Route route, RouteMode mode) {
		boolean success = false;
		try {
			success = analyseRoute(route, mode);
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
			m_progress.setState(Progress.State.IDLE);
			notify(Notifier.NOTIFY_ERROR, "Unknown error detected");
			return false;
		}
		return success;
	}

	private boolean analyseRoute(Route route, RouteMode mode) throws Exception {
		if (m_progress.getState() != Progress.State.IDLE) {
			notify(Notifier.NOTIFY_ERROR, "Engine busy");
			return false;
		}
		notify(Notifier.NOTIFY_ANALYSIS_BEGIN, "");

		if (m_xeLoader.isLoaded(m_nodeId, m_coreId)) {
			route.isConstrained(false);
			RouteStatus status = route.analyse(mode);
			switch (status) {
			case ABORTED:
				setAbort(false);
				notify(Notifier.NOTIFY_ERROR, "Abort detected");
				return false;

			case ERROR:
				notify(Notifier.NOTIFY_ERROR, "Cannot analyse path");
				return false;
			}

			int numUnresolvedPaths = route.getUnresolvedPaths().size();
			if (numUnresolvedPaths > 0) {
				notify(Notifier.NOTIFY_ERROR, String.format("Found "
						+ numUnresolvedPaths + " unresolved path(s)"));
				
			} else if (route.getCompletedPaths().size() == 0){
				if (route.wasConstrained()){
					notify(Notifier.NOTIFY_ERROR, "No valid path of execution (constraints applied).");
				} else {
					notify(Notifier.NOTIFY_ERROR, "No valid path of execution (no constraints applied).");
				}
			}

			notify(Notifier.NOTIFY_ANALYSIS_COMPLETE, "");
		} else {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Node: %d, Core: %d image not found in file: %s",
					m_nodeId, m_coreId, m_executableFile));
			return false;
		}
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
				m_srcLabels.add(new SrcLabelMap(node, core, ".xtalabel"));
				m_srcLabelTables.add(new SrcTableMap(node, core, ".xtalabeltable"));
				m_endpoints.add(new SrcLabelMap(node, core, ".xtaendpoint"));
				m_endpointTables.add(new SrcTableMap(node, core, ".xtaendpointtable"));
				m_xeObjects.add(new XeObject(m_xeLoader, node, core, fileFinder));
			}
		}
	}

	private void loadBranchMaps() {
		for (BranchMap branchMap : m_branches) {
			branchMap.load(this, m_xeLoader);
		}
	}
	
	private void loadLabelMaps() {
		for (LabelMap labelMap : m_labels) {
			labelMap.load(this, m_xeLoader);
		}		
	}

	private void loadSrcLabelMaps(List<SrcLabelMap> labelMaps) {
		for (SrcLabelMap labelMap : labelMaps) {
			labelMap.load(this, m_xeLoader);
		}
	}
	
	private void loadSrcLabelTables(List<SrcTableMap> tableMaps) {
		for (SrcTableMap tableMap : tableMaps) {
			tableMap.load(this, m_xeLoader);
		}
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
	
	private boolean addToSrcLabelMap(List<SrcLabelMap> labelMaps, List<SrcTableMap> tableMaps,
			String label, Reference ref) {
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
		
		SrcLabelMap labelMap = getSrcLabelMapNull(labelMaps, m_nodeId, m_coreId);
		if (labelMap != null) {
			if (!labelMap.getMap().containsKey(label)) {										
				labelMap.getMap().put(label, new SrcLabelInfo(ref.getPc(), label, "", "", 0));
			} else {
				notify(Notifier.NOTIFY_ERROR, String.format(
						"Label already exists: %s", label));
			}
			return true;
		}
		return false;
	}

	private void removeFromSrcLabelMap(List<SrcLabelMap> labelMaps, String label) {
		SrcLabelMap labelMap = getSrcLabelMapNull(labelMaps, m_nodeId, m_coreId);
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

	private Map<String, SrcLabelInfo> getSrcLabelMap(List<SrcLabelMap> labelMaps) {
	    SrcLabelMap labelMap = getSrcLabelMapNull(labelMaps, m_nodeId, m_coreId);
	    if (labelMap != null) {
		    return labelMap.getMap();
	    }
	    return new HashMap<String, SrcLabelInfo>();
	}
	
	private Map<Integer, SrcTableInfo> getSrcLabelTableMap(List<SrcTableMap> tableMaps) {
		SrcTableMap tableMap = getSrcLabelTableMapNull(tableMaps, m_nodeId, m_coreId);
		if (tableMap != null) {
			return tableMap.getMap();
		}
		return new HashMap<Integer, SrcTableInfo>();
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
	
	private SrcLabelMap getSrcLabelMapNull(List<SrcLabelMap> labels, int nodeId, int coreId) {
		for (SrcLabelMap labelMap : labels) {
			if ((labelMap.getNodeId() == nodeId) &&
				(labelMap.getCoreId() == coreId)) {
				return labelMap;
			}
		}
		return null;
	}

	private SrcTableMap getSrcLabelTableMapNull(List<SrcTableMap> tables, int nodeId, int coreId) {
		for (SrcTableMap tableMap : tables) {
			if ((tableMap.getNodeId() == nodeId) &&
				(tableMap.getCoreId() == coreId)) {
				return tableMap;
			}
		}
		return null;
	}	
}
