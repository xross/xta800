package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.CommandSource.Type;
import com.xmos.xta.core.engine.analysis.ReferenceMatcher;
import com.xmos.xta.core.engine.analysis.routes.RouteDescription.RouteMode;
import com.xmos.xta.core.engine.analysis.structural.StructuralAnalysisEngine;
import com.xmos.xta.core.engine.analysis.structural.StructureGraph;
import com.xmos.xta.core.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.EndNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InfiniteSelfLoopNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.NodeDescriptor;
import com.xmos.xta.core.engine.analysis.structural.nodes.NullNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownType;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.core.engine.instructions.InstructionMnemonics;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.core.engine.sections.SrcLevelLoopInfo;

public class RouteConstructor {
	private static final boolean DEBUG_ROUTE_CONSTRUCTION = false;
	
	private static final int SR_IEBLE_SHIFT = 1;
	private static final int SR_IEBLE_SIZE = 1; 	
	
	enum RouteStatus {
		COMPLETED, ABORTED, ERROR
	}

	public enum RouteError {
		NO_ENDPOINTS_FOUND, 
		ENDPOINTS_MISSING, 
		STARTPOINT_MISSING, 
		COULD_NOT_FIND_STARTPOINT, 
		COULD_NOT_FIND_ENDPOINT,
		SUCCESS
	}
	
	private RouteDescription m_description;
	private StructureGraph m_structureGraph;
	private List<Route> m_routes;
	private Engine m_engine;
	private Collection<NodeDescriptor> m_exclusions;
	private Set<NodeDescriptor> m_excludedRouteNodes;

	public RouteConstructor(Engine engine, StructureGraph structureGraph, RouteDescription description) {
		m_structureGraph = structureGraph;
		m_description = description;
		m_routes = new ArrayList<Route>();
		m_engine = engine;
		createExclusions();
	}

	public List<Route> getRoutes() {
		return m_routes;
	}

	public RouteError analyse(CommandSource src) throws Exception {
		boolean seenEndPoint = false;
		
		if (m_description.getStartPoint() == null) {
			return RouteError.STARTPOINT_MISSING;
		}
		if (m_description.getEndPoint() == null) {
			return RouteError.ENDPOINTS_MISSING;
		}
		
		Collection<NodeDescriptor> startNodeDescriptors = getReferencedNodeDescriptors(m_description.getStartPoint());
		if (startNodeDescriptors.isEmpty()) {
			return RouteError.COULD_NOT_FIND_STARTPOINT;
		}
		
		Collection<NodeDescriptor> endNodeDescriptors = findEndNodeDescriptors();

		boolean notFoundFunctionYet = true;
		for (NodeDescriptor startNodeDescriptor : startNodeDescriptors) {
			
			NodeDescriptor realStartNodeDescriptor = startNodeDescriptor;
			startNodeDescriptor = getStartNodeDescriptor(startNodeDescriptor);			 
						
			switch (m_description.getMode()) {
			case FUNCTION:				
				if (m_engine.getRecursiveFunctions().contains(m_description.getFunctionName())) {
					m_engine.notify(src, Notifier.NOTIFY_ERROR, "Cannot analyse function " + m_description.getFunctionName() +" as it is recursive");
					break;
				}
					
				if (startNodeDescriptor.getNode().isFunctionNode()&& notFoundFunctionYet) {
					if (!containAnyNodes(startNodeDescriptor, m_exclusions)) {
						startNodeDescriptor.getNode().setParent(null);
						m_routes.add(new Route(src, m_engine, startNodeDescriptor.getNode().deepCopy(), m_description));
						
					} else {
						seenEndPoint = makeSubRoute(src, realStartNodeDescriptor, seenEndPoint, 
								endNodeDescriptors, startNodeDescriptor, true);
					}	
					notFoundFunctionYet = false;
				}
			    break;
			
			case PATH:
				seenEndPoint = makeSubRoute(src, realStartNodeDescriptor, seenEndPoint, 
						endNodeDescriptors, startNodeDescriptor, false);
				break;
			}
		}
		setParents();
		setUnknowns();	
		setSrcLoopCounts();	
		checkSupportedInstructions(src);

		switch (m_description.getMode()) {
		case FUNCTION:
			return RouteError.SUCCESS;
			
		case PATH:
			return seenEndPoint ? RouteError.SUCCESS : RouteError.NO_ENDPOINTS_FOUND;				
		}
		
		assert(false);
		return null;
	}
	
	private boolean firstNodeAdded;
	
