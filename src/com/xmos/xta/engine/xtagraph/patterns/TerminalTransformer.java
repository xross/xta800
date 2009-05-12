package com.xmos.xta.engine.xtagraph.patterns;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.xtagraph.nodes.Node;

public class TerminalTransformer extends PatternMatcher {

	public boolean isMatch(Node node, Collection<Node> workingSet) {
		
		//no terminal may have more then one in
		
		if(node.getNextNodes().size() == 0 && node.getPrevNodes().size() > 1){
			CommonMethods.wedgeDown(node, workingSet);
			return true;
		} 
		
		if(node.getNextNodes().size() > 1) {
			Set<Node> terminals = new HashSet<Node>();
			for(Node next : node.getNextNodes()){
				if(CommonMethods.isTerminal(next)){
					terminals.add(next);
				}
			}
			if(terminals.size() >  0 && terminals.size() < node.getNextNodes().size() && node.getPrevNodes().size()>0){
				
				List<Node> list = new LinkedList<Node>(node.getNextNodes());
				list.removeAll(terminals);
				
				CommonMethods.wedgeUp(node, list.get(0), workingSet);
				return true;
			}
		}
		
		
		return false;
	}

}
