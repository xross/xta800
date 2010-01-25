package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.ConditionalNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Status;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser.TraverseStatus;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.types.ReferenceType;

public class ConditionalNode extends Node {
	private ConditionalNodeAttributes m_attributes;
	private Collection<Node> m_options;

	public ConditionalNode() {
		super();
		m_options = new TreeSet<Node>();
		m_attributes = new ConditionalNodeAttributes(this);
	}
	
	public ConditionalNode(Collection<Node> options, boolean isOptional) {
		super();
		m_options = options;
		if (isOptional)
			m_options.add(new BlockNode());
		m_attributes = new ConditionalNodeAttributes(this);
	}
	
	public ConditionalNode(ConditionalNode ms) {
		super();
		m_options = ms.m_options;
		m_attributes = new ConditionalNodeAttributes(this);
	}

	public Node copyStructure() {
		return new ConditionalNode(this);
	}

	public Node deepCopy(Aborter aborter) throws AbortedException {
		ConditionalNode sn = new ConditionalNode();
		for (Node option : m_options) {
			Node clone = option.deepCopy(aborter);
			sn.m_options.add(clone);
		}
		sn.m_attributes = new ConditionalNodeAttributes(sn);
		return sn;
	}
	
    public String getTypeName() {
        return "cond";
    }
	
    public NodeAttributes getAttributes() {
    	return m_attributes;
    }    
    
	public LinkedList<XtaInstruction> getInstructions() {
		LinkedList<XtaInstruction> list = new LinkedList<XtaInstruction>();
		for (Node node : m_options){
			list.addAll(node.getInstructions());
		}
		return list;		
	}

	public XtaInstruction getStartInstruction() {
		Iterator<Node> iter = m_options.iterator();
		if (iter.hasNext()) {
			return iter.next().getStartInstruction();
		} else {
			return null;
		}
	}

	public long getStartPc() {
		return 0;
	}

	public Collection<Node> getOptions(){
		return m_options;
	}
	
	public TraverseStatus traverse(Stack<Node> ancestors, NodeTraverser nodeTraverser) {
		TraverseStatus status = nodeTraverser.foundConditionalNode(this, ancestors);
		if (status == TraverseStatus.CONTINUE) {
			ancestors.push(this);
			for (Node node : m_options) {
				status = node.traverse(ancestors, nodeTraverser);
				if (status != TraverseStatus.CONTINUE) {
					break;
				}
			}		
			ancestors.pop();
		}

		if (status == TraverseStatus.ABORT) {
			return status;
		}
		return TraverseStatus.CONTINUE;
	}

	public List<Node> getIndirectChildren() {
		return new LinkedList<Node>(m_options);
	}

	public Collection<NodeDescriptor> whatsNext(NodeDescriptor nodeDescriptor) {
		assert(m_options.contains(nodeDescriptor.getNode()));

		NodeDescriptor thisNodeDescriptor = new NodeDescriptor(this, nodeDescriptor.getAncestors());
		thisNodeDescriptor.getAncestors().pop();

		if (!thisNodeDescriptor.getAncestors().isEmpty()) {
			return thisNodeDescriptor.getAncestors().peek().whatsNext(thisNodeDescriptor);
		}
		return new TreeSet<NodeDescriptor>();
	}

	public Collection<NodeDescriptor> whatsBefore(NodeDescriptor nodeDescriptor) {
		assert(m_options.contains(nodeDescriptor.getNode()));

		NodeDescriptor thisNodeDescriptor = new NodeDescriptor(this, nodeDescriptor.getAncestors());
		thisNodeDescriptor.getAncestors().pop();

		if (!thisNodeDescriptor.getAncestors().isEmpty()) {
			return thisNodeDescriptor.getAncestors().peek().whatsBefore(thisNodeDescriptor);
		}
		return new TreeSet<NodeDescriptor>();
    }
	
	public Node getOption(Case c, Node ignoreIterationsAboveCond) {
		// Returns the best/worst case option of this conditional.
		Node option = getOption(c, ignoreIterationsAboveCond, m_options);
		return option;
    }
	
