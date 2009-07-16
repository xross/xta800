package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.SectionNodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.Status;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

public class SectionNode extends Node {
	private SectionNodeAttributes m_attributes;
	private Collection<Node> m_options;
	private boolean m_isOptional;

	public SectionNode() {
		super();
		m_options = new TreeSet<Node>();
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

	public Node deepCopy() {
		SectionNode sn = new SectionNode();
		for (Node section : m_options) {
			Node clone = section.deepCopy();
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
		Node option = getOption(Case.WORST, null);
		if (option != null) {			
			list.addAll(option.getInstructions());
		}
		return list;		
	}
	
	public Set<Integer> getPCs() {
		Set<Integer> pcs = new TreeSet<Integer>();
		for(Node node : m_options){
			pcs.addAll(node.getPCs());
		}
		return pcs;
	}

	public Collection<Node> getContainedNodes() {
		Collection<Node> containedNodes = new TreeSet<Node> ();
		for(Node option: m_options){
			containedNodes.addAll(option.getContainedNodes());
		}
		return containedNodes;
	}
	
	public int getStartPc() {
	//	assert(false);
		return 0;
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
		if (getParent() != null){
			return getParent().whatsNext(this);
		}
		return new TreeSet<Node>();		
	}

	public Node getOption(Case c, Node ignoreIterationsAboveSection) {
		// Returns the best/worst case option of this section.
		return getOption(c, ignoreIterationsAboveSection, m_options);
    }
	
	public Node getUnsetOption(Case c, Node ignoreIterationsAboveSection) {
		// Gets the best/worst case option taken from each of the 
		// option nodes that does not have its iteration 
		// count explicitly set by the user.
		List<Node> allUnsetOptions = getAllUnsetOptions();
		if (allUnsetOptions.isEmpty())
			return null;
		
		return getOption(c, ignoreIterationsAboveSection, allUnsetOptions);
	}
		
	public boolean isSectionNode() {
		return true;
	}

	public boolean isSectionOptional() {
		return m_isOptional;
	}

	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, Node node, 
			boolean calledFromAbove, FunctionCallStatus functionCallStatus) {
		
		if (!calledFromAbove) {
		    return super.checkBackTrail(referenceTypes, node, calledFromAbove, functionCallStatus);
		}
		return false;
	}	

	public long getError(Case c) {
		long error = 0;
		return error;
	}
	
	public List<Node> getAllUnsetOptions() {
		List<Node> unsetOptions = new ArrayList<Node>();
		for (Node option : m_options) {
			if (!getAttributes().getSetOption(option)) {
				unsetOptions.add(option);				
			}
		}
		return unsetOptions;
	}

	public List<Node> getAllSetOptions() {
		List<Node> setOptions = new ArrayList<Node>();
		for (Node option : m_options) {
			if (getAttributes().getSetOption(option)) {
				setOptions.add(option);
			}
		}
		return setOptions;
	}
	
	public Collection<Node> whatsBefore(Node node) {
		assert(m_options.contains(node));
		if(getParent() != null){
			return getParent().whatsBefore(this);
		} else {
			return new TreeSet<Node>();
		}
	}

	public List<Node> getImmediateChildren() {
		return new LinkedList<Node> (m_options);
	}

	private Node getOption(Case c, Node ignoreIterationsAboveSection, Collection<Node> nodes) {
		// Must handle optional sections as a special case.. 
		if ((c == Case.BEST) && (m_isOptional))
			return null;
		
		// Finds the best/worst case of the real options..
		Status status = new Status();
		Iterator<Node> iter = nodes.iterator();
		Node caseNode = iter.next();		
		double caseTime = caseNode.getAttributes().getTimeTaken(c, status, ignoreIterationsAboveSection);
		
		while (iter.hasNext()) {
			Node node = iter.next();
			double time = node.getAttributes().getTimeTaken(c, status, ignoreIterationsAboveSection);

			switch (c) {
			case WORST:
				// Finds the worst case option...
				if (time > caseTime) {
				    caseNode = node;
				    caseTime = time;
				}
				break;
				
			case BEST:
				// Finds the best (non-zero) case option... 
				if ((caseTime == 0) && (time != 0)) {
					caseNode = node;
				    caseTime = time;
				} else if ((time > 0) && (time < caseTime)) {
				    caseNode = node;
				    caseTime = time;
				}
				break;
			}			
		}		
		return caseNode;		
	}


	public TimeDistributionBuckets getTimeSpread() {
		TimeDistributionBuckets tdb = new TimeDistributionBuckets();
		tdb = new TimeDistributionBuckets();
		Collection<TimeDistributionBuckets> tdbs = new HashSet<TimeDistributionBuckets>();
		for(Node node : m_options){
			tdbs.add(node.getTimeSpread());
		}
		if(m_isOptional){
			tdbs.add(new TimeDistributionBuckets(0));
		}
		tdb.merge(tdbs);
		return tdb;
	}
}
