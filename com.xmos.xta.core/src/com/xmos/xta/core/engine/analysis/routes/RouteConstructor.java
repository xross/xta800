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
import java.util.TreeMap;
import java.util.TreeSet;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.CommandSource.Type;
import com.xmos.xta.core.engine.analysis.ReferenceMatcher;
import com.xmos.xta.core.engine.analysis.routes.RouteDescription.RouteMode;
import com.xmos.xta.core.engine.analysis.structural.StructuralAnalysisEngine;
import com.xmos.xta.core.engine.analysis.structural.StructureGraph;
import com.xmos.xta.core.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.NullNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.Source;
import com.xmos.xta.core.engine.analysis.structural.patterns.CommonMethods;
import com.xmos.xta.core.engine.defines.FunctionTimeInfo;
import com.xmos.xta.core.engine.defines.InstructionTimeInfo;
import com.xmos.xta.core.engine.defines.LoopInfo;
import com.xmos.xta.core.engine.defines.LoopPathInfo;
import com.xmos.xta.core.engine.defines.LoopScopeInfo;
import com.xmos.xta.core.engine.defines.PathTimeInfo;
import com.xmos.xta.core.engine.instructions.InstructionMnemonics;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.core.engine.sections.SrcLevelLoopInfo;

public class RouteConstructor {
	
