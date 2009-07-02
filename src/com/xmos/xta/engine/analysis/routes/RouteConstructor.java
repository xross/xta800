package com.xmos.xta.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;
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
import com.xmos.xta.engine.analysis.structural.nodes.SelfLoopNode;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownType;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.engine.analysis.structural.patterns.CommonMethods;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.Reference;

public class RouteConstructor {

	enum RouteStatus {
		COMPLETED, ABORTED, ERROR
	}

	private int m_routeId;
	private RouteDescription m_description;
	private StructureGraph m_structureGraph;
	private List<Node> m_subRoutes;
	private Engine m_engine;
	private Collection<Node> m_exclusions;
	private Set<Node> m_excludedRouteNodes;

	public RouteConstructor(Engine engine, StructureGraph structureGraph, RouteDescription description) {
		m_structureGraph = structureGraph;
		m_description = description;
		m_subRoutes = new ArrayList<Node>();
		m_engine = engine;
		createExclusions();
	}

	public int getRouteId() {
		return m_routeId;
	}

	public String getDescription() {
		return m_description.getDescription();
	}
	
	public List<Node> getSubRoutes() {
		return m_subRoutes;
	}

	public boolean analyse() {
		boolean seenEndPoint = false;
		switch (m_description.getMode()) {
		case FUNCTION:
			
			
			
			Map<String, TreeSet<Node>> functionMap = buildFunctionMap(m_structureGraph);
			TreeSet<Node> functions = functionMap.get(m_description.getFunctionName());
			if (functions != null) {
				for(Node function : functions){
					if(m_subRoutes.isEmpty()){
						m_subRoutes.add(function.deepCopy(new TreeMap<Integer, FunctionNode>(), m_engine));
					} else {
						break;
					}
				}
			}
			
			if(m_subRoutes.isEmpty()){
				Collection<FunctionNode> functionss = m_structureGraph.getFunctions();
				for(FunctionNode function : functionss ){
					if(m_description.getFunctionName().equals(function.getFunctionName())){
						m_subRoutes.add(function.deepCopy(new TreeMap<Integer, FunctionNode>(), m_engine));
						break;
					}
				}
			}
			seenEndPoint = true;
			setParents();
			setUnknowns();				
			break;

		case PATH:
			
			Collection<Node> startNodes =getReferencedNodes(m_description.getStartPoint());
			Collection<Node> endNodes = getReferencedNodes(m_description.getEndPoint());
			for (Node start : startNodes) {
				Collection<Node> workingSet = new TreeSet<Node>();

				m_structureGraph.removeLinks();
				m_excludedRouteNodes = new TreeSet<Node>();
				seenEndPoint |= explore(start, workingSet, endNodes, null);
				
				StructuralAnalysisEngine sae = new StructuralAnalysisEngine();								
				pruneGraphOfExcludedPaths(workingSet);
				if(workingSet.isEmpty()){
					continue;
				}
		
				Set<Node> newEndNodes = removeFinalInstruction(endNodes, start,
						workingSet);
		
				Node newStart = makeBasicBlocks(start, newEndNodes, workingSet);
				assert(cfgSanityCheck(workingSet));
				
				sae.createStructure(workingSet, newStart, m_engine, true, "Analysing route");
				
				assert(workingSet.size() == 1);
				
				Node baseNode = workingSet.iterator().next();
				
				if(baseNode.isSelfLoopNode()) {
					SelfLoopNode sln = (SelfLoopNode)baseNode;
					baseNode = sln.getLoop();
				}
				
				Node newGraph = baseNode.deepCopy(new TreeMap<Integer, FunctionNode>(), m_engine);
							
				m_subRoutes.add(newGraph);
			}
			setParents();
			setUnknowns();				
			sortSubRoutes();
			break;
		default:
			assert(false);
		    break;
		}
		return seenEndPoint;
	}

	private boolean explore(Node node, Collection<Node> set, Collection<Node> endNodes, Node parent) {	
		Set<Node> nodes = new TreeSet<Node>(endNodes);
		nodes.retainAll(node.getContainedNodes());
		
		Collection<Node> exclusions = new TreeSet<Node>(m_exclusions);
		exclusions.retainAll(node.getContainedNodes());
		boolean seenEndPoint = false;

		if (nodes.isEmpty() || set.isEmpty()) {
			
			if(!exclusions.isEmpty() ){
				if( node.isBlockNode() ){
					
					if(set.contains(node)) {
						return false;
					}
					set.add(node);
					makeConnection(node, parent);
					m_excludedRouteNodes.add(node);
				} else {
					assert (!node.getImmediateChildren().isEmpty());
					for(Node child : node.getImmediateChildren()){
						seenEndPoint |= explore(child, set, endNodes, parent);
					}
				}
				return false;
			}
			makeConnection(node, parent);
			if(set.contains(node)) {
				return false;
			}
			set.add(node);

			assert(node.getParent() != null);
			if (!node.getParent().whatsNext(node).isEmpty()) {
				
				Collection<Node> nextNodes = node.getParent().whatsNext(node);
				for (Node next : nextNodes) {
					seenEndPoint |= explore(next, set, endNodes, node);
				}
			}
		} else {
			if (endNodes.contains(node)) {
				makeConnection(node, parent);
				set.add(node);
				return true;
			} else {
				assert (!node.getImmediateChildren().isEmpty());
				for(Node child : node.getImmediateChildren()){
					seenEndPoint |= explore(child, set, endNodes, parent);
				}
			}
		}
		return seenEndPoint;
	}
	
