package com.xmos.xta.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.ConditionalNode;

public class ConditionalPatternMatcher extends PatternMatcher {

	private static final boolean OLD_METHOD = true;
	
	
	
	public boolean isMatch(Node currentNode, Collection<Node> workingSet, Node treeStart) {
		m_treeStart = treeStart;

		if (currentNode.getNextNodesSize()< 2) {
			return false;
		} 
		
		if(OLD_METHOD){
			//detect all the jump nodes first
			Set<Node> jumpNodes = new TreeSet<Node>();
			for(Node nextNode : currentNode.getNextNodes()){
				//hop backwards
				Set<Node> previous = new TreeSet<Node>(nextNode.getPrevNodes());
				previous.remove(currentNode);
				previous.remove(nextNode);
				for(Node prev : previous){
					Set<Node> prevSet = new TreeSet<Node>(prev.getPrevNodes());
					if(prevSet.contains(currentNode)){
						jumpNodes.add(nextNode);
					}
				}
			}
			
			Set<Node> endNodes = new TreeSet<Node>(jumpNodes);
			
			List<Node> nextList = new LinkedList<Node>(currentNode.getNextNodes());
			
			for (int i = 0; i <= nextList.size() - 2; i++) {
				Set<Node> intersection = new TreeSet<Node>();
				for (int j = i + 1; j <= nextList.size() - 1; j++) {
					Node a = nextList.get(i);
					Node b = nextList.get(j);
					intersection.clear();
					intersection.addAll(a.getNextNodes());
					intersection.retainAll(b.getNextNodes());
					endNodes.addAll(intersection);
				}
			}
			
			for (Node endNode : endNodes) {
				if (endNode == currentNode)
					continue;
				Set<Node> condNodes = new TreeSet<Node>();
				for (Node nextNode : currentNode.getNextNodes()) {
					if (nextNode.getNextNodes().contains(endNode)) {
						if (nextNode.getNextNodesSize() == 1 && nextNode.getPrevNodes().size() == 1) {
							condNodes.add(nextNode);
						} else {
							continue;
						}
					}
				}
				
				if (jumpNodes.contains(endNode)) {
					if (condNodes.size() > 0) {
						
						//if something goes wrong this is probably to blame
						if(endNode.getNextNodesSize() == 1 && endNode.getNextNode() == currentNode && currentNode != m_treeStart){
							continue;
						}
						if (!condSanityCheck(currentNode, condNodes, endNode, endNode)) {
							System.err.println("#cond failed sanity check");
							continue;
						}
						updateGraph(currentNode, condNodes, true, workingSet, endNode);
						return true;
					}
				} else {
					if (condNodes.size() > 1) {
	
						assert(condSanityCheck(currentNode, condNodes, null, endNode));
						updateGraph(currentNode, condNodes, false, workingSet, endNode);
						return true;
					}
				}
			}
			return false;
		} else {
			//TODO finish this method - as of yet is can't complete every graph
			
			Map<Integer, Set<Node>> condMap = new TreeMap<Integer, Set<Node>>();
			
			//search down the tree until a node with two prev nodes is found.
			searchForCondEnd(currentNode, 0, condMap);
			
			List<Integer> depths = new LinkedList<Integer>(condMap.keySet());
			Collections.sort(depths);
			for(int depth : depths){
				Set<Node> nodes = condMap.get(depth);
				for(Node node : nodes){
					if(verifyGraphAgainstDominators(currentNode, node)){
						//now check that the conditional has been fully reduced
						
						Set<Node> nextNodes = new TreeSet<Node>(currentNode.getNextNodes());
						boolean jumpNode = nextNodes.remove(node);
						
						Set<Node> prevNodes = new TreeSet<Node>(node.getPrevNodes());
						prevNodes.remove(currentNode);
						
						if(nextNodes.equals(prevNodes)){
							updateGraph(currentNode,nextNodes , jumpNode, workingSet, node);
							return true;
						}
						
					}
				}
				
			}
			
			
			return false;
		}
	}

	private void searchForCondEnd(Node currentNode, int depth, Map<Integer, Set<Node>> condMap) {
		if(currentNode.getPrevNodesSize() > 1){
			Set<Node> nodes = condMap.get(depth);
			if(nodes == null){
				nodes = new TreeSet<Node>();
			}
			nodes.add(currentNode);
			condMap.put(depth,nodes);
			return;
		} else {
			for(Node next : currentNode.getNextNodes()){
				searchForCondEnd(next, depth + 1, condMap);
			}
		}
		
	}

	private boolean verifyGraphAgainstDominators(Node startNode, Node endNode) {
		
		//for a conditional to pass it must have the following property:
		// dom(endNode) = dom(startNode) n endNode
		Set<Node> set = new TreeSet<Node>(startNode.getDominators());
		set.add(endNode);
		
		if(endNode.getDominators().containsAll(set)){
			return true;
		}

		return false;
	}

	private boolean condSanityCheck(Node startNode, Collection<Node> condNodes, Node jumpNode, Node endNode) {

		if (!startNode.getNextNodes().containsAll(condNodes)) {
			return false;
		}

		int edgeCount = condNodes.size();
		if (jumpNode != null) {
			edgeCount++;
		}
		if (edgeCount < 2) {
			return false;
		}

		if (!endNode.getPrevNodes().containsAll(condNodes)) {
			return false;
		}

		if (jumpNode != null) {
			if (jumpNode != endNode) {
				return false;
			}
		}
		return true;
	}

	protected void updateGraph(Node startNode, Collection<Node> condNodes, boolean isOptional, Collection<Node> workingSet, Node endNode) {
		ConditionalNode ms = new ConditionalNode(condNodes, isOptional);
		
		for(Node condNode : condNodes){
			startNode.removeNextNode(condNode);
		}
		
		//startNode.getNextNodes().removeAll(condNodes);
		startNode.addNextNode(ms);
		
		endNode.getPrevNodes().removeAll(condNodes);
		endNode.addPrevNode(ms);

		if (isOptional) {
			
			startNode.removeNextNode(endNode);
			//startNode.getNextNodes().remove(endNode);
			endNode.getPrevNodes().remove(startNode);
		}

		ms.addNextNode(endNode);
		ms.addPrevNode(startNode);

		workingSet.removeAll(condNodes);
		workingSet.add(ms);
	}
	
	public boolean usesDominatorTree() {
		if(OLD_METHOD){
			return false;
		} else {
			return true;
		}
		
	}
}
