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
import com.xmos.xta.core.engine.CallGraph;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.CommandSource.Type;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer.RouteMode;
import com.xmos.xta.core.engine.analysis.structural.StructuralAnalysisEngine;
import com.xmos.xta.core.engine.analysis.structural.nodes.BlockNode;
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
		ABORTED
	}

	private CallGraph m_callGraph;
	private List<Route> m_routes;
	private Engine m_engine;
	private RouteCoreContainer m_coreContainer;
	
	public RouteConstructor(Engine engine, CallGraph callGraph,
			RouteCoreContainer routeCoreContainer) throws AbortedException {
		assert(m_callGraph != null);
		assert(m_engine != null);
		
		
		m_callGraph = callGraph;
		m_routes = new ArrayList<Route>();
		m_engine = engine;
		m_coreContainer = routeCoreContainer;		
	}

	public List<Route> getRoutes() {
		return m_routes;
	}

	public RouteError analyse(CommandSource src) throws Exception {
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
		
		if (routeContainer.getMode() == RouteMode.FUNCTION){
			List<StackNode> startStackNodes = getFunctionStartStackNodes(m_callGraph, routeContainer.getFunctionName());
			for(StackNode startStackNode : startStackNodes){
				for(Stack<FunctionReferenceNode> stack : startStackNode.getNodeStacks()){
					List<StackNode> endStackNodes = getFunctionEndStackNodes(stack);
					FunctionNode newFunction = makeFunction(stack, exclusionStackNodes, m_engine, endStackNodes, true);
					if (newFunction != null){
						setParent(newFunction);
						Route route = new Route(src, m_engine, stack, newFunction, m_coreContainer);
						if (!alreadyContainRoute(newFunction)) {
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
					Node newPath = makePath(currentStack, startStackNode.getInstruction(), endStackNodes, exclusionStackNodes, m_engine);
					if (newPath != null){
						setParent(newPath);
						Route route = new Route(src, m_engine, currentStack, newPath, m_coreContainer);
						if (!alreadyContainRoute(newPath)) {
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
		return prm.matchReference(callGraph, ref);
	}

	private List<StackNode> getFunctionStartStackNodes(CallGraph callGraph, String function) {
		PreanalysisReferenceMatcher prm = new PreanalysisReferenceMatcher();
		return prm.matchReference(callGraph, function);
	}

	private List<StackNode> getFunctionEndStackNodes(Stack<FunctionReferenceNode> stack) {
		// TODO Auto-generated method stub
		//get all the end 
		
		return null;
	}

	private FunctionNode makeFunction(Stack<FunctionReferenceNode> currentStack, List<StackNode> exclusionStackNodes, 
			Aborter aborter, List<StackNode> endStackNodes, boolean start) throws AbortedException{
		
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
				exclusionStackNodes, currentExclusionsFound,  aborter, RouteMode.FUNCTION,
				endStackNodes, nodeToWorkingSet, loopFormingEndNodes, start);

		dumpGraph(workingSet);
		removeExclusionBranches(workingSet, currentExclusionsFound, loopFormingEndNodes);
		dumpGraph(workingSet);

		if(workingSet.isEmpty()){
			return null;
		}
		Node node = convertGraphToStructure(workingSet, startNodeDescriptor, nodeToWorkingSet);

		FunctionNode newFunction = new FunctionNode(node, fnRef.getFunctionName());

		return newFunction;
	}


	private Node makePath(Stack<FunctionReferenceNode> currentStack, InstructionNode startInst, List<StackNode> endStackNodes,
			List<StackNode> exclusionStackNodes, Aborter aborter) throws AbortedException{

		Set<Node> workingSet = new TreeSet<Node>();
		Set<Node> currentExclusionsFound = new TreeSet<Node>();

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

	private void findReachableGraph(
			Node parent,
			Stack<FunctionReferenceNode> currentStack, Node node,
			Set<Node> workingSet,
			List<StackNode> exclusionStackNodes,
			Set<Node> currentExclusionsFound,
			Aborter aborter,
			RouteMode routeMode,
			List<StackNode> endStackNodes,
			Map<NodeDescriptor, Node> nodeToWorkingSet, Set<Node> loopFormingEndNodes,
			boolean firstInstruction)
	throws AbortedException {

		Node currentNode = getCurrentNode(currentStack, node, nodeToWorkingSet, aborter);
		
		if(stackNodesContainNode(exclusionStackNodes, currentStack, node)&& !firstInstruction){
			currentExclusionsFound.add(currentNode);
			addToWorkingSet(parent, currentNode, workingSet);
			return;
		}
		
		if(currentNode instanceof FunctionReferenceNode){
			
			FunctionReferenceNode frn = ((FunctionReferenceNode)currentNode);
			
			boolean endPointsWithin = false; 
			
			for(StackNode endStackNode : endStackNodes){
				for(Stack<FunctionReferenceNode> stack : endStackNode.getNodeStacks()){
					if(stack.contains(frn)){
						endPointsWithin = true;
					}
				}
			}
			
			Stack<FunctionReferenceNode> newStack = new Stack<FunctionReferenceNode>();
			newStack.addAll(currentStack);
			newStack.add(frn);
			if(endPointsWithin){
				InstructionNode inst = frn.getFunctionStartInstruction();
				findReachableGraph(currentNode, newStack, inst, workingSet,
						exclusionStackNodes, currentExclusionsFound, aborter, routeMode,
						endStackNodes, nodeToWorkingSet, loopFormingEndNodes, false);
			} else {
				FunctionNode fn = makeFunction(newStack, exclusionStackNodes, aborter, endStackNodes, false);
				if(fn != null){
					addToWorkingSet(parent, fn, workingSet);
					currentNode = fn;
				} else {
					currentExclusionsFound.add(currentNode);
					return;
				}
			}
		} else if(stackNodesContainNode(endStackNodes, currentStack, node) && !workingSet.isEmpty()){
			switch (routeMode) {
			case FUNCTION:
				addToWorkingSet(parent, currentNode, workingSet);
			case PATH:
				loopFormingEndNodes.add(parent);
			}
			return;
		} else {
			addToWorkingSet(parent, currentNode, workingSet);
		}
		
		for(Node next : node.getNextNodes()){
			if(next instanceof NullNode){
				FunctionReferenceNode callingFnRef = currentStack.peek();
				for(Node followingFunction : callingFnRef.getNextNodes()){
					Stack<FunctionReferenceNode> newStack = new Stack<FunctionReferenceNode>();
					newStack.addAll(currentStack);
					newStack.pop();
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
		StructuralAnalysisEngine sae = new StructuralAnalysisEngine();
		Node startNode = makeBasicBlocks(workingSet, startNodeDescriptor, nodeToWorkingSet);
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
			while(nextIsInSameBlock(current, workingSet, seenNodes) ){
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

	private boolean nextIsInSameBlock(Node current, Set<Node> workingSet, Set<Node> seenNodes) {
		if(getNextNodes(current, workingSet).size() != 1){
			return false;
		}
		Node next = getNextNodes(current, workingSet).iterator().next();
		
		if(seenNodes.contains(next)){
			return false;
		}
		
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

	private boolean formsRecursion(Stack<FunctionReferenceNode> currentStack,
			FunctionReferenceNode frn) {
		//TODO
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

	private boolean stackNodesContainNode(List<StackNode> stackNodes, Stack<FunctionReferenceNode> currentStack, Node currentNode){
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
			node.traverse( new DefaultNodeTraverser() {
				// Sets all the pausing instructions to unknown.
				public TraverseStatus foundInstructionNode(Node node) {
					XtaInstruction instruction = ((InstructionNode)node).getXtaInstruction();
					if (instruction.canPause()) {
						NodeAttributes nodeAttributes = node.getAttributes();
						nodeAttributes.addUnknown(UnknownFactory.Type.INSTRUCTION_TIME);
					}
					return TraverseStatus.CONTINUE;
				}
				// Sets all the loop iterations to unknown.
				public TraverseStatus foundLoopNode(Node node) {
					NodeAttributes nodeAttributes = node.getAttributes();
					nodeAttributes.addUnknown(UnknownFactory.Type.LOOP_ITERATIONS);
					return TraverseStatus.CONTINUE;
				}
				// Sets all the self loop iterations to unknown.
				public TraverseStatus foundSelfLoopNode(Node node) {
					NodeAttributes nodeAttributes = node.getAttributes();
					nodeAttributes.addUnknown(UnknownFactory.Type.LOOP_ITERATIONS);
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
    				defines.applyFunctionDefine(src, route, info, Source.BINARY);
				}
			}
			for (PathTimeInfo info : m_engine.getPathTimeDefines()) {
				if (coreMatch(route, info.getFrom()) && 
					coreMatch(route, info.getTo())) {
    				defines.applyPathDefine(m_engine, src, route, info, Source.BINARY);
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
				    		info.getReference(), info.getIterations(), false, Source.BINARY);
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
				    		info.getReference(), info.getIterations(), Source.BINARY);
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
				    		info.getReference(), info.getAbsolute(), Source.BINARY);
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
    			        		info.getTimeUnit(), UnknownFactory.Type.INSTRUCTION_TIME, Source.BINARY);
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
			parent.getAttributes().setDisplayAsUnknown(true);
			parent = parent.getParent();
		}
	}


/*
	private Set<StackNode> getEndStackNodes(RouteContainer routeContainer) {
		Set<StackNode> stackNodes = new HashSet<StackNode>();
		
		if (routeContainer.getMode() == RouteMode.FUNCTION){
			List<FunctionNode> fns = m_coreContainer.getFunction(m_structureGraph);
			for(FunctionNode fn : fns){
				for(InstructionNode inst : m_structureGraph.getFunctionsEndNodes(fn)){
					StackNode stackNode = new StackNode(inst, getCallStacks(fn));
					stackNodes.add(stackNode);
				}
			}
		} else {
			Collection<Reference> toRefs = m_coreContainer.getToReferences();
			for (Reference toRef : toRefs){
				stackNodes.addAll(referenceToStackNode(toRef));
			}
		}
		return stackNodes;
	}
*/
}
