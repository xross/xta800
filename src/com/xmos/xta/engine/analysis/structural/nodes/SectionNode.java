package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

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

	public Node shallowCopy() {
		return new SectionNode(this);
	}
	
	public Node deepCopy(Map<Integer, FunctionNode> startPcs) {
		SectionNode sn = new SectionNode();
		for (Node section : m_options) {
			Node clone = section.deepCopy(startPcs);
			sn.m_options.add(clone);
		}
		sn.m_isOptional = m_isOptional; 
		return sn;
	}
	
    public String getTypeName() {
        return "section";
    }
	
	public LinkedList<XtaInstruction> getInstructions() {
		LinkedList<XtaInstruction> list = new LinkedList<XtaInstruction>();
		list.addAll(getWorstCaseOption().getInstructions());
		return list;		
	}
	
	public Set<Integer> getPCs() {
		Set<Integer> pcs = new HashSet<Integer>();
		for(Node node : m_options){
			pcs.addAll(node.getPCs());
		}
		return pcs;
	}

	public Collection<Node> getContainedNodes() {
		Collection<Node> containedNodes = new HashSet<Node> ();
		for(Node loop: m_options){
			containedNodes.addAll(loop.getContainedNodes());
		}
		return containedNodes;
	}
	
	public int getStartPc() {
		int pc = 0;
		for(Node n : m_options){
			pc = Math.max(pc, n.getStartPc());
		}
		return pc;
	}

	public Collection<Node> getOptions(){
		return m_options;
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

	public List<Node> getChildren() {
		return new LinkedList<Node>(m_options);
	}

	public Collection<Node> whatsNext(Node node) {
		return getParent().whatsNext(this);
	}
	
	public void setNumIterations(long numIterations) {
	    if (!m_numIterationsDone) {
	    	m_numIterationsDone = true;
	    	
	    	boolean allChildrenNotDone = true;
			for (Node child : getChildren()) {
				if (child.getNumIterationsDone()) { 
					allChildrenNotDone = false;
				    break;
				}
			}
	    	
			if (allChildrenNotDone) {
		    	m_numIterations = numIterations;
		    	Node worstCaseNode = getWorstCaseOption();
				for (Node child : getChildren()) {
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
	
	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, Node node, 
			boolean calledFromAbove, FunctionCallStatus functionCallStatus) {
		
		if (!calledFromAbove) {
		    return super.checkBackTrail(referenceTypes, node, calledFromAbove, functionCallStatus);
		}
		return false;
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
	
	private Node getBestCaseOption() {
		Iterator<Node> iter = m_options.iterator();
		Node bestCaseNode = iter.next();
		while (iter.hasNext()) {
			Node node = iter.next();
			if (node.getTimeTaken(Case.BEST) < bestCaseNode.getTimeTaken(Case.BEST)) {
				bestCaseNode = node;
			}						
		}		
		return bestCaseNode;
	}
	
	private int getTotalIterations() {
		int totalIterations = 0;
		for (Node child : getChildren()) {
			totalIterations += child.getNumIterations();
		}
		return totalIterations;
	}
	
	public boolean isSectionNode() {
		return true;
	}

	public boolean isOptional() {
		return m_isOptional;
	}

	// --------------------------------------------------------------------- //
	// Statistics Gathering Methods
	// --------------------------------------------------------------------- //
	public double getTimeTakenRelative(Case c) {
		switch (c) {
		case BEST:
		    return (!m_isOptional) ? getBestCaseOption().getTimeTakenRelative(c) : 0;
		case WORST:
			return getWorstCaseOption().getTimeTakenRelative(c);
		}
		assert(false);
		return 0;		
	}

	public double getTimeTakenAbsolute(Case c) {
		switch (c) {
		case BEST:
		    return (!m_isOptional) ? getBestCaseOption().getTimeTakenAbsolute(c) : 0;
		case WORST:
			return getWorstCaseOption().getTimeTakenAbsolute(c);
		}
		assert(false);
		return 0;		
	}
	
	public long getNumCyclesRelative(Case c) {
		switch (c) {
		case BEST:
		    return (!m_isOptional) ? getBestCaseOption().getNumCyclesRelative(c) : 0;
		case WORST:
			return getWorstCaseOption().getNumCyclesRelative(c);
		}
		assert(false);
		return 0;		
	}

	public long getNumCyclesAbsolute(Case c) {
		switch (c) {
		case BEST:
		    return (!m_isOptional) ? getBestCaseOption().getNumCyclesAbsolute(c) : 0;
		case WORST:
			return getWorstCaseOption().getNumCyclesAbsolute(c);
		}
		assert(false);
		return 0;		
	}
	
	public long getNumInstructionsRelative(Case c) {		
		switch (c) {
		case BEST:
		    return (!m_isOptional) ? getBestCaseOption().getNumInstructionsRelative(c) : 0;
		case WORST:
			return getWorstCaseOption().getNumInstructionsRelative(c);
		}
		assert(false);
		return 0;		
	}

	public long getNumInstructionsAbsolute(Case c) {		
		switch (c) {
		case BEST:
		    return (!m_isOptional) ? getBestCaseOption().getNumInstructionsAbsolute(c) : 0;
		case WORST:
			return getWorstCaseOption().getNumInstructionsAbsolute(c);
		}
		assert(false);
		return 0;		
	}
	
	public long getNumFNopsRelative(Case c) {
		switch (c) {
		case BEST:
		    return (!m_isOptional) ? getBestCaseOption().getNumFNopsRelative(c) : 0;
		case WORST:
			return getWorstCaseOption().getNumFNopsRelative(c);
		}
		assert(false);
		return 0;		
	}

	public long getNumFNopsAbsolute(Case c) {
		switch (c) {
		case BEST:
		    return (!m_isOptional) ? getBestCaseOption().getNumFNopsAbsolute(c) : 0;
		case WORST:
			return getWorstCaseOption().getNumFNopsAbsolute(c);
		}
		assert(false);
		return 0;		
	}
/*
	public void updateFnops(Case c, IBufferVector ibv) {
		Map<Node, IBufferVector> nodeToIBuff = new HashMap<Node, IBufferVector>();
		for(Node option : m_options){
			option.updateFnops(c, ibv);
			nodeToIBuff.put(option, option.getIBufferVector(c));
		}
		if(c == Case.WORST){
			m_worstCaseEndIbuffer = maximiseWorst(nodeToIBuff);
		} else if(c == Case.BEST){
			m_bestCaseEndIbuffer = maximiseBest(nodeToIBuff);
		} else {
			assert(false);
		}
	}

	private IBufferVector maximiseBest(Map<Node, IBufferVector> nodeToIBuff) {
		if(nodeToIBuff.size() == 1){
			return nodeToIBuff.values().iterator().next();
		} else {
			//must work out the best mixture of the fnops to take
			return null;
		}
	}

	private IBufferVector maximiseWorst(Map<Node, IBufferVector> nodeToIBuff) {
		if(nodeToIBuff.size() == 1){
			return nodeToIBuff.values().iterator().next();
		} else {
			//must work out the best mixture of the fnops to take
			return null;
		}
	}	
	
	public void updateFnops(IBufferVector bestVector, IBufferVector worstVector) {

		
	}
	
	*/

	public void getInstructionStream(Case c, List<XtaInstruction> instructions, int iteration) {
		List<Node> sectionList = new LinkedList<Node>(m_options);
		Collections.sort(sectionList);
		int itt=0;
		for(Node option : sectionList){
			option.getInstructionStream(c, instructions, itt);
			itt++;
		}
	}


	
}
