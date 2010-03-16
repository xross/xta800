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

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.CallGraph;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.CommandSource.Type;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer.RouteMode;
import com.xmos.xta.core.engine.analysis.structural.StructuralAnalysisEngine;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
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

public class RouteConstructor {

	private static boolean debug_route = false;
	private static boolean debug_exculsions = false;
	private static boolean debug_branches = false;
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

	private CallGraph m_callGraph;
	private List<Route> m_routes;
	private Engine m_engine;
	private RouteCoreContainer m_coreContainer;
	
	public RouteConstructor(Engine engine, CallGraph callGraph,
			RouteCoreContainer routeCoreContainer) throws AbortedException {
		m_callGraph = callGraph;
		m_routes = new ArrayList<Route>();
		m_engine = engine;
		m_coreContainer = routeCoreContainer;	
		
		assert(m_callGraph != null);
		assert(m_engine != null);
	}

	public List<Route> getRoutes() {
		return m_routes;
	}

	public RouteError analyze(CommandSource src) throws Exception {
		RouteContainer routeContainer = m_coreContainer.getRouteContainer();

		if (m_coreContainer.getFromReference() == null) {
			return RouteError.STARTPOINT_MISSING;
		}
		if (m_coreContainer.getToReferences() == null) {
			return RouteError.ENDPOINTS_MISSING;
		}

		List<StackNode> exclusionStackNodes = new LinkedList<StackNode>();
		for(Reference ref : m_engine.getExclusions()){
			exclusionStackNodes.addAll(getPathStackNodes(m_callGraph, ref));
		}
		
		List<StackNode> branchStackNodes = new LinkedList<StackNode>();
		for(Reference ref : m_engine.getUserBranches().keySet()){
			branchStackNodes.addAll(getPathStackNodes(m_callGraph, ref));
		}
		
		if (debug_exculsions) {
			System.out.println("exclusions are:");
			for(StackNode sn : exclusionStackNodes){
				System.out.println(sn);
			}	
		}
		
		if (debug_branches) {
			System.out.println("branches are:");
			for(StackNode sn : branchStackNodes){
				System.out.println(sn);
			}	
		}
		
		if (routeContainer.getMode() == RouteMode.FUNCTION){
			List<StackNode> startStackNodes = getFunctionStartStackNodes(m_callGraph, m_coreContainer.getFromReference());
			for(StackNode startStackNode : startStackNodes){
				for(Stack<FunctionReferenceNode> stack : startStackNode.getNodeStacks()){
					if (debug_exculsions) {
						System.out.println("starting from " +stack +  " " + startStackNode.getInstruction());
					}
					
					List<StackNode> endStackNodes = getFunctionEndStackNodes(stack);
					FunctionNode newFunction = makeFunction(stack, exclusionStackNodes, endStackNodes, true);
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
					Node newPath = makePath(currentStack, startStackNode.getInstruction(), endStackNodes, exclusionStackNodes);
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
			
			
		} else  
			assert(false);
		
		setUnknowns();
		setSrcLoopCounts();
		checkSupportedInstructions(src);
		applyDefines(src);
		return RouteError.SUCCESS;
	}

	private List<StackNode> getPathStackNodes(CallGraph callGraph, Reference ref) {
		PreanalysisReferenceMatcher prm = new PreanalysisReferenceMatcher();
		return prm.matchReference(callGraph, ref, m_engine);
	}

	private List<StackNode> getFunctionStartStackNodes(CallGraph callGraph, Reference reference) {
		PreanalysisReferenceMatcher prm = new PreanalysisReferenceMatcher();
		return prm.matchReference(callGraph, reference, m_engine);
	}
	
	private FunctionNode makeFunction(Stack<FunctionReferenceNode> currentStack, List<StackNode> exclusionStackNodes, 
			List<StackNode> endStackNodes, boolean start) throws AbortedException{
		
		FunctionReferenceNode fnRef = currentStack.peek();
		
		if(!fnRef.isKnownFunction()){
			return new FunctionNode();
		}
		
		InstructionNode startInst = fnRef.getStartInstruction();
		
		Set<Node> workingSet = new TreeSet<Node>();
		Set<Node> currentExclusionsFound = new TreeSet<Node>();
		Map<NodeDescriptor, Node> nodeToWorkingSet = new HashMap<NodeDescriptor, Node>();

		NodeDescriptor startNodeDescriptor = new NodeDescriptor(startInst, currentStack);

		Set<Node> loopFormingEndNodes = new TreeSet<Node>();
		
		findReachableGraph(null, currentStack, startInst, workingSet,
				exclusionStackNodes, currentExclusionsFound, RouteMode.FUNCTION,
				endStackNodes, nodeToWorkingSet, loopFormingEndNodes, start);

		dumpGraph(workingSet);
		removeExclusionBranches(workingSet, currentExclusionsFound, loopFormingEndNodes);
		dumpGraph(workingSet);

		if(workingSet.isEmpty()){
			return null;
		}
		Node node = convertGraphToStructure(workingSet, startNodeDescriptor, nodeToWorkingSet);

		FunctionNode newFunction = new FunctionNode(node, fnRef);

		return newFunction;
	}

	private Node makePath(Stack<FunctionReferenceNode> currentStack, InstructionNode startInst, List<StackNode> endStackNodes,
			List<StackNode> exclusionStackNodes) throws AbortedException{

		Set<Node> workingSet = new TreeSet<Node>();
		Set<Node> currentExclusionsFound = new TreeSet<Node>();

		Map<NodeDescriptor, Node> nodeToWorkingSet = new HashMap<NodeDescriptor, Node>();
		NodeDescriptor startNodeDescriptor = new NodeDescriptor(startInst, currentStack);
		Set<Node> loopFormingEndNodes = new TreeSet<Node>();
		findReachableGraph(null, currentStack, startInst, workingSet,
				exclusionStackNodes, currentExclusionsFound,
				RouteMode.PATH, endStackNodes, nodeToWorkingSet, loopFormingEndNodes, true);

		dumpGraph(workingSet);
		removeExclusionBranches(workingSet, currentExclusionsFound, loopFormingEndNodes);

		if(workingSet.isEmpty()){
			return null;
		}
		Node node = convertGraphToStructure(workingSet, startNodeDescriptor, nodeToWorkingSet);

		return node;
	}

	private void findReachableGraph(
			Node parent,
			Stack<FunctionReferenceNode> currentStack, 
			Node node,
			Set<Node> workingSet,
			List<StackNode> exclusionStackNodes,
			Set<Node> currentExclusionsFound,
			RouteMode routeMode,
			List<StackNode> endStackNodes,
			Map<NodeDescriptor, Node> nodeToWorkingSet, 
			Set<Node> loopFormingEndNodes,
			boolean firstInstruction)
	throws AbortedException {
		
		boolean repeat = true;
		while(repeat){
			
			repeat = false;
			
			Node currentNode = getCurrentNode(currentStack, node, nodeToWorkingSet);
			
			if(stackNodesContainNode(exclusionStackNodes, currentStack, node)&& !firstInstruction){
				currentExclusionsFound.add(currentNode);
				addToWorkingSet(parent, currentNode, workingSet);
				return;
			}
			
			if(node instanceof FunctionReferenceNode){
				FunctionReferenceNode frn = ((FunctionReferenceNode)node);
				
				
				List<InstructionNode> functionCallInstructions = new LinkedList<InstructionNode>();
				for(FunctionReferenceNode f : currentStack){
					functionCallInstructions.add(f.getFunctionStartInstruction());
				}
				if(functionCallInstructions.contains(frn.getFunctionStartInstruction())){
	
					FunctionNode fn = new FunctionNode(frn.getFunctionName());
					currentNode = fn;
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
						
						findReachableGraph(parent, newStack, inst, workingSet,
								exclusionStackNodes, currentExclusionsFound, routeMode,
								endStackNodes, nodeToWorkingSet, loopFormingEndNodes, false);
						return;
					} else {
						FunctionNode fn = null;
						if(frn.isKnownFunction()){
							List<StackNode> newEndStackNodes = getFunctionEndStackNodes(newStack);
							fn = makeFunction(newStack, exclusionStackNodes, newEndStackNodes, false);
							
						} else {
							fn = new FunctionNode();
						}
						if(fn != null){
							currentNode = fn;
						} else {
							currentExclusionsFound.add(parent);
							return;
						}
						
					}
				}
			} else if(stackNodesContainNode(endStackNodes, currentStack, node) ){
				switch (routeMode) {
				case FUNCTION:
					addToWorkingSet(parent, currentNode, workingSet);
					return;
				case PATH:
					if(!workingSet.isEmpty()){
						loopFormingEndNodes.add(parent);
						return;
					}
				}
	
			} 
				
			addToWorkingSet(parent, currentNode, workingSet);
			
			if(node.getNextNodes().size() == 0){
				FunctionReferenceNode callingFnRef = currentStack.peek();
				for(Node followingFunction : callingFnRef.getNextNodes()){
					Stack<FunctionReferenceNode> newStack = new Stack<FunctionReferenceNode>();
					newStack.addAll(currentStack);
					newStack.pop();
					findReachableGraph(currentNode, newStack, followingFunction, workingSet,
							exclusionStackNodes, currentExclusionsFound, routeMode, endStackNodes,
							nodeToWorkingSet, loopFormingEndNodes, false);
					 
				}
			} else if(node.getNextNodes().size() == 1){
				Node next = node.getNextNodes().iterator().next();
				Node nextWorkingSetNode = containsNode(next, currentStack, nodeToWorkingSet);
				if(nextWorkingSetNode == null){
					parent = currentNode;
					node = next;
					firstInstruction = false;
					repeat = true;
				} else {
					if(routeMode == RouteMode.FUNCTION || !stackNodesContainNode(endStackNodes, currentStack, next)){
						CommonMethods.connect(nextWorkingSetNode, currentNode);
					}
					if(stackNodesContainNode(endStackNodes, currentStack, next)){
						loopFormingEndNodes.add(currentNode);
					} 
				}
			} else if(node.getNextNodes().size() > 1){
				for(Node next : node.getNextNodes()){
					Node nextWorkingSetNode = containsNode(next, currentStack, nodeToWorkingSet);
					if(nextWorkingSetNode == null){
						findReachableGraph(currentNode, currentStack, next, workingSet,
								exclusionStackNodes, currentExclusionsFound, routeMode, endStackNodes,
								nodeToWorkingSet, loopFormingEndNodes, false);
					} else {
						if(routeMode == RouteMode.FUNCTION || !stackNodesContainNode(endStackNodes, currentStack, next)){
							CommonMethods.connect(nextWorkingSetNode, currentNode);
						}
						if(stackNodesContainNode(endStackNodes, currentStack, next)){
							loopFormingEndNodes.add(currentNode);
						} 
					}
				}
			} 
		}
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
			Map<NodeDescriptor, Node> nodeToWorkingSet) throws AbortedException {
		
		Node originalStartNode = null;
		for(NodeDescriptor nd : nodeToWorkingSet.keySet()){
			if(startNodeDescriptor.equals(nd) && workingSet.contains(nodeToWorkingSet.get(nd))){
				originalStartNode = nodeToWorkingSet.get(nd);
				break;
			}
		}
		assert(originalStartNode != null);
		StructuralAnalysisEngine sae = new StructuralAnalysisEngine();
		Node startNode = sae.basicBlock(workingSet, originalStartNode, m_engine, "route");
		Node structuralNode = sae.createStructure(workingSet, startNode, m_engine, "Converting CFG to structure");
		assert(workingSet.size() == 1);
		sae.normaliseGraph(structuralNode);
		return structuralNode;
	}

	//TODO clean up the next two functions
	private List<StackNode> getFunctionEndStackNodes(Stack<FunctionReferenceNode> stack) {
		List<StackNode> endStacks = new LinkedList<StackNode>();
		Set<Node> seen = new HashSet<Node>();
		Set<InstructionNode> endNodes = new TreeSet<InstructionNode>();
		Set<Node> following = new TreeSet<Node>();
		following.add(stack.peek().getFunctionStartInstruction());
		while(!following.isEmpty()){
			Node current = following.iterator().next();
			seen.add(current);
			if(current.getNextNodesSize() == 0){
				if(current instanceof InstructionNode){
					endNodes.add((InstructionNode)current);
				} else {
					System.out.println("broken on node " + current);
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
			Node node, Map<NodeDescriptor, Node> nodeToWorkingSet) throws AbortedException {
		NodeDescriptor nd = new NodeDescriptor(node, currentStack);
		if(nodeToWorkingSet.containsKey(nd)){
			return nodeToWorkingSet.get(nd);
		} else {
			Node copy = node.deepCopy(m_engine);
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
			
			if(debug_route) System.out.println("\t\t" + parent+ "->"  + currentNode );
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

	private void setSrcLoopCounts() {
		int nodeNumber = m_coreContainer.getNodeNumber();
		int coreNumber = m_coreContainer.getCoreNumber();
		
		// Sets the loop iteration counts specified at compile time.
		for (SrcLevelLoopInfo srcLevelLoopInfo : m_engine.getSrcLevelLoops(nodeNumber, coreNumber)) {

			Reference reference = ReferenceFactory.createReference(
					String.format("0x%x", srcLevelLoopInfo.getPc()),
					ReferenceClass.PC_REF, nodeNumber, coreNumber);

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

	private void applyDefines(CommandSource src) throws AbortedException {
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
		int nodeNumber = route.getRouteCoreContainer().getNodeNumber();
		int coreNumber = route.getRouteCoreContainer().getCoreNumber();
		return ((reference.getNodeNumber() == nodeNumber) &&
		        (reference.getCoreNumber() == coreNumber));
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
