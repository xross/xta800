package com.xmos.xta.engine.analysis.routes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.RouteDescription;
import com.xmos.xta.engine.analysis.routes.RouteDescription.RouteMode;
import com.xmos.xta.engine.analysis.structural.StructuralAnalysisEngine;
import com.xmos.xta.engine.analysis.structural.StructureGraph;
import com.xmos.xta.engine.analysis.structural.nodes.DefaultNodeTraverser;
import com.xmos.xta.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.patterns.CommonMethods;
import com.xmos.xta.engine.constraint.Constraint;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.unknowns.Unknowns;

class AbortedException extends Exception {
	private static final long serialVersionUID = 1L;
}

public class Route {
	
	private static int s_routeCount;
	public static final int INVALID_ROUTE_ID = -1;
	public static int WORST_CASE_PATH_ID = -2;
	
	enum RouteStatus {
		COMPLETED, ABORTED, ERROR
	}

	private int m_routeId;
	private RouteDescription m_description;
	private StructureGraph m_structureGraph;
	private Collection<Node> m_routes;
	private Engine m_engine;
	private RouteTiming m_routeTiming;
	private Unknowns m_unknowns;
	
	private Constraint m_constraint;
	private Set<Node> m_routeExclusionNodes;
	Collection<Reference> m_excludedRefs;
	
	public static void resetRouteCount() {
		s_routeCount = 0;
	}

	public Route(Engine engine, StructureGraph structureGraph, RouteDescription description) {
		m_structureGraph = structureGraph;
		m_description = description;
		m_routeId = s_routeCount++;
		m_routes = new HashSet<Node>();
		m_engine = engine;
		m_constraint = engine.getConstraint().clone();
		m_routeTiming = new RouteTiming(this, engine.getConfig(), engine.getNodeId(), engine.getCoreId());
		m_unknowns = new Unknowns();
		m_routeExclusionNodes = new HashSet<Node>();
		m_excludedRefs = new HashSet<Reference>();
	}

	public void addExclededReference(Reference ref){
		m_excludedRefs.add(ref);
		buildExclustion();
	}
	
	public Collection<Reference> getExcludedReferences(){
		return m_excludedRefs;
	}
	
	public void removeExcludedReference(Reference ref){
		m_excludedRefs.remove(ref);
		buildExclustion();
	}
	
	public boolean isExcluded(Node node){
		return m_routeExclusionNodes.contains(node) || node.isExcluded();
	}

	public int getRouteId() {
		return m_routeId;
	}

	public String getDescription() {
		return m_description.getDescription();
	}

	public RouteTiming getRouteTiming() {
		return m_routeTiming;
	}

	public Unknowns getUnknowns() {
		return m_unknowns;
	}

	public String getEndpointsPcsString() {
		return "";
	}

	public Set<Integer> getPcsHit() {
		final Set<Integer> pcs = new HashSet<Integer>();
		for (Node node : m_routes) {
			node.traverse(0, new DefaultNodeTraverser() {
				public boolean foundInstructionNode(Node node, int depth) {
					pcs.add(node.getStartPc());
					return true;
				}
			});
		}
		return pcs;
	}

	public Constraint getConstraint() {
		return m_constraint;
	}

	public Collection<Node> getCompletedPaths() {
		return m_routes;
	}
	
