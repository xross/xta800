package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import com.xmos.xta.engine.analysis.controlflow.InstructionBuffer;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.SectionNodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

public class SectionNode extends Node {
	private SectionNodeAttributes m_attributes;
	private Collection<Node> m_options;
	private boolean m_isOptional;

	public SectionNode() {
		super();
		m_options = new HashSet<Node>();
		m_isOptional = false;
		m_attributes = new SectionNodeAttributes(this);
	}
	
	public SectionNode(Collection<Node> options, boolean isOptional) {
		super();
		m_options = options;
		m_isOptional = isOptional;
		m_attributes = new SectionNodeAttributes(this);
	}
	
	public SectionNode(SectionNode ms) {
		super();
		m_options = ms.m_options;
		m_isOptional = ms.m_isOptional;
		m_attributes = new SectionNodeAttributes(this);
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
		sn.m_attributes = new SectionNodeAttributes(sn);
		return sn;
	}
	
    public String getTypeName() {
        return "section";
    }
	
    public NodeAttributes getAttributes() {
    	return m_attributes;
    }    
    
	public LinkedList<XtaInstruction> getInstructions() {
		LinkedList<XtaInstruction> list = new LinkedList<XtaInstruction>();
		list.addAll(getOption(Case.WORST, false).getInstructions());
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
		
	public Node getOption(Case c, boolean ignoreIterations) {
		// Gets the best/worst case option taken from each of the 
		// option nodes that does not have its iteration 
		// count explicitly set by the user.				
		Iterator<Node> iter = getUnsetOptions().iterator();
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
		
	public boolean isSectionNode() {
		return true;
	}

	public boolean isOptional() {
		return m_isOptional;
	}

	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, Node node, 
			boolean calledFromAbove, FunctionCallStatus functionCallStatus) {
		
		if (!calledFromAbove) {
		    return super.checkBackTrail(referenceTypes, node, calledFromAbove, functionCallStatus);
		}
		return false;
	}	
	

	public void getInstructionStream(Case c, List<InstructionNode> instructions, long iteration) {
/*
		if(totalIterations() == 1){
			switch (c) {
			case BEST:
			    if(!m_isOptional){
			    	getBestCaseOption().getInstructionStream(c, instructions, iteration);
			    } else {
			    	return;
			    }
			case WORST:
				getWorstCaseOption().getInstructionStream(c, instructions, iteration);
			}
		} else {
			List<Node> sectionList = new LinkedList<Node>(m_options);
			Collections.sort(sectionList);
			int itt=0;
			for(Node option : sectionList){
				itt += option.getAttributes().getNumIterations();
				
				if(itt > iteration) {
					option.getInstructionStream(c, instructions, iteration);
					return;
				}
				
			}
		}
*/
		getOption(Case.WORST, false).getInstructionStream(c, instructions, iteration);

	}


	public long getError(Case c) {
		long error = 0;
		return error;
	}

	public void addFnops() {
		for(Node node : m_options){
			node.addFnops();
		}
		Collection<Node> sectionNodesContributingToIbufferDueToWorstCase = new HashSet<Node>();
		Collection<Node> sectionNodesContributingToIbufferDueToBestCase = new HashSet<Node>();
		for(Node node : m_options){
			if(node.getAttributes().getNumIterations(Case.WORST) > 0) {
				sectionNodesContributingToIbufferDueToWorstCase.add(node);
			}
			if(node.getAttributes().getNumIterations(Case.BEST) > 0) {
				sectionNodesContributingToIbufferDueToBestCase.add(node);
			}
		}
		for(Node node : sectionNodesContributingToIbufferDueToWorstCase){
			for(InstructionBuffer ib : node.getIbufferDueToWorstTime()){
				getParent().addEndIbufferDueToWorst(ib);
			}
		}
		for(Node node : sectionNodesContributingToIbufferDueToWorstCase){
			for(InstructionBuffer ib : node.getIbufferDueToBestTime()){
				getParent().addEndIbufferDueToBest(ib);
			}
		}
	}


	
	public List<Node> getUnsetOptions() {
		Map<Node, Boolean> setOptionMap = getAttributes().getSetOptions();
		
		List<Node> unsetOptions = new ArrayList<Node>();
		for (Node option : m_options) {
			if (!setOptionMap.get(option)) {
				unsetOptions.add(option);
			}
		}
		return unsetOptions;
	}

	public Collection<Node> whatsBefore(Node node) {
		assert(m_options.contains(node));
		if(getParent() != null){
			return getParent().whatsBefore(this);
		} else {
			return new HashSet<Node>();
		}
	}

	public List<Node> getImmediateChildren() {
		return new LinkedList<Node> (m_options);
	}

	
}
