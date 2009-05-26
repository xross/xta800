package com.xmos.xta.engine.analysis.patterns;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.analysis.nodes.Node;

public class SectionTransformer extends PatternMatcher {

	public boolean isMatch(Node currentNode, Collection<Node> workingSet, boolean verbose, Node treeStart) {
		m_treeStart = treeStart;
		//detect all the jump nodes first
		Set<Node> jumpNodes = new HashSet<Node>();
		for(Node nextNode : currentNode.getNextNodes()){
			//hop backwards
			Set<Node> previous = new HashSet<Node>(nextNode.getPrevNodes());
			previous.remove(currentNode);
			previous.remove(nextNode);
			for(Node prev : previous){
				Set<Node> prevSet = new HashSet<Node>(prev.getPrevNodes());
				if(prevSet.contains(currentNode)){
					jumpNodes.add(nextNode);
				}
			}
		}
		
		Set<Node> sectionNodes = new HashSet<Node>(jumpNodes);
		List<Node> nextList = new LinkedList<Node>(currentNode.getNextNodes());
		
		for(int i=0;i<nextList.size()-2;i++){
			for(int j=i+1;j<nextList.size()-1;j++){
				Node a = nextList.get(i);
				Node b = nextList.get(j);
				
				Set<Node> intersection = new HashSet<Node>(a.getNextNodes());
				intersection.retainAll(b.getNextNodes());
				sectionNodes.addAll(intersection);
			}
		}
		
		for(Node sectionEnd : sectionNodes){
			for(Node nextNode : currentNode.getNextNodes()){
				if(!jumpNodes.contains(nextNode)){
					if(nextNode.getNextNodes().contains(sectionEnd)){
						//then this nextNode is interesting
						if(nextNode.getNextNodes().size() > 1){
							//wedge up
							if(verbose){
								System.out.println("# wedging for the multisection");
							}
							CommonMethods.wedgeUp(nextNode, workingSet, verbose);
							return true;
						} else if(nextNode.getPrevNodes().size() > 1){
							//wedge down
							if(verbose){
								System.out.println("# wedging for the multisection");
							}
							CommonMethods.wedgeDown(nextNode, workingSet, verbose);
							return true;
						} 
					}
				}
			}
		}
		return false;
	}

}