	/**
	 * @param src					- 
	 * @param startInstruction      - the instruction node descriptor that the route begins at
	 * @param seenEndPoint			- if true then an end node has been seen on this route
	 * @param endInstructions		- a list of the node descriptors that the route terminates on
	 * @param startNode  			- the greatest parent that begins with the startInstruction and contains no exclusions
	 * @param addFunctionWrappers	- to solve the function wrappers problem
	 * @return						- true if an end point has been seen false otherwise
	 * @throws Exception			- fails if the jvm dies for some reason or one of the endless assertions fails
	 */
	private boolean makeSubRoute(CommandSource src, NodeDescriptor startInstruction, 
			boolean seenEndPoint, Collection<NodeDescriptor> endInstructions, 
			NodeDescriptor startNode, boolean isFunction) throws Exception {
		
		if (DEBUG_ROUTE_CONSTRUCTION)
			System.out.printf("makeSubRoute: start: %d, \n", startNode.getNode().getId());
		
		firstNodeAdded = false;
		Collection<NodeDescriptor> workingNodeDescriptorSet = new TreeSet<NodeDescriptor>();
		
		m_structureGraph.removeLinks();
		m_excludedRouteNodes = new TreeSet<NodeDescriptor>();
		
		Collection<NodeDescriptor> realEndNodes = new HashSet<NodeDescriptor>();
		
		seenEndPoint |= explore(startInstruction, new NodeDescriptorWrapper(startNode), 
				workingNodeDescriptorSet, endInstructions, null, isFunction, realEndNodes);
		
		assert(cfgSanityCheckNodeDescriptors(workingNodeDescriptorSet));	
		
		pruneGraphOfExcludedPaths(workingNodeDescriptorSet);
		
		if (!workingNodeDescriptorSet.isEmpty()) {
			NodeDescriptor newStart = makeBasicBlocks(startInstruction, realEndNodes, workingNodeDescriptorSet);
			
			addTerminator(workingNodeDescriptorSet);
			assert(cfgSanityCheckNodeDescriptors(workingNodeDescriptorSet));

			WorkingSet workingSet = convertWorkingSet(workingNodeDescriptorSet, newStart);

			if (DEBUG_ROUTE_CONSTRUCTION) {
			    System.out.println(StructuralAnalysisEngine.dumpTree("eek", workingSet.m_set));
			    System.out.println("end instructions: " +endInstructions);
			    System.out.println("real end Nodes: "+ realEndNodes +"\n\n" );
			}
						
			StructuralAnalysisEngine sae = new StructuralAnalysisEngine();				
			sae.createStructure(workingSet.m_set, workingSet.m_start, m_engine, "Analysing route");
			assert(cfgSanityCheckNodes(workingSet.m_set));			
			assert(workingSet.m_set.size() == 1);
			
			Node baseNode = workingSet.m_set.iterator().next();			
			sae.normaliseGraph(baseNode);			
			assert(!baseNode.isInfiniteLoopNode());

			if ((m_description.getMode() == RouteMode.PATH) && 
				baseNode.isInfiniteSelfLoopNode()) {
				baseNode = ((InfiniteSelfLoopNode)baseNode).getLoop();
			} 

			Node newGraph = baseNode.deepCopy();
			newGraph.setParent(null);			
			
			if (!alreadyContainRoute(newGraph)) {
				m_routes.add(new Route(src, m_engine, newGraph, m_description));
			}
		}	
		return seenEndPoint;
	}
	
