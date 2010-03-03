package com.xmos.xta.core.engine;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.NullNode;
import com.xmos.xta.core.engine.analysis.structural.patterns.CommonMethods;

public class TerminatorAdder {

	private Set<InstructionNode> m_functionStartNodes;

	public TerminatorAdder(Set<InstructionNode> functionStartNodes){
		m_functionStartNodes = functionStartNodes;
	}

	public void process() {
		for(InstructionNode inst : m_functionStartNodes){
			Set<Node> terminals = new TreeSet<Node>();
			Set<Node> seen = new HashSet<Node>();
			findEndPoints(inst, seen, terminals);
			NullNode nullNode = null;
			for(Node node : terminals){
				if(node instanceof NullNode){
					nullNode = (NullNode) node;
					terminals.remove(node);
					break;
				}
			}
			if(nullNode == null) nullNode = new NullNode();
			for(Node terminal : terminals){
				CommonMethods.connect(nullNode, terminal);
			}
		}
	}

	private void findEndPoints(Node inst, Set<Node> seen, Set<Node> terminals) {
		Set<Node> following = new TreeSet<Node>();
		following.add(inst);
		while(!following.isEmpty()){
			Node current = following.iterator().next();
			seen.add(current);
			if(current.getNextNodesSize() == 0){
				terminals.add(current);
			} else {
				for(Node next : current.getNextNodes()){
					if(!seen.contains(next)){
						following.add(next);
					}
				}
			}
			following.remove(current);
		}
	}
}
