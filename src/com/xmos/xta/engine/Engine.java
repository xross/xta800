package com.xmos.xta.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import org.eclipse.core.runtime.IProgressMonitor;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.analysis.controlflow.ControlFlowGraph;
import com.xmos.xta.engine.analysis.controlflow.FunctionHandler;
import com.xmos.xta.engine.analysis.controlflow.InstructionDecoder;
import com.xmos.xta.engine.analysis.controlflow.SuccessorAdder;
import com.xmos.xta.engine.analysis.controlflow.ThreadHandler;
import com.xmos.xta.engine.analysis.controlflow.UnreachableCodeFinder;
import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.StructuralAnalysisEngine;
import com.xmos.xta.engine.analysis.structural.StructureGraph;
import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.routes.RouteConstructor;
import com.xmos.xta.engine.analysis.routes.RouteDescription;
import com.xmos.xta.engine.analysis.routes.RouteTiming;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.types.ReferenceTypeFactory;
import com.xmos.xta.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.engine.sections.CodePointInfo;
import com.xmos.xta.engine.sections.CodePointTableInfo;
import com.xmos.xta.engine.sections.Sections;
import com.xmos.xta.engine.sections.SrcLabelTableInfo;
import com.xmos.xta.sourcemapper.FileFinder;
import com.xmos.xta.sourcemapper.SourceMapper;

public class Engine implements Notifier {
	public static final int ROUTE_ID_LAST = -1;
	public static final int ROUTE_ID_INVALID = -2;
	public static final int ROUTE_ID_GLOBAL = -3;
	
	private XeLoader m_xeLoader;
	private FileFinder m_fileFinder;
	private List<Route> m_routes;
	private Collection<Reference> m_exclusions;
	private Route m_selectedRoute;
	private Config m_config;
	private File m_executableFile;
	private int m_nodeId;
	private int m_coreId;
	private Notifier m_notifier;	
	private Progress m_progress;
	private IProgressMonitor m_jobMonitor;
	private String m_srcPaths;
	private Sections m_sections;
	private Collection<String> m_functionsVisited;
	
	private NodeCoreCollection<SourceMapper> m_sourceMapper;
	private NodeCoreCollection<StructureGraph> m_structuralGraph;
	private NodeCoreCollection<ControlFlowGraph> m_controlFlowGraph;
	private NodeCoreCollection<InstructionDecoder> m_instructionDecoder;
	private NodeCoreCollection<TreeMap<String, InstructionNode>> m_functionNames;
	
	public Engine(Config config) {		
		m_sourceMapper = new NodeCoreCollection<SourceMapper>();
		m_structuralGraph = new NodeCoreCollection<StructureGraph>();
		m_controlFlowGraph = new NodeCoreCollection<ControlFlowGraph>();
		m_instructionDecoder = new NodeCoreCollection<InstructionDecoder>();
		m_functionNames = new NodeCoreCollection<TreeMap<String, InstructionNode>>();
		m_xeLoader = new XeLoader();
		m_routes = new LinkedList<Route>();
		m_exclusions = new ArrayList<Reference>();
		m_config = config;
		m_executableFile = null;
		m_fileFinder = null;
		m_nodeId = 0;
		m_coreId = 0;
		m_srcPaths = ".";
		m_progress = new Progress(Progress.State.IDLE, 0);
		m_jobMonitor = null;
		m_sections = new Sections();

		ReferenceTypeFactory.setEngine(this);
	}

	public Set<String> getFunctionNames(int nodeId, int coreId){
		return m_functionNames.get(nodeId, coreId).keySet();
	}
	
