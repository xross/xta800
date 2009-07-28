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
import com.xmos.xta.engine.analysis.structural.nodes.SectionNode;

public class SectionPatternMatcher extends PatternMatcher {

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
				Set<Node> sectionNodes = new TreeSet<Node>();
				for (Node nextNode : currentNode.getNextNodes()) {
					if (nextNode.getNextNodes().contains(endNode)) {
						if (nextNode.getNextNodesSize() == 1 && nextNode.getPrevNodes().size() == 1) {
							sectionNodes.add(nextNode);
						} else {
							continue;
						}
					}
				}
				
				if (jumpNodes.contains(endNode)) {
					if (sectionNodes.size() > 0) {
						
						//if something goes wrong this is probably to blame
						if(endNode.getNextNodesSize() == 1 && endNode.getNextNode() == currentNode && currentNode != m_treeStart){
							continue;
						}
						if (!sectionSanityCheck(currentNode, sectionNodes, endNode, endNode)) {
							System.err.println("#section failed sanity check");
							continue;
						}
						updateGraph(currentNode, sectionNodes, true, workingSet, endNode);
						return true;
					}
				} else {
					if (sectionNodes.size() > 1) {
	
						assert(sectionSanityCheck(currentNode, sectionNodes, null, endNode));
						updateGraph(currentNode, sectionNodes, false, workingSet, endNode);
						return true;
					}
				}
			}
			return false;
		} else {
			//TODO finish this method - as of yet is can't complete every graph
			
			Map<Integer, Set<Node>> sectionMap = new TreeMap<Integer, Set<Node>>();
			
			//search down the tree until a node with two prev nodes is found.
			searchForSectionEnd(currentNode, 0, sectionMap);
			
			List<Integer> depths = new LinkedList<Integer>(sectionMap.keySet());
			Collections.sort(depths);
			for(int depth : depths){
				Set<Node> nodes = sectionMap.get(depth);
				for(Node node : nodes){
					if(verifyGraphAgainstDominators(currentNode, node)){
						//now check that the section has been fully reduced
						
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

	private void searchForSectionEnd(Node currentNode, int depth, Map<Integer, Set<Node>> sectionMap) {
		if(currentNode.getPrevNodesSize() > 1){
			Set<Node> nodes = sectionMap.get(depth);
			if(nodes == null){
				nodes = new TreeSet<Node>();
			}
			nodes.add(currentNode);
			sectionMap.put(depth,nodes);
			return;
		} else {
			for(Node next : currentNode.getNextNodes()){
				searchForSectionEnd(next, depth + 1, sectionMap);
			}
		}
		
	}

	private boolean verifyGraphAgainstDominators(Node startNode, Node endNode) {
		
		//for a section to pass it must have the following property:
		// dom(endNode) = dom(startNode) n endNode
		Set<Node> set = new TreeSet<Node>(startNode.getDominators());
		set.add(endNode);
		
		if(endNode.getDominators().containsAll(set)){
			return true;
		}

		return false;
	}

	private boolean sectionSanityCheck(Node startNode, Collection<Node> sectionNodes, Node jumpNode, Node endNode) {

		if (!startNode.getNextNodes().containsAll(sectionNodes)) {
			return false;
		}

		int edgeCount = sectionNodes.size();
		if (jumpNode != null) {
			edgeCount++;
		}
		if (edgeCount < 2) {
			return false;
		}

		if (!endNode.getPrevNodes().containsAll(sectionNodes)) {
			return false;
		}

		if (jumpNode != null) {
			if (jumpNode != endNode) {
				return false;
			}
		}
		return true;
	}

	protected void updateGraph(Node startNode, Collection<Node> sectionNodes, boolean isOptional, Collection<Node> workingSet, Node endNode) {
		SectionNode ms = new SectionNode(sectionNodes, isOptional);
		
		for(Node sectionNode : sectionNodes){
			startNode.removeNextNode(sectionNode);
		}
		
		//startNode.getNextNodes().removeAll(sectionNodes);
		startNode.addNextNode(ms);
		
		endNode.getPrevNodes().removeAll(sectionNodes);
		endNode.addPrevNode(ms);

		if (isOptional) {
			
			startNode.removeNextNode(endNode);
			//startNode.getNextNodes().remove(endNode);
			endNode.getPrevNodes().remove(startNode);
		}

		ms.addNextNode(endNode);
		ms.addPrevNode(startNode);

		workingSet.removeAll(sectionNodes);
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
