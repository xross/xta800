package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.TerminalNodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

public class TerminalNode extends Node {
	private TerminalNodeAttributes m_attributes;
	private Collection<Node> m_terminals;
	
	public TerminalNode(Collection<Node> terminals) {
		m_terminals = new TreeSet<Node>(terminals);
		m_attributes = new TerminalNodeAttributes(this);
	}
	
	public TerminalNode() {
		m_terminals = new TreeSet<Node>();
		m_attributes = new TerminalNodeAttributes(this);
	}

	public Node shallowCopy() {
		return null;
	}
	
	public Node deepCopy(Map<Integer, FunctionNode> startPcs) {
		TerminalNode tn = new TerminalNode();
		for (Node terminal : m_terminals) {
			Node clone = terminal.deepCopy(startPcs);
			tn.m_terminals.add(clone);
		}
		tn.m_attributes = new TerminalNodeAttributes(tn);
		return tn;
    }
		
    public String getTypeName() {
        return "terminal";
    }
		
    public NodeAttributes getAttributes() {
    	return m_attributes;
    }    
    
	public LinkedList<XtaInstruction> getInstructions() {
		LinkedList<XtaInstruction> insts = new LinkedList<XtaInstruction>();
		for(Node nodes: m_terminals) {
			insts.addAll(nodes.getInstructions());
		}
		return insts;
	}
	
	public Collection<Node> getContainedNodes() {
		Collection<Node> containedNodes = new TreeSet<Node> ();
		for(Node loop: m_terminals){
			containedNodes.addAll(loop.getContainedNodes());
		}
		return containedNodes;
	}

	public Set<Integer> getPCs() {
		Set<Integer> pcs = new TreeSet<Integer>();
		for(Node node : m_terminals){
			pcs.addAll(node.getPCs());
		}
		return pcs;
	}

	public int getStartPc() {
		int pc = 0;
		for(Node n : m_terminals){
			pc = Math.max(pc, n.getStartPc());
		}
		return pc;
	}
	
	public void traverse(int depth, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundTerminalNode(this, depth))
			return;
		
		for (Node node : m_terminals) {
			node.traverse(depth + 1, nodeTraverser);			
		}		
	}
	
	public String toString(){
		return "Terminal" + m_id;
	}

	public List<Node> getChildren() {
		return new LinkedList<Node>(m_terminals);
	}

	public Collection<Node> whatsNext(Node node) {
		if (getParent() != null){
			return getParent().whatsNext(this);
		}
		return new TreeSet<Node>();
	}

	public Node getOption(Case c, boolean ignoreIterations) {
		// Gets the best/worst case option taken from each of the 
		// option nodes that does not have its iteration 
		// count explicitly set by the user.
		List<Node> allUnsetOptions = getAllUnsetOptions();
		if (allUnsetOptions.isEmpty())
			return null;
		
		Iterator<Node> iter = getAllUnsetOptions().iterator();
		Node caseNode = iter.next();
		
		while (iter.hasNext()) {
			Node node = iter.next();
			switch (c) {
			case WORST:				
				if (node.getAttributes().getTimeTaken(c, ignoreIterations) > 
			        caseNode.getAttributes().getTimeTaken(c, ignoreIterations)) {
				    caseNode = node;
			    }										
				break;
				
			case BEST:
				if (node.getAttributes().getTimeTaken(c, ignoreIterations) < 
		            caseNode.getAttributes().getTimeTaken(c, ignoreIterations)) {
			        caseNode = node;
		        }										
				break;
			}			
		}		
		return caseNode;
	}
			
	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, Node node, 
			boolean calledFromAbove, FunctionCallStatus functionCallStatus) {
		
		if (!calledFromAbove) {
		    return super.checkBackTrail(referenceTypes, node, calledFromAbove, functionCallStatus);
		}
		return false;
	}
	
	public void getInstructionStream(Case c, List<InstructionNode> instructions, long iteration) {
		getOption(c, false).getInstructionStream(c, instructions, iteration);
	}

	public long getError(Case c) {
		return getOption(c, false).getError(c);
	}

	
	public List<Node> getAllUnsetOptions() {
		List<Node> unsetOptions = new ArrayList<Node>();
		for (Node option : m_terminals) {
			if (!getAttributes().getSetOption(option)) {
				unsetOptions.add(option);				
			}
		}
		return unsetOptions;
	}

	public List<Node> getAllSetOptions() {
		List<Node> setOptions = new ArrayList<Node>();
		for (Node option : m_terminals) {
			if (getAttributes().getSetOption(option)) {
				setOptions.add(option);
			}
		}
		return setOptions;
	}
	
	public Collection<Node> whatsBefore(Node node) {
		assert(m_terminals.contains(node));
		if(getParent()!= null){
			return getParent().whatsBefore(this);
		} else {
			return new TreeSet<Node>();
		}
	}
	public List<Node> getImmediateChildren() {
		return new LinkedList<Node> (m_terminals);
	}

	public boolean isTerminalNode() {
		return true;
	}	
}