	public void notify(String message, String argument) {
		if (m_notifier != null)
			m_notifier.notify(message, argument);
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

	public SourceMapper getSourceMapper() {
		return m_sourceMapper.get(m_nodeId, m_coreId);
	}
	
	public ControlFlowGraph getControlFlowGraph() {
		return m_controlFlowGraph.get(m_nodeId, m_coreId);
	}
	
	public StructureGraph getStructureGraph() {
		return m_structuralGraph.get(m_nodeId, m_coreId);
	}
	
	public InstructionDecoder getInstructionDecoder() {
		return m_instructionDecoder.get(m_nodeId, m_coreId);
	}
	
	public Set<String> getFunctionNames() {
		return m_functionNames.get(m_nodeId, m_coreId).keySet();
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
    	try {
    		executableFile = executableFile.getCanonicalFile();
		} catch (IOException e) {
			notify(Notifier.NOTIFY_ERROR, String.format("Error: Failed to resolve executable path: %s\n", executableFile.getName()));
			return false;			
		}		
		
		XeLoader xeLoader = new XeLoader();
		if (!xeLoader.loadExecutable(executableFile)) {
			notify(Notifier.NOTIFY_ERROR, "The file '" + executableFile + "' could not be loaded.");
			return false;			
		}
		
		Sections sections = new Sections();
		if (!sections.load(xeLoader, this)) {
			return false;
		} 
		
		if (!initialiseGlobalStructure(xeLoader, sections, fileFinder))
			return false;
		
		m_executableFile = executableFile;
		m_fileFinder = fileFinder;
		m_xeLoader = xeLoader;
		m_sections = sections;
		m_nodeId = 0;
		m_coreId = 0;			
		m_routes.clear();
		m_selectedRoute = null;
		
		notify(Notifier.NOTIFY_LOAD_EXECUTABLE, executableFile.getAbsolutePath());		
		return true;
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
			RouteDescription routeDescription = new RouteDescription(functionName);		
			return analyseRouteSafely(routeDescription);
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
		} else if (!resolveRefernece(fromRef)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Unresolved reference: %s", from));
			return false;
		} else if (!resolveRefernece(toRef)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Unresolved reference: %s", to));
			return false;
		} else {		
			RouteDescription routeDescription = new RouteDescription(fromRef, toRef);
			return analyseRouteSafely(routeDescription);
		}
	}
	
	private boolean analyseRouteSafely(RouteDescription routeDescription) {				
		boolean success = false;
		try {
			success = doRouteAnalysis(routeDescription);
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

	private boolean doRouteAnalysis(RouteDescription routeDescription) throws Exception {	
		if (m_progress.getState() != Progress.State.IDLE) {
			notify(Notifier.NOTIFY_ERROR, "Engine busy");
			return false;
		}
		if (!m_xeLoader.isLoaded(m_nodeId, m_coreId)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Node: %d, Core: %d image not found in file: %s",
					m_nodeId, m_coreId, m_executableFile));
			return false;
		}

		RouteConstructor routeConstructor = new RouteConstructor(this, m_structuralGraph.get(m_nodeId, m_coreId), routeDescription);
			
		notify(Notifier.NOTIFY_ANALYSIS_BEGIN, "");
		m_progress.resetState(Progress.State.PROCESSING_PATHS);
		
		boolean foundAtLeastOneEndPoint = routeConstructor.analyse();		

		if (!foundAtLeastOneEndPoint ) {
			notify(Notifier.NOTIFY_ERROR, "No valid paths of execution");
			return false;
		} else {
			int currentSize = m_routes.size();
			for(Node subRoute : routeConstructor.getSubRoutes()){
				Route route = new Route(this, subRoute, routeDescription);
				m_routes.add(route);
			}
			if (!m_routes.isEmpty() && !routeConstructor.getSubRoutes().isEmpty()) {
				setSelectedRoute(m_routes.get(currentSize));
				notify(Notifier.NOTIFY_ANALYSIS_COMPLETE, Integer.toString(m_routes.get(currentSize).getRouteId()));
			} else {
				notify(Notifier.NOTIFY_WARNING, "No paths added");
			}
	
			return true;
		}		
	}
	
