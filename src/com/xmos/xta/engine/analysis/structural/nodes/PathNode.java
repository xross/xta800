package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

public class PathNode extends Node {
	private NodeAttributes m_attributes;
	List<Node> m_nodes;

	public PathNode(List<Node> nodes) {
		super();
		assert (nodes != null);
		m_nodes = nodes;
		m_attributes = new NodeAttributes(this);
	}

	public PathNode(PathNode path) {
		super();
		m_nodes = path.m_nodes;
		m_attributes = new NodeAttributes(this);
	}

	public PathNode() {
		super();
		m_nodes = new LinkedList<Node>();
		m_attributes = new NodeAttributes(this);
	}

	public Node shallowCopy() {
		return new PathNode(this);
	}

	public Node deepCopy() {
		PathNode pn = new PathNode();
		for (Node path : m_nodes) {
			Node clone = path.deepCopy();
			pn.m_nodes.add(clone);
		}
		pn.m_attributes = new NodeAttributes(pn);
		return pn;
	}
	public boolean isPathNode() {
		return true;
	}
    public String getTypeName() {
        return "path";
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

	public Set<Integer> getPCs() {
		Set<Integer> pcs = new TreeSet<Integer>();
		for (Node node : m_nodes) {
			pcs.addAll(node.getPCs());
		}
		return pcs;
	}

	public Collection<Node> getContainedNodes() {
		Collection<Node> containedNodes = new TreeSet<Node>();
		for (Node path : m_nodes) {
			containedNodes.addAll(path.getContainedNodes());
		}
		return containedNodes;
	}

	public String getFunctionName() {
		return "";
	}

	public int getStartPc() {
		return m_nodes.get(0).getStartPc();
	}

	public void traverse(int depth, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundPathNode(this, depth))
			return;

		for (Node node : m_nodes) {
			node.traverse(depth + 1, nodeTraverser);
		}
	}

	public String toString() {
		return "Path" + m_id;
	}

	public List<Node> getChildren() {
		return m_nodes;
	}

	public Collection<Node> whatsNext(Node node) {
		assert (m_nodes.contains(node));
		int index = m_nodes.indexOf(node);
		if (index < m_nodes.size() - 1) {
			Collection<Node> next = new TreeSet<Node>();
			next.add(m_nodes.get(index + 1));
			return next;
		} else {
			if (getParent() != null){
				return getParent().whatsNext(this);
			}
			return new TreeSet<Node>();
		}
	}

	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, Node node, boolean calledFromAbove,
			FunctionCallStatus functionCallStatus) {

		if (!calledFromAbove) {
			return super.checkBackTrail(referenceTypes, node, calledFromAbove, functionCallStatus);
		}
		return false;
	}

	public Collection<Node> whatsBefore(Node node) {
		assert(m_nodes.contains(node));
		int index = m_nodes.indexOf(node);
		if(index > 0){
			Collection<Node> before = new TreeSet<Node>();
			before.add(m_nodes.get(index-1));
			return before;
		} else {
			if(getParent()!= null){
				return getParent().whatsBefore(this);
			} else {
				return new TreeSet<Node>();
			}
		}
	}

	public List<Node> getImmediateChildren() {
		List<Node> list = new LinkedList<Node>();
		list.add(m_nodes.get(0));
		return list;
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
}