	/**
	 * @param startInstruction - the instruction that is at the beginning of the route
	 * @param node - a pointer to the current node
	 * @param workingSet - the nodes in the control flow graph we are making
	 * @param endInstructions - a set of instructions that the route will end on
	 * @param previous - the node before this one
	 * @param realEndNodes 
	 * @return true if the explore saw an end node
	 */
	private boolean explore(NodeDescriptor startInstruction, NodeDescriptorWrapper node, 
			Collection<NodeDescriptor> workingSet, Collection<NodeDescriptor> endInstructions, 
			NodeDescriptor previous, boolean isFunction, Collection<NodeDescriptor> realEndNodes) {	

		NodeDescriptor currentNode = node.m_nodeDescriptor;

		if (DEBUG_ROUTE_CONSTRUCTION)
			System.out.printf("explore: nodeId: %s\n", currentNode);

		Collection<NodeDescriptor> exclusionsWithinCurrentNode = new TreeSet<NodeDescriptor>();
		Set<NodeDescriptor> endInstructionsWithinCurrentNode = new TreeSet<NodeDescriptor>();

		if (currentNode.getNode().isInstructionNode()) {
			if (m_exclusions.contains(currentNode) && !workingSet.isEmpty()) {
				exclusionsWithinCurrentNode.add(currentNode);
			}
			if (endInstructions.contains(currentNode)) {
				endInstructionsWithinCurrentNode.add(currentNode);
			}
			
		} else {
			exclusionsWithinCurrentNode = new TreeSet<NodeDescriptor>(m_exclusions);
			exclusionsWithinCurrentNode.retainAll(getContainedNodes(currentNode));
			endInstructionsWithinCurrentNode = new TreeSet<NodeDescriptor>(endInstructions);
			endInstructionsWithinCurrentNode.retainAll(getContainedNodes(currentNode));
		}
		
		if (exclusionsWithinCurrentNode.isEmpty()) {
			if (endInstructionsWithinCurrentNode.isEmpty()){
				// no exclusions and no endpoints
				return exploreWhatsNext(startInstruction, workingSet, endInstructions, 
						currentNode, previous, currentNode, isFunction, realEndNodes);

			} else {
				// there are no exclusions on this block but it does contain at least one endpoint
				if (currentNode.getNode().isInstructionNode()) {
					
					if (workingSet.isEmpty()) {
						return exploreWhatsNext(startInstruction, workingSet, endInstructions, 
								currentNode, previous, currentNode, isFunction, realEndNodes);
						
					} else {
						// connect an end node and stop
						if (isFunction) {
							realEndNodes.add(currentNode);
							makeConnection(currentNode, previous);
							addNodeToWorkingSet(workingSet, currentNode);
							return true;

						} else {
							// there could be a problem with the end nodes not being a real end node.
							realEndNodes.add(previous);							
							NodeDescriptor endOfTheLineDescriptor = new NodeDescriptor(new EndNode(), previous.getAncestors());							
							makeConnection(endOfTheLineDescriptor, previous);
							addNodeToWorkingSet(workingSet, endOfTheLineDescriptor);
							return true;
						}
					}
				} else {
					return exploreIntoCurrentNode(startInstruction, workingSet, 
							endInstructions, currentNode, node,  previous, 
							exclusionsWithinCurrentNode, endInstructionsWithinCurrentNode, 
							isFunction, realEndNodes);
				}
			}
		} else {
			// exclusions
			if (currentNode.getNode().isInstructionNode()) {
				addNodeToWorkingSet(workingSet, currentNode);
				if (!currentNode.equals(startInstruction)) {
					// this doesn't allow excluded nodes to form a loop with the start node! --check
					// maybe it should also contain 'endNodes.contains(start) &&'
					makeConnection(currentNode, previous);
					m_excludedRouteNodes.add(currentNode);
					
				} else {
					m_excludedRouteNodes.add(previous);
				}
				return false;
				
			} else {
				return exploreIntoCurrentNode(startInstruction, workingSet, 
						endInstructions, currentNode, node, previous, 
						exclusionsWithinCurrentNode, endInstructionsWithinCurrentNode, 
						isFunction, realEndNodes);
			}
		}
	}

	private boolean exploreWhatsNext(NodeDescriptor startInstruction, 
			Collection<NodeDescriptor> workingSet, 
			Collection<NodeDescriptor> endInstructions, 
			NodeDescriptor currentNode, 
			NodeDescriptor originalParent, 
			NodeDescriptor replacementNode, boolean isFunction, 
			Collection<NodeDescriptor> realEndNodes) {
		
		if (DEBUG_ROUTE_CONSTRUCTION)
			System.out.printf("-- exploreWhatsNext: nodeId: %s\n", currentNode);
		
		boolean seenEndPoint = false;
		makeConnection(replacementNode, originalParent);

		addNodeToWorkingSet(workingSet, replacementNode);
		firstNodeAdded = true;
		
		Node parent = currentNode.getAncestors().peek();				
		if (parent != null) {			
			Collection<NodeDescriptor> nexts = parent.whatsNext(currentNode);
		    if (!nexts.isEmpty()) {
				for (NodeDescriptor next : nexts) {	
					if (!workingSet.contains(next) ||
						next.equals(startInstruction)) {
						seenEndPoint |= explore(startInstruction, new NodeDescriptorWrapper(next), 
								workingSet, endInstructions, replacementNode, isFunction, realEndNodes);						
					}
				}	
		    }
			
		}
		return seenEndPoint;
	}