	public Node getUnsetOption(Case c, Node ignoreIterationsAboveCond) {
		// Gets the best/worst case option taken from each of the 
		// option nodes that does not have its iteration 
		// count explicitly set by the user.
		List<Node> allUnsetOptions = getAllUnsetOptions();
		if (allUnsetOptions.isEmpty())
			return null;
		
		return getOption(c, ignoreIterationsAboveCond, allUnsetOptions);
	}
		
	public boolean isCondNode() {
		return true;
	}
	
	public List<Node> getAllUnsetOptions() {
		List<Node> unsetOptions = new LinkedList<Node>();
		for (Node option : m_options) {
			if (!getAttributes().getSetOption(option)) {
				unsetOptions.add(option);				
			}
		}
		return unsetOptions;
	}

	public List<Node> getAllSetOptions() {
		List<Node> setOptions = new LinkedList<Node>();
		for (Node option : m_options) {
			if (getAttributes().getSetOption(option)) {
				setOptions.add(option);
			}
		}
		return setOptions;
	}
	
	public List<NodeDescriptor> getDirectChildren(Stack<Node> ancestors) {
		List<NodeDescriptor> list = new LinkedList<NodeDescriptor>();		
		ancestors.push(this);
		for (Node option : m_options) {
		    list.add(new NodeDescriptor(option, ancestors));
		}
		ancestors.pop();
		return list;
	}

	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, 
			Stack<Node> ancestors, Node node, boolean calledFromAbove) {
		
		if (!calledFromAbove) {
		    return super.checkBackTrail(referenceTypes, ancestors, node, calledFromAbove);
		}
		return false;
	}	
	
	private Node getOption(Case c, Node ignoreIterationsAboveCond, Collection<Node> nodes) {			
		// Finds the best/worst case of the real options..
		Iterator<Node> iter = nodes.iterator();
		Node caseNode = iter.next();		
		Status s = new Status();
		double caseTime = caseNode.getAttributes().getTimeTaken(c, s, ignoreIterationsAboveCond);
		if (c == Case.WORST && s.isUnresolved()) {
			return caseNode;
		} else if (c == Case.BEST && (s.isZero() || s.isUnresolved())) {
			caseTime = Double.MAX_VALUE;
		}

		while (iter.hasNext()) {
			Node node = iter.next();
			Status status = new Status();
			double time = node.getAttributes().getTimeTaken(c, status, ignoreIterationsAboveCond);
			switch (c) {
			case WORST:
				if (status.isUnresolved()) {
					return node;
				} else if ((time > caseTime)) {
				    caseNode = node;
				    caseTime = time;
				}
				break;
				
			case BEST:
				if (time < caseTime && !status.isZero() && !status.isUnresolved()) {
				    caseNode = node;
				    caseTime = time;
				}
				break;
			}	
			assert(time >= 0);
		}	
		
		
		return caseNode;		
	}

	public Set<NodeDescriptor> getEndInstructions(Stack<Node> ancestors) {
		Set<NodeDescriptor> set = new TreeSet<NodeDescriptor>();
		
		ancestors.push(this);
		for (Node options : m_options) {
			set.addAll(options.getEndInstructions(ancestors));
		}
		ancestors.pop();
		
		return set;
	}

	public TimeDistributionBuckets getTimeSpread() {
		TimeDistributionBuckets tdb = new TimeDistributionBuckets();
		Collection<TimeDistributionBuckets> tdbs = new LinkedList<TimeDistributionBuckets>();
		for(Node node : m_options){
			tdbs.add(node.getTimeSpread());
		}
		tdb.merge(tdbs);
		return tdb;
	}	
	
	public void replaceChild(Node oldNode, Node newNode) {
		m_options.remove(oldNode);
		m_options.add(newNode);
	}
	
	public void replaceChildren(List<Node> children) {
		m_options.clear();
		m_options.addAll(children);
	}	
}
