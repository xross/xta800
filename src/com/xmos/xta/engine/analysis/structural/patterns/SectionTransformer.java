package com.xmos.xta.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class SectionTransformer extends PatternMatcher {

	public boolean isMatch(Node currentNode, Collection<Node> workingSet, boolean verbose, Node treeStart) {
		m_treeStart = treeStart;

		if (currentNode.getNextNodes().size() < 2) {
			return false;
		} 
		
		//detect all the jump nodes first
		Set<Node> jumpNodes = new HashSet<Node>();
		for (Node nextNode : currentNode.getNextNodes()) {
			//hop backwards
			Set<Node> previous = new HashSet<Node>(nextNode.getPrevNodes());
			previous.remove(currentNode);
			previous.remove(nextNode);
			for (Node prev : previous) {
				Set<Node> prevSet = new HashSet<Node>(prev.getPrevNodes());
				if (prevSet.contains(currentNode)) {
					jumpNodes.add(nextNode);
				}
			}
		}
		
		Set<Node> endNodes = new HashSet<Node>(jumpNodes);
		
		List<Node> nextList = new LinkedList<Node>(currentNode.getNextNodes());
		
		for (int i = 0; i <= nextList.size() - 2; i++) {
			Set<Node> intersection = new HashSet<Node>();
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
								System.out.println("# wedging for the multisection");
							}
							CommonMethods.wedgeUp(nextNode, workingSet);
							return true;
							
						} else if (nextNode.getPrevNodes().size() > 1) {
							//wedge down
							if (verbose) {
								System.out.println("# wedging for the multisection");
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
		return false;
	}
}
