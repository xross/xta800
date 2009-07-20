package com.xmos.xta.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.analysis.structural.StructuralAnalysisEngine;
import com.xmos.xta.engine.analysis.structural.StructureGraph;
import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.routes.RouteConstructor;
import com.xmos.xta.engine.analysis.routes.RouteDescription;
import com.xmos.xta.engine.analysis.routes.RouteTiming;
import com.xmos.xta.engine.analysis.routes.RouteConstructor.RouteError;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.types.ReferenceTypeFactory;
import com.xmos.xta.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.engine.sections.CodePointInfo;
import com.xmos.xta.engine.sections.CodePointTableInfo;
import com.xmos.xta.engine.sections.SrcLevelLoopInfo;
import com.xmos.xta.engine.sections.Sections;
import com.xmos.xta.engine.sections.SrcLabelTableInfo;
import com.xmos.xta.sourcemapper.FileFinder;
import com.xmos.xta.sourcemapper.SourceMapper;

public class Engine implements Notifier {
	public static final int ROUTE_ID_LAST     = -1;
	public static final int ROUTE_ID_INVALID  = -2;
	public static final int ROUTE_ID_GLOBAL   = -3;
	public static final int NODE_ID_TOP_LEVEL = -4;
	
	private XeLoader m_xeLoader;
	private FileFinder m_fileFinder;
	private List<Route> m_routes;
	private int m_numRoutesLastAnalysed;
	private Collection<Reference> m_exclusions;
	private Route m_selectedRoute;
	private Config m_config;
	private File m_executableFile;
	private int m_nodeId;
	private int m_coreId;
	private Notifier m_notifier;	
	private IProgressMonitor m_jobMonitor;
	private String m_srcPaths;
	private Sections m_sections;
	private Collection<String> m_functionsVisited;
	private int m_timeOut;
	private long m_loadStartTime;
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
		m_numRoutesLastAnalysed = 0;
		m_exclusions = new ArrayList<Reference>();
		m_config = config;
		m_executableFile = null;
		m_fileFinder = null;
		m_nodeId = 0;
		m_coreId = 0;
		m_srcPaths = ".";
		m_jobMonitor = null;
		m_sections = new Sections();
		m_timeOut = -1;
		ReferenceTypeFactory.setEngine(this);
	}

	public void notify(String message, String argument) {
		if (m_notifier != null){
			m_notifier.notify(message, argument);
		}
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
		TreeMap<String, InstructionNode> functionNames = m_functionNames.get(m_nodeId, m_coreId);
		if (functionNames != null) {
		    return functionNames.keySet();
	    }
		return new TreeSet<String>();
	}
	
	public Set<String> getFunctionNames(int nodeId, int coreId){
		TreeMap<String, InstructionNode> functionNames = m_functionNames.get(nodeId, coreId);
		if (functionNames != null) {
		    return functionNames.keySet();
	    }
		return new TreeSet<String>();
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
		
		m_exclusions = new ArrayList<Reference>();
		m_executableFile = executableFile;
		m_fileFinder = fileFinder;
		m_xeLoader = xeLoader;
		m_sections = sections;
		m_nodeId = 0;
		m_coreId = 0;			
		m_routes.clear();
		m_numRoutesLastAnalysed = 0;
		m_selectedRoute = null;
		m_exclusions.clear();
		Route.resetRouteCount();
		
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
			RouteDescription routeDescription = new RouteDescription(functionName, this, m_nodeId, m_coreId);		
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
			RouteDescription routeDescription = new RouteDescription(fromRef, toRef, m_nodeId, m_coreId);
			return  analyseRouteSafely(routeDescription);
		}
	}

	private boolean analyseRouteSafely(RouteDescription routeDescription) {				
		boolean success = false;
		try {
			success = doRouteAnalysis(routeDescription);
			
		} catch (final java.lang.OutOfMemoryError e) {
			notify(Notifier.NOTIFY_ERROR, "Out of Memory (Heap)");
			return false;

		} catch (final java.lang.StackOverflowError e) {
			notify(Notifier.NOTIFY_ERROR, "Out of Memory (Stack)");
			return false;

		} catch (Exception e) {
			notify(Notifier.NOTIFY_ERROR, "Unknown error detected");
			e.printStackTrace();
			return false;
		}
		return success;
	}

	private boolean doRouteAnalysis(RouteDescription routeDescription) throws Exception {	
		if (!m_xeLoader.isLoaded(m_nodeId, m_coreId)) {
			notify(Notifier.NOTIFY_ERROR, String.format(
					"Node: %d, Core: %d image not found in file: %s",
					m_nodeId, m_coreId, m_executableFile));
			return false;
		}

		notify(Notifier.NOTIFY_ANALYSIS_BEGIN, "");		
		
		RouteConstructor routeConstructor = new RouteConstructor(this, m_structuralGraph.get(m_nodeId, m_coreId), routeDescription);			
		RouteError error = routeConstructor.analyse();		

		if (error == RouteError.NO_ENDPOINTS_FOUND ) {
			notify(Notifier.NOTIFY_ERROR, "No paths of execution could reach the endpoint(s)");
			return false;
			
		} else if (error == RouteError.COULD_NOT_FIND_ENDPOINT ) {
			notify(Notifier.NOTIFY_ERROR, "Could node find any end points");
			return false;
			
		} else if (error == RouteError.COULD_NOT_FIND_STARTPOINT ) {
			notify(Notifier.NOTIFY_ERROR,"Could node find any start points");
			return false;
			
		} else if (error == RouteError.ENDPOINTS_MISSING ) {
			notify(Notifier.NOTIFY_ERROR, "Could not determine the endpoint(s)");
			return false;
			
		} else if (error == RouteError.STARTPOINT_MISSING ) {
			notify(Notifier.NOTIFY_ERROR, "Could not determine the startpoint");
			return false;
			
		} else {
			if(!getAbort()){
				int currentSize = m_routes.size();
				m_numRoutesLastAnalysed = 0;
				for (Route route : routeConstructor.getRoutes()) {
					m_routes.add(route);
					++m_numRoutesLastAnalysed;
				}
				if (!m_routes.isEmpty() && !routeConstructor.getRoutes().isEmpty()) {
					setSelectedRoute(m_routes.get(currentSize));
					notify(Notifier.NOTIFY_ANALYSIS_COMPLETE, Integer.toString(m_routes.get(currentSize).getRouteId()));
				} else {
					notify(Notifier.NOTIFY_WARNING, "No valid paths of execution");
				}
				return true;
			} else {
				return false;
			}
		}		
	}
		
	public Notifier getNotifier() {
		return m_notifier;
	}

	public void setNotifier(Notifier notifier) {
		m_notifier = notifier;
	}


	
	public boolean getAbort() {
		
		if(m_timeOut > 0 && (System.currentTimeMillis() - m_loadStartTime)/1000 > m_timeOut){
			return true;
		}
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

	public void setCase(Case c) {
		m_config.m_case = c;
		notify(Notifier.NOTIFY_UPDATE_STATE, Notifier.STATE_CASE);
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
	
	public boolean canInstructionPause(int routeId, String refString) {
		Reference reference = ReferenceFactory.createReference(
				refString, ReferenceClass.ENDPOINT_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}
		for (Route route : getRoute(routeId)) {
			 if (route.getRouteUnknowns().canInstructionPause(reference))
				 return true;			
		}		
		return false;
	}
		
	public boolean removeRoute(int routeId) {		
		Collection<Route> routes = getRoute(routeId);
		if (routes.size() == 0)
			return false;
		
		m_routes.removeAll(routes);			
		if (m_routes.size() == 0) {
			Route.resetRouteCount();
			setSelectedRoute(null);
			
		} else if (m_selectedRoute.getRouteId() == routeId) {
			m_selectedRoute = m_routes.get(m_routes.size() - 1);
		}
		notify(Notifier.NOTIFY_REMOVE_ROUTE, "");
		return true;
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
		if (m_jobMonitor != null) {
			m_jobMonitor.subTask(feedback);	
		}		
	}
	
	public void startAnalysis(String task, int functionCount) {
		if (m_jobMonitor != null) {
			m_jobMonitor.beginTask(task, functionCount);
		}	
	}
	
	public void provideWorkDone(int work) {
		if (m_jobMonitor != null) {
			m_jobMonitor.worked(work);
		}
	}
	
	public boolean isBusy() {
		return m_jobMonitor != null;
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

	public int getSelectedRouteId() {
		if (m_selectedRoute != null)
			return m_selectedRoute.getRouteId();
		else
			return ROUTE_ID_INVALID;
	}
	
	public void setSelectedRouteId(int routeId) {
		assert((routeId != ROUTE_ID_INVALID) || (routeId != ROUTE_ID_LAST));
		Collection<Route> routes = getRoute(routeId);
		assert(routes.size() == 1);
		setSelectedRoute(routes.iterator().next());
	}
	
	// ===================================================================== //
	// Requirements Methods
	// ===================================================================== //		
	public boolean setRouteMaxTime(int routeId, double time) {
		Collection<Route> routes = getRoute(routeId);
		if (routes.size() == 0)
			return false;
			
		for (Route route : routes) {
			route.getRouteTiming().setMaxTime(time);
		}
		notify(Notifier.NOTIFY_UPDATE_STATE, "");
		return true;
	}

	public boolean setRouteMaxCycles(int routeId, int cycles) {
		Collection<Route> routes = getRoute(routeId);
		if (routes.size() == 0)
			return false;

		for (Route route : routes) {			
			route.getRouteTiming().setMaxCycles(cycles);
		}
		notify(Notifier.NOTIFY_UPDATE_STATE, "");
		return true;
	}
	
	public boolean unsetRouteMax(int routeId) {
		Collection<Route> routes = getRoute(routeId);
		if (routes.size() == 0)
			return false;
		
		for (Route route : routes) {			
			route.getRouteTiming().resetMaxTime();
		}
		notify(Notifier.NOTIFY_UPDATE_STATE, "");
		return true;
	}	
	
	// ===================================================================== //
	// Unknown (Loop Iteration) Methods
	// ===================================================================== //		
	public boolean setLoopIterations(int routeId, String refString, long iterations) {
		Reference reference = ReferenceFactory.createReference(refString, ReferenceClass.ANY_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}
		Collection<Route> routes = getRoute(routeId);
		if (routes.size() == 0) {
			return false;
		}
		for (Route route : routes) {
			if (!route.getRouteUnknowns().canSetLoopIterations(reference, iterations)) {
				return false;
			}
		}
		for (Route route : routes) {
		    route.getRouteUnknowns().setLoopIterations(reference, iterations);
		}
	    notify(Notifier.NOTIFY_UPDATE_STATE, "");
	    return true;
	}

	public boolean setLoopIterations(Node node, long iterations) {
		if (m_selectedRoute.getRouteUnknowns().setLoopIterations(node, iterations)) {
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
			return true;
		}
		return false;
	}

	public boolean canSetLoopIterations(Node node) {
		return m_selectedRoute.getRouteUnknowns().canSetLoopIterations(node);
	}

	public long getSetLoopIterations(Node node) {
		return m_selectedRoute.getRouteUnknowns().getSetLoopIterations(node);
	}
	
	public boolean setLoopPathIterations(int routeId, String refString, long iterations) {
		Reference reference = ReferenceFactory.createReference(refString, ReferenceClass.ANY_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}		
		Collection<Route> routes = getRoute(routeId);
		if (routes.size() == 0) {
			return false;
		}
		for (Route route : routes) {
			if (!route.getRouteUnknowns().canSetLoopPathIterations(reference, iterations)) {
				return false;
			}
		}
		for (Route route : routes) {
		    route.getRouteUnknowns().setLoopPathIterations(reference, iterations);
		}
		notify(Notifier.NOTIFY_UPDATE_STATE, "");
		return true;
	}

	public boolean setLoopPathIterations(Node node, long iterations) {
		if (m_selectedRoute.getRouteUnknowns().setLoopPathIterations(node, iterations)) {
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
			return true;
		}
		return false;
	}

	public boolean canSetLoopPathIterations(Node node) {
		return m_selectedRoute.getRouteUnknowns().canSetLoopPathIterations(node);
	}
	
	public long getSetLoopPathIterations(Node node) {
		return m_selectedRoute.getRouteUnknowns().getSetLoopPathIterations(node);
	}

	public boolean setLoopScope(int routeId, String refString, boolean absolute) {
		Reference reference = ReferenceFactory.createReference(refString, ReferenceClass.ANY_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}
		Collection<Route> routes = getRoute(routeId);
		if (routes.size() == 0) {
			return false;
		}
		for (Route route : routes) {
			if (!route.getRouteUnknowns().canSetLoopScope(reference, absolute)) {
				return false;
			}
		}
		for (Route route : routes) {
		    route.getRouteUnknowns().setLoopScope(reference, absolute);		   
		}
		notify(Notifier.NOTIFY_UPDATE_STATE, "");
		return true;
	}

	public boolean setLoopScope(Node node, boolean absolute) {
		if (m_selectedRoute.getRouteUnknowns().setLoopScope(node, absolute)) {
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
			return true;
		}
		return false;
	}

	public boolean canSetLoopScope(Node node) {
		return m_selectedRoute.getRouteUnknowns().canSetLoopScope(node);
	}
	
	// ===================================================================== //
	// Unknown (Instruction Time) Methods
	// ===================================================================== //			
	public boolean setInstructionTimeTaken(int routeId, String refString, double timeTaken) {
		Reference reference = ReferenceFactory.createReference(refString, ReferenceClass.ENDPOINT_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}
		Collection<Route> routes = getRoute(routeId);
		if (routes.size() == 0)
			return false;
		
		for (Route route : routes) {
		    if (!route.getRouteUnknowns().canInstructionPause(reference))
		    	return false;		    	
		}

		boolean passed = false;
		for (Route route : routes) {
		    if (route.getRouteUnknowns().setInstructionTimeTaken(reference, timeTaken)) {
			    passed = true;
		    }
		}
		if (passed) {
		    notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
		}
		return passed;
	}

	public void setInstructionTimeTaken(Node node, double timeTaken) {
		m_selectedRoute.getRouteUnknowns().setInstructionTimeTaken(node, timeTaken);
		notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
	}
	
	public boolean unsetInstructionTimeTaken(int routeId, String refString) {
		Reference reference = ReferenceFactory.createReference(refString, ReferenceClass.ENDPOINT_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}
		Collection<Route> routes = getRoute(routeId);
		if (routes.size() == 0)
			return false;
		
		boolean passed = false;
		for (Route route : routes) {
		    if (route.getRouteUnknowns().unsetInstructionTimeTaken(reference)) {
			    passed = true;
		    }
		}
		if (passed) {
		    notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
		}
		return passed;
	}

	public void unsetInstructionTimeTaken(Node node) {
		m_selectedRoute.getRouteUnknowns().unsetInstructionTimeTaken(node);
		notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
	}

	public double getInstructionTimeTaken(Node node) {
		return m_selectedRoute.getRouteUnknowns().getInstructionTimeTaken(node);
	}

	public boolean setInstructionNumCycles(int routeId, String refString, long numCycles) {
		Reference reference = ReferenceFactory.createReference(refString, ReferenceClass.ENDPOINT_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}
		Collection<Route> routes = getRoute(routeId);
		if (routes.size() == 0)
			return false;
		
		for (Route route : routes) {
		    if (!route.getRouteUnknowns().canInstructionPause(reference))
		    	return false;		    	
		}
		
		boolean passed = false;
		for (Route route : routes) {
     		if (route.getRouteUnknowns().setInstructionNumCycles(reference, numCycles)) {
			    passed = true;
		    }
		}
		if (passed) {
		    notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
		}
		return passed;
	}

	public void setInstructionNumCycles(Node node, long numCycles) {
		m_selectedRoute.getRouteUnknowns().setInstructionNumCycles(node, numCycles);
		notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
	}
	
	public boolean unsetInstructionNumCycles(int routeId, String refString) {
		Reference reference = ReferenceFactory.createReference(refString, ReferenceClass.ENDPOINT_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}
		Collection<Route> routes = getRoute(routeId);
		if (routes.size() == 0)
			return false;
		
		boolean passed = false;
		for (Route route : routes) {
		    if (route.getRouteUnknowns().unsetInstructionNumCycles(reference)) {
			    passed = true;
		    }
		}
		if (passed) {
		    notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
		}
		return passed;
	}
			
	public void unsetInstructionNumCycles(Node node) {
		m_selectedRoute.getRouteUnknowns().unsetInstructionNumCycles(node);
		notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
	}
	
	public long getInstructionNumCycles(Node node) {
		return m_selectedRoute.getRouteUnknowns().getInstructionNumCycles(node);
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
		if (alreadyExcluded(reference.getPc())) {
			notify(Notifier.NOTIFY_ERROR, String.format("Cannot add duplicate exclusion for PC: 0x%x", reference.getPc()));			
			return false;
		}
		m_exclusions.add(reference);
		notify(Notifier.NOTIFY_UPDATE_EXCLUSIONS, "");
		return true;
	}
	
	private boolean alreadyExcluded(int pc) {
		for (Reference ref : m_exclusions) {
			if (ref.getPc() == pc) {
				return true;
			}
		}
		return false;
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

	public Collection<SrcLevelLoopInfo> getSrcLevelLoops() {
		return m_sections.getSrcLevelLoops(m_nodeId, m_coreId);
	}
	
	// ===================================================================== //
	// Private Methods
	// ===================================================================== //
	private boolean initialiseGlobalStructure(XeLoader xeLoader, Sections sections, FileFinder fileFinder) {
		assert(fileFinder != null);
		
		m_loadStartTime = System.currentTimeMillis();
		notify(Notifier.HOLD_MESSAGES, "");
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

				Set<InstructionNode> threadStartNodes = 
					th.getThreadNodes(sections.getThreadStartPoints(nodeId, coreId), "thread start");
				Set<InstructionNode> threadStopNodes = 
					th.getThreadNodes(sections.getThreadStopPoints(nodeId, coreId), "thread stop");
				
				threadStartNodes = th.ensureThreadStartExists(threadStartNodes);

				SuccessorAdder sa = new SuccessorAdder(nodeId, coreId, id, sections, threadStopNodes, xeLoader, this);
				if (!sa.process()) 
					return false;

				FunctionHandler fh = new FunctionHandler(id);
				if (!fh.addFunctions()) 
					return false;
				
				Map<Integer, TreeSet<Node>> pcToEndPoints = th.addTerminators(threadStartNodes);
				pcToEndPoints.putAll(fh.getFunctionsToEndPoints());
				
				Set<InstructionNode> starts = new TreeSet<InstructionNode>();
				starts.addAll(fh.getKnownFuncStarts());
				starts.addAll(threadStartNodes);
				
				ensureAllFunctionsAreNamed(xeLoader.getSymbolTable(nodeId, coreId), 
						newFunctionNames.get(nodeId, coreId), fh.getKnownFuncStarts(), threadStartNodes);
				
				setBranchTargets(id, newFunctionNames.get(nodeId, coreId));
				
				
				SourceMapper sourceMapper = new SourceMapper(xeLoader, nodeId, coreId, starts, 
						fileFinder, id, newFunctionNames.get(nodeId, coreId));
				
				newSourceMappers.add(nodeId, coreId, sourceMapper);
				
				UnreachableCodeFinder uca = new UnreachableCodeFinder();
				uca.analyseReachable(id, threadStartNodes, fh.getKnownFuncStarts()); 
	
				ControlFlowGraph cfg = new ControlFlowGraph(threadStartNodes, fh.getKnownFuncStarts(), fh.getFunctions());
				newCfg.add(nodeId, coreId, cfg);
				
				StructuralAnalysisEngine sae = new StructuralAnalysisEngine(cfg);
				
				if (!sae.analyse(this, newFunctionNames.get(nodeId, coreId), pcToEndPoints))
					return false;
				
				newStructuralGraph.add(nodeId, coreId, sae.getStructureGraph());
			}
		}
		m_instructionDecoder = newInstructionDecoder;
		m_controlFlowGraph = newCfg;
		m_structuralGraph = newStructuralGraph;
		m_functionNames = newFunctionNames;
		m_sourceMapper = newSourceMappers;
		
		notify(Notifier.RELEASE_MESSAGES, "");
		return true;
	}
	
	private void setBranchTargets(InstructionDecoder id,
			TreeMap<String, InstructionNode> treeMap) {
		for(InstructionNode inst : id.getInstructions()){
			if(inst.getXtaInstruction().getBranchPcRelative()){
				int branchTarget = inst.getXtaInstruction().getBranchPcRelativeTargetPc();
				inst.getXtaInstruction().setBranchPcRelativeTargetFunction("0x" + Integer.toHexString(branchTarget));
				for(String fn : treeMap.keySet()){
					if(treeMap.get(fn).getStartPc() == branchTarget){
						inst.getXtaInstruction().setBranchPcRelativeTargetFunction(fn);
						break;
					}
				}
			}
		}
		
	}

	private void ensureAllFunctionsAreNamed(SymbolTable symbolTable,
			TreeMap<String, InstructionNode> treeMap,
			Collection<InstructionNode> funcStarts, Set<InstructionNode> threadStarts ) {
		
		
		//TODO test this properly
		for(InstructionNode inst : funcStarts){
			String name = symbolTable.functionName(inst.getStartPc());
			
			if(treeMap.containsKey(name)){
				//the one with the lower pc will stay in the set and the other will take an auto-generated name.
				
				InstructionNode instNode = treeMap.get(name);
				
				if(instNode.getStartPc() < inst.getStartPc()){
					name = "unnamed_function_at_0x" + Integer.toHexString(inst.getStartPc());
					treeMap.put(name, inst);
				} else if(instNode.getStartPc() > inst.getStartPc()){
					String newName = "unnamed_function_at_0x" + Integer.toHexString(instNode.getStartPc());
					treeMap.put(newName, instNode);
					treeMap.put(name, inst);
				} else {
					assert(false);
				}
			} else {
				assert(!treeMap.containsKey(name));
				treeMap.put(name, inst);
			}
		}
		for(InstructionNode inst : threadStarts){
			String name = symbolTable.functionName(inst.getStartPc());
			
			if(treeMap.containsKey(name)){
				name = "unnamed_thread_at_0x" + Integer.toHexString(inst.getStartPc());
				treeMap.put(name, inst);
			} else {
				assert(!treeMap.containsKey(name));
				treeMap.put(name, inst);
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


	public void setAnalysisTimeOut(int time) {
		m_timeOut = time;
	}
}

class NodeCoreCollection<E> {
	private Map<String, E> m_collectedObjects;

	public NodeCoreCollection() {
		m_collectedObjects = new HashMap<String, E>();
	}

	public void add(int nodeId, int coreId, E newObject) {
		m_collectedObjects.put(nodeCoreToString(nodeId, coreId), newObject);
	}

	public E get(int nodeId, int coreId) {
		return m_collectedObjects.get(nodeCoreToString(nodeId, coreId));
	}

	private String nodeCoreToString(int nodeId, int coreId) {
		return nodeId + ":" + coreId;
	}
}
