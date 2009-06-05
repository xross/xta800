package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.xta.engine.instructions.XtaInstruction;

public class SectionNode extends Node {
	private Collection<Node> m_options;
	private boolean m_isOptional;

	public SectionNode() {
		super();
		m_options = new HashSet<Node>();
		m_isOptional = false;
	}
	
	public SectionNode(Collection<Node> options, boolean isOptional) {
		super();
		m_options = options;
		m_isOptional = isOptional;
	}
	
	public SectionNode(SectionNode ms) {
		super();
		m_options = ms.m_options;
		m_isOptional = ms.m_isOptional;
	}

	public Node deepClone(Map<Integer, FunctionNode> startPcs) {
		SectionNode sn = new SectionNode();
		for (Node section : m_options) {
			Node clone = section.deepClone(startPcs);
			sn.m_options.add(clone);
		}
		sn.m_isOptional = m_isOptional; 
		return sn;
	}

	public double getTimeTaken(Case c) {
		return 0;
	}

	public long getNumCycles(Case c) {
		return 0;
	}

	public long getNumInstructions(Case c) {
		return getInstructions().size();
	}
	
	public long getNumFNops(Case c) {
		return 0;
	}
	
	public Collection<Node> getOptions(){
		return m_options;
	}
	
	public List<XtaInstruction> getInstructions() {
		return new LinkedList<XtaInstruction>();
	}

	public Set<Integer> getPCs() {
		Set<Integer> pcs = new HashSet<Integer>();
		for(Node node : m_options){
			pcs.addAll(node.getPCs());
		}
		return pcs;
	}

	public Node clone() {
		return new SectionNode(this);
	}

	public Collection<Node> getContaintedNodes() {
		Collection<Node> containedNodes = new HashSet<Node> ();
		for(Node loop: m_options){
			containedNodes.addAll(loop.getContaintedNodes());
		}
		return containedNodes;
	}

	public String getFunctionName() {
		return "";
	}
	
	public int getStartPc() {
		int pc = 0;
		for(Node n : m_options){
			pc = Math.max(pc, n.getStartPc());
		}
		return pc;
	}

	public void traverse(int depth, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundSectionNode(this, depth))
			return;
		
		for (Node node : m_options) {
			node.traverse(depth + 1, nodeTraverser);			
		}		
	}
	public String toString(){
		return "Section" + m_id;
	}

	public List<Node> getChildern() {
		return new LinkedList<Node>(m_options);
	}

	public Collection<Node> whatsNext(Node node) {
		return getParent().whatsNext(this);
	}
	
	public void setNumIterations(long numIterations) {
	    if (!m_numIterationsDone) {
	    	m_numIterationsDone = true;
	    	
	    	boolean allChildrenNotDone = true;
			for (Node child : getChildern()) {
				if (child.getNumIterationsDone()) { 
					allChildrenNotDone = false;
				    break;
				}
			}
	    	
			if (allChildrenNotDone) {
		    	m_numIterations = numIterations;
		    	Node worstCaseNode = getWorstCaseOption();
				for (Node child : getChildern()) {
					if (child == worstCaseNode) {
						child.setNumIterations(numIterations);
					} else {
					    child.setNumIterations(0);
					}
				}		    	
				m_parent.setNumIterations(numIterations);
			} else {
		    	m_numIterations = getTotalIterations();
				m_parent.setNumIterations(m_numIterations);
			}
	    }
	}
	
	private Node getWorstCaseOption() {
		Iterator<Node> iter = m_options.iterator();
		Node worstCaseNode = iter.next();
		while (iter.hasNext()) {
			Node node = iter.next();
			if (node.getTimeTaken(Case.WORST) > worstCaseNode.getTimeTaken(Case.WORST)) {
				worstCaseNode = node;
			}						
		}		
		return worstCaseNode;
	}
	
	private int getTotalIterations() {
		int totalIterations = 0;
		for (Node child : getChildern()) {
			totalIterations += child.getNumIterations();
		}
		return totalIterations;
	}
	public boolean isSection() {
		return true;
	}
}
