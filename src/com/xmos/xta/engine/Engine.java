package com.xmos.xta.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;
import org.eclipse.core.runtime.IProgressMonitor;
import com.xmos.elfhandler.ElfHandler;
import com.xmos.elfhandler.Symbol;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeloader.XeLoader;
import com.xmos.xeobject.FileFinder;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.engine.analysis.controlflow.ControlFlowGraph;
import com.xmos.xta.engine.analysis.controlflow.FunctionHandler;
import com.xmos.xta.engine.analysis.controlflow.InstructionDecoder;
import com.xmos.xta.engine.analysis.controlflow.SuccessorAdder;
import com.xmos.xta.engine.analysis.controlflow.ThreadHandler;
import com.xmos.xta.engine.analysis.controlflow.UnreachableCodeFinder;
import com.xmos.xta.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.StructuralAnalysisEngine;
import com.xmos.xta.engine.analysis.structural.StructureGraph;
import com.xmos.xta.engine.analysis.routes.Route;
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

public class Engine implements Notifier {
	public static int ROUTE_ID_LAST = -1;
	
	private XeLoader m_xeLoader;
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
	
	private NodeCoreCollection<XeObject> m_xeObjects;
	private NodeCoreCollection<StructureGraph> m_structuralGraph;
	private NodeCoreCollection<ControlFlowGraph> m_controlFlowGraph;
	private NodeCoreCollection<InstructionDecoder> m_instructionDecoder;
	
