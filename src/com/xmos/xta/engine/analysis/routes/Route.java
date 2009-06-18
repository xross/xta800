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
import com.xmos.xta.engine.analysis.structural.ReferenceMatcher;
import com.xmos.xta.engine.analysis.structural.StructuralAnalysisEngine;
import com.xmos.xta.engine.analysis.structural.StructureGraph;
import com.xmos.xta.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownType;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.engine.analysis.structural.patterns.CommonMethods;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.Reference;

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
	private Collection<Node> m_exclusions;
	private Set<Node> m_excludedRouteNodes;
	
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
		createExclusions();
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
	
	public List<Node> getSubRoutes() {
		return m_subRoutes;
	}

	public void analyse() {
		switch (m_description.getMode()) {
		case FUNCTION:
			Map<String, HashSet<Node>> functionMap = buildFunctionMap(m_structureGraph);
			HashSet<Node> functions = functionMap.get(m_description.getFunctionName());
			if (functions != null) {
				for(Node function : functions){
					m_subRoutes.add(function.deepCopy(new HashMap<Integer, FunctionNode>()));
				}
				
			}			
			setParents();
			sortSubRoutes();
			break;

		case PATH:
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
			
			
			for (Node start : startNodes) {
				Collection<Node> workingSet = new HashSet<Node>();

				m_structureGraph.removeLinks();
				m_excludedRouteNodes = new HashSet<Node>();
				explore(start, workingSet, endPcs, endNodes, null);
								
				StructuralAnalysisEngine sae = new StructuralAnalysisEngine();								
				pruneGraphOfExcludedPaths(workingSet);
				
				if (false) StructuralAnalysisEngine.dumpTree(workingSet);
				Node newStart = makeBasicBlocks(start, endNodes, workingSet);
				
				//copy every node into a new working set
				sae.createStructure(workingSet, newStart, m_engine, true);

				Node baseNode = workingSet.iterator().next();
				Node newGraph = baseNode.deepCopy(new HashMap<Integer, FunctionNode>());
			
				setUnknowns(newGraph);				
				setInitialIterations(newGraph);
				
				m_subRoutes.add(newGraph);
			}
			setParents();
			sortSubRoutes();
			break;
			
		default:
			assert(false);
		    break;
		}
	}

	private void pruneGraphOfExcludedPaths(Collection<Node> workingSet) {
		while(!m_excludedRouteNodes.isEmpty()){
			Node excludedNode = m_excludedRouteNodes.iterator().next();
			for(Node prev : excludedNode.getPrevNodes()){
				prev.removeNextNode(excludedNode);
				if(prev.getNextNodes().size() == 0){
					m_excludedRouteNodes.add(prev);
				}
			}
			m_excludedRouteNodes.remove(excludedNode);
			workingSet.remove(excludedNode);
			
		}
		
	}
		
	public boolean setLoopIterations(Reference reference, long iterations) {
		if (iterations < 0)
			return false;
		
		boolean found = false;
		for (Node node : getReferencedNodes(reference)) {						
			Node enclosingLoopNode = node.getEnclosingLoopNode();
			if (enclosingLoopNode != null) {				
				enclosingLoopNode.getAttributes().setLoopIterations(iterations);
				enclosingLoopNode.getAttributes().setUnknownState(UnknownState.KNOWN);	
				found = true;
			}
		}		
		if (found) {
			refreshDisplayedUnknowns();
			sortSubRoutes();
		}		
		return found;
	}

	public boolean setLoopPathIterations(Reference reference, long iterations) {
		if (iterations < 0)
			return false;

		boolean found = false;
		for (Node node : getReferencedNodes(reference)) {						
			Node loopNode = node.getEnclosingLoopNode();
			if (loopNode == null)
				continue;
			
			Node sectionNode = node.getEnclosingSectionNode();
			if (sectionNode == null)
				continue;
			
			Node optionNode = node.getEnclosingOptionNode();
			if (optionNode == null)
				continue;
			
			if (!sectionNode.getAttributes().setLoopPathIterations(loopNode, optionNode, iterations))
				continue;
			
			found = true;
		}		
		if (found) {
			refreshDisplayedUnknowns();			
			sortSubRoutes();
		}		
		return found;		
	}
	
	public boolean setLoopScope(Reference reference, boolean isAbsolute) {
		boolean found = false;
	    for (Node node : getReferencedNodes(reference)) {
			Node enclosingLoopNode = node.getEnclosingLoopNode();
			if (enclosingLoopNode != null) {				
				enclosingLoopNode.getAttributes().setIsAbsolute(isAbsolute);
				found = true;
			}
		}		
		if (found) {
			sortSubRoutes();
		}		
		return found;
	}
	
	public boolean setInstructionTimeTaken(Reference reference, double timeTaken) {
		boolean found = false;
	    for (Node node : getReferencedNodes(reference)) {
	    	node.getAttributes().setInstructionTimeTaken(timeTaken);
			node.getAttributes().setUnknownState(UnknownState.KNOWN);	
	    	found = true;
		}
		if (found) {
			refreshDisplayedUnknowns();			
			sortSubRoutes();
		}		
		return found;
	}

	public boolean unsetInstructionTimeTaken(Reference reference) {
		boolean found = false;
	    for (Node node : getReferencedNodes(reference)) {
	    	node.getAttributes().unsetInstructionTimeTaken();
			node.getAttributes().setUnknownState(UnknownState.UNKNOWN);	
	    	found = true;
		}
		if (found) {
			refreshDisplayedUnknowns();
			sortSubRoutes();
		}		
		return found;
	}
	
	public boolean setInstructionNumCycles(Reference reference, long numCycles) {
		boolean found = false;
	    for (Node node : getReferencedNodes(reference)) {
	    	node.getAttributes().setInstructionNumCycles(numCycles);
			node.getAttributes().setUnknownState(UnknownState.KNOWN);	
	    	found = true;
		}
		if (found) {
			refreshDisplayedUnknowns();			
			sortSubRoutes();
		}		
		return found;
	}
	
	public boolean unsetInstructionNumCycles(Reference reference) {
		boolean found = false;
        for (Node node : getReferencedNodes(reference)) {
        	node.getAttributes().unsetInstructionNumCycles();
			node.getAttributes().setUnknownState(UnknownState.UNKNOWN);	
	    	found = true;
		}
		if (found) {
			refreshDisplayedUnknowns();			
			sortSubRoutes();
		}		
		return found;
	}
	
	public double getInstructionTimeTaken(Reference reference) {
		// TODO
		return 0;
	}
	
	public long getInstructionNumCycles(Reference reference) {
		// TODO
		return 0;
	}
	
	public boolean canInstructionPause(Reference reference) {
		// TODO
		return false;
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
		if (pcs.isEmpty() || set.isEmpty()) {
			makeConnection(node, parent);
			if(set.contains(node)) {
				return;
			}
			set.add(node);
			Collection<Node> exclusions = new HashSet<Node>(m_exclusions);
			exclusions.retainAll(node.getContainedNodes());
			if(!exclusions.isEmpty()){
				m_excludedRouteNodes.add(node);
				return;
			}
			assert(node.getParent() != null);
			if (!node.getParent().whatsNext(node).isEmpty()) {
				for (Node next : node.getParent().whatsNext(node)) {
					explore(next, set, endPcs, endNodes, node);
				}
			}
		} else {
			if (endNodes.contains(node)) {
				makeConnection(node, parent);
				set.add(node);
				return;
			} else {
				assert (!node.getImmediateChildren().isEmpty());
				for(Node child : node.getImmediateChildren()){
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
	private static int c = 0;
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
					c++;
					return true;
				}
			});
		}
		//System.out.println(c);
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

	private void setParents() {
		for (Node node : m_subRoutes) {
			setParent(node);
		}
	}

	private void setParent(Node node) {
		for (Node child : node.getChildren()){
			child.setParent(node);
			setParent(child);
		}
	}
	
	private void sortSubRoutes() {
		Collections.sort(m_subRoutes);
	}		
	
	private void createExclusions() {
		m_exclusions = new ArrayList<Node>();
		
		ReferenceMatcher referenceMatcher = new ReferenceMatcher();		
		for (Reference reference : m_engine.getExclusions()) {
			for (Node threadNode : m_structureGraph.getThreads()) {
    			m_exclusions.addAll(referenceMatcher.getNodes(threadNode, reference));
			}
		}
	}
	
	private void setInitialIterations(Node node) {		
		// Sets the top level nodes to iterate once..
	    node.getAttributes().setLoopIterations(1);
	    
		// Sets all the loops to zero iterations..							
	    node.traverse(0, new DefaultNodeTraverser() {
	        public boolean foundLoopNode(Node node, int depth) {
	           	node.getAttributes().setLoopIterations(0);
	         	return true;
	        }	        	
	    });
	}
	
	private void setUnknowns(Node node) {
	    node.traverse(0, new DefaultNodeTraverser() {
			// Sets all the pausing instructions to unknown.
			public boolean foundInstructionNode(Node node, int depth) {
				XtaInstruction instruction = ((InstructionNode)node).getXtaInstruction();
				if (instruction.canPause()) {
					NodeAttributes nodeAttributes = node.getAttributes();
					nodeAttributes.setUnknownType(UnknownType.INSTRUCTION_TIME);
				}
				return true;
			}

			// Sets all the loop iterations to unknown.
			public boolean foundLoopNode(Node node, int depth) {
				NodeAttributes nodeAttributes = node.getAttributes();
				nodeAttributes.setUnknownType(UnknownType.LOOP_ITERATIONS);
				return true;
			}
	    });			
		
	    refreshDisplayedUnknowns(node);
	}
	
	private void refreshDisplayedUnknowns() {
		for (Node subRouteNode : m_subRoutes) {
			refreshDisplayedUnknowns(subRouteNode);
		}
	}
	
	private void refreshDisplayedUnknowns(Node node) {	
		// Clears the display unknown flags on all nodes..
	    node.traverse(0, new UntypedNodeTraverser() {
	    	public boolean foundNode(Node node, int depth) {
	    		node.getAttributes().setDisplayUnknown(false);
	        	return true;
	    	}
	    });

		// On each of the unknown nodes, mark all of their parents to 'display unknown'.
	    node.traverse(0, new UntypedNodeTraverser() {
	    	public boolean foundNode(Node node, int depth) {
	    		if (node.getAttributes().isUnknown() &&
	    			node.getAttributes().getUnknownState() == UnknownState.UNKNOWN) {
	    			setDisplayUnknownOnParentNodes(node);
	    		}
	        	return true;
	    	}
	    });	   
	}
	
	private void setDisplayUnknownOnParentNodes(Node node) {
		Node parent = node;
		while (parent != null) {
			parent.getAttributes().setDisplayUnknown(true);
			parent = parent.getParent();		    
		}
	}		
	
	private Collection<Node> getReferencedNodes(Reference reference) {
		ReferenceMatcher referenceMatcher = new ReferenceMatcher();		
		Collection<Node> referencedNodes = new ArrayList<Node>();
		for (Node subRouteNode : m_subRoutes) {
			referencedNodes.addAll(referenceMatcher.getNodes(subRouteNode, reference));
		}
		return referencedNodes;		
	}
}
