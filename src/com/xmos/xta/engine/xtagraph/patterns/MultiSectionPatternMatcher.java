package com.xmos.xta.engine.xtagraph.patterns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.xtagraph.nodes.MultiSection;
import com.xmos.xta.engine.xtagraph.nodes.Node;

public class MultiSectionPatternMatcher extends PatternMatcher {


	public boolean isMatch(Node node, Set<Node> workingSet) {
		
		if(node.getNextNodes().size()  < 2){
			//return false;
		}
		
		Set<Node> singlyLinked = new HashSet<Node>();
		
		for(Node next : node.getNextNodes()){
			if(next.getPrevNodes().size() == 1 && next.getNextNodes().size() == 1){
				singlyLinked.add(next);	
			}
		}
		
		List<Node> reachableList = new ArrayList<Node>();
		Set<Node> reachableSet = new HashSet<Node>();
		
		for(Node singlyLinkedNode : singlyLinked){
			reachableList.addAll(singlyLinkedNode.getNextNodes());
			reachableSet.addAll(singlyLinkedNode.getNextNodes());
		}
		
		//now count the number of nodes that are reachable 
		
		Set<Node> sectionTargets = new HashSet<Node>();
		Set<Node> jumpNodes = new HashSet<Node>();
		
		for(Node set : reachableSet){
			int count = 0;
			for(Node list :reachableList ){
				if(set == list){
					count++;
				}
			}
			if(count > 1){
				sectionTargets.add(set);
			} else if(count == 1){
				//if any edge jumps directly here then it is also sectionTarget
				for(Node jump : node.getNextNodes()){
					if(jump == set){
						jumpNodes.add(jump);
						sectionTargets.add(set);
					}
				}
			}
			
		}
		
		if(sectionTargets.size() == 0) return false;

		for(Node sectionTarget : sectionTargets){
			Collection<Node> sectionNodes = new HashSet<Node>();
			
			for(Node next : singlyLinked){
				if(next.getNextNodes().contains(sectionTarget) && next.getNextNodes().size() == 1){
					sectionNodes.add(next);
				}
			}
			Node jumpNode = null;
			for(Node jump : jumpNodes){
				if(jump == sectionTarget){
					jumpNode = jump;
					break;
				}
			}
			
			//System.out.println("# from " + node);
			//System.out.println("# to " + sectionTarget);
			//System.out.println("# jump " + jumpNode);
			//System.out.println("# via " + sectionNodes + "\n");
			
			updateGraph(node, sectionNodes, jumpNode, workingSet, sectionTarget);
			//may have to return true here.
		}
		return true;
		
	
		
	}

	protected void updateGraph(Node startNode, Collection<Node> sectionNodes, Node jumpNode, Set<Node> workingSet, Node endNode) {
		
		MultiSection ms = new MultiSection(sectionNodes, jumpNode);
		
		startNode.getNextNodes().removeAll(sectionNodes);
		startNode.addNextNode(ms);
		
		endNode.getPrevNodes().removeAll(sectionNodes);
		endNode.addPrevNode(ms);
		
		
		if(jumpNode != null){
			startNode.getNextNodes().remove(endNode);
			endNode.getPrevNodes().remove(startNode);
		}
		
		ms.addNextNode(endNode);
		ms.addPrevNode(startNode);
		
		workingSet.removeAll(sectionNodes);
		workingSet.add(ms);

	}

	
	
	
}