	private boolean exploreIntoCurrentNode(NodeDescriptor startInstruction, 
			Collection<NodeDescriptor> workingSet, Collection<NodeDescriptor> endInstructions, 
			NodeDescriptor currentNode, NodeDescriptorWrapper node, NodeDescriptor parent, 
			Collection<NodeDescriptor> currentNodesExclusions, 
			Set<NodeDescriptor> containedEndPoints, boolean isFunction, 
			Collection<NodeDescriptor> realEndNodes ) {
		
		boolean seenEndPoint = false;
		assert (!currentNode.getImmediateChildren().isEmpty());

		if (DEBUG_ROUTE_CONSTRUCTION)
			System.out.printf("-- exploreIntoCurrentNode: nodeId: %s\n", currentNode);
		
		if (currentNode.getNode().isFunctionNode()) {
			// the node contains end instructions?
			if (!containedEndPoints.isEmpty()) {
				for (NodeDescriptor child : currentNode.getImmediateChildren()) {
					if (!workingSet.contains(child) ||
						child.equals(startInstruction)) {							
						NodeDescriptorWrapper childDescriptorWrapper = new NodeDescriptorWrapper(child);
											
						seenEndPoint |= explore(startInstruction, childDescriptorWrapper, 
								workingSet, endInstructions, parent, isFunction, realEndNodes);
						
						if (parent.getNode() == null) {
							node.m_nodeDescriptor = childDescriptorWrapper.m_nodeDescriptor;
						}						
					}					
				}
			} else {
				assert(!currentNodesExclusions.isEmpty());
				
				NodeDescriptor currentFunction = currentNode;
				
				NodeDescriptor functionStartNode = findFirstInstruction(currentNode);				
				if (m_exclusions.contains(functionStartNode) && firstNodeAdded){
					m_excludedRouteNodes.add(parent);
					return false;
				}

				assert(currentNode.getImmediateChildren().size() == 1);
				Node firstFunctionNode = currentNode.getImmediateChildren().get(0).getNode();				
				Stack<Node> firstFunctionNodeAncestors = new Stack<Node>();
				firstFunctionNodeAncestors.addAll(currentNode.getAncestors());
				firstFunctionNodeAncestors.push(currentNode.getNode());				
				NodeDescriptor functionNodeDescriptor = new NodeDescriptor(firstFunctionNode, firstFunctionNodeAncestors);
				NodeDescriptorWrapper functionNodeWrapper = new NodeDescriptorWrapper(functionNodeDescriptor);
				
				Collection<NodeDescriptor> functionEndNodes = currentNode.getEndInstructions();
				Collection<NodeDescriptor> newWorkingSet = new TreeSet<NodeDescriptor>();
				
				seenEndPoint |= explore(functionStartNode, functionNodeWrapper, newWorkingSet, 
						functionEndNodes, null, true, realEndNodes);
				
				assert(cfgSanityCheckNodeDescriptors(newWorkingSet));	
				pruneGraphOfExcludedPaths(newWorkingSet);
				
				// there should be nothing in the working set that is excluded now
				for (NodeDescriptor nodeDescriptor : newWorkingSet) {
					Collection<NodeDescriptor> set = new TreeSet<NodeDescriptor>(getContainedNodes(nodeDescriptor));
					if (!workingSet.isEmpty())
						addNodeToWorkingSet(set, nodeDescriptor);
					set.retainAll(m_exclusions);
					assert(set.isEmpty());
				}
				
				// remember that the function might have 0 paths so terminates
				if (!newWorkingSet.isEmpty()){

					NodeDescriptor newStart = makeBasicBlocks(functionStartNode, realEndNodes, newWorkingSet);
					System.out.println(realEndNodes);
					// check that there are no instruction nodes in the graph.
					for (NodeDescriptor graphNode : newWorkingSet) {
						
						if (graphNode.getNode().isInstructionNode()) {
							System.out.println(StructuralAnalysisEngine.dumpTree(
									"broken", convertWorkingSet(newWorkingSet, newStart).m_set));
						}
						
						assert(!graphNode.getNode().isInstructionNode());
					}
										
					addTerminator(newWorkingSet);
					
					WorkingSet ws = convertWorkingSet(newWorkingSet, newStart);
					
					StructuralAnalysisEngine sae = new StructuralAnalysisEngine();	
					sae.createStructure(ws.m_set, ws.m_start, m_engine, "Analysing sub route");
					
					assert(newWorkingSet.size() == 1);
					NodeDescriptor newCurrentNode = newWorkingSet.iterator().next();
					
					sae.normaliseGraph(newCurrentNode.getNode());
					
					NodeDescriptor replacementFunction = new NodeDescriptor(
							new FunctionNode(newCurrentNode.getNode(), currentNode.getNode().getFunctionName()),
							newCurrentNode.getAncestors());
					
					for (NodeDescriptor next : currentNode.getNextNodes()) {
						replacementFunction.addNextNode(next);						
					}
					
					// the replacement function should be without exclusions and end points
					// maybe check this
					
					for (NodeDescriptor containedInstruction : getContainedNodes(currentFunction)){
						assert(!endInstructions.contains(containedInstruction));
					}
					
					seenEndPoint |= exploreWhatsNext(startInstruction, workingSet, endInstructions, currentNode, 
							parent, replacementFunction, isFunction, realEndNodes);
				} else {
					// probably should add the parent to the list of excluded route nodes
					if  (!m_excludedRouteNodes.contains(parent)){
						m_excludedRouteNodes.add(parent);
					}
				}
			}
		} else {
			for (NodeDescriptor child : currentNode.getImmediateChildren()) {
				if (!workingSet.contains(child) ||
					child.equals(startInstruction)) {							
					NodeDescriptorWrapper childDescriptorWrapper = new NodeDescriptorWrapper(child);

					seenEndPoint |= explore(startInstruction, childDescriptorWrapper, workingSet, endInstructions, parent, isFunction, realEndNodes);
					if (parent == null) {
						node.m_nodeDescriptor = childDescriptorWrapper.m_nodeDescriptor;
					}					
				}				
			}
		}
		return seenEndPoint;
	}

