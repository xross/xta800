package com.xmos.xta.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.CommandSource;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.CommandSource.Type;
import com.xmos.xta.engine.analysis.routes.RouteDescription;
import com.xmos.xta.engine.analysis.routes.RouteDescription.RouteMode;
import com.xmos.xta.engine.analysis.structural.ReferenceMatcher;
import com.xmos.xta.engine.analysis.structural.StructuralAnalysisEngine;
import com.xmos.xta.engine.analysis.structural.StructureGraph;
import com.xmos.xta.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.engine.analysis.structural.nodes.InfiniteSelfLoopNode;
import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.NullNode;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownType;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.engine.analysis.structural.patterns.CommonMethods;
import com.xmos.xta.engine.instructions.InstructionMnemonics;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.engine.sections.SrcLevelLoopInfo;

public class RouteConstructor {
	private static final int SR_IEBLE_SHIFT = 1;
	private static final int SR_IEBLE_SIZE = 1; 
	
	enum RouteStatus {
		COMPLETED, ABORTED, ERROR
	}

	public enum RouteError {
		NO_ENDPOINTS_FOUND, ENDPOINTS_MISSING, STARTPOINT_MISSING, SUCCESS, COULD_NOT_FIND_STARTPOINT, COULD_NOT_FIND_ENDPOINT
	}
	
	private int m_routeId;
	private RouteDescription m_description;
	private StructureGraph m_structureGraph;
	private List<Route> m_routes;
	private Engine m_engine;
	private Collection<InstructionNode> m_exclusions;
	private Set<Node> m_excludedRouteNodes;

	public RouteConstructor(Engine engine, StructureGraph structureGraph, RouteDescription description) {
		m_structureGraph = structureGraph;
		m_description = description;
		m_routes = new ArrayList<Route>();
		m_engine = engine;
		createExclusions();
	}

	public int getRouteId() {
		return m_routeId;
	}

	public String getDescription() {
		return m_description.getDescription();
	}
	
	public List<Route> getRoutes() {
		return m_routes;
	}

	public RouteError analyse(CommandSource src) throws Exception {
		resetParent();
		boolean seenEndPoint = false;
		
		if (m_description.getStartPoint() == null ) {
			return RouteError.STARTPOINT_MISSING;
		}
		if (m_description.getEndPoint() == null ) {
			return RouteError.ENDPOINTS_MISSING;
		}
		
		Collection<InstructionNode> startNodes = getReferencedNodes(m_description.getStartPoint());
		if (startNodes.isEmpty()) {
			return RouteError.COULD_NOT_FIND_STARTPOINT;
		}
		
		Set<InstructionNode> endNodes = new TreeSet<InstructionNode>();
		findEndInstructions(endNodes);

		boolean notFoundFunctionYet = true;
		for (Node start : startNodes) {
			resetParent();
			InstructionNode realStart = (InstructionNode) start;
			while(parentIsBetterThanChild(start)){
				start = start.getParent();
			}
			if(m_description.getMode() == RouteMode.FUNCTION ){
				
				if(m_engine.getRecursiveFunctions().contains(m_description.getFunctionName())){
					m_engine.notify(src, Notifier.NOTIFY_ERROR, "Cannot analyse function " + m_description.getFunctionName() +" as it is recursive");
				} else {
					if(start.isFunctionNode()&& notFoundFunctionYet){
						if(!containAnyNodes(start, m_exclusions)){
							start.setParent(null);
							m_routes.add(new Route(src, m_engine, start.deepCopy(), m_description));
						} else {
							seenEndPoint = makeSubRoute(src, realStart,	seenEndPoint, endNodes, start);
						}	
						notFoundFunctionYet = false;
					}
				}
			} else {
				seenEndPoint = makeSubRoute(src, realStart, seenEndPoint, endNodes, start);
			}
		}
		setParents();
		setUnknowns();			
		setSrcLoopCounts();	
		checkSupportedInstructions(src);
		
		if (m_description.getMode() == RouteMode.PATH) {
			if (seenEndPoint) {
				return RouteError.SUCCESS;
			} else {
				return RouteError.NO_ENDPOINTS_FOUND;
			}
		} else {
			return RouteError.SUCCESS;
		}
	}
	
	private boolean firstNodeAdded;
	
