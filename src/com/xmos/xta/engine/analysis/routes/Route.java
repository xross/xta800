package com.xmos.xta.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.RouteDescription;
import com.xmos.xta.engine.analysis.structural.StructuralAnalysisEngine;
import com.xmos.xta.engine.analysis.structural.StructureGraph;
import com.xmos.xta.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.engine.analysis.structural.patterns.CommonMethods;

class AbortedException extends Exception {
	private static final long serialVersionUID = 1L;
}

public class Route {
	private static int s_routeCount;
	public static final int INVALID_ROUTE_ID = -1;

	enum RouteStatus {
		COMPLETED, ABORTED, ERROR
	}

	private int m_routeId;
	private RouteDescription m_description;
	private RouteTiming m_routeTiming;
	private StructureGraph m_structureGraph;
	private List<Node> m_subRoutes;
	private Engine m_engine;
	
	public static void resetRouteCount() {
		s_routeCount = 0;
	}

	public Route(Engine engine, StructureGraph structureGraph, RouteDescription description) {
		m_structureGraph = structureGraph;
		m_description = description;
		m_routeId = s_routeCount++;
		m_subRoutes = new ArrayList<Node>();
		m_engine = engine;
		m_routeTiming = new RouteTiming(this, engine.getConfig(), engine.getNodeId(), engine.getCoreId());
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

	public String getEndpointsPcsString() {
		return "";
	}

	public Set<Integer> getPcsHit() {
		final Set<Integer> pcs = new HashSet<Integer>();
		for (Node node : m_subRoutes) {
			node.traverse(0, new DefaultNodeTraverser() {
				public boolean foundInstructionNode(Node node, int depth) {
					pcs.add(node.getStartPc());
					return true;
				}
			});
		}
		return pcs;
	}
	
	public Collection<Node> getSubRoutes() {
		return m_subRoutes;
	}

	public void analyse() {
		switch (m_description.getMode()) {
		case FUNCTION:
			Map<String, HashSet<Node>> functionMap = buildFunctionMap(m_structureGraph);
			HashSet<Node> functions = functionMap.get(m_description.getFunctionName());
			if (functions != null) {
				m_subRoutes.addAll(functions);
			}
			sortSubRoutes();
			break;
			
		case ABSOLUTE:
			Set<Integer> startPcs = getStartPcs();
			Set<Integer> endPcs = getEndPcs();

			Collection<Node> startNodes = new HashSet<Node>();
			Collection<Node> endNodes = new HashSet<Node>();
			Map<Integer, HashSet<Node>> pcToStruct = buildPcMap(m_structureGraph);

			for (int from : startPcs) {
				if (pcToStruct.get(from) != null) {
					
					HashSet<Node> start = pcToStruct.get(from);
					for(Node node : start){
						Node nodeToAdd = node;
						if (nodeToAdd.getParent() != null && !nodeToAdd.getParent().isInstructionNode()){
							nodeToAdd = nodeToAdd.getParent();
						}
						startNodes.add(nodeToAdd);
					}
				}
			}
			for (int to : endPcs) {
				if (pcToStruct.get(to) != null) {
					endNodes.addAll(pcToStruct.get(to));
				}
			}
			
			if (debug) System.out.println(endPcs);
			for (Node start : startNodes) {
				Collection<Node> workingSet = new HashSet<Node>();

				m_structureGraph.removeLinks();
				explore(start, workingSet, endPcs, endNodes, null);

				StructuralAnalysisEngine sae = new StructuralAnalysisEngine();
				
				StructuralAnalysisEngine.dumpTree(workingSet);
				Node newStart = makeBasicBlocks(start, endNodes, workingSet);
				
				sae.createStructure(workingSet, newStart, m_engine);

				m_subRoutes.add(workingSet.iterator().next());
			}
			sortSubRoutes();
			break;
			
		default:
			assert(false);
		    break;
		}
	}

	public void sortSubRoutes() {
		Collections.sort(m_subRoutes);
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

	private boolean debug = false;
	
	private void explore(Node node, Collection<Node> set, Collection<Integer> endPcs, Collection<Node> endNodes, Node parent) {
		
		//if(set.contains(node)) return;
		if (debug) System.out.println(node);
		Set<Integer> pcs = new HashSet<Integer>(endPcs);
		pcs.retainAll(node.getPCs());

		if (debug) System.out.println("\t" + node + " contains an end pc: " + !pcs.isEmpty() );
		// if the node does not contain the pc then just add the node
		if (pcs.isEmpty() || set.isEmpty()) {
			
			makeConnection(node, parent);
			if(set.contains(node)) {
				if (debug) System.out.println("ive seen this node alreads");
				return;
			}
			if (debug) System.out.println("this node does not contain the end node");
			set.add(node);
			if (!node.getParent().whatsNext(node).isEmpty()) {
				for (Node next : node.getParent().whatsNext(node)) {
					if (debug) System.out.println("following next node of " + node + " -> " + next);
					explore(next, set, endPcs, endNodes, node);
				}
			}
		} else {

			if (debug) System.out.println("this node does contain the end node");
			// if this is an instruction then add the node and we are done
			if (endNodes.contains(node)) {
				if (debug) System.out.println("found end node");
				makeConnection(node, parent);
				set.add(node);
				return;
			} else {
				assert (!node.getImmediateChildren().isEmpty());
				for(Node child : node.getImmediateChildren()){
					if (debug) System.out.println("following immediate childer of " + node + " -> " + child);
					explore(child, set, endPcs, endNodes, parent);
				}
			}
		}
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
		Node startBlock = null;
		if(!start.isBlockNode()){
			Node currentNode = start;
			List<Node> startList = new LinkedList<Node>();
			
			startList.add(currentNode);
	
			while (currentNode.getNextNodes().size() == 1 && currentNode.getNextNodes().iterator().next().isInstructionNode()) {
				currentNode = currentNode.getNextNodes().iterator().next();
				startList.add(currentNode);
			}
			startBlock = new BlockNode(startList);
			workingSet.removeAll(startList);
			workingSet.add(startBlock);
	
			for (Node next : currentNode.getNextNodes()) {
				next.removePrevNode(currentNode);
				CommonMethods.connect(next, startBlock);
			}
		} else {
			startBlock = start;
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
