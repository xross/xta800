package com.xmos.xta.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.xmos.xta.engine.Route.RouteStatus;
import com.xmos.xta.engine.constraint.Constraint;
import com.xmos.xta.engine.maps.BranchMap;
import com.xmos.xta.engine.maps.LabelMap;
import com.xmos.xta.engine.maps.SrcLabelMap;
import com.xmos.xta.engine.maps.SrcLabelInfo;
import com.xmos.xta.engine.maps.SrcTableInfo;
import com.xmos.xta.engine.maps.SrcTableMap;
import com.xmos.xta.engine.references.CodeReference;
import com.xmos.xta.engine.references.CodeReferenceFactory;
import com.xmos.xta.engine.Unknown.UnknownType;

public class Engine {

	public enum ReferenceMode {
		FILTERED, PROHIBITED, REQUIRED
	}

	private XeLoader m_xeLoader;
	private Route m_route;
	private Route m_previousRoute;
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

	public Engine(Config config) {
		m_xeObjects = new ArrayList<XeObject>();
		m_branches = new ArrayList<BranchMap>();
		m_labels = new ArrayList<LabelMap>();
		m_srcLabels = new ArrayList<SrcLabelMap>();
		m_srcLabelTables = new ArrayList<SrcTableMap>();
		m_endpoints = new ArrayList<SrcLabelMap>();
		m_endpointTables = new ArrayList<SrcTableMap>();
		m_xeLoader = new XeLoader();
		m_route = null;
		m_previousRoute = null;
		m_config = config;
		m_executableFile = null;
		m_nodeId = 0;
		m_coreId = 0;
		m_srcPaths = ".";
		m_notifiers = new Vector<Notifier>();
		m_abort = false;
		m_progress = new Progress(Progress.State.IDLE, 0, 0);
		m_constraint = null;

		CodeReferenceFactory.setEngine(this);
	}

	public void notify(String message, String argument) {
		for (Notifier notifier : m_notifiers)
			notifier.notify(message, argument);
	}

	public Config getConfig() {
		return m_config;
	}

	public Vector<MemoryImage> getMemoryImages() {
		if (m_xeLoader != null)
			return m_xeLoader.getMemoryImages();
		else {
			System.err.println("xeLoader is missing: cannot getMemoryImages()");
			return new Vector<MemoryImage>();
		}
	}

	public Vector<SymbolTable> getSymbolTables() {
		if (m_xeLoader != null)
			return m_xeLoader.getSymbolTables();
		else {
			System.err.println("xeLoader is missing: cannot getSymbolTables()");
			return new Vector<SymbolTable>();
		}
	}

	public Vector<DebugInfo> getDebugInfos() {
		if (m_xeLoader != null)
			return m_xeLoader.getDebugInfos();
		else {
			System.err.println("xeLoader is missing: cannot getDebugInfos()");
			return new Vector<DebugInfo>();
		}
	}

	public boolean anythingToDisplay() {
		if (m_route != null) {
			if (m_route.getCompletedPaths().size() + m_route.getUnresolvedPaths().size() > 0) {
				return true;
			}
		}
		return false;
	}

	public MemoryImage getMemoryImage() {
		if (m_route != null) {
			return m_xeLoader.getMemoryImage(m_nodeId, m_coreId);
		}
		return null;
	}

	public SymbolTable getSymbolTable() {
		if (m_route != null) {
			return m_xeLoader.getSymbolTable(m_nodeId, m_coreId);
		}
		return null;
	}

	public DebugInfo getDebugInfo() {
		if (m_route != null) {
			return m_xeLoader.getDebugInfo(m_nodeId, m_coreId);
		}
		return null;
	}

	public Route getRoute() {
		// FIXME what happens if this is null
		return m_route;
	}

	public XeLoader getXeLoader() {
		// FIXME what happens if this is null
		return m_xeLoader;
	}

	public XeObject getXeObject() {
		// FIXME what happens if this is null
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

	public boolean loadExecutable(File executableFile, FileFinder fileFinder) {
		XeLoader newLoader = new XeLoader();

		boolean success = newLoader.loadExecutable(executableFile);
		if (success) {

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

			m_route = null;

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
			return analyseRoute(startPc, endPcs);
		}
	}

	public boolean analyseRoute(String from, String to) {
		CodeReference fromRef = CodeReferenceFactory.createEndpointReference(from);
		CodeReference toRef = CodeReferenceFactory.createEndpointReference(to);
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
			return analyseRoute(fromRef.getPc(), endPc);
		}
	}

	public boolean analyseRoute(int startPc, TreeSet<Integer> endPcs) {
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

		m_previousRoute = m_route;
		
		m_route = new Route(startPc, endPcs, m_ae.getNodeArray(m_nodeId, m_coreId), m_nodeCoreInstructionCache.getInstructionDecodeCache(m_nodeId, m_coreId));

		boolean success = analyseRouteSafley();
		if (success) {
			notify(Notifier.NOTIFY_SET_START_POINT, "0x"
					+ Integer.toHexString(startPc));
			notify(Notifier.NOTIFY_SET_END_POINT, endString);
			// may be able to scrap the m_previousRoute here. free a lot of
			// memory
		} else {
			m_route = m_previousRoute;
		}
		return success;

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
		notify(Notifier.NOTIFY_UPDATE_STATE, Notifier.STATE_NODE_ID);
	}