	public Progress getProgress() {
		if (m_progress != null) {
			return m_progress;
		} else {
			return new Progress(Progress.State.IDLE, 0);
		}
	}
	
	public Notifier getNotifier() {
		return m_notifier;
	}

	public void setNotifier(Notifier notifier) {
		m_notifier = notifier;
	}

	public boolean getAbort() {
		if (m_jobMonitor != null)
			return m_jobMonitor.isCanceled();
		return false;
	}

	public void setNodeId(int nodeId) {
		m_nodeId = nodeId;
		m_selectedRoute = null;
		notify(Notifier.NOTIFY_SET_NODE_CORE, "");
	}

	public int getNodeId() {
		return m_nodeId;
	}

	public void setCoreId(int coreId) {
		m_coreId = coreId;
		m_selectedRoute = null;
		notify(Notifier.NOTIFY_SET_NODE_CORE, "");
	}

	public int getCoreId() {
		return m_coreId;
	}

	public void setFrequency(int frequency) {
		m_config.m_frequency = frequency;
		notify(Notifier.NOTIFY_UPDATE_STATE, Notifier.STATE_FREQUENCY);
	}

	public int getFrequency() {
		return m_config.m_frequency;
	}

	public void setNumThreads(int numThreads) {
		m_config.m_numThreads = numThreads;
		notify(Notifier.NOTIFY_UPDATE_STATE, Notifier.STATE_NUM_THREADS);
	}

	public int getNumThreads() {
		return m_config.m_numThreads;
	}

	public void setVerbosity(int verbosity) {
		m_config.m_verbosity = verbosity;
	}

	public int getVerbosity() {
		return m_config.m_verbosity;
	}
	