	private Set<Node> removeFinalInstruction(Collection<Node> endNodes,
			Node start, Collection<Node> workingSet) {
		
		Set<Node> newEndNodes = new TreeSet<Node>();
		for(Node node : endNodes){
			for(Node prev : node.getPrevNodes()){
				newEndNodes.add(prev);
			}
		}
		
		if(endNodes.contains(start)){
			endNodes.remove(start);
		}
		
		for(Node endNode : endNodes){
			for(Node prev : endNode.getPrevNodes()){
				CommonMethods.disconnect(endNode, prev);
			}
			workingSet.remove(endNode);
		}
		return newEndNodes;
	}
	
	private Node makeBasicBlocks(Node start, Collection<Node> endNodes, Collection<Node> workingSet) {
		// from the start instruction crawl downwards stopping at basic block
		// boundaries
		Node startBlock = null;
		if(!start.isBlockNode()){
			InstructionNode currentNode = (InstructionNode)start;
			List<InstructionNode> startList = new LinkedList<InstructionNode>();
			
			startList.add(currentNode);
	
			while (currentNode.getNextNodesSize() == 1 && currentNode.getNextNode().isInstructionNode()) {
				currentNode = (InstructionNode)currentNode.getNextNode();
				startList.add(currentNode);
			}
			startBlock = new BlockNode(startList);
			workingSet.removeAll(startList);
			workingSet.add(startBlock);
	
			for (Node next : currentNode.getNextNodes()) {
				next.removePrevNode(currentNode);
				CommonMethods.connect(next, startBlock);
			}
			for(Node prev  : start.getPrevNodes()){
				prev.removeNextNode(start);
				CommonMethods.connect(startBlock, prev);
			}
		} else {
			startBlock = start;
		}
		// from every end node in the working set crawl upwards stopping at
		// basic block boundaries
		for (Node endNode : endNodes) {
			if (workingSet.contains(endNode) && endNode.isInstructionNode()) {
				List<InstructionNode> endList = new LinkedList<InstructionNode>();
				InstructionNode end = (InstructionNode)endNode;
				endList.add(end);
				while (end.getPrevNodesSize() == 1 && end.getPrevNode().isInstructionNode()) {
					end = (InstructionNode)end.getPrevNode();
					endList.add(0, end);
				}
				BlockNode endBlock = new BlockNode(endList);
				workingSet.removeAll(endList);
				workingSet.add(endBlock);

				for (Node prev : end.getPrevNodes()) {
					prev.removeNextNode(end);
					CommonMethods.connect(endBlock, prev);
				}
				for (Node next : endNode.getNextNodes()) {
					next.removePrevNode(endNode);
					CommonMethods.connect(next, endBlock);
				}
			}
		}
		return startBlock;
	}
	
	
	private void pruneGraphOfExcludedPaths(Collection<Node> workingSet) {
		
		while(!m_excludedRouteNodes.isEmpty()){
			Node excludedNode = m_excludedRouteNodes.iterator().next();
			for(Node prev : excludedNode.getPrevNodes()){
				prev.removeNextNode(excludedNode);
				if(prev.getNextNodesSize() == 0){
					m_excludedRouteNodes.add(prev);
				}
			}
			m_excludedRouteNodes.remove(excludedNode);
			workingSet.remove(excludedNode);
		}
	}

	private void makeConnection(Node node, Node parent) {
		if (parent != null) {
			CommonMethods.connect(node, parent);
		}
	}

	private Map<String, TreeSet<Node>> buildFunctionMap(StructureGraph structureGraph) {
		final Map<String, TreeSet<Node>> pcToNodes = new TreeMap<String, TreeSet<Node>>();
		for (Node node : structureGraph.getThreads()) {
			node.traverse(0, new DefaultNodeTraverser() {
				public boolean foundFunctionNode(Node node, int depth) {
					TreeSet<Node> map = pcToNodes.get(((FunctionNode) node).getFunctionName());
					if (map == null) {
						map = new TreeSet<Node>();
					}
					map.add(node);
					pcToNodes.put(((FunctionNode) node).getFunctionName(), map);
					return true;
				}
			});
		}
		return pcToNodes;
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
		
	private void setUnknowns() {
		for (Node node : m_subRoutes) {
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
	
	
	private boolean cfgSanityCheck(Collection<Node> workingSet) {
		//this checks to see that all nodes point to another node in the workingSet and no other
		for(Node node : workingSet){
			for(Node next: node.getNextNodes()){
				if(!workingSet.contains(next)){
					return false;
				}
			}
			for(Node prev: node.getPrevNodes()){
				if(!workingSet.contains(prev)){
					return false;
				}
			}
		}
		return true;
	}
	
	private Collection<Node> getReferencedNodes(Reference reference) {
		ReferenceMatcher referenceMatcher = new ReferenceMatcher();		
		Collection<Node> referencedNodes = new ArrayList<Node>();
		for(Node thread : m_structureGraph.getThreads()){
			referencedNodes.addAll(referenceMatcher.getNodes(thread, reference));
		}
		return referencedNodes;		
	}
}