	public int getNodeId() {
		return m_nodeId;
	}

	public void setCoreId(int coreId) {
		m_coreId = coreId;
		notify(Notifier.NOTIFY_UPDATE_STATE, Notifier.STATE_CORE_ID);
	}

	public int getCoreId() {
		return m_coreId;
	}

	public void setFrequency(int frequency) {
		m_config.m_frequency = frequency;
		if (m_route != null) {
			m_route.invalidateAllBlocks();
		}
		notify(Notifier.NOTIFY_UPDATE_STATE, Notifier.STATE_FREQUENCY);
	}

	public int getFrequency() {
		return m_config.m_frequency;
	}

	public void setNumThreads(int numThreads) {
		m_config.m_numThreads = numThreads;
		if (m_route != null) {
			m_route.invalidateAllBlocks();
		}
		notify(Notifier.NOTIFY_UPDATE_STATE, Notifier.STATE_NUM_THREADS);
	}

	public int getNumThreads() {
		return m_config.m_numThreads;
	}

	public boolean setLoopIterations(int pathId, boolean absolute,
			int iterations) {
		if (this.anythingToDisplay()) {
			boolean hit = m_route.setLoopIterations(pathId, absolute,
					iterations);
			if (hit) {
				m_route.invalidateAllBlocks();
				//m_route.notifyUpdate();
				notify(Notifier.NOTIFY_UPDATE_STATE, "");
			}
			return hit;
		}
		return false;
	}

