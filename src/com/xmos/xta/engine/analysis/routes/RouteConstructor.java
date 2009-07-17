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
	private static boolean DEBUG =  false;
	
	enum RouteStatus {
		COMPLETED, ABORTED, ERROR
	}

	public enum RouteError {
		NO_ENDPOINTS_FOUND, ENDPOINTS_MISSING, STARTPOINT_MISSING, SUCCESS
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
		
		boolean notFoundFunctionYet = true;
		for (Node start : startNodes) {
			resetParent();
			int startPc = start.getStartPc();
			
			//this should be reworked to not have the start.getParent().getStartPc() != 0 statement as this 
			//is a dirty way of testing to see if the top most block of a parent is in fact a section
			while(start.getParent() != null && start.getParent().getStartPc() != 0 && start.getParent().getStartPc() == startPc){
				start = start.getParent();
			}
			
			if(m_description.getMode() == RouteMode.FUNCTION ){
				if(start.isFunctionNode()&& notFoundFunctionYet){
					if(!containAnyNodes(start, m_exclusions)){
						start.setParent(null);
						m_routes.add(new Route(m_engine, start.deepCopy(), m_description));
					} else {
						seenEndPoint = makeSubRoute(seenEndPoint, endNodes, start);
					}
					notFoundFunctionYet = false;
				}
			} else {
				seenEndPoint = makeSubRoute(seenEndPoint, endNodes, start);
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

	private boolean containAnyNodes(Node start, Collection<Node> endNodes) {
		Collection<Node> containedNodes = start.getContainedNodes();
		for(Node endNode : endNodes){
			if(containedNodes.contains(endNode)){
				return true;
			}
		}
		return false;
	}

	private boolean makeSubRoute(boolean seenEndPoint, Collection<Node> endNodes,
			Node start) {
		
		if(DEBUG) System.out.println("starting from " + start + " " + start.getInstructions().get(0));
		
		Collection<Node> workingSet = new TreeSet<Node>();
		
		m_structureGraph.removeLinks();
		m_excludedRouteNodes = new TreeSet<Node>();
		NodeWrapper startNodeWrapper = new NodeWrapper(start);
		StructuralAnalysisEngine sae = new StructuralAnalysisEngine();	
		
		seenEndPoint |= explore(startNodeWrapper, workingSet, endNodes, null);

		assert(cfgSanityCheck(workingSet));
									
		pruneGraphOfExcludedPaths(workingSet);
		assert(cfgSanityCheck(workingSet));
		if(!workingSet.isEmpty()){
			assert(cfgSanityCheck(workingSet));
			Set<Node> newEndNodes;
			if(m_description.getMode() == RouteMode.PATH){
				newEndNodes = removeFinalInstruction(endNodes, startNodeWrapper.m_node,workingSet);
			} else {
				newEndNodes = new TreeSet<Node>(endNodes);
			}
			assert(cfgSanityCheck(workingSet));
			Node newStart = makeBasicBlocks(startNodeWrapper.m_node, newEndNodes, workingSet);
			assert(cfgSanityCheck(workingSet));
			
			addTerminator(workingSet);
			
			assert(workingSet.contains(newStart));

			sae.createStructure(workingSet, newStart, m_engine, true, "Analysing route");
			
			if(workingSet.size() != 1){
				System.err.println(StructuralAnalysisEngine.dumpTree("route_after",  workingSet));
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
	
	private boolean explore(NodeWrapper node, Collection<Node> workingSet, Collection<Node> endNodes, Node parent) {	
		Node currentNode = node.m_node;
		//System.out.println("exploring node " + currentNode + " " + currentNode.getInstructions().getFirst());
		Set<Node> containedEndPoints = new TreeSet<Node>(endNodes);
		containedEndPoints.retainAll(currentNode.getContainedNodes());
		
		Collection<Node> exclusions = new TreeSet<Node>(m_exclusions);
		exclusions.retainAll(currentNode.getContainedNodes());
		
		
		if(containedEndPoints.isEmpty()){
			// this node contains no end points or the start node is the end node
			if(!exclusions.isEmpty() ){
				if( currentNode.isInstructionNode() ){
					workingSet.add(currentNode);
					makeConnection(currentNode, parent);
					m_excludedRouteNodes.add(currentNode);
					return false;
				} else {
					return exploreIntoCurrentNode(node, workingSet, endNodes, parent, currentNode);
				}
				
			} else {
				//TODO find out what this is for
				if(workingSet.contains(currentNode)){
					return false;
				}
				makeConnection(currentNode, parent);
				workingSet.add(currentNode);
				return exploreWhatsNext(workingSet, endNodes, currentNode);
			}
			
		} else {
			//then we need to explore into the current node unless it is then end point
			if (currentNodeEndsWithEndNode(endNodes, currentNode)) {
				
				if(!exclusions.isEmpty() ){
					if( currentNode.isInstructionNode() ){
						workingSet.add(currentNode);
						makeConnection(currentNode, parent);
						m_excludedRouteNodes.add(currentNode);
						return false;
					} else {
						return exploreIntoCurrentNode(node, workingSet, endNodes, parent, currentNode);
					}
					
				} else {
					if(workingSet.isEmpty()){
						workingSet.add(currentNode);
						return exploreWhatsNext(workingSet, endNodes, currentNode);
					} else {
						makeConnection(currentNode, parent);
						workingSet.add(currentNode);
						return true;
					}
					
				}
				
		
			} else {
				// otherwise explore into the currentNode
				return exploreIntoCurrentNode(node, workingSet, endNodes, parent, currentNode);
			}
		}
		
	}

	private boolean currentNodeEndsWithEndNode(Collection<Node> endNodes, Node currentNode) {
		int endPc = currentNode.getInstructions().getLast().getPc();
		for(Node endNode : endNodes ){
			if(endNode.getStartPc() == endPc){
				return true;
			}
		}
		return false;
	}

	private boolean exploreWhatsNext(Collection<Node> workingSet, Collection<Node> endNodes, Node currentNode) {
		boolean seenEndPoint = false;
		Node parent = currentNode.getParent();
		if (parent != null && !parent.whatsNext(currentNode).isEmpty()) {
			for (Node next : parent.whatsNext(currentNode)) {
				seenEndPoint |= explore(new NodeWrapper(next), workingSet, endNodes, currentNode);
			}
		}
		return seenEndPoint;
	}

	private boolean exploreIntoCurrentNode(NodeWrapper node, Collection<Node> workingSet, Collection<Node> endNodes,
			Node parent, Node currentNode) {
		
		boolean seenEndPoint = false;
		
		assert (!currentNode.getImmediateChildren().isEmpty());
		
		for(Node child : currentNode.getImmediateChildren()){
			NodeWrapper childWrapper = new NodeWrapper(child);
			seenEndPoint |= explore(childWrapper, workingSet, endNodes, parent);
			if(parent == null) {
				//TODO check to see if parent is ever null or not
				node.m_node = childWrapper.m_node;
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
			if (DEBUG) System.out.println("connecting " + parent + " -> " + node);
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
				//System.out.println("Excluding " +referenceMatcher.getNodes(threadNode, reference) );
    			m_exclusions.addAll(referenceMatcher.getNodes(threadNode, reference));
			}
		}
		
		for (Reference reference : m_engine.getExclusions()) {
			for (Node threadNode : m_structureGraph.getUnlinkedFunctions()) {
		//		System.out.println("Excluding " +referenceMatcher.getNodes(threadNode, reference) );
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
					
					System.out.println(node + " points to " + next);
					return false;
				}
			}
			for(Node prev: node.getPrevNodes()){
				if(!workingSet.contains(prev)){
					System.out.println(node + " comes from " + prev);
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
	
	class NodeWrapper {
		Node m_node;
		public NodeWrapper(Node node){
			m_node = node;
		}
	}
}
