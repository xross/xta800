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
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.CommandSource.Type;
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
	
	private static final boolean debug_route = true;
	private static final boolean sanity_debug = true;
	
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
	
	private Map<FunctionNode, Set<Stack<FunctionReferenceNode>>> m_stackLookUp;
	
	private RouteDescription m_description;
	private StructureGraph m_structureGraph;
	private List<Route> m_routes;
	private Engine m_engine;
	private RouteContainer m_container;

	public RouteConstructor(Engine engine, StructureGraph structureGraph,
			RouteDescription description, RouteContainer container) throws AbortedException {
		m_stackLookUp = new HashMap<FunctionNode, Set<Stack<FunctionReferenceNode>>>();
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
		boolean seenEndPoint = false;
		
		if (m_description.getStartPoints() == null) {
			return RouteError.STARTPOINT_MISSING;
		}
		if (m_description.getEndPoints() == null) {
			return RouteError.ENDPOINTS_MISSING;
		}
		
		//find end instructions
		Set<StackNode> endStackNodes = new HashSet<StackNode>();
		
		Set<InstructionNode> endInstructions = getEndInstructions(m_description);
		
		for(InstructionNode endInstruction : endInstructions){
			FunctionNode endFunction = getFunction(endInstruction);
			Set<Stack<FunctionReferenceNode>> callStacks = getCallStacks(endFunction); 
			//really want endNode to be the set of end nodes that are reachable within the current context
			StackNode endNode = new StackNode(endInstruction, callStacks);
			endStackNodes.add(endNode);
		}
		
		//find exclusions
		Set<StackNode> exclusionStackNodes = new HashSet<StackNode>();
		Set<InstructionNode> exclusionInstructions = getExclusionInstructions();
		
		for(InstructionNode exclusionInstruction : exclusionInstructions){
			FunctionNode exclusionFunction = getFunction(exclusionInstruction);
			Set<Stack<FunctionReferenceNode>> callStacks = getCallStacks(exclusionFunction); 
			//really want exclusionInstruction to be the set of end nodes that are reachable within the current context
			StackNode exclusionNode = new StackNode(exclusionInstruction, callStacks);
			endStackNodes.add(exclusionNode);
		}
		
		//find the start instructions
		Set<InstructionNode> startInstructions = getStartInstruction(m_description);
		
		for(InstructionNode startInstruction : startInstructions){
			//start instructions -> start functions
			FunctionNode startFunction = getFunction(startInstruction);
			
			//start functions -> start call stacks
			Set<Stack<FunctionReferenceNode>> callStacks = getCallStacks(startFunction); 
			
			Node startNode  = getStartNode(startInstruction);
			
			//Analyse from startNode within each of the contexts to all of the end nodes
			for(Stack<FunctionReferenceNode> currentStack : callStacks){
				Node analysis = makeStructureNode(currentStack, startNode, endStackNodes, exclusionStackNodes, m_engine, null);
				Route route = new Route(src, m_engine, analysis, m_description, m_container);
				m_routes.add(route);
			}
		}
		setUnknowns();	
		setSrcLoopCounts();	
		checkSupportedInstructions(src);
		applyDefines(src);

		switch (m_description.getMode()) {
		case FUNCTION:
			return RouteError.SUCCESS;
		case PATH:
			return seenEndPoint ? RouteError.SUCCESS : RouteError.NO_ENDPOINTS_FOUND;			
		default:
			assert(false);
			return null;
		}
	}

	private Node makeStructureNode(Stack<FunctionReferenceNode> currentStack, Node startNode, 
			Set<StackNode> endStackNodes, Set<StackNode> exclusionStackNodes, Engine engine, Node prevCFGNode) throws AbortedException{
		if(debug_route){
			System.out.println("Making a route from " + currentStack  + " "+ startNode);
			System.out.println("End stacks: ");
			for(StackNode endStack : endStackNodes){
				System.out.println("\t" + endStack);
			}
			if(!exclusionStackNodes.isEmpty()){
				System.out.println("Exclusions: ");
				for(StackNode exclusionStack : exclusionStackNodes){
					System.out.println("\t" + exclusionStack);
				}
			}
		}
		Set<Node> workingSet = new TreeSet<Node>();
		
		NodeWrapper workingSetStartNode = new NodeWrapper(startNode);
		
		//This removes all edges from the structure graphs.
		//TODO - it would be unnecessary if the structure graph construction was done properly.
		m_structureGraph.removeLinks();
		
		routeExplore(currentStack, startNode, endStackNodes, exclusionStackNodes, workingSet, workingSetStartNode, prevCFGNode);
		
		Node newStructureNode = convertCFGToStructure(engine, workingSet, exclusionStackNodes, currentStack, workingSetStartNode);
		return newStructureNode;
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

	private void routeExplore(Stack<FunctionReferenceNode> currentStack, Node currentNode, 
			Set<StackNode> endStackNodes, Set<StackNode> exclusionStackNodes, Set<Node> workingSet, 
			NodeWrapper workingSetStartNode, Node prevCFGNode) throws AbortedException{
		
		
		//if current node is contained within any of the sets then set the boolean to true
		//follow all function calls when getting 
		boolean exclusionsWithinCurrentNode = nodesWithinCurrentContext(currentStack, currentNode, exclusionStackNodes);
		boolean endInstructionsWithinCurrentNode = nodesWithinCurrentContext(currentStack, currentNode, endStackNodes);
		
		if(debug_route){
			System.out.println("Current node: " + currentStack  + " "+ currentNode);
			System.out.println("\tContains exclusions: " + exclusionsWithinCurrentNode + 
					"\n\tContains end instruction " + endInstructionsWithinCurrentNode);
		}
		
		
		if(endInstructionsWithinCurrentNode){
			//if this node is an end node then add it to the tree and stop.
			if(matchesStackNode(currentNode, currentStack, endStackNodes)){
				CommonMethods.connect(currentNode, prevCFGNode);
				workingSet.add(currentNode);
				Node nullNode = getNullNode(workingSet);
				CommonMethods.connect(nullNode, currentNode);
			} else {
				routeExploreInto(currentStack, currentNode, endStackNodes, exclusionStackNodes, workingSet, workingSetStartNode, prevCFGNode);
			}
		} else {
			if(exclusionsWithinCurrentNode){
				routeExploreInto(currentStack, currentNode, endStackNodes, exclusionStackNodes, workingSet, workingSetStartNode, prevCFGNode);
			} else {
				routeExploreNext(currentStack, currentNode, endStackNodes, exclusionStackNodes, workingSet, workingSetStartNode, prevCFGNode);
			}
		}
	}

	private void routeExploreNext(Stack<FunctionReferenceNode> currentStack, Node currentNode, 
			Set<StackNode> endStackNodes, Set<StackNode> exclusionStackNodes, Set<Node> workingSet, 
			NodeWrapper workingSetStartNode, Node prevCFGNode) throws AbortedException{

		if(debug_route){
			System.out.println("Explore next: " + currentStack  + " "+ currentNode);
		}
		
		if(debug_route){
			System.out.println("Add "+ currentNode+ " to working set:" + workingSet);
		}
		if(prevCFGNode!= null){
			CommonMethods.connect(currentNode, prevCFGNode);
		}
		workingSet.add(currentNode);
		
		Node parent = getParent(currentNode, currentStack);
		if (parent != null) {			
			Set<Node> nexts = parent.structurGraphNext(currentNode);
		    if (!nexts.isEmpty()) {
				for (Node next : nexts) {	
					if (!workingSet.contains(next) || next.equals(workingSetStartNode)) {
						routeExplore(currentStack, next, endStackNodes, exclusionStackNodes, workingSet, workingSetStartNode, currentNode);			
					}
				}	
		    }
			
		} else {
			Node nullNode = getNullNode(workingSet);
			CommonMethods.connect(nullNode, currentNode);
		}
	}

	private void routeExploreInto(Stack<FunctionReferenceNode> currentStack, Node currentNode, 
			Set<StackNode> endStackNodes, Set<StackNode> exclusionStackNodes, Set<Node> workingSet, 
			NodeWrapper workingSetStartNode, Node prevCFGNode) throws AbortedException{
		
		if(debug_route){
			System.out.println("Explore into: " + currentStack  + " "+ currentNode);
		}
		
		if(currentNode == workingSetStartNode.node){
			if(!(currentNode instanceof InstructionNode)){
				workingSetStartNode.node = currentNode.dChildren().iterator().next();
			}
		}
		
		if(currentNode instanceof FunctionReferenceNode){
			FunctionReferenceNodeWrapper newFunctionReferenceNode = null;
			boolean allPathsTerminated = exploreFunctionReference(newFunctionReferenceNode, currentStack, (FunctionReferenceNode)currentNode, 
					endStackNodes, exclusionStackNodes);
			
			assert(newFunctionReferenceNode != null);
			
			if(!allPathsTerminated){
				routeExplore(currentStack, newFunctionReferenceNode.node, endStackNodes, exclusionStackNodes, 
						workingSet, workingSetStartNode, prevCFGNode);	
			} else {
				if(prevCFGNode!= null){
					CommonMethods.connect(newFunctionReferenceNode.node, prevCFGNode);
				}
				
			}
		} else {
			for (Node child : currentNode.dChildren()) {
				if (!workingSet.contains(child) || child.equals(workingSetStartNode)) {							
					routeExplore(currentStack, child, endStackNodes, exclusionStackNodes, workingSet, workingSetStartNode, prevCFGNode);		
				}				
			}
		}
	}
	
	/**
	 * When a function Reference node is found it 
	 * @param currentStack 
	 * @throws AbortedException 
	 */
	private boolean exploreFunctionReference(FunctionReferenceNodeWrapper newFunctionReferenceNode,
			Stack<FunctionReferenceNode> currentStack, FunctionReferenceNode currentFrn, Set<StackNode> endStackNodes, 
			Set<StackNode> exclusionStackNodes) throws AbortedException {
		
		Stack<FunctionReferenceNode> newStack = new Stack<FunctionReferenceNode>();
		
		newStack.addAll(currentStack);
		//check this is pushed on to the right end
		newStack.push(currentFrn);
		
		//copy all its properties
		FunctionReferenceNode newFrn= new FunctionReferenceNode(currentFrn);
		newFunctionReferenceNode.node = newFrn;
		
		if(newFrn.isKnownFunction()){
			Set<Node> workingSet = new TreeSet<Node>();
			
			FunctionNode fn = newFrn.getFunctionNode();
			
			NodeWrapper workingSetStartNode = new NodeWrapper(fn);
			
			Set<StackNode> newEndStackNodes = new HashSet<StackNode>();
			newEndStackNodes.addAll(endStackNodes);
			
			for(InstructionNode endInstruction : m_structureGraph.getFunctionsEndNodes(fn)){
				FunctionNode endFunction = getFunction(endInstruction);
				newEndStackNodes.add(new StackNode(endInstruction, getCallStacks(endFunction)));
			}
			
			//really want endNodes to be the set of end nodes that are reachable within the current context
			routeExplore(newStack, fn, newEndStackNodes, exclusionStackNodes, workingSet, workingSetStartNode, newFrn);
			
			
			
			//reduce the working set down
			Node newStructureNode = convertCFGToStructure(m_engine, workingSet, exclusionStackNodes, currentStack, workingSetStartNode);
			
			if(newStructureNode == null){
				return false;
			}
			
			FunctionNode newFunction = new FunctionNode(newStructureNode, fn.getFunctionName());
			//point the newFrn to the function
			
			newFrn.setFunction(fn.getFunctionName(), newFunction);
			return true;
		} else {
			//if we dont know where this function branched to then assume we returned from it
			return true;
		}
	}

	/**
	 * This finds the null node within the working set. As there may not always be one then it makes one when needed.
	 */
	private Node getNullNode(Set<Node> workingSet) {
		for(Node node : workingSet){
			if(node instanceof NullNode){
				return node;
			}
		}
		Node nullNode = new NullNode();
		workingSet.add(nullNode);
		return nullNode;
	}
	
	/** 
	 * This looks within the current node at all of the instructions at the current context.
	 * Return true if
	 */
	private boolean matchesStackNode(Node currentNode,
			Stack<FunctionReferenceNode> currentStack,
			Set<StackNode> nodesToBeMatched) {
		for(StackNode sn : nodesToBeMatched){
			for(Stack<FunctionReferenceNode> frn : sn.getNodeStacks()){
				if(frn.equals(currentStack) && currentNode == sn.getInstruction()){
					return true;
				} 
			}
		}
		return false;
	}

	/**
	 * This returns true if any of the instruction nodes reachable from this node are in the set stackNodes
	 * @param currentNode 
	 */
	private boolean nodesWithinCurrentContext(
			Stack<FunctionReferenceNode> currentStack,
			Node currentNode, Set<StackNode> stackNodes) {
		
		Set<InstructionNode> currentContextInstructionsWithinNode = currentNode.getInstructionNodes();
		Set<FunctionReferenceNode> currentContextFunctionReferencesWithinNode = currentNode.getFunctionReferencesNodes();
		for(StackNode sn : stackNodes){
			for(Stack<FunctionReferenceNode> frn : sn.getNodeStacks()){
				if(frn.equals(currentStack) && currentContextInstructionsWithinNode.contains(sn.getInstruction())){
					return true;
				} 
			}
		}
		//TODO --finish this
		
		
		return false;
	}
	
	/**
	 * When you are a function node for its parent then it is popped from the top of the stack.
	 * Watch out for this.
	 */
	private Node getParent(Node node, Stack<FunctionReferenceNode> currentStack) {
		if(node instanceof FunctionNode){
			return currentStack.pop().getParent();
		} else {
			return node.getParent();
		}
		
	}

	private Node makeBasicBlocks(Node startNode, Set<Node> workingSet) {
		
		if(startNode instanceof InstructionNode){
			Node currentNode = startNode;
			List<InstructionNode> blockInsts = new LinkedList<InstructionNode>();
			while(true){
				Set<Node> nextNodes = currentNode.getNextNodes();
				if(nextNodes.size() == 1){
					Node nextNode = nextNodes.iterator().next();
					if(nextNode.getPrevNodes().size() == 1 && nextNode instanceof InstructionNode){
						blockInsts.add((InstructionNode)currentNode);
						//TODO disconnect the instruction nodes from one another as we go
						if(nextNode != startNode){
							currentNode = nextNode;
							continue;
						}
					} 
				} 
				break;
			}
			startNode = replaceInstructionWithBlock(workingSet, blockInsts);
		}
		
		Node nullNode = getNullNode(workingSet);
		for(Node endNode : nullNode.getPrevNodes()){
			if(endNode instanceof InstructionNode){
				Node currentNode = endNode;
				List<InstructionNode> blockInsts = new LinkedList<InstructionNode>();
				blockInsts.add((InstructionNode) endNode);
				while(true){
					Set<Node> prevNodes = currentNode.getPrevNodes();
					if(prevNodes.size() == 1){
						Node prevNode = prevNodes.iterator().next();
						if(prevNode.getNextNodes().size() == 1 && prevNode instanceof InstructionNode){
							//TODO disconnect the instruction nodes from one another as we go
							blockInsts.add(0, (InstructionNode)prevNode);
							if(prevNode != endNode){
								currentNode = prevNode;
								continue;
							}
						} 
					}
					break;
				}
				replaceInstructionWithBlock(workingSet, blockInsts);
			}
		}
		return startNode;
	}

	private Node replaceInstructionWithBlock(Set<Node> workingSet, List<InstructionNode> blockInsts) {
		Node basicBlock = new BlockNode(blockInsts);
		workingSet.removeAll(blockInsts);
		Node startNode = blockInsts.get(0);
		if(!startNode.getPrevNodes().isEmpty()){
			for(Node prev : startNode.getPrevNodes()){
				prev.removeNextNode(startNode);
				CommonMethods.connect(basicBlock, prev);
			}
		}
		startNode.getPrevNodes().clear();
		Node endNode = blockInsts.get(blockInsts.size()-1);
		if(!endNode.getNextNodes().isEmpty()){
			for(Node next : endNode.getNextNodes()){
				next.removePrevNode(endNode);
				CommonMethods.connect(next, basicBlock);
			}
		}
		endNode.getNextNodes().clear();
		workingSet.add(basicBlock);
		return basicBlock;
	}

	static void setParent(Node node) {
		for (Node child : node.iChildren()){
			child.setParent(node);
			setParent(child);
		}
	}
/*		
	private void setParents() {
		for (Route route : m_routes) {
			setParent(route.getNode());
		}
	}

	private void createExclusions() throws AbortedException {
		m_exclusions = new ArrayList<NodeDescriptor>();
		ReferenceMatcher referenceMatcher = new ReferenceMatcher();
		for (Reference reference : m_engine.getExclusions()) {
			m_exclusions.addAll(referenceMatcher.getNodeDescriptors(m_engine, m_structureGraph, reference));
		}
	}
*/
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
	
	
	
	
	//below here has been written during the refactoring

	private Set<InstructionNode> refToInstructions(Reference ref) {
		Set<InstructionNode> insts = new TreeSet<InstructionNode>();
		for(long pc : ref.getPcs()){
			InstructionNode inst = m_engine.getInstructionDecoder().getNode(pc);
			insts.add(inst);
		}
		return insts;
	}

	/**
	 * This finds the best starting node from the given instruction
	 */
	private Node getStartNode(InstructionNode startInstruction) {
		Node currentNode = startInstruction;
		
		while(currentNode.getParent() != null && 
				currentNode.getParent().dChildren().contains(currentNode)){
			//TODO - don't use the parent if the parent contains exclusions
			currentNode = currentNode.getParent();
		}
		return currentNode;
	}
	
	/*
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

		*/
	

	/**
	 * For the given function find all possible call stacks to it. This also saves the call stack set
	 * in a lookup table to save on memory and speed it up.
	 */
	private Set<Stack<FunctionReferenceNode>> getCallStacks(FunctionNode function) {
		if(m_stackLookUp.containsKey(function)){
			return m_stackLookUp.get(function);
		}
		
		Set<Stack<FunctionReferenceNode>> stack = m_structureGraph.getFunctionCallStack(function);
		
		m_stackLookUp.put(function, stack);
		return stack;
	}
	
	/** 
	 * returns the function that the node is contained within
	 */
	private FunctionNode getFunction(Node node) {
		while(node.getParent() != null){
			node = node.getParent();
		}
		assert(node instanceof FunctionNode);
		return (FunctionNode) node;
	}
	
	/** 
	 * From the engine get all the excluded instructions
	 */
	private Set<InstructionNode> getExclusionInstructions() {
		Set<InstructionNode> excludedInstructions = new TreeSet<InstructionNode>();
		for(Reference ref : m_engine.getExclusions()){
			excludedInstructions.addAll(refToInstructions(ref));
		}
		return excludedInstructions;
	}
	
	/**
	 * From a route description return the instruction nodes that end that route
	 */
	private Set<InstructionNode> getEndInstructions(RouteDescription description) {
		Set<InstructionNode> instructionNodes = new TreeSet<InstructionNode>();
		if(description.getMode() == RouteMode.FUNCTION){
			FunctionNode fn = description.getFunction(description.getFunctionName(), m_structureGraph);
			instructionNodes = m_structureGraph.getFunctionsEndNodes(fn);
		} else {
			Collection<Reference> endRefs = description.getEndPoints();
			for(Reference endRef : endRefs){
				instructionNodes.addAll(refToInstructions(endRef));
			}
		}
		assert(instructionNodes != null);
		assert(!instructionNodes.isEmpty());
		return instructionNodes;
	}

	/**
	 * From a route description return the instruction nodes that begin that route
	 */
	private Set<InstructionNode> getStartInstruction(RouteDescription description) {
		Set<InstructionNode> instructionNodes = new TreeSet<InstructionNode>();
		if(description.getMode() == RouteMode.FUNCTION){
			FunctionNode fn = description.getFunction(description.getFunctionName(), m_structureGraph);
			InstructionNode start = m_structureGraph.getFunctionsStartNode(fn);
			assert(start != null);
			instructionNodes.add(start);
		} else {
			Reference startRef = description.getStartPoints();
			instructionNodes = refToInstructions(startRef);
			
		}
		assert(!instructionNodes.isEmpty());
		return instructionNodes;
	}
	
	/**
	 * This converts a CFG in the form of a workingSet to a single structural node
	 * @param currentStack 
	 * @param exclusionStackNodes 
	 */
	private Node convertCFGToStructure(Engine engine, Set<Node> workingSet, Set<StackNode> exclusionStackNodes, Stack<FunctionReferenceNode> currentStack, NodeWrapper workingSetStartNode) {
		dumpGraph(workingSet);
		
		cfgSanityCheckNodes(workingSet);
		pruneGraphOfExcludedPaths(workingSet, currentStack, exclusionStackNodes);
		cfgSanityCheckNodes(workingSet);
		if(workingSet.size() == 0){
			//this is naughty as it is overloading the return
			return null;
		}
		
		assert(!workingSet.isEmpty());
		Node cfgStartNode = makeBasicBlocks(workingSetStartNode.node, workingSet);
		cfgSanityCheckNodes(workingSet);
		dumpGraph(workingSet);
		StructuralAnalysisEngine sae = new StructuralAnalysisEngine();
		try {
			sae.createStructure(workingSet, cfgStartNode, engine, "Analysing sub-function");
		} catch (AbortedException e) {
			//notify the engine
			System.err.println("Could not reduce control flow graph to a structural form.");
			e.printStackTrace();
			assert(false);
			return null;
		}
		assert(workingSet.size() == 1);
		Node newStructureNode = workingSet.iterator().next();
		return newStructureNode;
	}
	
	private void pruneGraphOfExcludedPaths(Set<Node> workingSet, Stack<FunctionReferenceNode> currentStack, Set<StackNode> exclusions) {
		
		
		//TODO this need writing
		
		/*
		Set<Node> t = new TreeSet<Node>(m_excludedRouteNodes);
						
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
		*/
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
	
	
	class StackNode{
		private Set<Stack<FunctionReferenceNode>> m_nodeStacks;
		private InstructionNode m_instruction;
		public StackNode(InstructionNode instruction, Set<Stack<FunctionReferenceNode>> nodeStacks){
			m_nodeStacks = nodeStacks;
			m_instruction = instruction;
		}
		
		public Set<Stack<FunctionReferenceNode>> getNodeStacks(){
			return m_nodeStacks;
		}
		
		public InstructionNode getInstruction(){
			return m_instruction;
		}
		
		public String toString(){
			String stacks = "";
			for(Stack<FunctionReferenceNode> stack : m_nodeStacks){
				stacks += "\t" + stack + "\n";
			}
			return m_instruction + " from " + m_nodeStacks.size() + " stacks\n" + stacks;
		}
	}
	
	class NodeWrapper{
		public Node node;
		public NodeWrapper(Node n){
			node = n;
		}
	}
	
	class FunctionReferenceNodeWrapper{
		public FunctionReferenceNode node;
		public FunctionReferenceNodeWrapper(FunctionReferenceNode n){
			node = n;
		}
	}
	
}