	public boolean setInstructionTimeTaken(CodeReference ref, double timeTaken) {
		if (m_nodeCoreInstructionCache == null) {
			notify(Notifier.NOTIFY_ERROR, "No executable loaded");
			return false;
		}

		if (!resolveRefernece(ref, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Unresolved reference: %s", ref.getRef()));
			return false;
		}

		if (m_route != null)
			m_route.invalidateAllBlocks();

		boolean hit = m_nodeCoreInstructionCache.setInstructionTimeTaken(this,
				ref.getPc(), timeTaken, m_nodeId, m_coreId);
		if (hit) {
			boolean unknownsChanged = updateUnknown(ref.getPc(), true);
			if (unknownsChanged)
				notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");

			notify(Notifier.NOTIFY_UPDATE_STATE, "");
		}

		return hit;
	}

	public double getInstructionTimeTaken(int pc) {
		if (m_nodeCoreInstructionCache == null) {
			notify(Notifier.NOTIFY_ERROR, "No executable loaded");
			return 0;
		}
		return m_nodeCoreInstructionCache.getInstructionTimeTaken(pc, m_nodeId,
				m_coreId);
	}

	public boolean setInstructionNumCycles(CodeReference ref, int numCycles) {
		if (m_nodeCoreInstructionCache == null) {
			notify(Notifier.NOTIFY_ERROR, "No executable loaded");
			return false;
		}

		if (!resolveRefernece(ref, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Unresolved reference: %s", ref.getRef()));
			return false;
		}

		if (this.anythingToDisplay())
			m_route.invalidateAllBlocks();

		boolean hit = m_nodeCoreInstructionCache.setInstructionNumCycles(this,
				ref.getPc(), numCycles, m_nodeId, m_coreId);
		if (hit) {
			boolean unknownsChanged = updateUnknown(ref.getPc(), true);
			if (unknownsChanged)
				notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");

			notify(Notifier.NOTIFY_UPDATE_STATE, "");
		}

		return hit;
	}

	public ArrayList<Integer> getPcsWithTimeSet() {
		if (m_nodeCoreInstructionCache == null) {
			notify(Notifier.NOTIFY_ERROR, "No executable loaded");
			return new ArrayList<Integer>();
		}
		return m_nodeCoreInstructionCache.getPcsWithTimeSet(m_nodeId, m_coreId);
	}

	public boolean updateUnknown(int pc, boolean clear) {
		if (!this.anythingToDisplay())
			return false;

		boolean found = false;
		for (Unknown unknown : m_route.getUnknowns()) {
			if ((unknown.getPc() == pc)
					&& (unknown.getUnknownType() == UnknownType.PAUSABLETIME)) {
				unknown.setIsSet(clear);
				found = true;
			}
		}
		return found;
	}

	public int getInstructionNumCycles(int pc) {
		if (m_nodeCoreInstructionCache == null) {
			notify(Notifier.NOTIFY_ERROR, "No executable loaded");
			return 0;
		}
		return m_nodeCoreInstructionCache.getInstructionNumCycles(pc, m_nodeId,
				m_coreId);
	}

	public void unsetInstructionTime(int pc) {
		if (m_nodeCoreInstructionCache == null) {
			notify(Notifier.NOTIFY_ERROR, "No executable loaded");
			return;
		}
		m_nodeCoreInstructionCache.unsetInstructionTime(pc, m_nodeId, m_coreId);
	}

	public void unsetInstructionTime(CodeReference ref) {
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

	public boolean addBranch(CodeReference fromRef, CodeReference toRef) {
		return addToBranchMap(fromRef, toRef);
	}

	public void removeBranch(CodeReference fromRef) {
		removeFromBranchMap(fromRef);
	}

	public Map<Integer, TreeSet<Integer>> getBranches() {
		return getBranchMap();
	}

	public boolean addLabel(String label, CodeReference ref) {
		return addToLabelMap(label, ref);
	}

	public void removeLabel(String label) {
		removeFromLabelMap(label);
	}

	public Map<String, Integer> getLabels() {
		return getLabelMap();
	}
	
	public boolean addSrcLabel(String label, CodeReference ref) {
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
	
	public boolean addEndpoint(String endpoint, CodeReference ref) {
		return addToSrcLabelMap(m_endpoints, m_endpointTables, endpoint, ref);
	}

	public void removeEndpoint(String endpoint) {
		removeFromSrcLabelMap(m_endpoints, endpoint);
	}
	
	public Map<String, SrcLabelInfo> getEndpoints() {
		return getSrcLabelMap(m_endpoints);
	}
	
	public Map<Integer, SrcTableInfo> getEndpointTable() {
		return getSrcLabelTableMap(m_endpointTables);
	}
	
	public File getExecutable() {
		return m_executableFile;
	}

	private boolean analyseRouteSafley() {
		boolean success = false;
		try {
			success = analyseRoute();
		} catch (final java.lang.OutOfMemoryError e) {
			m_route = null;
			m_progress.setState(Progress.State.IDLE);
			System.gc();
			notify(Notifier.NOTIFY_ERROR, "Out of Memory (Heap)");
			return false;
		} catch (final java.lang.StackOverflowError e) {
			m_route = null;
			m_progress.setState(Progress.State.IDLE);
			System.gc();
			notify(Notifier.NOTIFY_ERROR, "Out of Memory (Stack)");
			return false;
		} catch (Exception e) {
			m_progress.setState(Progress.State.IDLE);
			notify(Notifier.NOTIFY_ERROR, "Unknown error detected");
			e.printStackTrace();
			return false;
		}
		return success;
	}

	private int getOpcode(int pc, int nodeId, int coreId) throws Exception {
		return m_xeLoader.getMemoryImage(nodeId, coreId)
				.readInstructionWord(pc);
	}

	private boolean analyseRoute() throws Exception {
		assert (m_route != null);

		if (m_progress.getState() != Progress.State.IDLE) {
			notify(Notifier.NOTIFY_ERROR, "Engine busy");
			return false;
		}
		notify(Notifier.NOTIFY_ANALYSIS_BEGIN, "");

		if (m_xeLoader.isLoaded(m_nodeId, m_coreId)) {

			RouteStatus status = RouteStatus.ABORTED;

			status = m_route.analyse();

			switch (status) {
			case ABORTED:
				notify(Notifier.NOTIFY_ERROR, "Abort detected");
				m_route = m_previousRoute;
				return false;

			case ERROR:
				notify(Notifier.NOTIFY_ERROR, "Cannot analyse path");
				m_route = m_previousRoute;
				return false;
			}

			int numUnresolvedPaths = m_route.getUnresolvedPaths().size();
			if (numUnresolvedPaths > 0) {
				notify(Notifier.NOTIFY_ERROR, String.format("Found "
						+ numUnresolvedPaths + " unresolved path(s)"));
			}

			notify(Notifier.NOTIFY_ANALYSIS_COMPLETE, "");
		} else {
			notify(Notifier.NOTIFY_ERROR, String
					.format("Node: %d, Core: %d image not found in file: %s",
							m_nodeId, m_coreId,
							m_executableFile));
			return false;
		}

		return true;
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

	private boolean resolveRefernece(CodeReference ref, Engine engine) {
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
			
	private boolean addToBranchMap(CodeReference fromRef, CodeReference toRef) {
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

	private void removeFromBranchMap(CodeReference fromRef) {
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
	
	private boolean addToLabelMap(String label, CodeReference ref) {
		if (!resolveRefernece(ref, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Unresolved reference: %s", ref.getRef()));
			return false;
		}
		
		if (CodeReferenceFactory.isPcString(label)) {
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
			String label, CodeReference ref) {
		if (!resolveRefernece(ref, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Unresolved reference: %s", ref.getRef()));
			return false;
		}

		if (CodeReferenceFactory.isPcString(label)) {
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

	public void setConstraint(Constraint constraint) {
		m_constraint = constraint;
	}

	public Constraint getConstraint() {
		return m_constraint;
	}
}