	private static boolean debug_route = false;
	private static boolean sanity_debug = true;
	
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
		SUCCESS,
		ABORTED
	}
	
	private Map<FunctionNode, List<Stack<FunctionReferenceNode>>> m_stackLookUp;
	
	private RouteDescription m_description;
	private StructureGraph m_structureGraph;
	private List<Route> m_routes;
	private Engine m_engine;
	private RouteContainer m_container;

	public RouteConstructor(Engine engine, StructureGraph structureGraph,
			RouteDescription description, RouteContainer container) throws AbortedException {
		m_stackLookUp = new TreeMap<FunctionNode, List<Stack<FunctionReferenceNode>>>();
		m_structureGraph = structureGraph;
		m_description = description;
		m_routes = new ArrayList<Route>();
		m_engine = engine;
    	m_container = container;
	}

	public List<Route> getRoutes() {
		return m_routes;
	}
	
	public RouteError analyse(CommandSource src) throws Exception {
		
		if (m_description.getStartPoint() == null) {
			return RouteError.STARTPOINT_MISSING;
		}
		if (m_description.getEndPoints() == null) {
			return RouteError.ENDPOINTS_MISSING;
		}
		
		//find end instructions
		Set<StackNode> endStackNodes = getEndStackNodes(m_description);

		//find exclusions
		Set<StackNode> exclusionStackNodes = new HashSet<StackNode>();
		for(Reference ref : m_engine.getExclusions()){
			exclusionStackNodes.addAll(referenceToStackNode(ref));
		}

		
		if(m_description.getMode() == RouteMode.FUNCTION){
			FunctionNode fn = m_description.getFunction(m_description.getFunctionName(), m_structureGraph);
			List<Stack<FunctionReferenceNode>> stacks = getCallStacks(fn);
			
			
			for(Stack<FunctionReferenceNode> stack : stacks){
				FunctionReferenceNode fnRef = stack.peek();
				FunctionNode newFunction = makeFunction(stack, fnRef, exclusionStackNodes, m_engine, endStackNodes, true);
				if(newFunction != null){
					setParent(newFunction);
					Route route = new Route(src, m_engine, newFunction, m_description, m_container);
					if (!alreadyContainRoute(newFunction)) {
						m_routes.add(route);
					}
				}
			}
			
		} else if(m_description.getMode() == RouteMode.PATH){
			Reference startRef = m_description.getStartPoint();
			boolean foundAtLeastOneStartPoint = false;
			List<StackNode> startStackNodes = referenceToStackNode(startRef);
			
			
			
			for(StackNode startStackNode : startStackNodes){
				for(Stack<FunctionReferenceNode> currentStack : startStackNode.getNodeStacks()){
					foundAtLeastOneStartPoint = true;
					Node newPath = makePath(currentStack, startStackNode.getInstruction(), endStackNodes, exclusionStackNodes, m_engine);
					if(newPath != null){
						setParent(newPath);
						Route route = new Route(src, m_engine, newPath, m_description, m_container);
						if (!alreadyContainRoute(newPath)) {
							m_routes.add(route);
						}
					}
				}
			}
			
			if(!foundAtLeastOneStartPoint){
				return RouteError.COULD_NOT_FIND_STARTPOINT;
			}
		} 
		
		setUnknowns();	
		setSrcLoopCounts();	
		checkSupportedInstructions(src);
		applyDefines(src);
		return RouteError.SUCCESS;
	}

	private List<StackNode> referenceToStackNode(Reference startRef) {
		ReferenceMatcher referenceMatcher = new ReferenceMatcher();
		List<StackNode> stackNodes= referenceMatcher.getStackNode(m_engine, startRef);

		return stackNodes;
	}

	private FunctionNode makeFunction(Stack<FunctionReferenceNode> currentStack, FunctionReferenceNode fnRef, 
			Set<StackNode> exclusionStackNodes, Aborter aborter, Set<StackNode> endStackNodes, boolean start) throws AbortedException{
		//if there are any exclusions at this level or within any function that this function calls
		//then this function needs to be manually explored
		if(!fnRef.isKnownFunction()){
			return new FunctionNode();
		}
		
		if(debug_route) System.out.println("###################### analysing function " + fnRef.getFunctionName());
		
		//find end instructions
		FunctionNode fn = fnRef.getFunctionNode();
		
		boolean exclusionsInOrBelowThisFunction = false;
		//find exclusions
		for(StackNode exclusion : exclusionStackNodes){
			for(Stack<FunctionReferenceNode> stack : exclusion.getNodeStacks()){
				if(stack.contains(fnRef)){
					exclusionsInOrBelowThisFunction = true;
				}
			}
		}
		
		for(StackNode exclusion : exclusionStackNodes){
			for(Stack<FunctionReferenceNode> stack : exclusion.getNodeStacks()){
				if(debug_route)  System.out.println("exclusion StackNode : " +stack + " " + exclusion.getInstruction());
			}
		}
		
		for(StackNode stackNode : endStackNodes){
			for(Stack<FunctionReferenceNode> stack : stackNode.getNodeStacks()){
				if(debug_route)  System.out.println("end StackNode : " + stack + " " + stackNode.getInstruction());
			}
		}
		
		boolean recursionInOrBelowThisFunction = false;
		
		recursionInOrBelowThisFunction = containsRecursion(fnRef);
		
		//need to find out of there are any exclusions at this context of lower
		if(!(exclusionsInOrBelowThisFunction || recursionInOrBelowThisFunction)){
			FunctionNode unmodifiedFunction = (FunctionNode)fnRef.getFunctionNode().deepCopy(aborter);
			return unmodifiedFunction;
		} else {
			InstructionNode startInst = m_structureGraph.getFunctionsStartNode(fnRef.getAlias().getFunctionNode());

			Set<Node> workingSet = new TreeSet<Node>();
			Set<Node> currentExclusionsFound = new TreeSet<Node>();
			Map<NodeDescriptor, Node> nodeToWorkingSet = new HashMap<NodeDescriptor, Node>();
			
			NodeDescriptor startNodeDescriptor = new NodeDescriptor(startInst, currentStack);
			
			Set<Node> loopFormingEndNodes = new TreeSet<Node>();
			findReachableGraph(null, currentStack, startInst, workingSet, 
					exclusionStackNodes, currentExclusionsFound,  aborter, RouteMode.FUNCTION, 
					endStackNodes, nodeToWorkingSet, loopFormingEndNodes, start);
			
			if(debug_route) System.out.println("found: " + currentExclusionsFound);
			if(debug_route) System.out.println();
			removeExclusionBranches(workingSet, currentExclusionsFound, loopFormingEndNodes);
			dumpGraph(workingSet);
			
			if(workingSet.isEmpty()){
				return null;
			}
			Node node = convertGraphToStructure(workingSet, startNodeDescriptor, nodeToWorkingSet);
			
			FunctionNode newFunction = new FunctionNode(node, fn.getFunctionName());

			return newFunction;
		}

	}
	
	boolean found = false;
	private boolean containsRecursion(FunctionReferenceNode fnRef) {
		final Collection<String> recursiveFunctions = m_engine.getRecursiveFunctions();
		found = false;
		fnRef.getFunctionNode().traverse(new Stack<Node>(), new UntypedNodeTraverser() {
	    	public TraverseStatus foundNode(Node node, Stack<Node> ancestors) {
	    		if(node instanceof FunctionNode){
	    			if(recursiveFunctions.contains(((FunctionNode)node).getFunctionName())){
	    				found = true;
	    				return TraverseStatus.ABORT;
	    			}
	    		}
	        	return TraverseStatus.CONTINUE;
	    	}
	    });
		return found;
	}

	private Node makePath(Stack<FunctionReferenceNode> currentStack, InstructionNode startInst, Set<StackNode> endStackNodes,
			Set<StackNode> exclusionStackNodes, Aborter aborter) throws AbortedException{
		
		Set<Node> workingSet = new TreeSet<Node>();
		Set<Node> currentExclusionsFound = new TreeSet<Node>();

		if(debug_route) {
			System.out.println("Analysing from " + currentStack + " " + startInst);
		
			System.out.println("To:");
			for(StackNode sn : endStackNodes){
				System.out.println("\t" + sn.getNodeStacks() + " " + sn.getInstruction());
			}
		}
		
		Map<NodeDescriptor, Node> nodeToWorkingSet = new HashMap<NodeDescriptor, Node>();
		NodeDescriptor startNodeDescriptor = new NodeDescriptor(startInst, currentStack);
		Set<Node> loopFormingEndNodes = new TreeSet<Node>();
		findReachableGraph(null, currentStack, startInst, workingSet, 
				exclusionStackNodes, currentExclusionsFound,  aborter, 
				RouteMode.PATH, endStackNodes, nodeToWorkingSet, loopFormingEndNodes, true);
		
		
		dumpGraph(workingSet);
		removeExclusionBranches(workingSet, currentExclusionsFound, loopFormingEndNodes);
		
		
		if(workingSet.isEmpty()){
			return null;
		}
		Node node = convertGraphToStructure(workingSet, startNodeDescriptor, nodeToWorkingSet);
		
		return node;
	}
	
	private boolean alreadyContainRoute(Node newGraph) {
		final List<String> newNodeList = createRouteDescription(newGraph);

		for (Route route : m_routes){			
			List<String> nodeList = createRouteDescription(route.getNode());
			if (newNodeList.containsAll(nodeList)){
				return true;
			}
		}
		return false;
	}

	private List<String> createRouteDescription(Node node) {
		final List<String> nodeList = new LinkedList<String>();
		
		node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
	    	public TraverseStatus foundNode(Node node, Stack<Node> ancestors) {
	    		if(node.isInstructionNode()){
	    			nodeList.add(node.getTypeName()+node.getStartPc());
	    		} else {
	    			nodeList.add(node.getTypeName());
	    		}
	        	return TraverseStatus.CONTINUE;
	    	}
	    });
		return nodeList;
	}
	
	private Node convertGraphToStructure(Set<Node> workingSet, NodeDescriptor startNodeDescriptor, 
			Map<NodeDescriptor, Node> nodeToWorkingSet) throws AbortedException {
		StructuralAnalysisEngine sae = new StructuralAnalysisEngine();
		dumpGraph(workingSet);
		Node startNode = makeBasicBlocks(workingSet, startNodeDescriptor, nodeToWorkingSet);
		dumpGraph(workingSet);
		assert(cfgSanityCheckNodes(workingSet));
		sae.createStructure(workingSet, startNode, m_engine, "Converting CFG to structure");
		assert(workingSet.size() == 1);
		Node structuralNode = workingSet.iterator().next();
		sae.normaliseGraph(structuralNode);
		return structuralNode;
	}
	
	private Node makeBasicBlocks(Set<Node> workingSet, NodeDescriptor startNodeDescriptor, 
			Map<NodeDescriptor, Node> nodeToWorkingSet) {
		
		Node firstNode = null;
		Set<Node> newWorkingSet = new TreeSet<Node>();
		
		Set<Node> seenNodes = new TreeSet<Node>();
		Set<Node> toDo = new TreeSet<Node>();
		
		for(NodeDescriptor nd : nodeToWorkingSet.keySet()){
			if(startNodeDescriptor.equals(nd) && workingSet.contains(nodeToWorkingSet.get(nd))){
				toDo.add(nodeToWorkingSet.get(nd));
			}
		}

		assert(!toDo.isEmpty());
		
		Map<Node, Set<Node>> nexts = new TreeMap<Node, Set<Node>>();
		Map<Node, Node> instToBlock = new TreeMap<Node, Node>();
		
		while(!toDo.isEmpty()){
			
			Node current = toDo.iterator().next();
			toDo.remove(current);
			
			List<InstructionNode> insts = new LinkedList<InstructionNode>();
			insts.add((InstructionNode)current);
			seenNodes.add(current);
			while(nextIsInSameBlock(current, workingSet) ){
				Node next = getNextNodes(current, workingSet).iterator().next();
				insts.add((InstructionNode)next);
				current = next;
			}
			seenNodes.addAll(insts);
			
			BlockNode basicBlock = new BlockNode(insts);
			
			InstructionNode startOfBlock = basicBlock.getInstructionList().get(0);
			instToBlock.put(startOfBlock, basicBlock);
			nexts.put(basicBlock, current.getNextNodes());
			if(newWorkingSet.isEmpty()){
				firstNode = basicBlock;
			}
			newWorkingSet.add(basicBlock);
			
			for(Node next : current.getNextNodes()){	
				if(next instanceof FunctionReferenceNode){
					next.getPrevNodes().clear();
					FunctionReferenceNode frn = new FunctionReferenceNode((FunctionReferenceNode) next);
					
					if(!frn.isKnownFunction()){
						frn.setUnresolved(true);
					}
					
					((FunctionReferenceNode) next).setRecursive(false);
					newWorkingSet.add(frn);
					seenNodes.add(next);
					instToBlock.put(next, frn);
					nexts.put(frn, new HashSet<Node>(next.getNextNodes()));
					for(Node nextNext : next.getNextNodes()){
						if(!seenNodes.contains(nextNext)){
							
							toDo.add(nextNext);
						}
					}
					next.getNextNodes().clear();
					
				} else if(!seenNodes.contains(next)){
					toDo.add(next);
				}
			}
		}

		for(Node node : nexts.keySet()){
			for(Node next : nexts.get(node)){
				CommonMethods.connect(instToBlock.get(next), node);
			}
		}
		
		Set<Node> seen = new TreeSet<Node>();
		Set<Node> terminals = new TreeSet<Node>();
		
		findEndPoints(firstNode, seen, terminals);
		
		NullNode n = new NullNode();
		for(Node node : terminals){
			CommonMethods.connect(n, node);
		}
		newWorkingSet.add(n);
		workingSet.clear();
		workingSet.addAll(newWorkingSet);
		return firstNode;
	}
	
	private void findEndPoints(Node inst, Set<Node> seen, Set<Node> terminals) {
		Set<Node> following = new TreeSet<Node>();
		following.add(inst);
		while(!following.isEmpty()){
			Node current = following.iterator().next();
			seen.add(current);
			if(current.getNextNodesSize() == 0){
				terminals.add(current);
			} else {
				for(Node next : current.getNextNodes()){
					if(!seen.contains(next)){
						following.add(next);
					}
				}
			}
			following.remove(current);
		}
	}
	
	private boolean nextIsInSameBlock(Node current, Set<Node> workingSet) {
		if(getNextNodes(current, workingSet).size() != 1){
			return false;
		}
		Node next = getNextNodes(current, workingSet).iterator().next();
		if(!(next instanceof InstructionNode || next instanceof NullNode)){
			return false;
		}
		if(getPrevNodes(next, workingSet).size() > 1){
			return false;
		}
		if( getPrevNodes(current, workingSet).size() > 1){
			return false;
		}
		
		if(current instanceof InstructionNode){
			if(((InstructionNode)current).getXtaInstruction().getReturn()){
				return false;
			}
			if(((InstructionNode)current).getXtaInstruction().isLink()){
				return false;
			}
		}
		return true;
	}

	private Set<Node> getNextNodes(Node node, Set<Node> workingSet){
		Set<Node> next = new TreeSet<Node>(node.getNextNodes());
		next.retainAll(workingSet);
		return next;
	}
	
	private Set<Node> getPrevNodes(Node node, Set<Node> workingSet){
		Set<Node> prev = new TreeSet<Node>(node.getPrevNodes());
		prev.retainAll(workingSet);
		return prev;
	}

	private void removeExclusionBranches(Set<Node> workingSet,
			Set<Node> currentExclusions, Set<Node> endNodes) {
		while(!currentExclusions.isEmpty()){
			Node current = currentExclusions.iterator().next();
			
			while(true){
				
				if(endNodes.contains(current)){
					break;
				} else if(current.getPrevNodes().size() == 1){
					Node prev = current.getPrevNodes().iterator().next();
					CommonMethods.disconnect(current, prev);
					workingSet.remove(current);
					if(prev.getNextNodes().size() == 0){
						current = prev;
					} else {
						break;
					}
					
				} else if(current.getPrevNodes().size() > 1){
					for(Node prev : current.getPrevNodes()){
						prev.removeNextNode(current);
						
						if(prev.getNextNodes().size() == 0){
							currentExclusions.add(prev);
						}
					}
					workingSet.remove(current);
					break;
				} else {
					workingSet.remove(current);
					break;
				}
			}
			currentExclusions.remove(current);
		}
	}

	private void findReachableGraph(
			Node parent, 
			Stack<FunctionReferenceNode> currentStack, Node node,
			Set<Node> workingSet,
			Set<StackNode> exclusionStackNodes, 
			Set<Node> currentExclusionsFound, 
			Aborter aborter, 
			RouteMode routeMode, 
			Set<StackNode> endStackNodes,
			Map<NodeDescriptor, Node> nodeToWorkingSet, Set<Node> loopFormingEndNodes,
			boolean firstInstruction) 
	throws AbortedException {

		Node currentNode = getCurrentNode(currentStack, node, nodeToWorkingSet, aborter);
		if(debug_route) System.out.println("on node "+ currentNode );
		
		if(stackNodesContainNode(exclusionStackNodes, currentStack, node)&& !firstInstruction){
			currentExclusionsFound.add(currentNode);
			addToWorkingSet(parent, currentNode, workingSet);
			return;
		}
		
		if(stackNodesContainNode(endStackNodes, currentStack, node) && !workingSet.isEmpty()){
			switch (routeMode) {
			case FUNCTION:
				addToWorkingSet(parent, currentNode, workingSet);
				return;
			case PATH:
				return;		
			}
		} else {
			
			if(debug_route) System.out.println("adding node "+ currentNode + " to workingSet");
			addToWorkingSet(parent, currentNode, workingSet);
		}
		
		for(Node next : node.getNextNodes()){
			
			if(next instanceof FunctionReferenceNode){
				Stack<FunctionReferenceNode> newStack = new Stack<FunctionReferenceNode>();
				newStack.addAll(currentStack);
				
				FunctionReferenceNode frn = ((FunctionReferenceNode)next);
				assert(((FunctionReferenceNode)next).getAlias() != null);
				newStack.add(frn);
				
				boolean recursion = formsRecursion(currentStack, frn);
				
				if(recursion) {
					frn.setRecursive(true);
				} else {
					if(routeMode == RouteMode.FUNCTION){
						//function mode function calls have to return
						FunctionNode fn = makeFunction(newStack, frn, exclusionStackNodes, aborter, endStackNodes, false);
						
						if(fn != null){
							((FunctionReferenceNode)next).setFunction(frn.getFunctionName(), fn);
						} else {
							currentExclusionsFound.add(currentNode);
							continue;
						}
					} else if(routeMode == RouteMode.PATH){
						
						boolean endPointsWithin = false;
						
						for(StackNode endStackNode : endStackNodes){
							for(Stack<FunctionReferenceNode> stack : endStackNode.getNodeStacks()){
								if(stack.contains(frn)){
									endPointsWithin = true;
								}
							}
						}
						if(endPointsWithin){
							//continue the path
							FunctionReferenceNode nextFunction = newStack.peek();
							if(nextFunction.isKnownFunction()){
								InstructionNode inst = m_engine.getInstructionDecoder().getNode(nextFunction.getFunctionStartPc());
								findReachableGraph(currentNode, newStack, inst, workingSet, 
										exclusionStackNodes, currentExclusionsFound, aborter, routeMode, 
										endStackNodes, nodeToWorkingSet, loopFormingEndNodes, false);
								
								//TODO - i think this might be ok
								continue;
							}
						} else {
							FunctionNode fn = makeFunction(newStack, frn, exclusionStackNodes, aborter, endStackNodes, false);
							if(fn != null){
								((FunctionReferenceNode)next).setFunction(frn.getFunctionName(), fn);
							} else {
								currentExclusionsFound.add(currentNode);
								continue;
							}
						}
					}
				}
			}
			
			if(next instanceof NullNode){
				FunctionReferenceNode callingFnRef = currentStack.peek();
				for(Node followingFunction : callingFnRef.getNextNodes()){
					
					Stack<FunctionReferenceNode> newStack = new Stack<FunctionReferenceNode>();
					newStack.addAll(currentStack);
					newStack.pop();
					
					//need to update the currentEnds
					findReachableGraph(currentNode, newStack, followingFunction, workingSet, 
							exclusionStackNodes, currentExclusionsFound, aborter, routeMode, endStackNodes, 
							nodeToWorkingSet, loopFormingEndNodes, false);
				}
			} else if(containsNode(next, currentStack, nodeToWorkingSet) == null){
				findReachableGraph(currentNode, currentStack, next, workingSet, 
						exclusionStackNodes, currentExclusionsFound, aborter, routeMode, endStackNodes, 
						nodeToWorkingSet, loopFormingEndNodes, false);
			} else {
				
				Node nextWorkingSetNode = containsNode(next, currentStack, nodeToWorkingSet);
				if(!stackNodesContainNode(endStackNodes, currentStack, next)){
					CommonMethods.connect(nextWorkingSetNode, currentNode);
				} else {
					loopFormingEndNodes.add(currentNode);
				}
				
			}
			
		}
	}

	private boolean formsRecursion(Stack<FunctionReferenceNode> currentStack,
			FunctionReferenceNode frn) {
		for(FunctionReferenceNode ref : currentStack){
			if(ref.isKnownFunction() && ref.getFunctionName().equals(frn.getFunctionName())){
				return true;
			}
		}
		return false;
	}

	private Node containsNode(Node next, Stack<FunctionReferenceNode> currentStack, Map<NodeDescriptor, Node> nodeToWorkingSet){
		NodeDescriptor nd = new NodeDescriptor(next, currentStack);
		for(NodeDescriptor workingNd : nodeToWorkingSet.keySet()){
			if(workingNd.equals(nd)){
				return nodeToWorkingSet.get(workingNd);
			}
		}
		return null;
	}
	
	class NodeDescriptor {
		private Node m_node;
		private Stack<FunctionReferenceNode> m_stack;
		
		public NodeDescriptor(Node node, Stack<FunctionReferenceNode> stack){
			m_node = node;
			m_stack = stack;
		}
		
		public Node getNode(){
			return m_node;
		}
		
		public Stack<FunctionReferenceNode> getStack(){
			return m_stack;
		}
		
		public boolean equals(Object obj) {
			if(obj instanceof NodeDescriptor){
				NodeDescriptor comp = (NodeDescriptor)obj;
				if(comp.m_node == m_node && comp.m_stack.equals(m_stack)){
					return true;
				}
				
			}
			return false;
		}
	}
	
	private Node getCurrentNode(Stack<FunctionReferenceNode> currentStack,
			Node node, Map<NodeDescriptor, Node> nodeToWorkingSet, Aborter aborter) throws AbortedException {
		NodeDescriptor nd = new NodeDescriptor(node, currentStack);
		if(nodeToWorkingSet.containsKey(nd)){
			return nodeToWorkingSet.get(nd);
		} else {
			Node copy = node.deepCopy(aborter);
			nodeToWorkingSet.put(nd, copy);
			return copy;
		}
	}

	private boolean stackNodesContainNode(Set<StackNode> stackNodes, Stack<FunctionReferenceNode> currentStack, Node currentNode){
		if(!(currentNode instanceof InstructionNode)){
			return false;
		}
		for(StackNode stackNode : stackNodes){
			
			if(stackNode.getInstruction().getPC() == ((InstructionNode)currentNode).getPC()){
				for(Stack<FunctionReferenceNode> nodeStack : stackNode.getNodeStacks()){
					if(nodeStack.equals(currentStack)){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private void addToWorkingSet(Node parent, Node currentNode, Set<Node> workingSet) {
		if(parent!= null)
			CommonMethods.connect(currentNode, parent);
		if(debug_route) System.out.println("adding " + currentNode + " to working set");
		workingSet.add(currentNode);
	}

	static void setParent(Node node) {
		for (Node child : node.iChildren()){
			child.setParent(node);
			setParent(child);
		}
	}
	
	private void dumpGraph(Set<Node> workingSet) {
		if(!debug_route) return;
		System.out.println("digraph g {");
		for(Node node : workingSet){
			System.out.println("node" +node.getId() + " [label=\"" + node + "\"];");
			for(Node next : node.getNextNodes()){
				System.out.println("node" +node.getId() + " -> node" + next.getId() + ";");
				if(!workingSet.contains(next)){
					System.out.println("node" +next.getId() + " [label=\"" + next + "\", style=filled, color=blue];");
				}
			}
			for(Node prev : node.getPrevNodes()){
				System.out.println("node" +prev.getId() + " -> node" + node.getId() + "[style=dotted];");
				if(!workingSet.contains(prev)){
					System.out.println("node" +prev.getId() + " [label=\"" + prev + "\", style=filled, color=red];");
				}
			}
		}
		System.out.println("}");
	}

	private void setUnknowns() {
		for (Route route : m_routes) {
			Node node = route.getNode();
			node.traverse(new Stack<Node>(), new DefaultNodeTraverser() {
				// Sets all the pausing instructions to unknown.
				public TraverseStatus foundInstructionNode(Node node, Stack<Node> ancestors) {
					XtaInstruction instruction = ((InstructionNode)node).getXtaInstruction();
					if (instruction.canPause()) {
						NodeAttributes nodeAttributes = node.getAttributes();						
						nodeAttributes.addUnknown(UnknownFactory.Type.INSTRUCTION_TIME);
					}
					return TraverseStatus.CONTINUE;
				}
				// Sets all the loop iterations to unknown.
				public TraverseStatus foundLoopNode(Node node, Stack<Node> ancestors) {
					NodeAttributes nodeAttributes = node.getAttributes();
					nodeAttributes.addUnknown(UnknownFactory.Type.LOOP_ITERATIONS);
					return TraverseStatus.CONTINUE;
				}
				// Sets all the self loop iterations to unknown.
				public TraverseStatus foundSelfLoopNode(Node node, Stack<Node> ancestors) {
					NodeAttributes nodeAttributes = node.getAttributes();
					nodeAttributes.addUnknown(UnknownFactory.Type.LOOP_ITERATIONS);
					return TraverseStatus.CONTINUE;
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

			CommandSource src = new CommandSource(Type.COMPILER);
			for (Route route : m_routes) {
				try {
					route.getRouteUnknowns().setLoopIterations(src, m_engine, 
							reference, srcLevelLoopInfo.getNumIterations(), true, Source.BINARY);
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
				public TraverseStatus foundInstructionNode(Node node, Stack<Node> ancestors) {
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
					return TraverseStatus.CONTINUE;
				}
			});
		}
	}

	private void applyDefines(CommandSource src) throws AbortedException {
		RouteDefines defines = new RouteDefines();
		for (Route route : m_routes) {			
			for (FunctionTimeInfo info : m_engine.getFunctionTimeDefines()) {			
				defines.applyFunctionDefine(src, route, info, Source.BINARY);
			}
			for (PathTimeInfo info : m_engine.getPathTimeDefines()) {			
				defines.applyPathDefine(m_engine, src, route, info, Source.BINARY);
			}
		}		
		for (LoopInfo info : m_engine.getLoopDefines()) {
			try {
				m_engine.applyLoopToRoutes(src, m_routes, info, Source.BINARY);
			} catch (Exception e) {}
		}
		for (LoopPathInfo info : m_engine.getLoopPathDefines()) {
			try {
				m_engine.applyLoopPathToRoutes(m_routes, info, Source.BINARY);
			} catch (Exception e) {}
		}
		for (LoopScopeInfo info : m_engine.getLoopScopeDefines()) {
			try {
				m_engine.applyLoopScopeToRoutes(m_routes, info, Source.BINARY);
			} catch (Exception e) {}
		}
		for (InstructionTimeInfo info : m_engine.getInstructionTimeDefines()) {
			try {
				m_engine.applyInstructionTimeToRoutes(m_routes, info, Source.BINARY);
			} catch (Exception e) {}
		}
	}

	private void refreshDisplayedUnknowns(Node node) {	
		// Clears the display unknown flags on all nodes..
	    node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
	    	public TraverseStatus foundNode(Node node, Stack<Node> ancestors) {
	    		node.getAttributes().setDisplayAsUnknown(false);
	        	return TraverseStatus.CONTINUE;
	    	}
	    });

		// On each of the unknown nodes, mark all of their parents to 'display unknown'.
	    node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
	    	public TraverseStatus foundNode(Node node, Stack<Node> ancestors) {
	    		if (node.getAttributes().hasUnknowns()) {
	    			setDisplayAsUnknownOnParentNodes(node);
	    		}
	        	return TraverseStatus.CONTINUE;
	    	}
	    });	   
	}
	
	private void setDisplayAsUnknownOnParentNodes(Node node) {
		Node parent = node;
		while (parent != null) {
			parent.getAttributes().setDisplayAsUnknown(true);
			parent = parent.getParent();		    
		}
	}		
	
	/**
	 * For the given function find all possible call stacks to it. This also saves the call stack set
	 * in a lookup table to save on memory and speed it up.
	 */
	private List<Stack<FunctionReferenceNode>> getCallStacks(FunctionNode function) {
		if(m_stackLookUp.containsKey(function)){
			return m_stackLookUp.get(function);
		}
		List<Stack<FunctionReferenceNode>> stack = m_structureGraph.getFunctionCallStack(function);
		m_stackLookUp.put(function, stack);
		return stack;
	}
	
	/**
	 * From a route description return the instruction nodes that end that route
	 */
	private Set<StackNode> getEndStackNodes(RouteDescription description) {
		Set<StackNode> stackNodes = new HashSet<StackNode>();
		if(description.getMode() == RouteMode.FUNCTION){
			FunctionNode fn = description.getFunction(description.getFunctionName(), m_structureGraph);
			for(InstructionNode inst : m_structureGraph.getFunctionsEndNodes(fn)){
				StackNode stackNode = new StackNode(inst, getCallStacks(fn));
				stackNodes.add(stackNode);
				//TODO this could be more specific -- would speed it up
			}
		} else {
			Collection<Reference> endRefs = description.getEndPoints();
			for(Reference endRef : endRefs){
				stackNodes.addAll(referenceToStackNode(endRef));
			}
		}
		return stackNodes;
	}
	
	/**
	 * One of the many sanity checks for the route construction. This one checks that the 
	 * CFG has every connection doubly connected.
	 */
	private boolean cfgSanityCheckNodes(Collection<Node> workingSet) {
		if(!sanity_debug) return true;
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
	
/*
	private Collection<NodeDescriptor> getReferencedNodeDescriptors(Reference reference) throws AbortedException {
		ReferenceMatcher referenceMatcher = new ReferenceMatcher();		
		Set<NodeDescriptor> referencedNodeDescriptors = new TreeSet<NodeDescriptor>();
		referencedNodeDescriptors.addAll(referenceMatcher.getNodeDescriptors(m_engine, m_structureGraph, reference));
		return referencedNodeDescriptors;		
	}

	private Collection<NodeDescriptor> findStartDescriptors() throws AbortedException {
    	return getReferencedNodeDescriptors(m_description.getStartPoint());
	}
	
	private Collection<NodeDescriptor> findEndNodeDescriptors() throws AbortedException {
		Set<NodeDescriptor> endNodeDescriptors = new TreeSet<NodeDescriptor>();
		
		for (Reference endPoint : m_description.getEndPoints()){
			for (NodeDescriptor endNodeDescriptor : getReferencedNodeDescriptors(endPoint)) {
				if (!m_exclusions.contains(endNodeDescriptor)) {
					endNodeDescriptors.add(endNodeDescriptor);
				}
			}
		}
		return endNodeDescriptors;
	}
	
	private Set<NodeDescriptor> containNodes(NodeDescriptor start, Collection<NodeDescriptor> ends) throws AbortedException {
		Collection<NodeDescriptor> contained = getContainedNodes(start);
		Set<NodeDescriptor> foundNodes = new TreeSet<NodeDescriptor>();
		for (NodeDescriptor end : ends) {
			if (contained.contains(end)) {
				foundNodes.add(end);
			}
		}
		return foundNodes;
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
		if (ancestors.isEmpty())
			return false;

		// Checks that the parent is not a conditional node..
		Node parent = ancestors.peek();		
		if (parent.isCondNode()) { 
			return false;
		}
		
		// The parent is a better start node than the child if the parent starts 
		// with the same PC as the child. Note: be careful of FNOPs		
		if ((parent.getStartPc() != child.getStartPc()) || 
			parent.getStartInstruction().isFnop()) {
			return false;
		}
		
		// Check that the parent doesn't contain any exclusions..
		Stack<Node> parentAncestors = new Stack<Node>();
		parentAncestors.addAll(ancestors);	
		parentAncestors.pop();
		NodeDescriptor parentDescriptor = new NodeDescriptor(parent, parentAncestors);	
		if (containsExclusions(parentDescriptor)) {
			return false;
		}		
		
		return true;
	}
	
	private Collection<NodeDescriptor> getContainedNodes(NodeDescriptor nodeDescriptor) throws AbortedException {
		final Collection<NodeDescriptor> nodeDescriptors = new ArrayList<NodeDescriptor>();
		
		if (!nodeDescriptor.getNode().isInstructionNode()) {
			nodeDescriptor.getNode().traverse(nodeDescriptor.getAncestors(), new DefaultNodeTraverser() {
				public TraverseStatus foundFunctionNode(Node node, Stack<Node> ancestors) {
					try {
						m_engine.getAbort();
					} catch (AbortedException e) {
						return TraverseStatus.ABORT;
					}
					return TraverseStatus.CONTINUE;
				}
				
				public TraverseStatus foundInstructionNode(Node node, Stack<Node> ancestors) {	
					nodeDescriptors.add(new NodeDescriptor(node, ancestors));
					return TraverseStatus.CONTINUE;
				}
		    });			
		}
		
		m_engine.getAbort();
		return nodeDescriptors;
	}

	private boolean m_containsExclusions;
	private boolean containsExclusions(NodeDescriptor nodeDescriptor) {
		if (m_exclusions.isEmpty())
			return false;
		
		m_containsExclusions = false;		
		nodeDescriptor.getNode().traverse(nodeDescriptor.getAncestors(), new DefaultNodeTraverser() {		
			public TraverseStatus foundInstructionNode(Node node, Stack<Node> ancestors) {	
				NodeDescriptor nodeDescriptor = new NodeDescriptor(node, ancestors);
				if (m_exclusions.contains(nodeDescriptor)) {
					m_containsExclusions = true;
				}
				return TraverseStatus.CONTINUE;
			}
	    });			
		return m_containsExclusions;
	}
	
	private WorkingSet convertWorkingSet(Collection<NodeDescriptor> workingSet, 
			NodeDescriptor start, final NodeDescriptor startInstruction) throws AbortedException {
		
		assert(workingSet.contains(start));
		
		Set<Node> nodes = new TreeSet<Node>();
		Node startNode = null; 
		
		Map<NodeDescriptor, Node> nodeMap = new HashMap<NodeDescriptor, Node>();
		
		// Duplicates all the nodes in the working set.
		for (NodeDescriptor nodeDescriptor : workingSet) {
			Node node = nodeDescriptor.getNode().deepCopy(m_engine);
			nodes.add(node);
			nodeMap.put(nodeDescriptor, node);
			
			if (nodeDescriptor.equals(start))
				startNode = node;
			
			updateStartInstruction(startInstruction, nodeDescriptor, node);
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
	
	private void displayNodeDescriptorWorkingSet(Collection<NodeDescriptor> workingSet) throws AbortedException {
		System.out.printf("== displayNodeDescriptorWorkingSet: size: %d\n", workingSet.size());
		for (NodeDescriptor nodeDescriptor : workingSet) {			
			System.out.printf("=> %s\n", nodeDescriptor);
			for (NodeDescriptor contained : getContainedNodes(nodeDescriptor)) {
				System.out.printf("--> %s\n", contained);				
			}
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
	
	private boolean checkNoExclusionsLeftInSet(Collection<NodeDescriptor> newWorkingSet,
			Collection<NodeDescriptor> workingSet) throws AbortedException {
		
		// Newly formed functions must contain no exclusions unless the exclusion
		// is on the first instruction of the function and the first instruction
		// is the start instruction.		
		for (NodeDescriptor nodeDescriptor : newWorkingSet) {
			Collection<NodeDescriptor> set = new TreeSet<NodeDescriptor>(getContainedNodes(nodeDescriptor));
			if (!workingSet.isEmpty())
				addNodeToWorkingSet(set, nodeDescriptor);
			set.retainAll(m_exclusions);
			
			if (!set.isEmpty())
				return false;
		}
		return true;
	}
	
	private boolean checkNoInstructionNodesLeftInSet(Collection<NodeDescriptor> workingSet) {
		for (NodeDescriptor nodeDescriptor : workingSet) {			
			if (nodeDescriptor.getNode().isInstructionNode()) {
				return false;
			}
		}
		return true;
	}
	
	private boolean checkNodesNotContainedInNode(Collection<NodeDescriptor> nodes, NodeDescriptor node) throws AbortedException {
		for (NodeDescriptor nodeDescriptor : getContainedNodes(node)){
			if (nodes.contains(nodeDescriptor)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean m_startNodeFound;
	private int m_startNodeIndex;
	private int m_index;
	
	private void updateStartInstruction(final NodeDescriptor startInstruction, NodeDescriptor nodeDescriptor, Node node) {
		// Checks if the route start instruction is found within this working set.
		m_startNodeFound = false;
		m_startNodeIndex = 0;
		m_index = 0;
		
		nodeDescriptor.getNode().traverse(nodeDescriptor.getAncestors(), new DefaultNodeTraverser() {		
			public TraverseStatus foundInstructionNode(Node node, Stack<Node> ancestors) {	
				++m_index;

				NodeDescriptor nd = new NodeDescriptor(node, ancestors);
				if (nd.equals(startInstruction)) {
					// We have found the start instruction.
					m_startNodeFound = true;
					m_startNodeIndex = m_index;
				}
				return TraverseStatus.CONTINUE;
			}
	    });	
		
		// If the start instruction was found in the working set, then we need to find
		// (and remember) the corresponding new duplicated node.
		if (m_startNodeFound) {
			m_index = 0;
			
			node.traverse(new Stack<Node>(), new DefaultNodeTraverser() {		
			    public TraverseStatus foundInstructionNode(Node node, Stack<Node> ancestors) {
			        ++m_index;
			        
			        if (m_index == m_startNodeIndex) {
			    	    m_startInstructionNode = node;
			    	}
				    return TraverseStatus.CONTINUE;
			    }
			});
		}		
	}
	
	private NodeDescriptor lookupStartInstruction(NodeDescriptor startInstruction,
			Collection<NodeDescriptor> workingSet) throws AbortedException {
		
		// First search the working set to find the node that contains the start instruction
		NodeDescriptor beginingOfFirstBlock = null;
		
		if (m_startInstructionNode != null) {
			// The start instruction was detected within a 'refurbished' function,
			for (NodeDescriptor wnd : workingSet) {
				if (wnd.getNode() == m_startInstructionNode) {
					beginingOfFirstBlock = wnd;
					break;
					
				} else {
					for (NodeDescriptor cnd : getContainedNodes(wnd)) {
						if (cnd.getNode() == m_startInstructionNode) {
							beginingOfFirstBlock = wnd;
							break;
						}						
					}					
				}				
			}
		} else {
			// Searches the working set for a match with the original start instruction node descriptor. 
			for (NodeDescriptor nodeDescriptor : workingSet) {						
				if ((getContainedNodes(nodeDescriptor).contains(startInstruction) && (beginingOfFirstBlock == null)) || 
			       (nodeDescriptor.equals(startInstruction))) {
					
					if (findFirstInstruction(nodeDescriptor).equals(startInstruction)) {
						beginingOfFirstBlock = nodeDescriptor;
					}
				}
			}			
		}		
		assert(beginingOfFirstBlock != null);
		return beginingOfFirstBlock;
	}
		
	private String dumpGraph(String text, Collection<NodeDescriptor> workingSet) {		
		StringBuffer sb = new StringBuffer();
		
		if (workingSet.size() > 1) {
			sb.append("digraph "+ text + "{");
			for (NodeDescriptor node : workingSet) {
				for (NodeDescriptor next : node.getNextNodes()) {
					sb.append(node.getNode() + " -> " + next.getNode() + ";");
				}
			}
			sb.append("}\n");

		} else {
			sb.append("# graph has only " + workingSet.size() + " nodes");
		}
		return sb.toString();
	}
	
	static class WorkingSet {
		Node m_start;
		Set<Node> m_set;
		
		public WorkingSet(Node start, Set<Node> set) {
			m_start = start;
			m_set = set;
		}
	}	
*/
}