	public void analyse() {
		if(m_description.getMode() == RouteMode.FUNCTION){
			Map<String, HashSet<Node>> functionMap = buildFunctionMap(m_structureGraph);
			HashSet<Node> functions = functionMap.get(m_description.getFunctionName());
			if(functions!= null){
				m_routes.addAll(functions);
			}
		} else if(m_description.getMode() == RouteMode.ABSLOUTE) {
		
			Set<Integer> startPcs = getStartPcs();
			Set<Integer> endPcs = getEndPcs();
			
			Collection<Node> startNodes = new HashSet<Node>();
			Collection<Node> endNodes = new HashSet<Node>();
			Map<Integer, HashSet<Node>> pcToStruct = buildPcMap(m_structureGraph);
	
			for (int from : startPcs) {
				if (pcToStruct.get(from) != null) {
					startNodes.addAll(pcToStruct.get(from));
				}
			}
			for (int to : endPcs) {
				if (pcToStruct.get(to) != null) {
					endNodes.addAll(pcToStruct.get(to));
				}
			}
			m_structureGraph.removeLinks();
			for (Node start : startNodes) {
				Collection<Node> setExplored = new HashSet<Node>();
				explore(start, setExplored, endPcs, endNodes, null);
	
				StructuralAnalysisEngine sae = new StructuralAnalysisEngine();
				sae.createStructure(setExplored, start, m_engine);
	
				m_structureGraph.removeLinks();
	
				m_routes.add(setExplored.iterator().next());
			}
		} else {
			assert(false);
		}

	}
	
	private void buildExclustion(){
		//this uses the m_excludedRefs to populate the m_routeExclusionNodes

		for(Reference ref : m_excludedRefs){
			Collection<Node> refNodes = null;
			
			
			
		}
	}
	
	private Set<Integer> getEndPcs() {
		Set<Integer> endPcs = new HashSet<Integer>();
		endPcs.add(m_description.getEndPoint().getPc());
		return endPcs;
	}

	private Set<Integer> getStartPcs() {
		Set<Integer> startPcs = new HashSet<Integer>();
		startPcs.add(m_description.getStartPoint().getPc());
		return startPcs;
	}

	private void explore(Node node, Collection<Node> set, Collection<Integer> endPcs, Collection<Node> endNodes, Node parent) {

		Set<Integer> pcs = new HashSet<Integer>(endPcs);
		pcs.retainAll(node.getPCs());
		// if the node does not contain the pc then just add the node
		if (pcs.isEmpty()) {
			makeConnection(node, parent);
			set.add(node);
			if (node.getParent().whatsNext(node) != null) {
				for (Node next : node.getParent().whatsNext(node)) {
					explore(next, set, endPcs, endNodes, node);
				}
			}
		} else {
			// if this is an instruction then add the node and we are done
			if (endNodes.contains(node)) {
				makeConnection(node, parent);
				set.add(node);
				return;
			} else {
				// dont add the node and start to drop down until it is found.
				for (Node children : node.getChildern()) {
					explore(children, set, endPcs, endNodes, parent);
					break;
				}
			}
		}
		return;
	}

	private void makeConnection(Node node, Node parent) {
		if (parent != null) {
			CommonMethods.connect(node, parent);
		}
	}

	private Map<Integer, HashSet<Node>> buildPcMap(StructureGraph structureGraph) {
		final Map<Integer, HashSet<Node>> pcToNodes = new HashMap<Integer, HashSet<Node>>();
		for (Node node : structureGraph.getThreads()) {
			node.traverse(0, new DefaultNodeTraverser() {
				public boolean foundInstructionNode(Node node, int depth) {
					HashSet<Node> map = pcToNodes.get(node.getStartPc());
					if (map == null) {
						map = new HashSet<Node>();
					}
					map.add(node);
					pcToNodes.put(node.getStartPc(), map);
					return true;
				}
			});
		}
		return pcToNodes;
	}
	
	private Map<String, HashSet<Node>> buildFunctionMap(StructureGraph structureGraph) {
		final Map<String, HashSet<Node>> pcToNodes = new HashMap<String, HashSet<Node>>();
		for (Node node : structureGraph.getThreads()) {
			node.traverse(0, new DefaultNodeTraverser() {
				public boolean foundFunctionNode(Node node, int depth) {
					HashSet<Node> map = pcToNodes.get(((FunctionNode)node).getFunctionName());
					if (map == null) {
						map = new HashSet<Node>();
					}
					map.add(node);
					pcToNodes.put(((FunctionNode)node).getFunctionName(), map);
					return true;
				}
			});
		}
		return pcToNodes;
	}
}