	public boolean canInstructionPause(int routeId, String refString) {
		Reference reference = ReferenceFactory.createReference(
				refString, ReferenceClass.ENDPOINT_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}
		return getRoute(routeId).canInstructionPause(reference);			
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

	public void setMonitor(IProgressMonitor monitor) {
		m_jobMonitor = monitor;
	}
	
	public void provideFeedback(String feedback) {
		if (m_jobMonitor != null){
			m_jobMonitor.subTask(feedback);	
		}		
	}
	
	public void startAnalysis(String task, int functionCount){
		if (m_jobMonitor != null){
			m_jobMonitor.beginTask(task, functionCount);
		}	
	}
	
	public void provideWorkDone(int work){
		if (m_jobMonitor != null){
			m_jobMonitor.worked(work);
		}
	}
	
	public Collection<String> getFunctionsVisited() {
		return m_functionsVisited;
	}
	
	// ===================================================================== //
	// Route Selection Methods
	// ===================================================================== //			
	public Route getSelectedRoute() {
		return m_selectedRoute;
	}

	public int getSelectedRouteId() {
		if (m_selectedRoute != null)
			return m_selectedRoute.getRouteId();
		else
			return ROUTE_ID_INVALID;
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
		notify(Notifier.NOTIFY_SELECT_ROUTE, "");
	}

	public void setSelectedRoute(int routeId) {
		 setSelectedRoute(getRoute(routeId));
	}
	
	// ===================================================================== //
	// Requirements Methods
	// ===================================================================== //		
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
	
	// ===================================================================== //
	// Unknown (Loop Iteration) Methods
	// ===================================================================== //		
	public boolean setLoopIterations(int routeId, String refString, long iterations) {
		Reference reference = ReferenceFactory.createReference(
				refString, ReferenceClass.ANY_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}		
		if (getRoute(routeId).setLoopIterations(reference, iterations)) {
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
			return true;
		}
		return false;
	}

	public boolean setLoopIterations(Node node, long iterations) {
		if (m_selectedRoute.setLoopIterations(node, iterations)) {
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
			return true;
		}
		return false;
	}

	public boolean canSetLoopIterations(Node node) {
		return m_selectedRoute.canSetLoopIterations(node);
	}
	
	public boolean setLoopPathIterations(int routeId, String refString, long iterations) {
		Reference reference = ReferenceFactory.createReference(
				refString, ReferenceClass.ANY_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}
		if (getRoute(routeId).setLoopPathIterations(reference, iterations)) {
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
			return true;
		}
		return false;
	}

	public boolean setLoopPathIterations(Node node, long iterations) {
		if (m_selectedRoute.setLoopPathIterations(node, iterations)) {
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
			return true;
		}
		return false;
	}

	public boolean canSetLoopPathIterations(Node node) {
		return m_selectedRoute.canSetLoopPathIterations(node);
	}
	
	public boolean setLoopScope(int routeId, String refString, boolean absolute) {
		Reference reference = ReferenceFactory.createReference(
				refString, ReferenceClass.ANY_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}
		if (getRoute(routeId).setLoopScope(reference, absolute)) {
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
			return true;
		}
		return false;
	}

	public boolean setLoopScope(Node node, boolean absolute) {
		if (m_selectedRoute.setLoopScope(node, absolute)) {
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
			return true;
		}
		return false;
	}

	public boolean canSetLoopScope(Node node) {
		return m_selectedRoute.canSetLoopScope(node);
	}
	
	// ===================================================================== //
	// Unknown (Instruction Time) Methods
	// ===================================================================== //			
	public boolean setInstructionTimeTaken(int routeId, String refString, double timeTaken) {
		Reference reference = ReferenceFactory.createReference(
				refString, ReferenceClass.ENDPOINT_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}
		if (getRoute(routeId).setInstructionTimeTaken(reference, timeTaken)) {
			notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
			return true;
		}
		return false;
	}

	public void setInstructionTimeTaken(Node node, double timeTaken) {
		m_selectedRoute.setInstructionTimeTaken(node, timeTaken);
		notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
	}
	
	public boolean unsetInstructionTimeTaken(int routeId, String refString) {
		Reference reference = ReferenceFactory.createReference(
				refString, ReferenceClass.ENDPOINT_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}
		if (getRoute(routeId).unsetInstructionTimeTaken(reference)) {
			notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
			return true;
		}
		return false;
	}

	public void unsetInstructionTimeTaken(Node node) {
		m_selectedRoute.unsetInstructionTimeTaken(node);
		notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
	}

	public double getInstructionTimeTaken(Node node) {
		return m_selectedRoute.getInstructionTimeTaken(node);
	}

	public boolean setInstructionNumCycles(int routeId, String refString, long numCycles) {
		Reference reference = ReferenceFactory.createReference(
				refString, ReferenceClass.ENDPOINT_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}
		if (getRoute(routeId).setInstructionNumCycles(reference, numCycles)) {
			notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
			return true;
		}
		return false;
	}

	public void setInstructionNumCycles(Node node, long numCycles) {
		m_selectedRoute.setInstructionNumCycles(node, numCycles);
		notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
	}
	
	public boolean unsetInstructionNumCycles(int routeId, String refString) {
		Reference reference = ReferenceFactory.createReference(
				refString, ReferenceClass.ENDPOINT_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}
		if (getRoute(routeId).unsetInstructionNumCycles(reference)) {
			notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
			return true;
		}
		return false;
	}
			
	public void unsetInstructionNumCycles(Node node) {
		m_selectedRoute.unsetInstructionNumCycles(node);
		notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
	}
	
	public long getInstructionNumCycles(Node node) {
		return m_selectedRoute.getInstructionNumCycles(node);
	}	

	// ===================================================================== //
	// Exclusion Methods
	// ===================================================================== //	
	public boolean addExclusion(String refString) {
		Reference reference = ReferenceFactory.createReference(
				refString, ReferenceClass.ANY_REF);
		if (!resolveRefernece(reference)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Failed to add exclusion at reference: %s", refString));			
			return false;
		}
		m_exclusions.add(reference);
		notify(Notifier.NOTIFY_UPDATE_EXCLUSIONS, "");
		return true;
	}
	
	public boolean removeExclusion(String refString) {
		if (refString.equals("*")) {
			m_exclusions.clear();

		} else {
			Reference reference = ReferenceFactory.createReference(
					refString, ReferenceClass.ANY_REF);
			if (!resolveRefernece(reference)) {
				notify(Notifier.NOTIFY_ERROR, String.format("Failed to remove exclusion at reference: %s", refString));			
				return false;
			}
			Iterator<Reference> iter = m_exclusions.iterator();
			while (iter.hasNext()) {
				Reference ref = iter.next();
				if (ref.equals(reference)) {
					iter.remove();
				}
			}			
		}
		notify(Notifier.NOTIFY_UPDATE_EXCLUSIONS, "");
		return true;
	}

	public Collection<Reference> getExclusions() {
		return m_exclusions;
	}
					
	// ===================================================================== //
	// Section Methods
	// ===================================================================== //
	public boolean addBranch(Reference fromRef, Reference toRef) {
		if (!resolveRefernece(fromRef)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Unresolved reference: %s", fromRef.getRef()));
			return false;
		} else if (!resolveRefernece(toRef)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Unresolved reference: %s", toRef.getRef()));
			return false;
		}		
		
		if (m_sections.addToBranches(m_nodeId, m_coreId, fromRef.getPc(), toRef.getPc())) {
			if (!initialiseGlobalStructure(m_xeLoader, m_sections, m_fileFinder)) {
				return false;
			}
			return true;			
		}		
		return false;
	}

	public boolean removeBranch(Reference fromRef) {
		if (!resolveRefernece(fromRef)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Unresolved reference: %s", fromRef.getRef()));
			return false;
		}
		
		if (m_sections.removeFromBranches(m_nodeId, m_coreId, fromRef.getPc())) {	
			if (!initialiseGlobalStructure(m_xeLoader, m_sections, m_fileFinder)) {
				return false;
			}
			return true;			
		}
		return false;
	}

	public Map<Integer, TreeSet<Integer>> getBranches() {
		return m_sections.getBranches(m_nodeId, m_coreId);
	}

	public boolean addLabel(String label, Reference ref) {
		if (checkAddParameters(label, ref, "label")) {
			return m_sections.addToLabels(m_nodeId, m_coreId, label, ref.getPc());			
		}
		return false;
	}

	public void removeLabel(String label) {
		m_sections.removeFromLabels(m_nodeId, m_coreId, label);
	}

	public Map<String, Integer> getLabels() {
		return m_sections.getLabels(m_nodeId, m_coreId);
	}
	
	public boolean addSrcLabel(String label, Reference ref) {
		if (checkAddParameters(label, ref, "src label")) {
		    return m_sections.addToSrcLabels(m_nodeId, m_coreId, label, ref.getPc());
		}
		return false;
	}

	public boolean removeSrcLabel(String label) {
		return m_sections.removeFromSrcLabels(m_nodeId, m_coreId,  label);
	}

	public Map<String, CodePointInfo> getSrcLabels() {
		return m_sections.getSrcLabels(m_nodeId, m_coreId);
	}

	public Map<Integer, SrcLabelTableInfo> getSrcLabelTable() {
		return m_sections.getSrcLabelTable(m_nodeId, m_coreId);
	}

	public boolean addEndpoint(String endpoint, Reference ref) {
		if (checkAddParameters(endpoint, ref, "endpoint label")) {
    		return m_sections.addToEndpoints(m_nodeId, m_coreId, endpoint, ref.getPc());
		}
		return false;
	}

	public boolean removeEndpoint(String endpoint) {
		return m_sections.removeFromEndpoints(m_nodeId, m_coreId, endpoint);
	}

	public Map<String, CodePointInfo> getEndpoints() {
		return m_sections.getEndpoints(m_nodeId, m_coreId);
	}
		
	public Map<Integer, CodePointTableInfo> getEndpointTable() {
		return m_sections.getEndpointTable(m_nodeId, m_coreId);
	}
	
	public boolean addCall(String call, Reference ref) {
		if (checkAddParameters(call, ref, "call label")) {		
		    return m_sections.addToCalls(m_nodeId, m_coreId, call, ref.getPc());
		}
		return false;
	}

	public boolean removeCall(String call) {
		return m_sections.removeFromCalls(m_nodeId, m_coreId, call);
	}

	public Map<String, CodePointInfo> getCalls() {
		return m_sections.getCalls(m_nodeId, m_coreId);
	}
		
	public Map<Integer, CodePointTableInfo> getCallTable() { 
		return m_sections.getCallTable(m_nodeId, m_coreId);
	}
	
	public boolean addThreadStartPoint(Reference ref) {
		if (!resolveRefernece(ref)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Unresolved reference: %s", ref.getRef()));
			return false;
		}		
		return m_sections.addToThreadStartPoints(m_nodeId, m_coreId, ref.getPc());
	}

	public boolean removeThreadStartPoint(Reference ref) {
		if (!resolveRefernece(ref)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Unresolved reference: %s", ref.getRef()));
			return false;
		}		
		return m_sections.removeFromThreadStartPoints(m_nodeId, m_coreId, ref.getPc());
	}

	public Collection<Integer> getThreadStartPoints() {
		return m_sections.getThreadStartPoints(m_nodeId, m_coreId);
	}	

	public boolean addThreadStopPoint(Reference ref) {
		if (!resolveRefernece(ref)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Unresolved reference: %s", ref.getRef()));
			return false;
		}				
		return m_sections.addToThreadStopPoints(m_nodeId, m_coreId, ref.getPc());
	}

	public boolean removeThreadStopPoint(Reference ref) {
		if (!resolveRefernece(ref)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Unresolved reference: %s", ref.getRef()));
			return false;
		}		
		return m_sections.removeFromThreadStopPoints(m_nodeId, m_coreId, ref.getPc());
	}

	public Collection<Integer> getThreadStopPoints() {
		return m_sections.getThreadStopPoints(m_nodeId, m_coreId);
	}	
	
	public Collection<String> getSrcLevelCommands() {
		return m_sections.getSrcLevelCommands(m_nodeId, m_coreId);
	}
	
	
	// ===================================================================== //
	// Private Methods
	// ===================================================================== //
	private boolean initialiseGlobalStructure(XeLoader xeLoader, Sections sections, FileFinder fileFinder) {
		assert(fileFinder != null);
		
		NodeCoreCollection<StructureGraph> newStructuralGraph = new NodeCoreCollection<StructureGraph>();
		NodeCoreCollection<ControlFlowGraph> newCfg = new NodeCoreCollection<ControlFlowGraph>();
		NodeCoreCollection<InstructionDecoder> newInstructionDecoder = new NodeCoreCollection<InstructionDecoder>();
		NodeCoreCollection<TreeMap<String, InstructionNode>> newFunctionNames = new NodeCoreCollection<TreeMap<String, InstructionNode>>();
		NodeCoreCollection<SourceMapper> newSourceMappers = new NodeCoreCollection<SourceMapper>();
		
		for (int nodeId : xeLoader.getNodeIds()){
			for (int coreId : xeLoader.getCoreIds(nodeId)){
				InstructionDecoder id = new InstructionDecoder(nodeId, coreId, xeLoader, m_config);

				newFunctionNames.add(nodeId, coreId, new TreeMap<String, InstructionNode>());
				
				if (!id.decode()) 
					return false;
				
				ThreadHandler th = new ThreadHandler(id, this);
				Collection<InstructionNode> threadStartNodes = 
					th.getThreadNodes(sections.getThreadStartPoints(nodeId, coreId));
				Collection<InstructionNode> threadStopNodes = 
					th.getThreadNodes(sections.getThreadStopPoints(nodeId, coreId));				
				threadStartNodes = th.ensureThreadStartExists(threadStartNodes);

				SuccessorAdder sa = new SuccessorAdder(nodeId, coreId, id, sections, threadStopNodes, xeLoader, this);
				if (!sa.process()) 
					return false;

				FunctionHandler fh = new FunctionHandler(id);
				if (!fh.addFunctions()) 
					return false;
				th.addTerminators(threadStartNodes);
				
				Set<InstructionNode> starts = new TreeSet<InstructionNode>();
				starts.addAll(fh.getKnownFuncStarts());
				starts.addAll(threadStartNodes);
				
				ensureAllFunctionsAreNamed(xeLoader.getSymbolTable(nodeId, coreId), 
						newFunctionNames.get(nodeId, coreId), starts);
				
				SourceMapper sourceMapper = new SourceMapper(xeLoader, nodeId, coreId, starts, 
						fileFinder, id, newFunctionNames.get(nodeId, coreId));
				
				newSourceMappers.add(nodeId, coreId, sourceMapper);
				

				UnreachableCodeFinder uca = new UnreachableCodeFinder();
				uca.analyseReachable(id, threadStartNodes, fh.getKnownFuncStarts()); 
	
				ControlFlowGraph cfg = new ControlFlowGraph(threadStartNodes, fh.getKnownFuncStarts(), fh.getFunctions());
				newCfg.add(nodeId, coreId, cfg);
				
				
				StructuralAnalysisEngine sae = new StructuralAnalysisEngine(cfg);
				if (!sae.analyse(this, newFunctionNames.get(nodeId, coreId)))
					return false;
				
				newStructuralGraph.add(nodeId, coreId, sae.getStructureGraph());
			}
		}
		m_instructionDecoder = newInstructionDecoder;
		m_controlFlowGraph = newCfg;
		m_structuralGraph = newStructuralGraph;
		m_functionNames = newFunctionNames;
		m_sourceMapper = newSourceMappers;
		return true;
	}

	private void ensureAllFunctionsAreNamed(SymbolTable symbolTable,
			TreeMap<String, InstructionNode> functionName,
			Set<InstructionNode> starts ) {
		for(InstructionNode inst : starts){
			String name = symbolTable.functionName(inst.getStartPc());
			
			if(functionName.containsKey(name)){
				//the one with the lower pc will stay in the set and the other will take an auto-generated name.
				
				InstructionNode instNode = functionName.get(name);
				
				if(instNode.getStartPc() < inst.getStartPc()){
					name = "unnamed_at_0x" + Integer.toHexString(inst.getStartPc());
					functionName.put(name, inst);
				} else if(instNode.getStartPc() > inst.getStartPc()){
					String newName = "unnamed_at_0x" + Integer.toHexString(instNode.getStartPc());
					functionName.put(newName, instNode);
					functionName.put(name, inst);
				} else {
					assert(false);
				}
			} else {
				assert(!functionName.containsKey(name));
				functionName.put(name, inst);
			}
		}
	}
	
	private boolean functionExists(String functionName) {
		return getFunctionNames().contains(functionName);
	}

	private boolean resolveRefernece(Reference ref) {
		return ref.resolve(this, m_nodeId, m_coreId);
	}
		
	private boolean checkAddParameters(String label, Reference ref, String type) {
		if (!resolveRefernece(ref)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Unresolved reference: %s", ref.getRef()));
			return false;
		}
		if (isValidPcString(label)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Cannot use PC as a %s: %s", type, label));
			return false;
		}				
		return true;
	}

	private boolean isValidPcString(String ref) {
		if (ref.matches("0x[0-9a-fA-F]+") ||
			ref.matches("[0-9]+")) {
			return true;
		}
		return false;
	}
}
