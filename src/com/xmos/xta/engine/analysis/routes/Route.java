package com.xmos.xta.engine.analysis.routes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.RouteDescription;
import com.xmos.xta.engine.analysis.routes.RouteDescription.RouteMode;
import com.xmos.xta.engine.analysis.structural.StructuralAnalysisEngine;
import com.xmos.xta.engine.analysis.structural.StructureGraph;
import com.xmos.xta.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.engine.analysis.structural.nodes.DefaultNodeTraverser;
import com.xmos.xta.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.SectionNode;
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

	public void addExclededReference(Reference ref) {
		m_excludedRefs.add(ref);
		buildExclustion();
	}

	public Collection<Reference> getExcludedReferences() {
		return m_excludedRefs;
	}

	public void removeExcludedReference(Reference ref) {
		m_excludedRefs.remove(ref);
		buildExclustion();
	}

	public boolean isExcluded(Node node) {
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
		if (m_description.getMode() == RouteMode.FUNCTION) {
			Map<String, HashSet<Node>> functionMap = buildFunctionMap(m_structureGraph);
			HashSet<Node> functions = functionMap.get(m_description.getFunctionName());
			if (functions != null) {
				m_routes.addAll(functions);
			}
		} else if (m_description.getMode() == RouteMode.ABSLOUTE) {

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
				Collection<Node> workingSet = new HashSet<Node>();
				explore(start, workingSet, endPcs, endNodes, null);

				StructuralAnalysisEngine sae = new StructuralAnalysisEngine();

				Node newStart = makeBasicBlocks(start, endNodes, workingSet);
				// Node newStart = start;
				sae.createStructure(workingSet, newStart, m_engine);

				m_structureGraph.removeLinks();

				m_routes.add(workingSet.iterator().next());
			}
		} else {
			assert (false);
		}

	}

	

	private void buildExclustion() {
		// this uses the m_excludedRefs to populate the m_routeExclusionNodes
		Set<Node> seenNodes = new HashSet<Node>();
		for (Reference ref : m_excludedRefs) {
			Collection<Node> refNodes = null;
			//crawl up to my highest point then set the whole branch as excluded
			
			for(Node refNode : refNodes){
				if(!seenNodes.contains(refNode)){
					Node currentNode = refNode;
					while(!currentNode.getParent().isSection() || noOptionsLeft(currentNode)){
						currentNode = currentNode.getParent();
					}
					Set<Node> branch = getBranch(currentNode);
					m_routeExclusionNodes.addAll(branch);
				}
				seenNodes.add(refNode);
			}
		}
	}
	
	private boolean noOptionsLeft(Node currentNode) {
		// TODO Auto-generated method stub
		return false;
	}

	private Set<Node> getBranch(Node start) {
		final Set<Node> nodes = new HashSet<Node>();
		start.traverse(0, new DefaultNodeTraverser() {
			public boolean foundInstructionNode(Node node, int depth) {
				nodes.add(node);
				return true;
			}
		});
		return nodes;
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
				if (!node.getChildren().isEmpty()) {
					explore(node.getChildren().get(0), set, endPcs, endNodes, parent);
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
					HashSet<Node> map = pcToNodes.get(((FunctionNode) node).getFunctionName());
					if (map == null) {
						map = new HashSet<Node>();
					}
					map.add(node);
					pcToNodes.put(((FunctionNode) node).getFunctionName(), map);
					return true;
				}
			});
		}
		return pcToNodes;
	}
	
	private Node makeBasicBlocks(Node start, Collection<Node> endNodes, Collection<Node> workingSet) {
		// from the start instruction crawl downwards stopping at basic block
		// boundaries
		Node currentNode = start;
		List<Node> startList = new LinkedList<Node>();
		startList.add(currentNode);

		while (currentNode.getNextNodes().size() == 1 && currentNode.getNextNodes().iterator().next().isInstructionNode()) {
			currentNode = currentNode.getNextNodes().iterator().next();
			startList.add(currentNode);
		}
		BlockNode startBlock = new BlockNode(startList);
		workingSet.removeAll(startList);
		workingSet.add(startBlock);

		for (Node next : currentNode.getNextNodes()) {
			next.removePrevNode(currentNode);
			CommonMethods.connect(next, startBlock);
		}

		// from every end node in the working set crawl upwards stopping at
		// basic block boundaries
		for (Node endNode : endNodes) {
			if (workingSet.contains(endNode)) {
				List<Node> endList = new LinkedList<Node>();
				Node end = endNode;
				endList.add(end);
				while (end.getPrevNodes().size() == 1 && end.getPrevNodes().iterator().next().isInstructionNode()) {
					end = end.getPrevNodes().iterator().next();
					endList.add(0, end);
				}

				BlockNode endBlock = new BlockNode(endList);
				workingSet.removeAll(endList);
				workingSet.add(endBlock);

				for (Node prev : end.getPrevNodes()) {
					prev.removeNextNode(end);
					CommonMethods.connect(endBlock, prev);
				}
			}
		}
		return startBlock;
	}
}
