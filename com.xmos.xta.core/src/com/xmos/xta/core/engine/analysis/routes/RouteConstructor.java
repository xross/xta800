package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.xmos.libs.memorymonitor.MemoryMonitorException;
import com.xmos.libs.util.IteratableMap;
import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.NonIteratableSet;
import com.xmos.libs.util.Pair;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.CommandSource.Type;
import com.xmos.xta.core.engine.analysis.controlflow.CallGraph;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer.RouteMode;
import com.xmos.xta.core.engine.analysis.structural.StructuralAnalysisEngine;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.NullNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
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
import com.xmos.xta.core.engine.utils.CoreId;

public class RouteConstructor {

	private static boolean debug_route = false;
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
	}

	private CallGraph m_callGraph;
	private List<Route> m_routes;
	private Engine m_engine;
	private RouteCoreContainer m_coreContainer;
	private CoreId m_core;
	
	public RouteConstructor(Engine engine, CallGraph callGraph,
			RouteCoreContainer routeCoreContainer, CoreId core) throws AbortedException {
		m_callGraph = callGraph;
		m_routes = new ArrayList<Route>();
		m_engine = engine;
		m_coreContainer = routeCoreContainer;	
		m_core = core;
		assert(m_callGraph != null);
		assert(m_engine != null);
	}

	public List<Route> getRoutes() {
		return m_routes;
	}

	public RouteError analyze(CommandSource src) throws Exception {
		m_engine.provideFeedback("Starting analysis...");
		
		RouteContainer routeContainer = m_coreContainer.getRouteContainer();

		if (m_coreContainer.getFromReference() == null) {
			return RouteError.STARTPOINT_MISSING;
		}
		if (m_coreContainer.getToReferences() == null) {
			return RouteError.ENDPOINTS_MISSING;
		}

		List<StackNode> branchStackNodes = new LinkedList<StackNode>();
		Map<StackNode, Set<Node>> branchToNext = new IteratableMap<StackNode, Set<Node>>();
		buildBranchStackNodes(branchStackNodes, branchToNext);
		
		List<StackNode> exclusionStackNodes = new LinkedList<StackNode>();

		for(Reference ref : m_engine.getExclusions()){
			if(ref.getCoreId().equals(m_core)){
				exclusionStackNodes.addAll(getPathStackNodes(m_callGraph, ref));
			}
		}
		
		if (routeContainer.getMode() == RouteMode.FUNCTION){
			List<StackNode> startStackNodes = getPathStackNodes(m_callGraph, m_coreContainer.getFromReference());
			for(StackNode startStackNode : startStackNodes){
				for(Stack<FunctionReferenceNode> stack : startStackNode.getNodeStacks()){
					
					FunctionNode newFunction = makeFunction(stack, exclusionStackNodes, branchStackNodes, true, branchToNext).first;
					if (newFunction != null){
						setParent(newFunction);
						if (!alreadyContainRoute(newFunction)) {
							Route route = new Route(src, m_engine, stack, newFunction, m_coreContainer);
							m_routes.add(route);
						}
					}
				}
			}
		} else if (routeContainer.getMode() == RouteMode.PATH){
			Reference startRef = m_coreContainer.getFromReference();
			
			List<StackNode> startStackNodes = getPathStackNodes(m_callGraph, startRef);
			
			Collection<Reference> endRefs = m_coreContainer.getToReferences();
			List<StackNode> endStackNodes = new LinkedList<StackNode>();
			for(Reference end : endRefs){
				
				endStackNodes.addAll(getPathStackNodes(m_callGraph, end));
			}
			boolean foundAtLeastOneStartPoint = false;
			for (StackNode startStackNode : startStackNodes){
				for (Stack<FunctionReferenceNode> currentStack : startStackNode.getNodeStacks()){
					foundAtLeastOneStartPoint = true;
					Node newPath = makePath(currentStack, startStackNode.getInstruction(), endStackNodes, exclusionStackNodes, branchStackNodes, branchToNext);
					if (newPath != null){
						setParent(newPath);
						
						if (!alreadyContainRoute(newPath)) {
							Route route = new Route(src, m_engine, currentStack, newPath, m_coreContainer);
							m_routes.add(route);
						}
					}
				}
			}

			if (!foundAtLeastOneStartPoint){
				return RouteError.COULD_NOT_FIND_STARTPOINT;
			}			
		} else {  
			assert(false);
		}
		
		setUnknowns();
		setSrcLoopCounts();
		checkSupportedInstructions(src);
		applyDefines(src);
		
		m_engine.provideFeedback("Analysis complete");
		return RouteError.SUCCESS;
	}

	private void buildBranchStackNodes(List<StackNode> branchStackNodes,
			Map<StackNode, Set<Node>> branchToNext) 
	    throws AbortedException, MemoryMonitorException {
		
		for(Reference ref : m_engine.getUserBranches().keySet()){
			List<StackNode> stackNodes = getPathStackNodes(m_callGraph, ref);
			branchStackNodes.addAll(stackNodes);
			for(StackNode sn : stackNodes){
				branchToNext.put(sn, new IteratableSet<Node>());
				for(Reference next : m_engine.getUserBranches().get(ref)){
					for(StackNode nextStackNode : getPathStackNodes(m_callGraph, next)){
						if(sn.getInstruction().getXtaInstruction().isLink()){
							for(Node nextFrn : sn.getInstruction().getNextNodes()){
								if(nextFrn instanceof FunctionReferenceNode){
									FunctionReferenceNode frn = (FunctionReferenceNode)nextFrn;
									if(frn.getFunctionStartInstruction() == nextStackNode.getInstruction()){
										branchToNext.get(sn).add(frn);	
									}
								}
							}
						} else {
							branchToNext.get(sn).add(nextStackNode.getInstruction());	
						}						
					}
				}
			}
		}
	}

	private List<StackNode> getPathStackNodes(CallGraph callGraph, Reference reference) 
	    throws AbortedException, MemoryMonitorException {
		
		PreanalysisReferenceMatcher prm = new PreanalysisReferenceMatcher();
		return prm.matchReference(callGraph, reference, m_engine);
	}
	
	private Pair<FunctionNode, Boolean> makeFunction(Stack<FunctionReferenceNode> currentStack, List<StackNode> exclusionStackNodes, 
			List<StackNode> branchStackNodes, boolean start, Map<StackNode, Set<Node>> branchToNext) 
		throws AbortedException, MemoryMonitorException {
		
		List<StackNode> endStackNodes = getFunctionEndStackNodes(currentStack);
		
		//find unresolved instructions
		FunctionReferenceNode fnRef = currentStack.peek();
		
		if(!fnRef.isKnownFunction()){
			return new Pair<FunctionNode, Boolean>(new FunctionNode(), false);
		}
		InstructionNode startInst = fnRef.getStartInstruction();

		Set<Node> workingSet = new IteratableSet<Node>();
		Set<Node> currentExclusionsFound = new IteratableSet<Node>();
		Map<NodeDescriptor, Node> nodeToWorkingSet = new IteratableMap<NodeDescriptor, Node>();
		
		NodeDescriptor startNodeDescriptor = new NodeDescriptor(startInst, currentStack);
		Set<Node> loopFormingEndNodes = new NonIteratableSet<Node>();
		
		findReachableGraph(null, currentStack, startInst, workingSet,
				exclusionStackNodes, currentExclusionsFound, RouteMode.FUNCTION,
				endStackNodes, nodeToWorkingSet, loopFormingEndNodes, start, branchStackNodes, branchToNext);

		removeExclusionBranches(workingSet, currentExclusionsFound, loopFormingEndNodes);

		if(workingSet.isEmpty()){
			return new Pair<FunctionNode, Boolean>(null, false);
		}
		
		//if working set contains an end node then we will continue else we wont
		boolean continueAfterFunction = true;
		
		for(StackNode sn : endStackNodes){
			for(Node node : workingSet){
				if(node instanceof InstructionNode){
					if(((InstructionNode)node).getXtaInstruction() == sn.getInstruction().getXtaInstruction()){
						continueAfterFunction = true;
						break;
					}
				}
			}
		}
		
		Node node = convertGraphToStructure(workingSet, startNodeDescriptor, nodeToWorkingSet);

		return new Pair<FunctionNode, Boolean>(new FunctionNode(node, fnRef), continueAfterFunction);
	}
	
	private Node makePath(Stack<FunctionReferenceNode> currentStack, InstructionNode startInst, List<StackNode> endStackNodes,
			List<StackNode> exclusionStackNodes, List<StackNode> branchStackNodes, Map<StackNode, Set<Node>> branchToNext) 
	    throws AbortedException, MemoryMonitorException {

		Set<Node> workingSet = new IteratableSet<Node>();
		Set<Node> currentExclusionsFound = new IteratableSet<Node>();

		Map<NodeDescriptor, Node> nodeToWorkingSet = new IteratableMap<NodeDescriptor, Node>();
		NodeDescriptor startNodeDescriptor = new NodeDescriptor(startInst, currentStack);
		Set<Node> loopFormingEndNodes = new NonIteratableSet<Node>();
		findReachableGraph(null, currentStack, startInst, workingSet,
				exclusionStackNodes, currentExclusionsFound,
				RouteMode.PATH, endStackNodes, nodeToWorkingSet, loopFormingEndNodes, true, branchStackNodes, branchToNext);
		removeExclusionBranches(workingSet, currentExclusionsFound, loopFormingEndNodes);

		if(workingSet.isEmpty()){
			return null;
		}
		Node node = convertGraphToStructure(workingSet, startNodeDescriptor, nodeToWorkingSet);

		return node;
	}

	private void findReachableGraph(
			Node previous,
			Stack<FunctionReferenceNode> currentStack, 
			Node node,
			Set<Node> workingSet,
			List<StackNode> exclusionStackNodes,
			Set<Node> currentExclusionsFound,
			RouteMode routeMode,
			List<StackNode> endStackNodes,
			Map<NodeDescriptor, Node> nodeToWorkingSet, 
			Set<Node> loopFormingEndNodes,
			boolean firstInstruction, List<StackNode> branchStackNodes, Map<StackNode, Set<Node>> branchToNext)
	throws AbortedException, MemoryMonitorException {
		
		boolean repeat = true;
		while(repeat){
			
			repeat = false;
			
			Node currentNode = getCurrentNode(currentStack, node, nodeToWorkingSet);
			//System.out.println("on node " + currentNode);
			if(currentNode instanceof InstructionNode && ((InstructionNode)currentNode).getPC() == 0x10120){
			//	System.out.println("on node " + currentNode);
			}
			
			if(stackNodesContainNode(exclusionStackNodes, currentStack, node)&& !firstInstruction){
				currentExclusionsFound.add(currentNode);
				addToWorkingSet(previous, currentNode, workingSet);
				return;
			}
			
			if(node instanceof FunctionReferenceNode){
				FunctionReferenceNode frn = ((FunctionReferenceNode)node);
				
				
				List<InstructionNode> functionCallInstructions = new LinkedList<InstructionNode>();
				for(FunctionReferenceNode f : currentStack){
					functionCallInstructions.add(f.getFunctionStartInstruction());
				}
				
				if(functionCallInstructions.contains(frn.getFunctionStartInstruction())){
					//recursion detected
					FunctionNode fn = new FunctionNode(frn.getFunctionName());
					currentNode = fn;
					previous.setFormsRecursion(true);
					loopFormingEndNodes.add(previous);
					return;

				} else {
					boolean endPointsWithin = false; 
					for(StackNode endStackNode : endStackNodes){
						for(Stack<FunctionReferenceNode> stack : endStackNode.getNodeStacks()){
							if(stack.contains(node)){
								endPointsWithin = true;
							}
						}
					}
					
					Stack<FunctionReferenceNode> newStack = new Stack<FunctionReferenceNode>();
					newStack.addAll(currentStack);
					newStack.add(frn);
					
					if(endPointsWithin){
						InstructionNode inst = frn.getFunctionStartInstruction();
						
						findReachableGraph(previous, newStack, inst, workingSet,
								exclusionStackNodes, currentExclusionsFound, routeMode,
								endStackNodes, nodeToWorkingSet, loopFormingEndNodes, false, branchStackNodes, branchToNext);
						return;
					} else {
						FunctionNode fn = null;
						boolean continueAnalysis = false;
						if(frn.isKnownFunction()){
							Pair<FunctionNode, Boolean> fnReturn = makeFunction(newStack, exclusionStackNodes, branchStackNodes, false, branchToNext);
							continueAnalysis = fnReturn.second;
							fn = fnReturn.first;
						} else {
							fn = new FunctionNode();
						}
						
						if(fn != null ){
							if(continueAnalysis){
								currentNode = fn;
							
						} else {
								currentNode = fn;
								addToWorkingSet(previous, currentNode, workingSet);
								loopFormingEndNodes.add(currentNode);
								return;
							}
							
						} else {
							
							currentExclusionsFound.add(previous);
								return;
						}
					}
				}
			} else if(currentNode.isUnresolved()){
				addToWorkingSet(previous, currentNode, workingSet);
				return;
			} else if(stackNodesContainNode(endStackNodes, currentStack, node)){
				switch (routeMode) {
				case FUNCTION:
					addToWorkingSet(previous, currentNode, workingSet);
					return;
				case PATH:
					if(!workingSet.isEmpty()){
						loopFormingEndNodes.add(previous);
						connectToNullNode(previous, workingSet);
						return;
					}
				}
	
			} 
				
			addToWorkingSet(previous, currentNode, workingSet);
			
			Set<Node> nextNodes = getValidBranches(branchStackNodes, currentStack, node, branchToNext);
			
			
		//	System.out.println("nexts: " + nextNodes);
			
			if(nextNodes.size() == 0){
				FunctionReferenceNode callingFnRef = currentStack.peek();
				for(Node followingFunction : callingFnRef.getNextNodes()){
					Stack<FunctionReferenceNode> newStack = new Stack<FunctionReferenceNode>();
					newStack.addAll(currentStack);
					newStack.pop();
					findReachableGraph(currentNode, newStack, followingFunction, workingSet,
							exclusionStackNodes, currentExclusionsFound, routeMode, endStackNodes,
							nodeToWorkingSet, loopFormingEndNodes, false, branchStackNodes, branchToNext);
					 
				}
			} else if(nextNodes.size() == 1){
				Node next = nextNodes.iterator().next();
				Node nextWorkingSetNode = containsNode(next, currentStack, nodeToWorkingSet);
				if(nextWorkingSetNode == null){
					previous = currentNode;
					node = next;
					firstInstruction = false;
					repeat = true;
				} else {
					boolean connected = false;
					if(routeMode == RouteMode.FUNCTION || 
							(!stackNodesContainNode(endStackNodes, currentStack, next) &&
									
									!stackNodesContainNode(exclusionStackNodes, currentStack, next)
							//true		
							)){
						CommonMethods.connect(nextWorkingSetNode, currentNode);
						connected = true;
					}
					if(stackNodesContainNode(endStackNodes, currentStack, next) && ! connected){
						loopFormingEndNodes.add(currentNode);
						connectToNullNode(currentNode, workingSet);
					} 
				}
			} else if(nextNodes.size() > 1){
				for(Node next : nextNodes){
					Node nextWorkingSetNode = containsNode(next, currentStack, nodeToWorkingSet);
					if(nextWorkingSetNode == null){
						findReachableGraph(currentNode, currentStack, next, workingSet,
								exclusionStackNodes, currentExclusionsFound, routeMode, endStackNodes,
								nodeToWorkingSet, loopFormingEndNodes, false, branchStackNodes, branchToNext);
					} else {
						boolean connected = false;
						if(routeMode == RouteMode.FUNCTION || !stackNodesContainNode(endStackNodes, currentStack, next)){
							CommonMethods.connect(nextWorkingSetNode, currentNode);
							connected = true;
						}
						if(stackNodesContainNode(endStackNodes, currentStack, next) && ! connected){
							loopFormingEndNodes.add(currentNode);
							connectToNullNode(currentNode, workingSet);
						} 
					}
				}
			} 
		}
	}
	
	private void connectToNullNode(Node currentNode,
			Set<Node> workingSet) {
		//search the working set for a null node
		NullNode nullNode = null;
		
		for(Node node : workingSet){
			if(node instanceof NullNode){
				nullNode = (NullNode) node;
				break;
			}
		}
		if(nullNode == null){
			nullNode = new NullNode();
		}
		
		addToWorkingSet(currentNode, nullNode, workingSet);
	}

	private Set<Node> getValidBranches(List<StackNode> branchStackNodes,
			Stack<FunctionReferenceNode> currentStack, Node node, Map<StackNode, Set<Node>> branchToNext) {
		Set<Node> validNextNodes = new IteratableSet<Node>();
		
		validNextNodes.addAll(node.getNextNodes());
		
		for(StackNode sn : branchStackNodes){
			if(!sn.getNodeStacks().contains(currentStack)){
				validNextNodes.removeAll(branchToNext.get(sn));
			}
		}
		
		return validNextNodes;
	}

	private boolean alreadyContainRoute(Node newGraph) {
		final List<String> newNodeList = createRouteDescription(newGraph);

		for (Route route : m_routes){
			List<String> nodeList = createRouteDescription(route.getNode());
			if (newNodeList.equals(nodeList)){
				return true;
			}
		}
		return false;
	}

	private List<String> createRouteDescription(Node node) {
		final List<String> nodeList = new LinkedList<String>();

		node.traverse(new UntypedNodeTraverser() {
	    	public TraverseStatus foundNode(Node node) {
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
			Map<NodeDescriptor, Node> nodeToWorkingSet) 
	    throws AbortedException, MemoryMonitorException {
		
		Node originalStartNode = null;
		for(NodeDescriptor nd : nodeToWorkingSet.keySet()){
			if(startNodeDescriptor.equals(nd) && workingSet.contains(nodeToWorkingSet.get(nd))){
				originalStartNode = nodeToWorkingSet.get(nd);
				break;
			}
		}
		assert(originalStartNode != null);
		StructuralAnalysisEngine sae = new StructuralAnalysisEngine();
		
		dumpGraph(workingSet);
		Node startNode = sae.basicBlock(workingSet, originalStartNode, m_engine, "route");
		
		dumpGraph(workingSet);
		Node structuralNode = sae.createStructure(workingSet, startNode, m_engine, "Converting CFG to structure");
		assert(workingSet.size() == 1);
		sae.normaliseGraph(structuralNode);
		return structuralNode;
	}

	//TODO clean up the next two functions
	private List<StackNode> getFunctionEndStackNodes(Stack<FunctionReferenceNode> stack) {
		List<StackNode> endStacks = new LinkedList<StackNode>();
		Set<Node> seen = new NonIteratableSet<Node>();
		Set<InstructionNode> endNodes = new IteratableSet<InstructionNode>();
		Set<Node> following = new IteratableSet<Node>();
		following.add(stack.peek().getFunctionStartInstruction());
		while(!following.isEmpty()){
			Node current = following.iterator().next();
			seen.add(current);
			if(current.getNextNodesSize() == 0){
				if(current instanceof InstructionNode){
					endNodes.add((InstructionNode)current);
				} else {
					assert(false);
				}
			} else {
				for(Node next : current.getNextNodes()){
					if(!seen.contains(next)){
						following.add(next);
					}
				}
			}
			following.remove(current);
		}
		for(InstructionNode endInst : endNodes){
			List<Stack<FunctionReferenceNode>> stacks = new LinkedList<Stack<FunctionReferenceNode>>();
			stacks.add(stack);
			StackNode end = new StackNode(endInst, stacks);
			endStacks.add(end);
		}
		return endStacks;
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

	private Node containsNode(Node next, Stack<FunctionReferenceNode> currentStack, Map<NodeDescriptor, Node> nodeToWorkingSet){
		NodeDescriptor nd = new NodeDescriptor(next, currentStack);
		return nodeToWorkingSet.get(nd);
	}

	
	static class NodeDescriptor implements Comparable<NodeDescriptor>{
		private Node m_node;
		private Stack<FunctionReferenceNode> m_stack;

		private int m_id;
		public NodeDescriptor(Node node, Stack<FunctionReferenceNode> stack){
			m_node = node;
			m_stack = stack;
			m_id = node.getId();
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

		public int compareTo(NodeDescriptor o) {
			return m_id - o.m_id;
		}
	}

	private Node getCurrentNode(Stack<FunctionReferenceNode> currentStack,
			Node node, Map<NodeDescriptor, Node> nodeToWorkingSet) throws AbortedException {
		NodeDescriptor nd = new NodeDescriptor(node, currentStack);
		if(nodeToWorkingSet.containsKey(nd)){
			return nodeToWorkingSet.get(nd);
		} else {
			Node copy = node.copyRouteStructure(m_engine);
			nodeToWorkingSet.put(nd, copy);
			return copy;
		}
	}

	private boolean stackNodesContainNode(List<StackNode> stackNodes, Stack<FunctionReferenceNode> currentStack, Node currentNode){
		if(!(currentNode instanceof InstructionNode)){
			return false;
		}
		for(StackNode stackNode : stackNodes){
			if(stackNode.getInstruction().equals(currentNode)){
			//if(stackNode.getInstruction().getPC() == ((InstructionNode)currentNode).getPC()){
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
		if(parent!= null){
			CommonMethods.connect(currentNode, parent);
		}
		
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
		System.out.print("digraph g {");
		for(Node node : workingSet){
			if(node instanceof InstructionNode){
				System.out.print("node" +node.getId() + " [label=\"" + ((InstructionNode)node).getXtaInstruction() + "\"];");
			} else {
				System.out.print("node" +node.getId() + " [label=\"" + node + "\"];");
			}
			for(Node next : node.getNextNodes()){
				System.out.print("node" +node.getId() + " -> node" + next.getId() + ";");
				if(!workingSet.contains(next)){
					System.out.print("node" +next.getId() + " [label=\"" + next + "\", style=filled, color=blue];");
				}
			}
			for(Node prev : node.getPrevNodes()){
				System.out.print("node" +prev.getId() + " -> node" + node.getId() + "[style=dotted];");
				if(!workingSet.contains(prev)){
					System.out.print("node" +prev.getId() + " [label=\"" + prev + "\", style=filled, color=red];");
				}
			}
		}
		System.out.println("}");
	}

	private void setUnknowns() throws AbortedException {
		m_engine.getAbort();
		m_engine.provideFeedback("Setting unknowns on routes...");
		
		for (final Route route : m_routes) {
			Node node = route.getNode();
			node.traverse( new DefaultNodeTraverser() {
				// Sets all the pausing instructions to unknown.
				public TraverseStatus foundInstructionNode(Node node) {
					XtaInstruction instruction = ((InstructionNode)node).getXtaInstruction();
					if (instruction.canPause()) {
						NodeAttributes nodeAttributes = node.getAttributes();
						nodeAttributes.addUnknown(route, UnknownFactory.Type.INSTRUCTION_TIME);
					}
					return TraverseStatus.CONTINUE;
				}
				// Sets all the loop iterations to unknown.
				public TraverseStatus foundLoopNode(Node node) {
					NodeAttributes nodeAttributes = node.getAttributes();
					nodeAttributes.addUnknown(route, UnknownFactory.Type.LOOP_ITERATIONS);
					return TraverseStatus.CONTINUE;
				}
				// Sets all the self loop iterations to unknown.
				public TraverseStatus foundSelfLoopNode(Node node) {
					NodeAttributes nodeAttributes = node.getAttributes();
					nodeAttributes.addUnknown(route, UnknownFactory.Type.LOOP_ITERATIONS);
					return TraverseStatus.CONTINUE;
				}
		    });
		    refreshDisplayedUnknowns(node);
		}
	}

	private void setSrcLoopCounts() throws AbortedException {
		m_engine.getAbort();
		m_engine.provideFeedback("Setting loop counts on routes...");

		CoreId coreId = m_coreContainer.getCoreId();
		
		// Sets the loop iteration counts specified at compile time.
		for (SrcLevelLoopInfo srcLevelLoopInfo : m_engine.getSrcLevelLoops(coreId)) {

			Reference reference = ReferenceFactory.createReference(
					String.format("0x%x", srcLevelLoopInfo.getPc()),
					ReferenceClass.INSTRUCTION, coreId);

			CommandSource src = new CommandSource(Type.COMPILER);
			for (Route route : m_routes) {
				try {
					route.getRouteUnknowns().setLoopIterations(src, m_engine,
							reference, srcLevelLoopInfo.getNumIterations(), true, Source.BINARY);
				} catch (Exception e) {}
			}
		}
	}

	private void checkSupportedInstructions(final CommandSource src) throws AbortedException {
		m_engine.getAbort();
		m_engine.provideFeedback("Checking for unsupported instructions...");		
		
		final Engine engine = m_engine;
		final InstructionMnemonics mnenonics = new InstructionMnemonics(
				m_engine.getXeLoader().getArchitecture());
		
		for (Route route : m_routes) {
            if (route.getRouteTiming().getWorstCaseTimeStatus().isComplete()) {
            	
    			Node node = route.getNode();				
    			node.traverse(new DefaultNodeTraverser() {
    				public TraverseStatus foundInstructionNode(Node node) {
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
	}

	private void applyDefines(CommandSource src) throws AbortedException {
		m_engine.getAbort();
		m_engine.provideFeedback("Applying defines to route...");		

		RouteDefines defines = new RouteDefines();
		for (Route route : m_routes) {
			for (FunctionTimeInfo info : m_engine.getFunctionTimeDefines()) {
				if (coreMatch(route, info.getFunction())) {
    				defines.applyFunctionDefine(src, route, info, Source.ADD);
				}
			}
			for (PathTimeInfo info : m_engine.getPathTimeDefines()) {
				if (coreMatch(route, info.getFrom()) && 
					coreMatch(route, info.getTo())) {
    				defines.applyPathDefine(m_engine, src, route, info, Source.ADD);
				}
			}
		}
		for (LoopInfo info : m_engine.getLoopDefines()) {			
			try {
				for (Route route : m_routes) {
					if (coreMatch(route, info.getReference())) {
					    route.getRouteUnknowns().checkSetLoopIterations(
							info.getReference(), info.getIterations());
					}
				}
				for (Route route : m_routes) {
					if (coreMatch(route, info.getReference())) {
    				    route.getRouteUnknowns().setLoopIterations(src, m_engine,
				    		info.getReference(), info.getIterations(), false, Source.ADD);
					}
				}				
			} catch (Exception e) {}
		}
		for (LoopPathInfo info : m_engine.getLoopPathDefines()) {
			try {
				for (Route route : m_routes) {
					if (coreMatch(route, info.getReference())) {
    					route.getRouteUnknowns().checkSetLoopPathIterations(
							info.getReference(), info.getIterations());
					}
				}
				for (Route route : m_routes) {
					if (coreMatch(route, info.getReference())) {
    				    route.getRouteUnknowns().setLoopPathIterations(
				    		info.getReference(), info.getIterations(), Source.ADD);
					}
				}
			} catch (Exception e) {}
		}
		for (LoopScopeInfo info : m_engine.getLoopScopeDefines()) {
			try {
				for (Route route : m_routes) {
					if (coreMatch(route, info.getReference())) {
    					route.getRouteUnknowns().checkSetLoopScope(
							info.getReference(), info.getAbsolute());
					}
				}
				for (Route route : m_routes) {
					if (coreMatch(route, info.getReference())) {
    				    route.getRouteUnknowns().setLoopScope(
				    		info.getReference(), info.getAbsolute(), Source.ADD);
					}
				}
			} catch (Exception e) {}
		}
		for (InstructionTimeInfo info : m_engine.getInstructionTimeDefines()) {
			try {
				for (Route route : m_routes) {
					if (coreMatch(route, info.getReference())) {
    					if (!route.getRouteUnknowns().canInstructionPause(info.getReference()))
	    					throw new Exception();
					}
			    }
			    for (Route route : m_routes) {
					if (coreMatch(route, info.getReference())) {
    			        route.getRouteUnknowns().setFixedTime(info.getReference(), 
    			        		info.getTimeUnit(), UnknownFactory.Type.INSTRUCTION_TIME, Source.ADD);
					}
    			}				
			} catch (Exception e) {}
		}
	}

	private boolean coreMatch(Route route, Reference reference) {
		return reference.getCoreId().equals(route.getRouteCoreContainer().getCoreId());
	}
	
	private void refreshDisplayedUnknowns(Node node) {
		// Clears the display unknown flags on all nodes..
	    node.traverse(new UntypedNodeTraverser() {
	    	public TraverseStatus foundNode(Node node) {
	    		node.getAttributes().setDisplayAsUnknown(false);
	        	return TraverseStatus.CONTINUE;
	    	}
	    });

		// On each of the unknown nodes, mark all of their parents to 'display unknown'.
	    node.traverse(new UntypedNodeTraverser() {
	    	public TraverseStatus foundNode(Node node) {
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
			if(parent.getAttributes().getDisplayAsUnknown() == false){
				parent.getAttributes().setDisplayAsUnknown(true);
			}
			parent.getAttributes().setDisplayAsUnknown(true);
			parent = parent.getParent();
		}
	}


}