	/**
	 * @param src					- 
	 * @param startInstruction		- the instruction that the route begins at
	 * @param seenEndPoint			- if true then an end node has been seen on this route
	 * @param endNodes				- a list of the nodes that the route terminates on
	 * @param start					- the greatest parent that begins with the startInstruction and contains no exclusions
	 * @param addFunctionWrappers	- to solve the function wrappers problem
	 * @return						- true if an end point has been seen false otherwise
	 * @throws Exception			- fails if the jvm dies for some reason or one of the endless assertions fails
	 */
	private boolean makeSubRoute(CommandSource src, InstructionNode startInstruction, boolean seenEndPoint, Collection<InstructionNode> endNodes,
			Node start) throws Exception {
		firstNodeAdded = false;
		Collection<Node> workingSet = new TreeSet<Node>();
		
		m_structureGraph.removeLinks();
		m_excludedRouteNodes = new TreeSet<Node>();
		NodeWrapper startNodeWrapper = new NodeWrapper(start);
		
		seenEndPoint |= explore(startInstruction, startNodeWrapper, workingSet, endNodes, null);
		assert(cfgSanityCheck(workingSet));	
		pruneGraphOfExcludedPaths(workingSet);
		if (!workingSet.isEmpty()){
			
			Node newStart = makeBasicBlocks(startInstruction, endNodes, workingSet);
			
			addTerminator(workingSet);
			assert(cfgSanityCheck(workingSet));
			
			StructuralAnalysisEngine sae = new StructuralAnalysisEngine();	
			sae.createStructure(workingSet, newStart, m_engine, "Analysing route");
			assert(cfgSanityCheck(workingSet));
			
			assert(cfgSanityCheck(workingSet));
			assert(workingSet.size() == 1);
			
			Node baseNode = workingSet.iterator().next();
			
			sae.normaliseGraph(baseNode);
			
			assert(!baseNode.isInfiniteLoopNode());
			
			if (m_description.getMode() == RouteMode.PATH && baseNode.isInfiniteSelfLoopNode()){
				baseNode = ((InfiniteSelfLoopNode)baseNode).getLoop();
			} 
			
			Node newGraph = baseNode.deepCopy();
			newGraph.setParent(null);			
			
			if (!alreadyContainRoute(newGraph)){
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
	 * @param parent - the node before this one
	 * @return true if the explore saw an end node
	 */
	private boolean explore(InstructionNode startInstruction, NodeWrapper node, Collection<Node> workingSet, 
			Collection<InstructionNode> endInstructions, Node parent) {	
		
		Node currentNode = node.m_node;
		
		Collection<Node> currentNodesExclusions = new TreeSet<Node>();
		Set<Node> containedEndPoints = new TreeSet<Node>();
		Set<Node> currentEndNodes = new TreeSet<Node>();	
		
		if(currentNode.isInstructionNode()){
			if(m_exclusions.contains(currentNode) && !workingSet.isEmpty()){
				addNodeToWorkingSet(currentNodesExclusions, currentNode);
			}
			if(endInstructions.contains(currentNode)){
				containedEndPoints.add(currentNode);
			}
			currentEndNodes.add(currentNode);
		} else {
			currentNodesExclusions = new TreeSet<Node>(m_exclusions);
			currentNodesExclusions.retainAll(currentNode.getContainedNodes());
			containedEndPoints = new TreeSet<Node>(endInstructions);
			containedEndPoints.retainAll(currentNode.getContainedNodes());
			currentEndNodes.addAll(currentNode.getEndNodes());
		}
		
		if(currentNodesExclusions.isEmpty()){

			if(containedEndPoints.isEmpty()){
				return exploreWhatsNext(startInstruction, workingSet, endInstructions, currentNode, parent, currentNode);
			} else {
				if(endInstructions.containsAll(currentEndNodes)){;
					makeConnection(currentNode, parent);
					addNodeToWorkingSet(workingSet, currentNode);
					firstNodeAdded = true;
					return true;
				} else {
					return exploreIntoCurrentNode(startInstruction, workingSet, endInstructions, currentNode, 
							node,  parent, currentNodesExclusions, containedEndPoints);
				}
			}
		} else {
			if( currentNode.isInstructionNode() ){
				addNodeToWorkingSet(workingSet, currentNode);
				
				firstNodeAdded = true;
				if(currentNode != startInstruction){
					//this doesn't allow excluded nodes to form a loop with the start node! --check
					//maybe it should also contain 'endNodes.contains(start) &&'
					makeConnection(currentNode, parent);
					
					//System.out.println(StructuralAnalysisEngine.dumpTree("inter", workingSet));
					m_excludedRouteNodes.add(currentNode);
					
				} else {
					m_excludedRouteNodes.add(parent);
				}
				return false;
			} else {
				return exploreIntoCurrentNode(startInstruction, workingSet, endInstructions, currentNode, 
						node, parent, currentNodesExclusions, containedEndPoints);
			}
		}
	}

	private void addNodeToWorkingSet(Collection<Node> workingSet,
			Node currentNode) {
		//System.out.println("adding " + currentNode + " to working set");
		workingSet.add(currentNode);
	}
	
	private boolean exploreWhatsNext(InstructionNode startInstruction, Collection<Node> workingSet, Collection<InstructionNode> endNodes,
			 Node currentNode, Node originalParent, Node replacementNode) {
		boolean seenEndPoint = false;
		makeConnection(replacementNode, originalParent);
		if(workingSet.contains(currentNode)){
			//this causes the search not to form infinite loops
			return false;
		}
		addNodeToWorkingSet(workingSet, replacementNode);
		firstNodeAdded = true;
		Node parent = currentNode.getParent();
		if (parent != null && !parent.whatsNext(currentNode).isEmpty()) {
			for (Node next : parent.whatsNext(currentNode)) {
				seenEndPoint |= explore(startInstruction, new NodeWrapper(next), workingSet, endNodes, replacementNode);
			}
		}
		return seenEndPoint;
	}

	private boolean exploreIntoCurrentNode(InstructionNode startInstruction, Collection<Node> workingSet, Collection<InstructionNode> endNodes,
			Node currentNode, NodeWrapper node, Node parent, Collection<Node> currentNodesExclusions, Set<Node> containedEndPoints ) {
		boolean seenEndPoint = false;
		assert (!currentNode.getImmediateChildren().isEmpty());
		
		if(currentNode.isFunctionNode()){
			//the node contains end instructions?
			if(!containedEndPoints.isEmpty()){
				for(Node child : currentNode.getImmediateChildren()){
					NodeWrapper childWrapper = new NodeWrapper(child);
					seenEndPoint |= explore(startInstruction, childWrapper, workingSet, endNodes, parent);
					if(parent == null) {
						node.m_node = childWrapper.m_node;
					}
				}
			} else {
				assert(!currentNodesExclusions.isEmpty());
				
				FunctionNode currentFunction = (FunctionNode)currentNode;
				
				assert(currentNode.getImmediateChildren().size() == 1);
				Node firstFunctionNode = currentNode.getImmediateChildren().get(0);
				
				Node firstInstructionNode = currentNode; 
				InstructionNode functionStartNode = findFirstInstruction(firstInstructionNode);
				
				if(m_exclusions.contains(functionStartNode) && firstNodeAdded){
					m_excludedRouteNodes.add(parent);
					return false;
				}
				
				NodeWrapper functionNodeWrapper = new NodeWrapper(firstFunctionNode);
				Collection<InstructionNode> functionEndNodes = currentNode.getEndNodes();
				Collection<Node> newWorkingSet = new TreeSet<Node>();
				seenEndPoint |= explore(functionStartNode, functionNodeWrapper, newWorkingSet, functionEndNodes, null);
				assert(cfgSanityCheck(newWorkingSet));	
				pruneGraphOfExcludedPaths(newWorkingSet);
				
				//there should be nothing in the working set that is excluded now
				for(Node nodes : newWorkingSet){
					Collection<Node> set = new TreeSet<Node>(nodes.getContainedNodes());
					if(!workingSet.isEmpty())
						addNodeToWorkingSet(set, nodes);
					set.retainAll(m_exclusions);
					assert(set.isEmpty());
				}
				
				//remember that the function might have 0 paths so terminates
				if(!newWorkingSet.isEmpty()){

					Node newStart = makeBasicBlocks(functionStartNode, functionEndNodes, newWorkingSet);
					
					//check that there are no instruction nodes in the graph.
					for(Node graphNode : newWorkingSet){
						assert(!graphNode.isInstructionNode());
					}
					
					StructuralAnalysisEngine sae = new StructuralAnalysisEngine();	
					
					addTerminator(newWorkingSet);
					
					sae.createStructure(newWorkingSet, newStart, m_engine, "Analysing sub route");
					
					assert(newWorkingSet.size() == 1);
					Node newCurrentNode = newWorkingSet.iterator().next();
					
					sae.normaliseGraph(newCurrentNode);
					
					FunctionNode replacementFunction = new FunctionNode(newCurrentNode,currentFunction.getFunctionName());
					
					replacementFunction.getNextNodes().addAll(currentFunction.getNextNodes());
					
					//the replacement function should be without exclusions and end points
					//maybe check this
					
					for(Node containedInstruction : currentFunction.getContainedNodes()){
						//assert(!m_exclusions.contains(containedInstruction) && firstNodeAdded);
						assert(!endNodes.contains(containedInstruction));
					}
					
					seenEndPoint |= exploreWhatsNext(startInstruction, workingSet, endNodes, currentNode, parent, replacementFunction);
				} else {
					//probably should add the parent to the list of excluded route nodes
					if(!m_excludedRouteNodes.contains(parent)){
						m_excludedRouteNodes.add(parent);
					}
				}
			}
			
		} else {
			for(Node child : currentNode.getImmediateChildren()){
				NodeWrapper childWrapper = new NodeWrapper(child);
				seenEndPoint |= explore(startInstruction, childWrapper, workingSet, endNodes, parent);
				if(parent == null) {
					node.m_node = childWrapper.m_node;
				}
			}
		}

		return seenEndPoint;
	}

	private InstructionNode findFirstInstruction(Node firstInstructionNode) {
		while(!firstInstructionNode.isInstructionNode()){
			assert(firstInstructionNode.getImmediateChildren().size() == 1);
			firstInstructionNode = firstInstructionNode.getImmediateChildren().get(0);
		}
		assert(firstInstructionNode.isInstructionNode());
		return (InstructionNode)firstInstructionNode;
	}

	private boolean alreadyContainRoute(Node newGraph) {

		final List<String> newNodeList = new LinkedList<String>();
		
		newGraph.traverse(0, new UntypedNodeTraverser() {
	    	public boolean foundNode(Node node, int depth) {
	    		newNodeList.add(node.getTypeName());
	        	return true;
	    	}
	    });	 
		for(Route route : m_routes){
			
			final List<String> nodeList = new LinkedList<String>();
			
			route.getNode().traverse(0, new UntypedNodeTraverser() {
		    	public boolean foundNode(Node node, int depth) {
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

	private void addTerminator(Collection<Node> workingSet) {
		int count = 0;
		for(Node node : workingSet){
			if(node.getNextNodesSize() == 0){
				count++;
			}
		}
		if(count < 2 ) return;
		Node nullNode = new NullNode();
		for(Node node : workingSet){
			if(node.getNextNodesSize() == 0){
				CommonMethods.connect(nullNode, node);
			}
		}
		addNodeToWorkingSet(workingSet, nullNode);
	}
	
	
	

	private Node makeBasicBlocks(InstructionNode startInstruction, Collection<InstructionNode> endNodes, Collection<Node> workingSet) {
		//first search the working set to find the node that contains the start instruction
		
		Node beginingOfFirstBlock = null;
		for(Node node : workingSet){
			if((node.getContainedNodes().contains(startInstruction) && beginingOfFirstBlock == null )|| node == startInstruction){
				if(findFirstInstruction(node) == startInstruction){
					beginingOfFirstBlock = node;
				}
			}
		}
		
		assert(beginingOfFirstBlock != null);
		Node startNode = null;
		if(beginingOfFirstBlock.isInstructionNode()){
			//if the sequence starts with an instruction then a new block will have to be made
			InstructionNode currentNode = startInstruction;
			List<InstructionNode> startList = new LinkedList<InstructionNode>();
			startList.add(currentNode);
			while (currentNode.getNextNodesSize() == 1 
					&& currentNode.getNextNode().isInstructionNode() 
					&& !startList.contains(currentNode.getNextNode())) {
				currentNode = (InstructionNode)currentNode.getNextNode();
				startList.add(currentNode);
			}
			startNode = new BlockNode(startList);
			workingSet.removeAll(startList);
			addNodeToWorkingSet(workingSet, startNode);
	
			for (Node next : currentNode.getNextNodes()) {
				next.removePrevNode(currentNode);
				CommonMethods.connect(next, startNode);
			}
			for(Node prev  : startInstruction.getPrevNodes()){
				prev.removeNextNode(startInstruction);
				CommonMethods.connect(startNode, prev);
			}
		} else {
			//otherwise that one will do
			startNode = beginingOfFirstBlock;
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
				addNodeToWorkingSet(workingSet, endBlock);
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
		assert(workingSet.contains(startNode));
		assert(cfgSanityCheck(workingSet));
		return startNode;
	}
	
	private void pruneGraphOfExcludedPaths(Collection<Node> workingSet) {
		Set<Node> t = new TreeSet<Node>(m_excludedRouteNodes);
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
		m_excludedRouteNodes.addAll(t);
		assert(cfgSanityCheck(workingSet));
	}

	private void makeConnection(Node node, Node parent) {
		if (parent != null) {
			CommonMethods.connect(node, parent);
		}
	}
	
	private void resetParent() {
		for (Node node : m_structureGraph.getThreads()) {
			setParent(node);
		}
		for (Node node : m_structureGraph.getUnlinkedFunctions()) {
			setParent(node);
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
		m_exclusions = new ArrayList<InstructionNode>();
		ReferenceMatcher referenceMatcher = new ReferenceMatcher();		
		for (Reference reference : m_engine.getExclusions()) {
			for (Node threadNode : m_structureGraph.getThreads()) {
    			m_exclusions.addAll(referenceMatcher.getNodes(threadNode, reference));
			}
		}
		
		for (Reference reference : m_engine.getExclusions()) {
			for (Node threadNode : m_structureGraph.getUnlinkedFunctions()) {
				m_exclusions.addAll(referenceMatcher.getNodes(threadNode, reference));
			}
		}
	}
		
	private void setUnknowns() {
		for (Route route : m_routes) {
			Node node = route.getNode();
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
				// Sets all the self loop iterations to unknown.
				public boolean foundSelfLoopNode(Node node, int depth) {
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
			node.traverse(0, new DefaultNodeTraverser() {
				public boolean foundInstructionNode(Node node, int depth) {
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
	
	private Collection<InstructionNode> getReferencedNodes(Reference reference) {
		ReferenceMatcher referenceMatcher = new ReferenceMatcher();		
		Set<InstructionNode> referencedNodes = new TreeSet<InstructionNode>();
		for(Node thread : m_structureGraph.getThreads()){
			referencedNodes.addAll(referenceMatcher.getNodes(thread, reference));
		}
		for(Node thread : m_structureGraph.getUnlinkedFunctions()){
			referencedNodes.addAll(referenceMatcher.getNodes(thread, reference));
		}
		return referencedNodes;		
	}
	
	static class NodeWrapper {
		Node m_node;
		public NodeWrapper(Node node){
			m_node = node;
		}
	}
	
	private void findEndInstructions(Set<InstructionNode> endNodes) {
		for (Reference endPoint : m_description.getEndPoint()){
			for(InstructionNode endInstruction : getReferencedNodes(endPoint)){
				if(endInstruction.getParent().whatsBefore(endInstruction) != null){
					for(Node node : endInstruction.getParent().whatsBefore(endInstruction)){
						Set<InstructionNode> newNodes = node.getEndNodes();
						for(InstructionNode newNode : newNodes){
							if(!m_exclusions.contains(newNode)){
								endNodes.add(newNode);
							}
						}
					}
				}
			}
		}
	}
	
	private boolean parentIsBetterThanChild(Node child) {
		//the parent is a better start node than the child if the parent starts with the same PC as the child
		//note: be careful of FNOPs
		if(child.getParent() == null) return false;
		Node parent = child.getParent();
		if(parent.isCondNode()) return false;
		if(parent.getStartPc() == child.getStartPc() && !parent.getInstructions().getFirst().isFnop())
			return true;
		return false;
	}

	private boolean containAnyNodes(Node start, Collection<InstructionNode> endNodes) {
		Collection<InstructionNode> containedNodes = start.getContainedNodes();
		for(InstructionNode endNode : endNodes){
			if(containedNodes.contains(endNode)){
				return true;
			}
		}
		return false;
	}
}
