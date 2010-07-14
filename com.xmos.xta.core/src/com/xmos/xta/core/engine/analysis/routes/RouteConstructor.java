package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.xmos.libs.controlflow.ControlFlow;
import com.xmos.libs.controlflow.nodes.CfgFunctionReferenceNode;
import com.xmos.libs.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.controlflow.nodes.CfgNode;
import com.xmos.libs.controlflow.nodes.CfgNullNode;
import com.xmos.libs.instructiondecoder.Instruction;
import com.xmos.libs.instructiondecoder.InstructionMnemonics;
import com.xmos.libs.memorymonitor.MemoryMonitorException;
import com.xmos.libs.util.CoreId;
import com.xmos.libs.util.IteratableMap;
import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.NonIteratableSet;
import com.xmos.libs.util.Pair;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.CommandSource.Type;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer.RouteMode;
import com.xmos.xta.core.engine.analysis.structural.CachedFunctionMap;
import com.xmos.xta.core.engine.analysis.structural.CfgContainerNode;
import com.xmos.xta.core.engine.analysis.structural.StructuralAnalysisEngine;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.Source;
import com.xmos.xta.core.engine.defines.ExclusionInfo;
import com.xmos.xta.core.engine.defines.FunctionTimeInfo;
import com.xmos.xta.core.engine.defines.InstructionTimeInfo;
import com.xmos.xta.core.engine.defines.LoopInfo;
import com.xmos.xta.core.engine.defines.LoopPathInfo;
import com.xmos.xta.core.engine.defines.LoopScopeInfo;
import com.xmos.xta.core.engine.defines.PathTimeInfo;
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

	private ControlFlow m_controlFlow;
	private CachedFunctionMap m_cachedFunctionMap;
	private List<Route> m_routes;
	private Engine m_engine;
	private RouteCoreContainer m_coreContainer;
	private CoreId m_core;
	
	public static List<StackNode> getPathStackNodes(ControlFlow controlFlow, 
			CachedFunctionMap cachedFunctionMap, Reference reference) 
			throws AbortedException, MemoryMonitorException {
		
	    ControlFlowReferenceMatcher cfrm = new ControlFlowReferenceMatcher(controlFlow, cachedFunctionMap);
	    return cfrm.matchReference(reference);
    }
	
	public RouteConstructor(Engine engine, ControlFlow controlFlow, 
			CachedFunctionMap cachedFunctionMap, RouteCoreContainer routeCoreContainer, 
			CoreId core) throws AbortedException {
		
		m_controlFlow = controlFlow;
		m_cachedFunctionMap = cachedFunctionMap;
		m_routes = new ArrayList<Route>();
		m_engine = engine;
		m_coreContainer = routeCoreContainer;	
		m_core = core;
	}

	public List<Route> getRoutes() {
		return m_routes;
	}

	public void analyze(CommandSource src) throws Exception {
		m_engine.provideFeedback("Starting analysis...");
		
		List<StackNode> branchStackNodes = new LinkedList<StackNode>();
		Map<StackNode, Set<CfgNode>> branchToNext = new IteratableMap<StackNode, Set<CfgNode>>();
		buildBranchStackNodes(branchStackNodes, branchToNext);

		List<StackNode> exclusionStackNodes = new LinkedList<StackNode>();
        buildExclusionStackNodes(exclusionStackNodes);
        
		switch (m_coreContainer.getRouteContainer().getMode()) {
		case FUNCTION:
			analyzeFunction(src, branchStackNodes, branchToNext, exclusionStackNodes);
			break;

		case LOOP:
			analyzeLoop(src, branchStackNodes, branchToNext, exclusionStackNodes);
			break;

		case ENDPOINTS:
		    analyzeEndpoints(src, branchStackNodes, branchToNext, exclusionStackNodes);
			break;
			
		default:
			assert(false);
		}
		
		setUnknowns();
		setSrcLoopCounts();
		checkSupportedInstructions(src);
		applyDefines(src);
		
		m_engine.provideFeedback("Analysis complete");
	}
	
	static void setParent(Node node) {
		for (Node child : node.iChildren()){
			child.setParent(node);
			setParent(child);
		}
	}
	
	private void analyzeFunction(CommandSource src, List<StackNode> branchStackNodes, 
			Map<StackNode, Set<CfgNode>> branchToNext, List<StackNode> exclusionStackNodes) 
	    throws AbortedException, MemoryMonitorException {
				
		List<StackNode> startStackNodes = getPathStackNodes(
				m_controlFlow, m_cachedFunctionMap, m_coreContainer.getFromReference());
		
		for (StackNode startStackNode : startStackNodes) {
			for (Stack<CfgFunctionReferenceNode> stack : startStackNode.getNodeStacks()) {

				FunctionNode newFunction = makeFunction(stack, exclusionStackNodes, 
						branchStackNodes, true, branchToNext).first;

				updateRoutes(src, newFunction);
			}
		}
	}
	
	private void analyzeLoop(CommandSource src, List<StackNode> branchStackNodes, 
			Map<StackNode, Set<CfgNode>> branchToNext, List<StackNode> exclusionStackNodes) 
	    throws AbortedException, MemoryMonitorException {
		
		List<StackNode> loopStartStackNodes = new ArrayList<StackNode>();
		
		RouteLoopConversion converter = new RouteLoopConversion(m_controlFlow, m_cachedFunctionMap);
		converter.updateLoopStackNodes(m_coreContainer.getFromReference(), loopStartStackNodes, exclusionStackNodes);
		
		analyzePath(src, branchToNext, branchStackNodes, 
			    exclusionStackNodes, loopStartStackNodes, loopStartStackNodes);
	}
	
	private void analyzeEndpoints(CommandSource src, List<StackNode> branchStackNodes, 
			Map<StackNode, Set<CfgNode>> branchToNext, List<StackNode> exclusionStackNodes) 
	    throws AbortedException, MemoryMonitorException {
						
		List<StackNode> startStackNodes = getPathStackNodes(
				m_controlFlow, m_cachedFunctionMap, m_coreContainer.getFromReference());

		List<StackNode> endStackNodes = new LinkedList<StackNode>();
		for (Reference end : m_coreContainer.getToReferences()) {			
			endStackNodes.addAll(getPathStackNodes(m_controlFlow, m_cachedFunctionMap, end));
		}
		
		analyzePath(src, branchToNext, branchStackNodes, 
		    exclusionStackNodes, startStackNodes, endStackNodes);
	}
	
	private void analyzePath(CommandSource src, 
			Map<StackNode, Set<CfgNode>> branchToNext, 
			List<StackNode> branchStackNodes, 
			List<StackNode> exclusionStackNodes,
			List<StackNode> startStackNodes,
			List<StackNode> endStackNodes) throws AbortedException, MemoryMonitorException {
		
		for (StackNode startStackNode : startStackNodes) {
			for (Stack<CfgFunctionReferenceNode> stack : startStackNode.getNodeStacks()) {
				
				Node node = makeEndpoints(stack, startStackNode.getInstruction(), 
						endStackNodes, exclusionStackNodes, branchStackNodes, branchToNext);

				updateRoutes(src, node);
			}
		}
	}
	
	private void updateRoutes(CommandSource src, Node node) {
		if (node != null) {
			setParent(node);
			if (!alreadyContainRoute(node)) {
				Route route = new Route(src, m_engine, node, m_coreContainer);
				m_routes.add(route);
			}
		}		
	}
	
	private void buildBranchStackNodes(List<StackNode> branchStackNodes,
			Map<StackNode, Set<CfgNode>> branchToNext) 
	    throws AbortedException, MemoryMonitorException {
		
		for(Reference ref : m_engine.getUserBranches().keySet()){
			List<StackNode> stackNodes = getPathStackNodes(m_controlFlow, m_cachedFunctionMap, ref);
			branchStackNodes.addAll(stackNodes);
			for(StackNode sn : stackNodes){
				branchToNext.put(sn, new IteratableSet<CfgNode>());

				for(Reference next : m_engine.getUserBranches().get(ref)){
					for(StackNode nextStackNode : getPathStackNodes(m_controlFlow, m_cachedFunctionMap, next)){
						if(sn.getInstruction().getInstruction().isLink()){
							for(CfgNode nextFrn : sn.getInstruction().getNextNodes()){
								if(nextFrn instanceof CfgFunctionReferenceNode){
									CfgFunctionReferenceNode frn = (CfgFunctionReferenceNode)nextFrn;
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

	private void buildExclusionStackNodes(List<StackNode> exclusionStackNodes) throws AbortedException, MemoryMonitorException {
		for (ExclusionInfo info : m_engine.getExclusions()) {
			Reference ref = info.getReference();
			if (ref.getCoreId().equals(m_core)) {
				exclusionStackNodes.addAll(getPathStackNodes(m_controlFlow, m_cachedFunctionMap, ref));
			}
		}
	}
		
	private Pair<FunctionNode, Boolean> makeFunction(
			Stack<CfgFunctionReferenceNode> currentStack, 
			List<StackNode> exclusionStackNodes, 
			List<StackNode> branchStackNodes, boolean start, 
			Map<StackNode, Set<CfgNode>> branchToNext) 
		throws AbortedException, MemoryMonitorException {
		
		List<StackNode> endStackNodes = getFunctionEndStackNodes(currentStack);
		
		CfgFunctionReferenceNode fnRef = currentStack.peek();
		
		if (!fnRef.isKnownFunction()) {
			return new Pair<FunctionNode, Boolean>(new FunctionNode(), false);
		}
		CfgInstructionNode startInst = fnRef.getFunctionStartInstruction();

		Set<CfgNode> workingSet = new IteratableSet<CfgNode>();
		Set<CfgNode> currentExclusionsFound = new IteratableSet<CfgNode>();
		Map<NodeDescriptor, CfgNode> nodeToWorkingSet = new IteratableMap<NodeDescriptor, CfgNode>();
		
		NodeDescriptor startNodeDescriptor = new NodeDescriptor(startInst, currentStack);
		Set<CfgNode> loopFormingEndNodes = new NonIteratableSet<CfgNode>();
		
		findReachableGraph(null, currentStack, startInst, workingSet,
				exclusionStackNodes, currentExclusionsFound, RouteMode.FUNCTION,
				endStackNodes, nodeToWorkingSet, loopFormingEndNodes, start, branchStackNodes, branchToNext);

		removeExclusionBranches(workingSet, currentExclusionsFound, loopFormingEndNodes);

		if (workingSet.isEmpty()) {
			return new Pair<FunctionNode, Boolean>(null, false);
		}
		
		//if working set contains an end node then we will continue else we wont
		boolean continueAfterFunction = true;
		
		for (StackNode sn : endStackNodes) {
			for (CfgNode node : workingSet) {
				if (node instanceof CfgInstructionNode) {
					if (((CfgInstructionNode)node).getInstruction() == sn.getInstruction().getInstruction()) {
						continueAfterFunction = true;
						break;
					}
				}
			}
		}

		Node node = convertGraphToStructure(workingSet, startNodeDescriptor, nodeToWorkingSet);

		return new Pair<FunctionNode, Boolean>(
				new FunctionNode(node, 
				    fnRef.getFunctionName(), 
				    fnRef.getFunctionCallPc()), 
				    continueAfterFunction);
	}

	private Node makeEndpoints(Stack<CfgFunctionReferenceNode> currentStack, 
			CfgInstructionNode startInst, 
			List<StackNode> endStackNodes,
			List<StackNode> exclusionStackNodes, 
			List<StackNode> branchStackNodes, 
			Map<StackNode, Set<CfgNode>> branchToNext) 
	    throws AbortedException, MemoryMonitorException {

		Set<CfgNode> workingSet = new IteratableSet<CfgNode>();
		Set<CfgNode> currentExclusionsFound = new IteratableSet<CfgNode>();

		Map<NodeDescriptor, CfgNode> nodeToWorkingSet = new IteratableMap<NodeDescriptor, CfgNode>();
		NodeDescriptor startNodeDescriptor = new NodeDescriptor(startInst, currentStack);
		Set<CfgNode> loopFormingEndNodes = new NonIteratableSet<CfgNode>();
		findReachableGraph(null, currentStack, startInst, workingSet,
				exclusionStackNodes, currentExclusionsFound,
				RouteMode.ENDPOINTS, endStackNodes, nodeToWorkingSet, loopFormingEndNodes, true, branchStackNodes, branchToNext);
		removeExclusionBranches(workingSet, currentExclusionsFound, loopFormingEndNodes);

		if(workingSet.isEmpty()){
			return null;
		}
		Node node = convertGraphToStructure(workingSet, startNodeDescriptor, nodeToWorkingSet);

		return node;
	}

	private void findReachableGraph(
			CfgNode previous,
			Stack<CfgFunctionReferenceNode> currentStack, 
			CfgNode node,
			Set<CfgNode> workingSet,
			List<StackNode> exclusionStackNodes,
			Set<CfgNode> currentExclusionsFound,
			RouteMode routeMode,
			List<StackNode> endStackNodes,
			Map<NodeDescriptor, CfgNode> nodeToWorkingSet, 
			Set<CfgNode> loopFormingEndNodes,
			boolean firstInstruction, 
			List<StackNode> branchStackNodes, 
			Map<StackNode, Set<CfgNode>> branchToNext)
	throws AbortedException, MemoryMonitorException {
		
		boolean repeat = true;
		while(repeat){
			
			repeat = false;
			
			CfgNode currentNode = getCurrentNode(currentStack, node, nodeToWorkingSet);
			//System.out.println("on node " + currentNode);
						
			if (stackNodesContainNode(exclusionStackNodes, currentStack, node)&& !firstInstruction){
				currentExclusionsFound.add(currentNode);
				addToWorkingSet(previous, currentNode, workingSet);
				return;
			}
			
			if (node instanceof CfgFunctionReferenceNode) {
				CfgFunctionReferenceNode frn = ((CfgFunctionReferenceNode)node);
				
				List<CfgInstructionNode> functionCallInstructions = new LinkedList<CfgInstructionNode>();
				for (CfgFunctionReferenceNode f : currentStack) {
					functionCallInstructions.add(f.getFunctionStartInstruction());
				}

				if (functionCallInstructions.contains(frn.getFunctionStartInstruction())) {
					//recursion detected
					FunctionNode fn = new FunctionNode(frn.getFunctionName());
					CfgContainerNode cn = new CfgContainerNode(fn);
					currentNode = cn;
					previous.setRecursive(true);
					loopFormingEndNodes.add(previous);
					return;

				} else {
					boolean endPointsWithin = false; 
					for(StackNode endStackNode : endStackNodes){
						for(Stack<CfgFunctionReferenceNode> stack : endStackNode.getNodeStacks()){
							if(stack.contains(node)){
								endPointsWithin = true;
							}
						}
					}
					
					Stack<CfgFunctionReferenceNode> newStack = new Stack<CfgFunctionReferenceNode>();
					newStack.addAll(currentStack);
					newStack.add(frn);
					
					if (endPointsWithin) {
						CfgInstructionNode inst = frn.getFunctionStartInstruction();
						
						findReachableGraph(previous, newStack, inst, workingSet,
								exclusionStackNodes, currentExclusionsFound, routeMode,
								endStackNodes, nodeToWorkingSet, loopFormingEndNodes, false, branchStackNodes, branchToNext);
						return;
						
					} else {
						FunctionNode fn = null;
						CfgContainerNode cn = null;
						boolean continueAnalysis = false;
						if (frn.isKnownFunction()){
							Pair<FunctionNode, Boolean> fnReturn = makeFunction(newStack, exclusionStackNodes, branchStackNodes, false, branchToNext);
							continueAnalysis = fnReturn.second;
							fn = fnReturn.first;
							cn = new CfgContainerNode(fn);
							
						} else {
							fn = new FunctionNode();
							cn =  new CfgContainerNode(fn);
						}
						
						if (fn != null) {
							if (continueAnalysis) {
								currentNode = cn;
							
						} else {
								currentNode = cn;
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
			} else if (currentNode.getUnresolved()){
				addToWorkingSet(previous, currentNode, workingSet);
				return;
				
			} else if (stackNodesContainNode(endStackNodes, currentStack, node)){
				switch (routeMode) {
				case FUNCTION:
					addToWorkingSet(previous, currentNode, workingSet);
					return;
				case ENDPOINTS:
					if(!workingSet.isEmpty()){
						loopFormingEndNodes.add(previous);
						connectToNullNode(previous, workingSet);
						return;
					}
				}
			}
				
			addToWorkingSet(previous, currentNode, workingSet);
			
			Set<CfgNode> nextNodes = getValidBranches(branchStackNodes, currentStack, node, branchToNext);
			//System.out.println("currentNode: " + currentNode + " nexts: " + nextNodes);
			
			if (nextNodes.size() == 0) {
				
				if (isNodeContainedInBranchStackNodes(currentNode, branchStackNodes)) {
					// Here we have no next nodes (i.e. it is unresolved), but isn't marked
					// as unresolved because there is at least one way we can get to this node
					// where there is a next node.					
					addToWorkingSet(previous, currentNode, workingSet);
					currentNode.setUnresolved(true);
					return;					
				}				
								
				CfgFunctionReferenceNode callingFnRef = currentStack.peek();
				for (CfgNode followingFunction : callingFnRef.getNextNodes()) {
					Stack<CfgFunctionReferenceNode> newStack = new Stack<CfgFunctionReferenceNode>();
					newStack.addAll(currentStack);
					newStack.pop();
					findReachableGraph(currentNode, newStack, followingFunction, workingSet,
							exclusionStackNodes, currentExclusionsFound, routeMode, endStackNodes,
							nodeToWorkingSet, loopFormingEndNodes, false, branchStackNodes, branchToNext);
					 
				}
			} else if (nextNodes.size() == 1) {
				
				CfgNode next = nextNodes.iterator().next();
				CfgNode nextWorkingSetNode = containsNode(next, currentStack, nodeToWorkingSet);				
				if (nextWorkingSetNode == null) {
					previous = currentNode;
					node = next;
					firstInstruction = false;
					repeat = true;
					
				} else {
					handleNodeAlreadyInWorkingSet(routeMode, exclusionStackNodes, 
							endStackNodes, currentStack, workingSet, currentNode, 
							next, nextWorkingSetNode, loopFormingEndNodes);					

					if (stackNodesContainNode(exclusionStackNodes, currentStack, next)) {
						// This is for the case where the next node is excluded, 
						// but we have seen it already (i.e. it's the start node),
						// so because it is the only successor to the currentNode, we can
						// mark the currentNode as excluded...
						currentExclusionsFound.add(currentNode);						
					}
				}
			} else if (nextNodes.size() > 1) {
				
				for (CfgNode next : nextNodes) {
					CfgNode nextWorkingSetNode = containsNode(next, currentStack, nodeToWorkingSet);
					if (nextWorkingSetNode == null) {
						findReachableGraph(currentNode, currentStack, next, workingSet,
								exclusionStackNodes, currentExclusionsFound, routeMode, endStackNodes,
								nodeToWorkingSet, loopFormingEndNodes, false, branchStackNodes, branchToNext);
					} else {						
						handleNodeAlreadyInWorkingSet(routeMode, exclusionStackNodes, 
								endStackNodes, currentStack, workingSet, currentNode, 
								next, nextWorkingSetNode, loopFormingEndNodes);
					}
				}
			} 
		}
	}
	
	private void handleNodeAlreadyInWorkingSet(
			RouteMode routeMode,
			List<StackNode> exclusionStackNodes,
			List<StackNode> endStackNodes,
			Stack<CfgFunctionReferenceNode> currentStack,
			Set<CfgNode> workingSet,
			CfgNode currentNode,
			CfgNode nextNode,
			CfgNode nextNodeFromWorkingSet,
			Set<CfgNode> loopFormingEndNodes) {
		
		boolean isEndNode = stackNodesContainNode(endStackNodes, currentStack, nextNode);
		boolean isExcluded = stackNodesContainNode(exclusionStackNodes, currentStack, nextNode);
		boolean connected = false;

		if (routeMode == RouteMode.FUNCTION || (!isEndNode && !isExcluded)) {
			// We've formed a loop, so connect the current to 
			// the target thats already in the working set..
			CfgNode.connect(nextNodeFromWorkingSet, currentNode);
			connected = true;
		}
		if (isEndNode && !connected) {
			loopFormingEndNodes.add(currentNode);
			connectToNullNode(currentNode, workingSet);
		} 		
	}
	
	private void connectToNullNode(CfgNode currentNode,
			Set<CfgNode> workingSet) {
		//search the working set for a null node
		CfgNullNode nullNode = null;
		
		for(CfgNode node : workingSet){
			if(node instanceof CfgNullNode){
				nullNode = (CfgNullNode)node;
				break;
			}
		}
		if (nullNode == null) {
			nullNode = new CfgNullNode();
		}		
		addToWorkingSet(currentNode, nullNode, workingSet);
	}

	private Set<CfgNode> getValidBranches(List<StackNode> branchStackNodes,
			Stack<CfgFunctionReferenceNode> currentStack, CfgNode node, 
			Map<StackNode, Set<CfgNode>> branchToNext) {

		Set<CfgNode> validNextNodes = new IteratableSet<CfgNode>();
		
		validNextNodes.addAll(node.getNextNodes());
		
		for (StackNode sn : branchStackNodes){
			if (!sn.getNodeStacks().contains(currentStack)){
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

	private Node convertGraphToStructure(Set<CfgNode> workingSet, NodeDescriptor startNodeDescriptor,
			Map<NodeDescriptor, CfgNode> nodeToWorkingSet) 
	    throws AbortedException, MemoryMonitorException {
		
		CfgNode originalStartNode = null;
		for (NodeDescriptor nd : nodeToWorkingSet.keySet()) {
			if (startNodeDescriptor.equals(nd) && workingSet.contains(nodeToWorkingSet.get(nd))) {
				originalStartNode = nodeToWorkingSet.get(nd);
				break;
			}
		}
		assert(originalStartNode != null);
		
		dumpGraph(workingSet);
		
		StructuralAnalysisEngine sae = new StructuralAnalysisEngine(m_engine);
		return sae.createStructure(workingSet, originalStartNode, "route");
	}

	//TODO clean up the next two functions
	private List<StackNode> getFunctionEndStackNodes(Stack<CfgFunctionReferenceNode> stack) {
		List<StackNode> endStacks = new LinkedList<StackNode>();
		Set<CfgNode> seen = new NonIteratableSet<CfgNode>();
		Set<CfgInstructionNode> endNodes = new IteratableSet<CfgInstructionNode>();
		Set<CfgNode> following = new IteratableSet<CfgNode>();
		following.add(stack.peek().getFunctionStartInstruction());
		while(!following.isEmpty()){
			CfgNode current = following.iterator().next();
			seen.add(current);
			if(current.getNextNodesSize() == 0){
				if(current instanceof CfgInstructionNode){
					endNodes.add((CfgInstructionNode)current);
				} else {
					assert(false);
				}
			} else {
				for(CfgNode next : current.getNextNodes()){
					if(!seen.contains(next)){
						following.add(next);
					}
				}
			}
			following.remove(current);
		}
		for(CfgInstructionNode endInst : endNodes){
			List<Stack<CfgFunctionReferenceNode>> stacks = new LinkedList<Stack<CfgFunctionReferenceNode>>();
			stacks.add(stack);
			StackNode end = new StackNode(endInst, stacks);
			endStacks.add(end);
		}
		return endStacks;
	}

	private void removeExclusionBranches(
			Set<CfgNode> workingSet,
			Set<CfgNode> currentExclusions, 
			Set<CfgNode> endNodes) {
		
		while(!currentExclusions.isEmpty()){
			CfgNode current = currentExclusions.iterator().next();

			while(true){

				if (endNodes.contains(current)) {
					break;
					
				} else if (current.getPrevNodes().size() == 1) {
					CfgNode prev = current.getPrevNodes().iterator().next();
					CfgNode.disconnect(current, prev);
					workingSet.remove(current);
					if (prev.getNextNodes().size() == 0){
						current = prev;
					} else {
						break;
					}

				} else if(current.getPrevNodes().size() > 1){
					for(CfgNode prev : current.getPrevNodes()){
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

	private CfgNode containsNode(CfgNode next, Stack<CfgFunctionReferenceNode> currentStack, 
			Map<NodeDescriptor, CfgNode> nodeToWorkingSet){
		
		NodeDescriptor nd = new NodeDescriptor(next, currentStack);
		return nodeToWorkingSet.get(nd);
	}

	private CfgNode getCurrentNode(Stack<CfgFunctionReferenceNode> currentStack,
			CfgNode node, Map<NodeDescriptor, CfgNode> nodeToWorkingSet) 
	    throws AbortedException {
		
		NodeDescriptor nd = new NodeDescriptor(node, currentStack);
		if (nodeToWorkingSet.containsKey(nd)){
			return nodeToWorkingSet.get(nd);

		} else {
			assert((node instanceof CfgInstructionNode) || 
				   (node instanceof CfgFunctionReferenceNode));
			
			CfgNode clone = node.clone();
			nodeToWorkingSet.put(nd, clone);
			return clone;
		}
	}

	private boolean stackNodesContainNode(List<StackNode> stackNodes, 
			Stack<CfgFunctionReferenceNode> currentStack, CfgNode currentNode){
		
		if (!(currentNode instanceof CfgInstructionNode)) {
			return false;
		}
		for (StackNode stackNode : stackNodes) {
			if (stackNode.getInstruction().equals(currentNode)) {
				for (Stack<CfgFunctionReferenceNode> nodeStack : stackNode.getNodeStacks()) {
					if (nodeStack.equals(currentStack)) {						
						return true;
					}
				}
			}
		}
		return false;
	}

	private void addToWorkingSet(CfgNode parent, CfgNode currentNode, Set<CfgNode> workingSet) {
		if (parent!= null) {
			CfgNode.connect(currentNode, parent);
		}		
		workingSet.add(currentNode);
	}

	private void dumpGraph(Set<CfgNode> workingSet) {
		if (!debug_route) return;
		System.out.print("digraph g {");
		for (CfgNode node : workingSet) {
			if (node instanceof CfgInstructionNode){
				System.out.print("node" +node.getId() + " [label=\"" + ((CfgInstructionNode)node).getInstruction() + "\"];");
			} else {
				System.out.print("node" +node.getId() + " [label=\"" + node + "\"];");
			}
			for(CfgNode next : node.getNextNodes()){
				System.out.print("node" +node.getId() + " -> node" + next.getId() + ";");
				if(!workingSet.contains(next)){
					System.out.print("node" +next.getId() + " [label=\"" + next + "\", style=filled, color=blue];");
				}
			}
			for(CfgNode prev : node.getPrevNodes()){
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
					Instruction instruction = ((InstructionNode)node).getInstruction();
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
		CoreId coreId = m_coreContainer.getCoreId();
		
		// Sets the loop iteration counts specified at compile time.
		for (SrcLevelLoopInfo srcLevelLoopInfo : m_engine.getSrcLevelLoops(coreId)) {
			m_engine.provideFeedback(String.format("Setting loop count at pc: 0x%x", srcLevelLoopInfo.getPc()));
			m_engine.getAbort();

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
    					Instruction instruction = ((InstructionNode)node).getInstruction();

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
    
    private boolean isNodeContainedInBranchStackNodes(CfgNode node, List<StackNode> branchStackNodes) {
    	if (node instanceof CfgInstructionNode) {
    		CfgInstructionNode instructionNode = (CfgInstructionNode)node;
    		for (StackNode stackNode : branchStackNodes) {
    			if (stackNode.getInstruction().getPc() == instructionNode.getPc()) {
    				return true;
    			}
    		}				
    	}
    	return false;
    }

	private static class NodeDescriptor implements Comparable<NodeDescriptor>{
		private CfgNode m_node;
		private Stack<CfgFunctionReferenceNode> m_stack;
		private int m_id;
		
		public NodeDescriptor(CfgNode node, Stack<CfgFunctionReferenceNode> stack){
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
}
