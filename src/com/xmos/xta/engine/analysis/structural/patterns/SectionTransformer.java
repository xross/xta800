package com.xmos.xta.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class SectionTransformer extends PatternMatcher {
	
	
	private boolean explore(Node node, TreeSet<Node> seen, Set<Node> dominators, Node start, Collection<Node> workingSet) {
		seen.add(node);
		
		
		if(node.getDominators().containsAll(dominators) && node.getDominators().size() == dominators.size() + 1){
			//then this must be one of the nodes we are looking for
			
			if(node.getNextNodes().size() >1){
				//System.out.println("node " + node + " is mega dominated by " + start);
				CommonMethods.wedgeUp(node, workingSet);
				return true;
			}
			
		}
		
		boolean found = false;
		for(Node next : node.getNextNodes()){
			if(!seen.contains(next)){
				found = explore(next, seen, dominators, start, workingSet);
			}
			if(found) return true;
		}
		return false;
	}

	
	public boolean isMatch(Node currentNode, Collection<Node> workingSet, boolean verbose, Node treeStart) {
		m_treeStart = treeStart;
		boolean found = explore(currentNode, new TreeSet<Node>(), currentNode.getDominators(), currentNode, workingSet);
		
		if(found) return true;
		
		if (currentNode.getNextNodes().size() < 2) {
			return false;
		} 
		//detect all the jump nodes first
		Set<Node> jumpNodes = new TreeSet<Node>();
		for (Node nextNode : currentNode.getNextNodes()) {
			//hop backwards
			Set<Node> previous = new TreeSet<Node>(nextNode.getPrevNodes());
			previous.remove(currentNode);
			previous.remove(nextNode);
			for (Node prev : previous) {
				Set<Node> prevSet = new TreeSet<Node>(prev.getPrevNodes());
				if (prevSet.contains(currentNode)) {
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
		
		for (Node sectionEnd : endNodes) {
			for (Node nextNode : currentNode.getNextNodes()) {
				if (!jumpNodes.contains(nextNode)) {
					if (nextNode.getNextNodes().contains(sectionEnd)) {
						//then this nextNode is interesting
						if (nextNode.getNextNodes().size() > 1) {
							//wedge up
							if (verbose) {
								System.out.println("# wedging up for the multisection");
							}
							CommonMethods.wedgeUp(nextNode, workingSet);
							return true;
							
						} else if (nextNode.getPrevNodes().size() > 1) {
							//wedge down
							if (verbose) {
								System.out.println("# wedging for down the multisection");
							}
							CommonMethods.wedgeDown(nextNode, workingSet);
							return true;
						} 
					}
				}
			}
		}
		return false;
	}

	public boolean usesDominatorTree() {
		return true;
	}


}
