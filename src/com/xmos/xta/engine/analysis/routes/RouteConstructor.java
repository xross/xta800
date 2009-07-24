package com.xmos.xta.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.analysis.routes.RouteDescription;
import com.xmos.xta.engine.analysis.routes.RouteDescription.RouteMode;
import com.xmos.xta.engine.analysis.structural.ReferenceMatcher;
import com.xmos.xta.engine.analysis.structural.StructuralAnalysisEngine;
import com.xmos.xta.engine.analysis.structural.StructureGraph;
import com.xmos.xta.engine.analysis.structural.nodes.BlockNode;
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
	private Collection<Node> m_exclusions;
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

	public RouteError analyse() {
		resetParent();
		boolean seenEndPoint = false;
		
		if(m_description.getStartPoint() == null ){
			return RouteError.STARTPOINT_MISSING;
		}
		if(m_description.getEndPoint() == null ){
			return RouteError.ENDPOINTS_MISSING;
		}
		
		Collection<Node> startNodes =getReferencedNodes(m_description.getStartPoint());
		Collection<Node> endNodes = new TreeSet<Node>();
		for(Reference endPoint : m_description.getEndPoint()){
			endNodes.addAll(getReferencedNodes(endPoint));
		}
		
		if(startNodes.isEmpty() ){
			return RouteError.COULD_NOT_FIND_STARTPOINT;
		}
		
		if(m_description.getMode() == RouteMode.PATH){
			Collection<Node> newEndNodes = new TreeSet<Node>();
			for(Node endNode : endNodes){
				if(endNode.getParent().whatsBefore(endNode) != null){
					for(Node node : endNode.getParent().whatsBefore(endNode)){
						Set<InstructionNode> newNodes = node.getEndNodes();
						for(Node newNode : newNodes){
							if(!m_exclusions.contains(newNode)){
								newEndNodes.add(newNode);
							}
						}
					}
				}
			}
			
			endNodes = newEndNodes;
		}
		boolean notFoundFunctionYet = true;
		for (Node start : startNodes) {
			resetParent();
			Node realStart = start;
			while(parentIsBetterThanChild(start)){
				start = start.getParent();
			}
			if(m_description.getMode() == RouteMode.FUNCTION ){
				if(start.isFunctionNode()&& notFoundFunctionYet){
					if(!containAnyNodes(start, m_exclusions)){
						start.setParent(null);
						m_routes.add(new Route(m_engine, start.deepCopy(), m_description));
					} else {
						seenEndPoint = makeSubRoute(realStart, seenEndPoint, endNodes, start);
					}
					notFoundFunctionYet = false;
				}
			} else {
				seenEndPoint = makeSubRoute(realStart, seenEndPoint, endNodes, start);
			}
			
		}
		setParents();
		setUnknowns();			
		setSelfLoops();	
		setSrcLoopCounts();	
		checkSupportedInstructions();
		
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
	
	private boolean explore(Node start, NodeWrapper node, Collection<Node> workingSet, Collection<Node> endNodes, Node parent) {	
		
		Node currentNode = node.m_node;
		Collection<Node> exclusions = new TreeSet<Node>();
		
		if(currentNode.isInstructionNode()){
			if(m_exclusions.contains(currentNode)){
				exclusions.add(currentNode);
			}
		} else {
			exclusions = new TreeSet<Node>(m_exclusions);
			exclusions.retainAll(currentNode.getContainedNodes());
		}
		
		if(endNodes.containsAll(currentNode.getEndNodes())){
			//all the ends of currentBlock are endNodes 
			if(exclusions.isEmpty()){
				//there are no exclusions in this block
				if(workingSet.isEmpty() && endNodes.contains(start)){
					return exploreWhatsNext(start, workingSet, endNodes, currentNode, parent);
				} else {
					makeConnection(currentNode, parent);
					workingSet.add(currentNode);
					return true;
				}
			} else {
				// there are exclusions in this block
				return exploreIntoCurrentNode(start, node, workingSet, endNodes, parent, currentNode);
			}
		} else {
			Set<Node> containedEndPoints = new TreeSet<Node>(endNodes);
			containedEndPoints.retainAll(currentNode.getContainedNodes());
			//we have to go into the current block
			if(containedEndPoints.isEmpty()){
				if(!exclusions.isEmpty() && !workingSet.isEmpty()){
					if( currentNode.isInstructionNode() ){
						workingSet.add(currentNode);
						makeConnection(currentNode, parent);
						m_excludedRouteNodes.add(currentNode);
						return false;
					} else {
						return exploreIntoCurrentNode(start, node, workingSet, endNodes, parent, currentNode);
					}
				} else {
					return exploreWhatsNext(start, workingSet, endNodes, currentNode, parent);
				}
			} else {
				
				//has multiple ends with at not all as end points
				return exploreIntoCurrentNode(start, node, workingSet, endNodes, parent, currentNode);
			}
		}
	}

	private boolean parentIsBetterThanChild(Node child) {
		if(child.getParent() == null) return false;
		Node parent = child.getParent();
		
		if(parent.isSectionNode()) return false;
		
		if(parent.getStartPc() == child.getStartPc() && !parent.getInstructions().getFirst().isFnop())
			return true;
		
		return false;
	}

	private boolean containAnyNodes(Node start, Collection<Node> endNodes) {
		Collection<Node> containedNodes = start.getContainedNodes();
		for(Node endNode : endNodes){
			if(containedNodes.contains(endNode)){
				return true;
			}
		}
		return false;
	}

	private boolean makeSubRoute(Node realStart, boolean seenEndPoint, Collection<Node> endNodes,
			Node start) {
		
		Collection<Node> workingSet = new TreeSet<Node>();
		
		m_structureGraph.removeLinks();
		m_excludedRouteNodes = new TreeSet<Node>();
		NodeWrapper startNodeWrapper = new NodeWrapper(start);
		StructuralAnalysisEngine sae = new StructuralAnalysisEngine();	
	
		seenEndPoint |= explore(realStart, startNodeWrapper, workingSet, endNodes, null);
		//System.out.println();
		assert(cfgSanityCheck(workingSet));
									
		pruneGraphOfExcludedPaths(workingSet);
		assert(cfgSanityCheck(workingSet));
		if(!workingSet.isEmpty()){
			assert(cfgSanityCheck(workingSet));
			assert(cfgSanityCheck(workingSet));
			
			Node newStart = makeBasicBlocks(startNodeWrapper.m_node, endNodes, workingSet);
			
			assert(cfgSanityCheck(workingSet));
			
			addTerminator(workingSet);
			
			assert(workingSet.contains(newStart));
			
			sae.createStructure(workingSet, newStart, m_engine, true, "Analysing route");
			
			assert(cfgSanityCheck(workingSet));
			
			if(workingSet.size() != 1){
				System.err.println( StructuralAnalysisEngine.dumpTree("route_after",  workingSet));
			}
			
			assert(workingSet.size() == 1);
			
			Node baseNode = workingSet.iterator().next();
		
			sae.normaliseGraph(baseNode);
			
			assert(!baseNode.isInfiniteLoopNode());
			
			if(m_description.getMode() == RouteMode.PATH && baseNode.isInfiniteSelfLoopNode()){
				baseNode = ((InfiniteSelfLoopNode)baseNode).getLoop();
			} 
			
			Node newGraph = baseNode.deepCopy();
			newGraph.setParent(null);
			m_routes.add(new Route(m_engine, newGraph, m_description));
		}
		return seenEndPoint;
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
		workingSet.add(nullNode);
	}
	
	
	private boolean exploreWhatsNext(Node start, Collection<Node> workingSet, Collection<Node> endNodes, 
			Node currentNode, Node originalParent) {
		boolean seenEndPoint = false;

		if(workingSet.contains(currentNode)){
			//this causes the search not to form infinite loops
			//System.out.println("stopping due to loop");
			return false;
		}
		makeConnection(currentNode, originalParent);
		workingSet.add(currentNode);
		Node parent = currentNode.getParent();
		if (parent != null && !parent.whatsNext(currentNode).isEmpty()) {
			
			//System.out.println("\t\t\t" + currentNode + " has next of " + parent.whatsNext(currentNode));
			for (Node next : parent.whatsNext(currentNode)) {
				
			//	System.out.println("going from " + currentNode + " to next: " + next+ "\n");
				seenEndPoint |= explore(start, new NodeWrapper(next), workingSet, endNodes, currentNode);
			}
		}
		return seenEndPoint;
	}

	private boolean exploreIntoCurrentNode(Node start, NodeWrapper node, Collection<Node> workingSet, Collection<Node> endNodes,
			Node parent, Node currentNode) {
		
		boolean seenEndPoint = false;
		
		assert (!currentNode.getImmediateChildren().isEmpty());
		
		for(Node child : currentNode.getImmediateChildren()){
			NodeWrapper childWrapper = new NodeWrapper(child);
			//System.out.println("going from " + currentNode + " into: " + child+ "\n");
			seenEndPoint |= explore(start, childWrapper, workingSet, endNodes, parent);
			if(parent == null) {
				//TODO check to see if parent is ever null or not
				node.m_node = childWrapper.m_node;
			}
		}
		return seenEndPoint;
	}

	private Node makeBasicBlocks(Node start, Collection<Node> endNodes, Collection<Node> workingSet) {
		// from the start instruction crawl downwards stopping at basic block
		// boundaries
		Node startBlock = null;
		if(start.isInstructionNode()){
			InstructionNode currentNode = (InstructionNode)start;
			List<InstructionNode> startList = new LinkedList<InstructionNode>();
			
			startList.add(currentNode);
	
			while (currentNode.getNextNodesSize() == 1 
					&& currentNode.getNextNode().isInstructionNode() 
					&& !startList.contains(currentNode.getNextNode())) {
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
		m_exclusions = new ArrayList<Node>();
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

	private void setSelfLoops() {
		// Sets the iteration count of all self loops to 1
		for (Route route : m_routes) {
			Node node = route.getNode();			
			node.traverse(0, new DefaultNodeTraverser() {
				public boolean foundSelfLoopNode(Node node, int depth) {
					NodeAttributes nodeAttributes = node.getAttributes();
					nodeAttributes.setLoopIterations(1);
					return true;
				}
			});
		}
	}
	
	private void setSrcLoopCounts() {
		// Sets the loop iteration counts specified at compile time.
		for (SrcLevelLoopInfo srcLevelLoopInfo : m_engine.getSrcLevelLoops()) {
			Reference reference = ReferenceFactory.createReference(
					String.format("0x%x", srcLevelLoopInfo.getPc()), 
					ReferenceClass.PC_REF);
			
			for (Route route : m_routes) {
				route.getRouteUnknowns().setLoopIterations(reference, srcLevelLoopInfo.getNumIterations());				
			}
		}		
	}
	
	private void checkSupportedInstructions() {
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
						engine.notify(Notifier.NOTIFY_WARNING, String.format(
								"Ssync (multiple threads) not supported. Pc: 0x%08x", instruction.getPc()));
					
					if (mnenonics.isMsync(instruction))
						engine.notify(Notifier.NOTIFY_WARNING, String.format(
								"Msync (multiple threads) not supported. Pc: 0x%08x", instruction.getPc()));

					if (mnenonics.isMjoin(instruction)) {
						engine.notify(Notifier.NOTIFY_WARNING, String.format(
								"Mjoin (multiple threads) not supported. Pc: 0x%08x", instruction.getPc()));
					}
					
					// Checks for interrupt enable..
					if (mnenonics.isSetSr(instruction)) {	
						int op1Field = instruction.getOperandField("op1");
						int shift = SR_IEBLE_SHIFT;
						int mask = (1 << SR_IEBLE_SIZE) - 1;
						if (((op1Field >> shift) & mask) == 1) 
							engine.notify(Notifier.NOTIFY_WARNING, String.format(
									"Interrupt enable detected. Pc: 0x%08x", instruction.getPc()));
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
	
	private Collection<Node> getReferencedNodes(Reference reference) {
		ReferenceMatcher referenceMatcher = new ReferenceMatcher();		
		Set<Node> referencedNodes = new TreeSet<Node>();
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
}
