package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.types.ReferenceType;

public class SequenceNode extends Node {
	private NodeAttributes m_attributes;
	List<Node> m_nodes;

	public SequenceNode(List<Node> nodes) {
		super();
		assert (nodes != null);
		m_nodes = nodes;
		m_attributes = new NodeAttributes(this);
	}

	public SequenceNode(SequenceNode path) {
		super();
		m_nodes = path.m_nodes;
		m_attributes = new NodeAttributes(this);
	}

	public SequenceNode() {
		super();
		m_nodes = new LinkedList<Node>();
		m_attributes = new NodeAttributes(this);
	}

	public Node shallowCopy() {
		return new SequenceNode(this);
	}

	public Node deepCopy() {
		SequenceNode pn = new SequenceNode();
		for (Node path : m_nodes) {
			Node clone = path.deepCopy();
			pn.m_nodes.add(clone);
		}
		pn.m_attributes = new NodeAttributes(pn);
		return pn;
	}
	public boolean isSequenceNode() {
		return true;
	}
    public String getTypeName() {
        return "seq";
    }
    
    public NodeAttributes getAttributes() {
    	return m_attributes;
    }    
		
	public LinkedList<XtaInstruction> getInstructions() {
		LinkedList<XtaInstruction> list = new LinkedList<XtaInstruction>();
		for (Node node : m_nodes) {
			list.addAll(node.getInstructions());
		}
		return list;
	}

	public String getFunctionName() {
		return "";
	}

	public long getStartPc() {
		return m_nodes.get(0).getStartPc();
	}

	public void traverse(Stack<Node> ancestors, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundSequenceNode(this, ancestors))
			return;

		ancestors.push(this);
		for (Node node : m_nodes) {
			node.traverse(ancestors, nodeTraverser);
		}
		ancestors.pop();
	}

	public List<Node> getIndirectChildren() {
		return m_nodes;
	}

	public Collection<NodeDescriptor> whatsNext(NodeDescriptor nodeDescriptor) {
		assert (m_nodes.contains(nodeDescriptor.getNode()));
		
		int index = m_nodes.indexOf(nodeDescriptor.getNode());
		if (index < m_nodes.size() - 1) {
			Collection<NodeDescriptor> next = new TreeSet<NodeDescriptor>();
			next.add(new NodeDescriptor(m_nodes.get(index + 1), nodeDescriptor.getAncestors()));
			return next;
			
		}  else if (nodeDescriptor.getNode().isInfiniteLoopNode() || 
				    nodeDescriptor.getNode().isInfiniteSelfLoopNode()) {
			Collection<NodeDescriptor> next = new TreeSet<NodeDescriptor>();
			next.add(new NodeDescriptor(nodeDescriptor.getNode(), nodeDescriptor.getAncestors()));
			return next;
			
		} else {		
			NodeDescriptor thisNodeDescriptor = new NodeDescriptor(this, nodeDescriptor.getAncestors());
		    thisNodeDescriptor.getAncestors().pop();

		    if (!thisNodeDescriptor.getAncestors().isEmpty()) {
			    return thisNodeDescriptor.getAncestors().peek().whatsNext(thisNodeDescriptor);
		    }
		    return new TreeSet<NodeDescriptor>();
		}
	}

	public Collection<NodeDescriptor> whatsBefore(NodeDescriptor nodeDescriptor) {
		assert(m_nodes.contains(nodeDescriptor.getNode()));
		
		int index = m_nodes.indexOf(nodeDescriptor.getNode());
		if (index > 0) {
			
			Collection<NodeDescriptor> before = new TreeSet<NodeDescriptor>();
			before.add(new NodeDescriptor(m_nodes.get(index - 1), nodeDescriptor.getAncestors()));
			return before;
			
		} else {
			NodeDescriptor thisNodeDescriptor = new NodeDescriptor(this, nodeDescriptor.getAncestors());
		    thisNodeDescriptor.getAncestors().pop();

		    if (!thisNodeDescriptor.getAncestors().isEmpty()) {
			    return thisNodeDescriptor.getAncestors().peek().whatsBefore(thisNodeDescriptor);
		    }
		    return new TreeSet<NodeDescriptor>();
		}
	}

	public List<NodeDescriptor> getDirectChildren(Stack<Node> ancestors) {
		List<NodeDescriptor> list = new LinkedList<NodeDescriptor>();		
		ancestors.push(this);
	    list.add(new NodeDescriptor(m_nodes.get(0), ancestors));
		ancestors.pop();
		return list;
	}			
	
	public Set<NodeDescriptor> getEndInstructions(Stack<Node> ancestors) {
		Set<NodeDescriptor> set = new TreeSet<NodeDescriptor>();
		
		ancestors.push(this);
		set.addAll(m_nodes.get(m_nodes.size()-1).getEndInstructions(ancestors));
		ancestors.pop();
		
		return set;
	}
	
	public TimeDistributionBuckets getTimeSpread() {
		TimeDistributionBuckets tdb = new TimeDistributionBuckets();
		List<TimeDistributionBuckets> tsbPath = new LinkedList<TimeDistributionBuckets>();
		for(Node node : m_nodes){
			tsbPath.add(node.getTimeSpread());
		}
		tdb.add(tsbPath);
		return tdb;
	}

	public List<Node> getPath() {
		return m_nodes;
	}

	public void replaceChild(Node oldNode, Node newNode) {
		int index = m_nodes.indexOf(oldNode);
		m_nodes.remove(index);
		m_nodes.add(index, newNode);
	}

	public void replaceChildren(List<Node> children) {
		m_nodes.clear();
		m_nodes.addAll(children);
	}	
	
	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, 
			Stack<Node> ancestors, Node node, boolean calledFromAbove) {
		
		if (!calledFromAbove) {
		    return super.checkBackTrail(referenceTypes, ancestors, node, calledFromAbove);
		}
		return false;
	}	
}