	private void addNodeToWorkingSet(Collection<NodeDescriptor> workingSet, NodeDescriptor currentNode) {		
		if (DEBUG_ROUTE_CONSTRUCTION)		
		    System.out.println("--- adding " + currentNode + " to working set");
		
		workingSet.add(currentNode);
	}
	
	private NodeDescriptor findFirstInstruction(NodeDescriptor nodeDescriptior) {
		NodeDescriptor nd = nodeDescriptior;
		while(!nd.getNode().isInstructionNode()){
			assert(nd.getImmediateChildren().size() == 1);			
			nd = nd.getImmediateChildren().get(0);
		}
		return nd;
	}

	private boolean alreadyContainRoute(Node newGraph) {

		final List<String> newNodeList = new LinkedList<String>();
		
		newGraph.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
	    	public boolean foundNode(Node node, Stack<Node> ancestors) {
	    		newNodeList.add(node.getTypeName());
	        	return true;
	    	}
	    });	 
		for(Route route : m_routes){
			
			final List<String> nodeList = new LinkedList<String>();
			
			route.getNode().traverse(new Stack<Node>(), new UntypedNodeTraverser() {
		    	public boolean foundNode(Node node, Stack<Node> ancestors) {
		    		if(node.isInstructionNode()){
		    			nodeList.add(node.getTypeName()+node.getStartPc());
		    		} else {
		    			
		    			nodeList.add(node.getTypeName());
		    		}
		        	return true;
		    	}
		    });	 
			
			if(newNodeList.containsAll(nodeList)){
				return true;
			}	
		}
		return false;
	}

	private void addTerminator(Collection<NodeDescriptor> workingSet) {
		int count = 0;
		for (NodeDescriptor node : workingSet) {
			if (node.getNextNodes().size() == 0) {
				count++;
			}
		}
		if (count < 2) 
			return;
		
		NodeDescriptor nullNode = new NodeDescriptor(new NullNode(), new Stack<Node>());		
		
		for (NodeDescriptor node : workingSet) {
			if (node.getNextNodes().size() == 0) {
				makeConnection(nullNode, node);
			}
		}
		addNodeToWorkingSet(workingSet, nullNode);
	}

	private NodeDescriptor makeBasicBlocks(NodeDescriptor startInstruction, 
			Collection<NodeDescriptor> endNodes, 
			Collection<NodeDescriptor> workingSet) {
		
		// first search the working set to find the node that contains the start instruction
		NodeDescriptor beginingOfFirstBlock = null;
		for (NodeDescriptor nodeDescriptor : workingSet) {
			if ((getContainedNodes(nodeDescriptor).contains(startInstruction) && (beginingOfFirstBlock == null)) || 
		       (nodeDescriptor == startInstruction)) {
				
				if (findFirstInstruction(nodeDescriptor).equals(startInstruction)) {
					beginingOfFirstBlock = nodeDescriptor;
				}
			}
		}

		assert(beginingOfFirstBlock != null);
		
		NodeDescriptor startNode = null;
		if (beginingOfFirstBlock.getNode().isInstructionNode()) {
			// if the sequence starts with an instruction 
			// then a new block will have to be made
			NodeDescriptor currentNode = beginingOfFirstBlock;
			List<NodeDescriptor> startList = new LinkedList<NodeDescriptor>();
			startList.add(currentNode);
			
			while ((currentNode.getNextNodes().size() == 1) && 
					currentNode.getNextNode().getNode().isInstructionNode() &&
					!startList.contains(currentNode.getNextNode())) {
				
				currentNode = currentNode.getNextNode();
				startList.add(currentNode);
			}
			
			List<InstructionNode> startListNodes = new LinkedList<InstructionNode>();
			for (NodeDescriptor nd : startList) {
				startListNodes.add((InstructionNode)nd.getNode());
			}
			
			startNode = new NodeDescriptor(new BlockNode(startListNodes), beginingOfFirstBlock.getAncestors());
			workingSet.removeAll(startList);
			addNodeToWorkingSet(workingSet, startNode);

			for (NodeDescriptor next : currentNode.getNextNodes()) {
				next.removePreviousNode(currentNode);				
				makeConnection(next, startNode);
			}
			for (NodeDescriptor prev : beginingOfFirstBlock.getPreviousNodes()) {
				prev.removeNextNode(beginingOfFirstBlock);
				makeConnection(startNode, prev);
			}
		} else {
			// otherwise that one will do
			startNode = beginingOfFirstBlock;
		}
				
		// from every end node in the working set crawl upwards stopping at
		// basic block boundaries
		for (NodeDescriptor endNode : endNodes) {
			
			if (workingSet.contains(endNode) && 
				endNode.getNode().isInstructionNode()) {
				
				List<NodeDescriptor> endList = new LinkedList<NodeDescriptor>();
				NodeDescriptor end = endNode;
				endList.add(end);
				
				while ((end.getPreviousNodes().size() == 1) && 
					   end.getPreviousNode().getNode().isInstructionNode()) {
					end = end.getPreviousNode();
					endList.add(0, end);
				}

				List<InstructionNode> endListNodes = new LinkedList<InstructionNode>();
				for (NodeDescriptor nd : endList) {
					endListNodes.add((InstructionNode)nd.getNode());
				}
								
				NodeDescriptor endBlock = new NodeDescriptor(new BlockNode(endListNodes), endNode.getAncestors());
				workingSet.removeAll(endList);
				addNodeToWorkingSet(workingSet, endBlock);
				
				for (NodeDescriptor prev : end.getPreviousNodes()) {
					prev.removeNextNode(end);
					makeConnection(endBlock, prev);
				}
				for (NodeDescriptor next : endNode.getNextNodes()) {
					next.removePreviousNode(endNode);
					makeConnection(next, endBlock);
				}
			}
		}
		assert(workingSet.contains(startNode));
		assert(cfgSanityCheckNodeDescriptors(workingSet));
		return startNode;
	}
	
	private void pruneGraphOfExcludedPaths(Collection<NodeDescriptor> workingSet) {
		Set<NodeDescriptor> t = new TreeSet<NodeDescriptor>(m_excludedRouteNodes);
						
		while (!m_excludedRouteNodes.isEmpty()) {
			NodeDescriptor excludedNode = m_excludedRouteNodes.iterator().next();
			for (NodeDescriptor previous : excludedNode.getPreviousNodes()){
				previous.removeNextNode(excludedNode);
				if (previous.getNextNodes().size() == 0){
					m_excludedRouteNodes.add(previous);
				}
			}
			m_excludedRouteNodes.remove(excludedNode);
			workingSet.remove(excludedNode);
		}
		m_excludedRouteNodes.addAll(t);
		assert(cfgSanityCheckNodeDescriptors(workingSet));
	}

	private void makeConnection(NodeDescriptor node, NodeDescriptor previous) {		
		if (previous != null) {
			if (DEBUG_ROUTE_CONSTRUCTION) {
			    System.out.printf("---- makeConnection (%s, %s)\n", 
			    		node.getNode().getId(),
			    		previous.getNode().getId());
			}
						
			previous.addNextNode(node);
			node.addPreviousNode(previous);			
		}
	}
		
	private void setParents() {
		for (Route route : m_routes) {
			setParent(route.getNode());
		}
	}

	private void setParent(Node node) {
		for (Node child : node.getChildren()){
			child.setParent(node);
			setParent(child);
		}
	}
	
	private void createExclusions() {
		m_exclusions = new ArrayList<NodeDescriptor>();
		ReferenceMatcher referenceMatcher = new ReferenceMatcher();		
		for (Reference reference : m_engine.getExclusions()) {
			for (Node threadNode : m_structureGraph.getThreads()) {
				m_exclusions.addAll(referenceMatcher.getNodeDescriptors(threadNode, reference));
			}
		}

		for (Reference reference : m_engine.getExclusions()) {
			for (Node threadNode : m_structureGraph.getUnlinkedFunctions()) {				
				m_exclusions.addAll(referenceMatcher.getNodeDescriptors(threadNode, reference));
			}
		}
	}

	private void setUnknowns() {
		for (Route route : m_routes) {
			Node node = route.getNode();
			node.traverse(new Stack<Node>(), new DefaultNodeTraverser() {
				// Sets all the pausing instructions to unknown.
				public boolean foundInstructionNode(Node node, Stack<Node> ancestors) {
					XtaInstruction instruction = ((InstructionNode)node).getXtaInstruction();
					if (instruction.canPause()) {
						NodeAttributes nodeAttributes = node.getAttributes();
						nodeAttributes.setUnknownType(UnknownType.INSTRUCTION_TIME);
					}
					return true;
				}
				// Sets all the loop iterations to unknown.
				public boolean foundLoopNode(Node node, Stack<Node> ancestors) {
					NodeAttributes nodeAttributes = node.getAttributes();
					nodeAttributes.setUnknownType(UnknownType.LOOP_ITERATIONS);
					return true;
				}
				// Sets all the self loop iterations to unknown.
				public boolean foundSelfLoopNode(Node node, Stack<Node> ancestors) {
					NodeAttributes nodeAttributes = node.getAttributes();
					nodeAttributes.setUnknownType(UnknownType.LOOP_ITERATIONS);
					return true;
				}
		    });
		    refreshDisplayedUnknowns(node);			
		}
	}

	private void setSrcLoopCounts() {
		// Sets the loop iteration counts specified at compile time.
		for (SrcLevelLoopInfo srcLevelLoopInfo : m_engine.getSrcLevelLoops()) {
			Reference reference = ReferenceFactory.createReference(
					String.format("0x%x", srcLevelLoopInfo.getPc()), 
					ReferenceClass.PC_REF);

			CommandSource src = new CommandSource(Type.INTERNAL);
			for (Route route : m_routes) {
				try {
					route.getRouteUnknowns().setLoopIterations(src, m_engine, 
							reference, srcLevelLoopInfo.getNumIterations(), true);
				} catch (Exception e) {}				
			}
		}		
	}
	
	private void checkSupportedInstructions(final CommandSource src) {
		final Engine engine = m_engine;
		final InstructionMnemonics mnenonics = new InstructionMnemonics(
				m_engine.getXeLoader().getArchitecture());
		
		for (Route route : m_routes) {
			Node node = route.getNode();
			node.traverse(new Stack<Node>(), new DefaultNodeTraverser() {
				public boolean foundInstructionNode(Node node, Stack<Node> ancestors) {
					XtaInstruction instruction = ((InstructionNode)node).getXtaInstruction();

					// Checks for threading instructions...
					if (mnenonics.isSsync(instruction))
						engine.notify(src, Notifier.NOTIFY_WARNING, String.format(
								"ssync (multiple threads) not supported. Pc: 0x%08x", instruction.getPc()));
					
					if (mnenonics.isMsync(instruction))
						engine.notify(src, Notifier.NOTIFY_WARNING, String.format(
								"msync (multiple threads) not supported. Pc: 0x%08x", instruction.getPc()));

					if (mnenonics.isMjoin(instruction)) {
						engine.notify(src, Notifier.NOTIFY_WARNING, String.format(
								"mjoin (multiple threads) not supported. Pc: 0x%08x", instruction.getPc()));
					}
					
					// Checks for interrupt enable..
					if (mnenonics.isSetSr(instruction)) {
						
						long op1Field = instruction.getOperandField("op1");
						int shift = SR_IEBLE_SHIFT;
						int mask = (1 << SR_IEBLE_SIZE) - 1;
						if (((op1Field >> shift) & mask) == 1) 
							engine.notify(src, Notifier.NOTIFY_WARNING, String.format(
									"interrupt enable detected. Pc: 0x%08x", instruction.getPc()));
					}					
					return true;
				}
			});
		}
	}
	
	private void refreshDisplayedUnknowns(Node node) {	
		// Clears the display unknown flags on all nodes..
	    node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
	    	public boolean foundNode(Node node, Stack<Node> ancestors) {
	    		node.getAttributes().setDisplayUnknown(false);
	        	return true;
	    	}
	    });

		// On each of the unknown nodes, mark all of their parents to 'display unknown'.
	    node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
	    	public boolean foundNode(Node node, Stack<Node> ancestors) {
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
	
	private boolean cfgSanityCheckNodes(Collection<Node> workingSet) {
		// this checks to see that all nodes point to another 
		// node in the workingSet and no other
		for (Node node : workingSet){
			for (Node next : node.getNextNodes()) {
				if (!workingSet.contains(next)) {
					return false;
				}
			}
			for (Node previous: node.getPrevNodes()){
				if (!workingSet.contains(previous)){
					return false;
				}
			}
		}
		return true;
	}

	private boolean cfgSanityCheckNodeDescriptors(Collection<NodeDescriptor> workingSet) {
		// this checks to see that all nodes point to another 
		// node in the workingSet and no other
		for (NodeDescriptor nodeDescriptor : workingSet){
			for (NodeDescriptor next : nodeDescriptor.getNextNodes()) {
				if (!workingSet.contains(next)) {
					return false;
				}
			}
			for (NodeDescriptor previous: nodeDescriptor.getPreviousNodes()){
				if (!workingSet.contains(previous)){
					return false;
				}
			}
		}
		return true;
	}
	
	private Collection<NodeDescriptor> getReferencedNodeDescriptors(Reference reference) {
		ReferenceMatcher referenceMatcher = new ReferenceMatcher();		
		Set<NodeDescriptor> referencedNodeDescriptors = new TreeSet<NodeDescriptor>();
		
		for (Node thread : m_structureGraph.getThreads()) {	
			referencedNodeDescriptors.addAll(
					referenceMatcher.getNodeDescriptors(thread, reference));
		}
		for (Node thread : m_structureGraph.getUnlinkedFunctions()) {
			referencedNodeDescriptors.addAll(
					referenceMatcher.getNodeDescriptors(thread, reference));
		}
		return referencedNodeDescriptors;		
	}
		
	private Set<NodeDescriptor> findEndNodeDescriptors() {
		Set<NodeDescriptor> endNodeDescriptors = new TreeSet<NodeDescriptor>();
		
		for (Reference endPoint : m_description.getEndPoint()){
			for (NodeDescriptor endNodeDescriptor : getReferencedNodeDescriptors(endPoint)) {
				if (!m_exclusions.contains(endNodeDescriptor)) {
					endNodeDescriptors.add(endNodeDescriptor);
				}
			}
		}
		return endNodeDescriptors;
	}
	
	private boolean containAnyNodes(NodeDescriptor start, Collection<NodeDescriptor> ends) {
		Collection<NodeDescriptor> contained = getContainedNodes(start);
		for (NodeDescriptor end : ends) {
			if (contained.contains(end)) {
				return true;
			}
		}
		return false;
	}
	
	private NodeDescriptor getStartNodeDescriptor(NodeDescriptor startNodeDescriptor) {
		Node startNode = startNodeDescriptor.getNode();

		Stack<Node> ancestors = new Stack<Node>();
		ancestors.addAll(startNodeDescriptor.getAncestors());
		
		while (parentIsBetterThanChild(startNode, ancestors)) {
			startNode = ancestors.pop();
		}
		return new NodeDescriptor(startNode, ancestors);
	}

	private boolean parentIsBetterThanChild(Node child, Stack<Node> ancestors) {
		// The parent is a better start node than the child if the parent starts 
		// with the same PC as the child. Note: be careful of FNOPs
		if (ancestors.isEmpty()) 
			return false;

		Node parent = ancestors.peek();		
		if (parent.isCondNode()) { 
			return false;
		}
		if ((parent.getStartPc() == child.getStartPc()) && 
			!parent.getInstructions().getFirst().isFnop()) {
			return true;
		}
		return false;
	}
	
	private Collection<NodeDescriptor> getContainedNodes(NodeDescriptor nodeDescriptor) {
		final Collection<NodeDescriptor> nodeDescriptors = new ArrayList<NodeDescriptor>();
		
		nodeDescriptor.getNode().traverse(nodeDescriptor.getAncestors(), new DefaultNodeTraverser() {		
			public boolean foundInstructionNode(Node node, Stack<Node> ancestors) {	
				Stack<Node> newAncestors = new Stack<Node>();
				newAncestors.addAll(ancestors);
				nodeDescriptors.add(new NodeDescriptor(node, newAncestors));
				return true;
			}
	    });
		return nodeDescriptors;
	}
	
	private WorkingSet convertWorkingSet(Collection<NodeDescriptor> workingSet, NodeDescriptor start) {
		assert(workingSet.contains(start));
		
		Set<Node> nodes = new TreeSet<Node>();
		Node startNode = null; 
		
		Map<NodeDescriptor, Node> nodeMap = new HashMap<NodeDescriptor, Node>();
		
		// Duplicates all the nodes in the working set.
		for (NodeDescriptor nodeDescriptor : workingSet) {
			Node node = nodeDescriptor.getNode().deepCopy();
			nodes.add(node);
			nodeMap.put(nodeDescriptor, node);
			
			if (nodeDescriptor.equals(start))
				startNode = node;
		}		
		assert(startNode != null);

		// Links all the nodes in the new working set.
		for (NodeDescriptor nodeDescriptor : workingSet) {
			Node fromNode = nodeMap.get(nodeDescriptor);

			// Connects all the next nodes...
			for (NodeDescriptor next : nodeDescriptor.getNextNodes()) {
				Node toNode = nodeMap.get(next);
				fromNode.addNextNode(toNode);
			}
			
			// Connects all the previous nodes...
			for (NodeDescriptor previous : nodeDescriptor.getPreviousNodes()) {
				Node toNode = nodeMap.get(previous);
				fromNode.addPrevNode(toNode);
			}
		}
		
		if (DEBUG_ROUTE_CONSTRUCTION) {
			System.out.printf("convertWorkingSet: from (NodeDescriptors):\n");
			displayNodeDescriptorWorkingSet(workingSet);
			System.out.printf("convertWorkingSet: to (Nodes):\n");
			displayNodeWorkingSet(nodes);
		}		
		return new WorkingSet(startNode, nodes);
	}
	
	private void displayNodeDescriptorWorkingSet(Collection<NodeDescriptor> workingSet) {
		System.out.printf("== displayNodeDescriptorWorkingSet: size: %d\n", workingSet.size());
		for (NodeDescriptor nodeDescriptor : workingSet) {
			System.out.printf("-> %s\n", nodeDescriptor);
		}
	}

	private void displayNodeWorkingSet(Collection<Node> workingSet) {
		for (Node node : workingSet) {
			System.out.printf("--> %s, next: ", node);
			for (Node next : node.getNextNodes()) {
				System.out.printf("%s ", next);
			}
			System.out.printf("prev: ");
			for (Node prev : node.getPrevNodes()) {
				System.out.printf("%s ", prev);
			}
			System.out.printf("\n");
		}
	}
	
	static class NodeDescriptorWrapper {
		NodeDescriptor m_nodeDescriptor;
		
		public NodeDescriptorWrapper(NodeDescriptor nodeDescriptor){
			m_nodeDescriptor = nodeDescriptor;
		}
	}	

	static class WorkingSet {
		Node m_start;
		Set<Node> m_set;
		
		public WorkingSet(Node start, Set<Node> set) {
			m_start = start;
			m_set = set;
		}
	}	
}