	public Engine(Config config) {
		
		m_xeObjects = new NodeCoreCollection<XeObject>();
		m_structuralGraph = new NodeCoreCollection<StructureGraph>();
		m_controlFlowGraph = new NodeCoreCollection<ControlFlowGraph>();
		m_instructionDecoder = new NodeCoreCollection<InstructionDecoder>();
		
		m_xeLoader = new XeLoader();
		m_routes = new LinkedList<Route>();
		m_exclusions = new ArrayList<Reference>();
		m_config = config;
		m_executableFile = null;
		m_nodeId = 0;
		m_coreId = 0;
		m_srcPaths = ".";
		m_progress = new Progress(Progress.State.IDLE, 0);
		m_jobMonitor = null;
		m_sections = new Sections();

		ReferenceTypeFactory.setEngine(this);
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
		notify(Notifier.NOTIFY_SELECT_ROUTE, "");
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
		return m_xeObjects.get(m_nodeId, m_coreId);
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
		
		NodeCoreCollection<XeObject> newXeObjects = new NodeCoreCollection<XeObject>();
		NodeCoreCollection<StructureGraph> newStructuralGraph = new NodeCoreCollection<StructureGraph>();
		NodeCoreCollection<ControlFlowGraph> newCfg = new NodeCoreCollection<ControlFlowGraph>();
		NodeCoreCollection<InstructionDecoder> newInstructionDecoder = new NodeCoreCollection<InstructionDecoder>();
		
		for(int nodeId : xeLoader.getNodeIds()){
			for(int coreId : xeLoader.getCoreIds(nodeId)){
				InstructionDecoder id = new InstructionDecoder(nodeId, coreId, xeLoader, m_config);
				
				if(!id.decode()) 
					return false;
				
				ThreadHandler th = new ThreadHandler(id, this);
				Collection<Node> threadStartNodes = 
					th.getThreadNodes(sections.getThreadStartPoints(nodeId, coreId));
				Collection<Node> threadStopNodes = 
					th.getThreadNodes(sections.getThreadStopPoints(nodeId, coreId));				
				threadStartNodes = th.ensureThreadStartExists(threadStartNodes);
				
				SuccessorAdder sa = new SuccessorAdder(nodeId, coreId, id, sections, threadStopNodes, xeLoader, this);
				if (!sa.process()) 
					return false;

				FunctionHandler fh = new FunctionHandler(id);
				if (!fh.addFunctions()) 
					return false;
				
				UnreachableCodeFinder uca = new UnreachableCodeFinder();
				uca.analyseReachable(id, threadStartNodes, fh.getKnownFuncStarts()); 
	
				newXeObjects.add(nodeId, coreId, new XeObject(xeLoader, nodeId, coreId, fileFinder));

				ControlFlowGraph cfg = new ControlFlowGraph(threadStartNodes, fh.getKnownFuncStarts(), fh.getFunctions());
				newCfg.add(nodeId, coreId, cfg);
				
				StructuralAnalysisEngine sae = new StructuralAnalysisEngine(cfg);
				if (!sae.analyse(this))
					return false;
				
				newStructuralGraph.add(nodeId, coreId, sae.getStructureGraph());
			}		
		}
		
		m_instructionDecoder = newInstructionDecoder;
		m_sections = sections;
		m_xeObjects = newXeObjects;
		m_controlFlowGraph = newCfg;
		m_structuralGraph = newStructuralGraph;
		m_xeLoader = xeLoader;
		m_executableFile = executableFile;
		m_nodeId = 0;
		m_coreId = 0;			
		
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

 		Route route = new Route(this, m_structuralGraph.get(m_nodeId, m_coreId), routeDescription);
			
		//FIXME
		
		//route.setConstrained(false);
			
		notify(Notifier.NOTIFY_ANALYSIS_BEGIN, "");
		m_progress.resetState(Progress.State.PROCESSING_PATHS);
		
		route.analyse();		

		if (route.getSubRoutes().size() == 0) {
			/*
			if (route.wasConstrained()) {
				notify(Notifier.NOTIFY_ERROR, "No valid paths of execution (constraints applied)");
			} else {
				notify(Notifier.NOTIFY_ERROR, "No valid paths of execution");
			}
			*/
		}
		
		m_routes.add(route);
		setSelectedRoute(route);

		notify(Notifier.NOTIFY_ANALYSIS_COMPLETE, Integer.toString(route.getRouteId()));

		return true;
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
	
	public boolean setInstructionTimeTaken(int routeId, String refString, double timeTaken) {
		Reference reference = ReferenceFactory.createReference(
				refString, ReferenceClass.ENDPOINT_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}
		if (getRoute(routeId).setInstructionTimeTaken(reference, timeTaken)) {
			notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
			return true;
		}
		return false;
	}

	public boolean unsetInstructionTimeTaken(int routeId, String refString) {
		Reference reference = ReferenceFactory.createReference(
				refString, ReferenceClass.ENDPOINT_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}
		if (getRoute(routeId).unsetInstructionTimeTaken(reference)) {
			notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
			return true;
		}
		return false;
	}
	
	public double getInstructionTimeTaken(int routeId, String refString) {
		Reference reference = ReferenceFactory.createReference(
				refString, ReferenceClass.ENDPOINT_REF);
		if (!resolveRefernece(reference)) {
			return 0;
		}
		return getRoute(routeId).getInstructionTimeTaken(reference);
	}
	
	public boolean setInstructionNumCycles(int routeId, String refString, long numCycles) {
		Reference reference = ReferenceFactory.createReference(
				refString, ReferenceClass.ENDPOINT_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}
		if (getRoute(routeId).setInstructionNumCycles(reference, numCycles)) {
			notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
			notify(Notifier.NOTIFY_UPDATE_STATE, "");
			return true;
		}
		return false;
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
	
	public long getInstructionNumCycles(int routeId, String refString) {
		Reference reference = ReferenceFactory.createReference(
				refString, ReferenceClass.ENDPOINT_REF);
		if (!resolveRefernece(reference)) {
			return 0;
		}
		return getRoute(routeId).getInstructionNumCycles(reference);		
	}
		
	public boolean canInstructionPause(int routeId, String refString) {
		Reference reference = ReferenceFactory.createReference(
				refString, ReferenceClass.ENDPOINT_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}
		return getRoute(routeId).canInstructionPause(reference);			
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

	public boolean addExclusion(String refString) {
		Reference reference = ReferenceFactory.createReference(
				refString, ReferenceClass.ANY_REF);
		if (!resolveRefernece(reference)) {
			return false;
		}
		m_exclusions.add(reference);		
		return true;
	}
	
	public boolean removeExclusion(String refString) {
		if (refString.equals("*")) {
			m_exclusions.clear();

		} else {
			Reference reference = ReferenceFactory.createReference(
					refString, ReferenceClass.ANY_REF);
			if (!resolveRefernece(reference)) {
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
		return true;
	}

	public Collection<Reference> getExclusions() {
		return m_exclusions;
	}
	
	public boolean addBranch(Reference fromRef, Reference toRef) {
		if (!resolveRefernece(fromRef)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Unresolved reference: %s", fromRef.getRef()));
			return false;
		} else if (!resolveRefernece(toRef)) {
			notify(Notifier.NOTIFY_ERROR, String.format("Unresolved reference: %s", toRef.getRef()));
			return false;
		}		
		
		if (m_sections.addToBranches(m_nodeId, m_coreId, fromRef.getPc(), toRef.getPc())) {
			//FIXME
			//  m_ae.run();
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
		    //FIXME
			//  m_ae.run();
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
	
	public void setMonitor(IProgressMonitor monitor) {
		m_jobMonitor = monitor;
	}
	
	public void provideFeedback(String feedback) {
		if (m_jobMonitor != null)
			m_jobMonitor.subTask(feedback);				
	}
	
	public Collection<String> getFunctionsVisited() {
		return m_functionsVisited;
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
		
	// ===================================================================== //
	// Debug Methods
	// ===================================================================== //
	public void printCFG(InstructionDecoder id, FunctionHandler pfh, Collection<Node> threadStarts, 
			Collection<Node> threadEnds, Collection<Node> functionStarts) {
		System.out.println("digraph g {");
		for(Node n : id.getInstructions()){
			System.out.print(n + " [label=\"" + n.getInstructions() + "\"");
			if(threadStarts.contains(n)){
				System.out.print(", style=filled, color = green");
			} else if(functionStarts.contains(n)){
				System.out.print(", style=filled, color = lightblue");
			} else if(threadEnds.contains(n)){
				System.out.print(", style=filled, color = pink");
			} 
			System.out.println(", shape=box]; ");
		}
		for(FunctionNode n : pfh.getFunctions()){
			System.out.println(n + " [label=\"" + n.getFunctionName() + "\"]; ");
		}
		for(Node n : id.getInstructions()){
			for(Node next : n.getNextNodes()){
				System.out.println(n + " -> " + next + ";");
			}
		}
		for(Node n : pfh.getFunctions()){
			for(Node next : n.getNextNodes()){
				System.out.println(n + " -> " + next + ";");
			}
		}
		System.out.println("}");
	}	
}
