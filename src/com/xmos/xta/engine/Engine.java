package com.xmos.xta.engine;

import java.io.File;
import java.util.ArrayList;
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
	private ArrayList<CodeReferenceList> m_exclusions;
	private ArrayList<CodeReferenceList> m_prohibited;
	private ArrayList<CodeReferenceList> m_required;
	private ArrayList<LabelMap> m_labels;
	private ArrayList<BranchMap> m_branches;
	private ArrayList<PathQuery> m_pathQueries;

	private AnalysisEngine m_ae;

	public Engine(Config config) {
		m_xeObjects = new ArrayList<XeObject>();
		m_exclusions = new ArrayList<CodeReferenceList>();
		m_prohibited = new ArrayList<CodeReferenceList>();
		m_required = new ArrayList<CodeReferenceList>();
		m_labels = new ArrayList<LabelMap>();
		m_branches = new ArrayList<BranchMap>();
		m_xeLoader = new XeLoader();
		m_route = null;
		m_previousRoute = null;
		m_config = config;
		m_executableFile = null;
		m_nodeId = 0;
		m_coreId = 0;
		m_srcPaths = ".";
		m_notifiers = new Vector<Notifier>();
		m_pathQueries = new ArrayList<PathQuery>();
		m_abort = false;
		m_progress = new Progress(Progress.State.IDLE, 0, 0);

		CodeReferenceFactory.setEngine(this);
	}

	public boolean addReference(ReferenceMode mode, CodeReference ref) {
		if (!resolveRefernece(ref, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Unresolved reference: %s", ref.getRef()));
			return false;
		}
		if (mode == ReferenceMode.FILTERED) {
			if (referenceListContainsReference(ref, getReferenceList(m_exclusions, m_nodeId, m_coreId))) {
				notify(Notifier.NOTIFY_ERROR, String.format("Reference already filtered: %s", ref.getRef()));
				return false;
			} else {
				getReferenceList(m_exclusions, m_nodeId, m_coreId).add(ref);
			}

		} else if (mode == ReferenceMode.PROHIBITED) {
			if (referenceListContainsReference(ref, getReferenceList(m_prohibited, m_nodeId, m_coreId))) {
				notify(Notifier.NOTIFY_ERROR, String.format("Reference already prohibited: %s", ref.getRef()));
				return false;
			} else {
				getReferenceList(m_prohibited, m_nodeId, m_coreId).add(ref);
			}
		} else if (mode == ReferenceMode.REQUIRED) {
			if (referenceListContainsReference(ref, getReferenceList(m_required, m_nodeId, m_coreId))) {
				notify(Notifier.NOTIFY_ERROR, String.format("Reference already required: %s", ref.getRef()));
				return false;
			} else {
				getReferenceList(m_required, m_nodeId, m_coreId).add(ref);
			}
		} else {
			assert (false);
			return false;
		}
		if (m_route != null)
			m_route.filterPaths();

		notify(Notifier.NOTIFY_UPDATE_EXCLUDES, "");
		return true;
	}

	public boolean removeReference(ReferenceMode mode, CodeReference ref) {
		boolean found = true;

		if (mode == ReferenceMode.FILTERED) {
			List<CodeReference> references = getReferenceList(m_exclusions, m_nodeId, m_coreId);
			found = removeReferences(ref, references);
		} else if (mode == ReferenceMode.PROHIBITED) {
			List<CodeReference> references = getReferenceList(m_prohibited, m_nodeId, m_coreId);
			found = removeReferences(ref, references);
		} else if (mode == ReferenceMode.REQUIRED) {
			List<CodeReference> references = getReferenceList(m_required, m_nodeId, m_coreId);
			found = removeReferences(ref, references);
		} else {
			assert (false);
			return false;
		}
		if (!found) {
			notify(Notifier.NOTIFY_ERROR, String.format("Unable to remove reference: %s", ref.getRef()));
			return false;
		}

		if (m_route != null)
			m_route.filterPaths();

		notify(Notifier.NOTIFY_UPDATE_EXCLUDES, "");
		return true;
	}

	public void notify(String message, String argument) {
		for (Notifier notifier : m_notifiers)
			notifier.notify(message, argument);
	}

	public List<CodeReference> getReference(ReferenceMode mode) {
		if (mode == ReferenceMode.FILTERED) {
			return getReferenceList(m_exclusions, m_nodeId, m_coreId);
		} else if (mode == ReferenceMode.PROHIBITED) {
			return getReferenceList(m_prohibited, m_nodeId, m_coreId);
		} else if (mode == ReferenceMode.REQUIRED) {
			return getReferenceList(m_required, m_nodeId, m_coreId);
		} else {
			assert (false);
			return null;
		}
	}

	public Config getConfig() {
		return m_config;
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
		if (m_route != null) {
			return m_xeLoader.getMemoryImage(m_route.getNodeId(), m_route.getCoreId());
		}
		return null;
	}

	public SymbolTable getSymbolTable() {
		if (m_route != null) {
			return m_xeLoader.getSymbolTable(m_route.getNodeId(), m_route.getCoreId());
		}
		return null;
	}

	public DebugInfo getDebugInfo() {
		if (m_route != null) {
			return m_xeLoader.getDebugInfo(m_route.getNodeId(), m_route.getCoreId());
		}
		return null;
	}

	public Route getRoute() {
		return m_route;
	}

	public XeLoader getXeLoader() {
		return m_xeLoader;
	}

	public XeObject getXeObject() {
		return getXeObject(m_nodeId, m_coreId);
	}

	public String getSrcPaths() {
		return m_srcPaths;
	}

	public void setSrcPaths(String srcPaths) {
		m_srcPaths = srcPaths;
		m_srcPaths += ";.";
	}

	public boolean loadExecutable(File executableFile, FileFinder fileFinder) {
		m_pathQueries.clear();

		XeLoader newLoader = new XeLoader();

		boolean success = newLoader.loadExecutable(executableFile);
		if (success) {

			m_xeLoader = newLoader;
			m_executableFile = executableFile;
			initialiseLists(fileFinder);
			m_nodeCoreInstructionCache = new NodeCoreInstructionCaches(this, m_xeLoader, m_config);

			try {
				decodeInstructions();
			} catch (Exception e) {
				notify(Notifier.NOTIFY_ERROR, "Some of the pc's refered to in the executable could not be found");
				success = false;
			}

			loadLabels();
			loadBranches();

			m_ae = new AnalysisEngine(m_xeLoader, m_nodeCoreInstructionCache, m_xeObjects, m_branches);
			runAnalysisEngine();

			m_route = null;
			notify(Notifier.NOTIFY_LOAD_EXECUTABLE, executableFile.getAbsolutePath());
		} else {
			notify(Notifier.NOTIFY_ERROR, "The file '" + executableFile + "' could not be loaded.");
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
					m_nodeCoreInstructionCache.decode(getOpcode(pc, nodeId, coreId), pc, nodeId, coreId);
				}
			}
		}
	}

	public Progress getProgress() {
		return m_progress;
	}

	public boolean analyseRoute(String functionName) {
		if (!m_xeLoader.isLoaded(m_nodeId, m_coreId)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Node: %d, Core: %d image not found in file: %s", m_nodeId,
					m_coreId, m_executableFile));
			return false;
		} else if (!functionExists(functionName)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Cannot find function: %s in file: %s, node: %d, core: %d",
					functionName, m_executableFile, m_nodeId, m_coreId));
			return false;
		} else {
			m_previousRoute = m_route;
			m_route = new Route(m_config, m_progress, functionName, m_nodeId, m_coreId, m_nodeCoreInstructionCache);

			boolean success = analyseRouteSafley();
			if (success) {
				notify(Notifier.NOTIFY_SELECT_FUNCTION, functionName);
			} else {
				m_route = m_previousRoute;
			}
			return success;
		}
	}

	public boolean analyseRoute(String from, String to) {
		CodeReference fromRef = CodeReferenceFactory.createCodeReference(from);
		CodeReference toRef = CodeReferenceFactory.createCodeReference(to);

		if (!m_xeLoader.isLoaded(m_nodeId, m_coreId)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Node: %d, Core: %d image not found in file: %s", m_nodeId,
					m_coreId, m_executableFile));
			return false;
		} else if (!resolveRefernece(fromRef, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Unresolved reference: %s", from));
			return false;
		} else if (!resolveRefernece(toRef, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Unresolved reference: %s", to));
			return false;
		} else {
			m_previousRoute = m_route;
			m_route = new Route(m_config, m_progress, fromRef, toRef, m_nodeId, m_coreId, m_nodeCoreInstructionCache);

			boolean success = analyseRouteSafley();
			if (success) {
				notify(Notifier.NOTIFY_SET_START_POINT, from);
				notify(Notifier.NOTIFY_SET_END_POINT, to);
			} else {
				m_route = m_previousRoute;
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

	public boolean setLoopIterations(int pathId, boolean absolute, int iterations) {
		if (m_route != null) {
			
			boolean hit = m_route.setLoopIterations(pathId, absolute, iterations);
			if (hit) {
				m_route.invalidateAllBlocks();
				m_route.notifyUpdate();
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
			notify(Notifier.NOTIFY_ERROR, String.format("Unresolved reference: %s", ref.getRef()));
			return false;
		}

		if (m_route != null)
			m_route.invalidateAllBlocks();
		
		boolean hit = m_nodeCoreInstructionCache.setInstructionTimeTaken(this, ref.getPc(), timeTaken, m_nodeId, m_coreId);
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
		return m_nodeCoreInstructionCache.getInstructionTimeTaken(pc, m_nodeId, m_coreId);
	}

	public boolean setInstructionNumCycles(CodeReference ref, int numCycles) {
		if (m_nodeCoreInstructionCache == null) {
			notify(Notifier.NOTIFY_ERROR, "No executable loaded");
			return false;
		}

		if (!resolveRefernece(ref, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Unresolved reference: %s", ref.getRef()));
			return false;
		}

		if (m_route != null)
			m_route.invalidateAllBlocks();
			
		boolean hit = m_nodeCoreInstructionCache.setInstructionNumCycles(this, ref.getPc(), numCycles, m_nodeId, m_coreId);
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
			return null;
		}
		return m_nodeCoreInstructionCache.getPcsWithTimeSet(m_nodeId, m_coreId);
	}

	public boolean updateUnknown(int pc, boolean clear) {
		if (m_route == null)
			return false;

		boolean found = false;
		for (Unknown unknown : m_route.getUnknowns()) {
			if ((unknown.getPc() == pc) && (unknown.getUnknownType() == UnknownType.PAUSABLETIME)) {
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
		return m_nodeCoreInstructionCache.getInstructionNumCycles(pc, m_nodeId, m_coreId);
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

		if (!m_nodeCoreInstructionCache.unsetInstructionTime(ref, m_nodeId, m_coreId)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Unable to unset time for: %s", ref.getRef()));
		}
	}

	public boolean canInstructionPause(int pc) {
		if (m_nodeCoreInstructionCache == null) {
			notify(Notifier.NOTIFY_ERROR, "No executable loaded");
			return false;
		}
		return m_nodeCoreInstructionCache.getInstructionDecodeCache(m_nodeId, m_coreId).getInstruction(pc).canPause();
	}

	public Double getTimeTaken(int lineNumber) {
		return new Double(0.0);
	}

	public boolean addLabel(String label, CodeReference ref) {
		if (!resolveRefernece(ref, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Unresolved reference: %s", ref.getRef()));
			return false;
		}

		// Test whether the label happens to be a PC string
		if (CodeReferenceFactory.isPcString(label)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Cannot use PC as a label: %s", label));
			return false;
		}

		LabelMap labelMap = getLabelsMap(m_nodeId, m_coreId);
		if (labelMap != null) {
			if (!labelMap.getLabels().containsKey(label)) {
				labelMap.getLabels().put(label, ref.getPc());
			} else {
				notify(Notifier.NOTIFY_ERROR, String.format("Label already exists: %s", label));
			}
			return true;
		}
		return false;
	}

	public void removeLabel(String label) {
		LabelMap labelMap = getLabelsMap(m_nodeId, m_coreId);
		if (labelMap != null) {
			List<String> matches = new ArrayList<String>();

			for (String mapEntry : labelMap.getLabels().keySet()) {
				try {
					if (mapEntry.matches(label)) {
						matches.add(mapEntry);
					}
				} catch (PatternSyntaxException e) {
					// Ignore
				}
			}

			if (matches.size() > 0) {
				for (String toRemove : matches)
					labelMap.getLabels().remove(toRemove);
			} else {
				notify(Notifier.NOTIFY_ERROR, String.format("Unable to remove label: %s", label));
			}
		}
	}

	public Map<String, Integer> getLabels() {
		LabelMap labelMap = getLabelsMap(m_nodeId, m_coreId);
		if (labelMap != null) {
			return labelMap.getLabels();
		}
		return null;
	}

	public boolean isLabel(String label) {
		LabelMap labelMap = getLabelsMap(m_nodeId, m_coreId);
		if (labelMap != null) {
			return labelMap.getLabels().containsKey(label);
		}
		return false;
	}

	public int getLabelPc(String label) {
		LabelMap labelMap = getLabelsMap(m_nodeId, m_coreId);
		if (labelMap != null) {
			if (labelMap.getLabels().containsKey(label)) {
				return labelMap.getLabels().get(label);
			}
		}
		assert(false);
		return 0;
	}

	public boolean addBranch(CodeReference fromRef, CodeReference toRef) {
		if (!resolveRefernece(fromRef, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Unresolved reference: %s", fromRef.getRef()));
			return false;
		} else if (!resolveRefernece(toRef, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Unresolved reference: %s", toRef.getRef()));
			return false;
		}

		BranchMap branchMap = getBranchesMap(m_nodeId, m_coreId);
		if (branchMap != null) {
			branchMap.add(fromRef.getPc(), toRef.getPc());
			return true;
		}
		return false;
	}

	public void removeBranch(CodeReference fromRef) {
		if (!resolveRefernece(fromRef, this)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Unresolved reference: %s", fromRef.getRef()));
			return;
		}

		BranchMap branchMap = getBranchesMap(m_nodeId, m_coreId);
		if (branchMap != null) {
			if (branchMap.getBranches().containsKey(fromRef.getPc())) {
				branchMap.remove(fromRef.getPc());
			} else {
				notify(Notifier.NOTIFY_ERROR, String.format("Unable to remove branch: 0x%x", fromRef.getPc()));
			}
		}
	}

	public Map<Integer, TreeSet<Integer>> getBranches() {
		BranchMap branchMap = getBranchesMap(m_nodeId, m_coreId);
		if (branchMap != null) {
			return branchMap.getBranches();
		}
		return null;
	}

	public void addQuery(PathQuery pq) {
		this.m_pathQueries.add(pq);
	}

	public ArrayList<PathQuery> getPathQueries() {
		return m_pathQueries;
	}

	private boolean referenceListContainsReference(CodeReference ref, List<CodeReference> list) {
		for (CodeReference cr : list) {
			if (cr.getRef().equals(ref.getRef())) {
				return true;
			}
		}
		return false;
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
		return m_xeLoader.getMemoryImage(nodeId, coreId).readInstructionWord(pc);
	}

	private boolean analyseRoute() throws Exception {
		assert (m_route != null);

		if (m_progress.getState() != Progress.State.IDLE) {
			notify(Notifier.NOTIFY_ERROR, "Engine busy");
			return false;
		}
		notify(Notifier.NOTIFY_ANALYSIS_BEGIN, "");

		int nodeId = m_route.getNodeId();
		int coreId = m_route.getCoreId();

		if (m_xeLoader.isLoaded(nodeId, coreId)) {
			MemoryImage memoryImage = m_xeLoader.getMemoryImage(nodeId, coreId);
			SymbolTable symbolTable = m_xeLoader.getSymbolTable(nodeId, coreId);
			DebugInfo debugInfo = m_xeLoader.getDebugInfo(nodeId, coreId);

			assert (memoryImage != null);
			assert (symbolTable != null);
			assert (debugInfo != null);
			RouteStatus status = RouteStatus.ABORTED;
			status = m_route.analyse(memoryImage, symbolTable, debugInfo, this);
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

			int numUnresolvedPaths = m_route.unresolvedPaths().size();
			if (numUnresolvedPaths > 0) {
				notify(Notifier.NOTIFY_ERROR, String.format("Found " + numUnresolvedPaths + " unresolved path(s)"));
			}

			notify(Notifier.NOTIFY_ANALYSIS_COMPLETE, "");
		} else {
			notify(Notifier.NOTIFY_ERROR, String.format("Node: %d, Core: %d image not found in file: %s", m_route
					.getNodeId(), m_route.getCoreId(), m_executableFile));
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

	private List<CodeReference> getReferenceList(List<CodeReferenceList> referenceList, int nodeId, int coreId) {
		for (CodeReferenceList references : referenceList) {
			if ((references.getNodeId() == nodeId) && (references.getCoreId() == coreId)) {
				return references.getReferences();
			}
		}
		assert (false);
		return null;
	}

	private boolean removeReferences(CodeReference ref, List<CodeReference> references) {
		List<CodeReference> matches = new ArrayList<CodeReference>();

		for (CodeReference present : references) {
			try {
				if (present.getRef().matches(ref.getRef())) {
					matches.add(present);
				}
			} catch (PatternSyntaxException e) {
				// Ignore
			}
		}

		if (matches.size() > 0) {
			references.removeAll(matches);
			return true;
		}
		return false;
	}

	private LabelMap getLabelsMap(int nodeId, int coreId) {
		for (LabelMap labels : m_labels) {
			if ((labels.getNodeId() == nodeId) && (labels.getCoreId() == coreId)) {
				return labels;
			}
		}
		return null;
	}

	private BranchMap getBranchesMap(int nodeId, int coreId) {
		for (BranchMap branches : m_branches) {
			if ((branches.getNodeId() == nodeId) && (branches.getCoreId() == coreId)) {
				return branches;
			}
		}
		return null;
	}

	private XeObject getXeObject(int nodeId, int coreId) {
		for (XeObject xeObject : m_xeObjects) {
			if ((xeObject.getNodeId() == nodeId) && (xeObject.getCoreId() == coreId)) {
				return xeObject;
			}
		}
		assert (false);
		return null;
	}

	private void initialiseLists(FileFinder fileFinder) {
		m_exclusions.clear();
		m_prohibited.clear();
		m_required.clear();
		m_labels.clear();
		m_branches.clear();
		m_xeObjects.clear();

		for (int node : m_xeLoader.getNodeIds()) {
			for (int core : m_xeLoader.getCoreIds(node)) {
				m_exclusions.add(new CodeReferenceList(node, core));
				m_prohibited.add(new CodeReferenceList(node, core));
				m_required.add(new CodeReferenceList(node, core));
				m_labels.add(new LabelMap(node, core));
				m_branches.add(new BranchMap(node, core));
				m_xeObjects.add(new XeObject(m_xeLoader, node, core, fileFinder));
			}
		}
	}

	private void loadLabels() {
		for (LabelMap labelMap : m_labels) {
			labelMap.loadLabels(this, m_xeLoader);
		}
		return;
	}

	private void loadBranches() {
		for (BranchMap branchMap : m_branches) {
			branchMap.loadBranches(this, m_xeLoader);
		}
		return;
	}

	public File getExecutable() {
		return m_executableFile;
	}

}
