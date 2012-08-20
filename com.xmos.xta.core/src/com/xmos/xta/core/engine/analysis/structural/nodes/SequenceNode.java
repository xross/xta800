package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverserWithAncestors;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;

public class SequenceNode extends Node {
	private NodeAttributes m_attributes;
	List<Node> m_nodes;

	public SequenceNode(List<Node> nodes) {
		super();
		assert (nodes != null);
		m_nodes = nodes;
	}

	public SequenceNode() {
		super();
		m_nodes = new LinkedList<Node>();
	}

	public Node copyRouteStructure() {
		SequenceNode pn = new SequenceNode();
		for (Node path : m_nodes) {
			Node clone = path.copyRouteStructure();
			pn.m_nodes.add(clone);
		}
		return pn;
	}
	public boolean isSequenceNode() {
		return true;
	}
    public String getTypeName() {
        return "seq";
    }

    public NodeAttributes getAttributes() {
    	if(m_attributes==null)
    		m_attributes = new NodeAttributes(this);
    	return m_attributes;
    }

	public LinkedList<Instruction> getInstructions() {
		LinkedList<Instruction> list = new LinkedList<Instruction>();
		for (Node node : m_nodes) {
			list.addAll(node.getInstructions());
		}
		return list;
	}

	public long getStartPc() {
		return m_nodes.get(0).getStartPc();
	}

	public TraverseStatus traverse(Stack<Node> ancestors, NodeTraverserWithAncestors nodeTraverser) {
		TraverseStatus status = nodeTraverser.foundSequenceNode(this, ancestors);

		if (status == TraverseStatus.CONTINUE) {
			ancestors.push(this);
			for (Node node : m_nodes) {
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
	
	public TraverseStatus traverse(NodeTraverser nodeTraverser) {
		TraverseStatus status = nodeTraverser.foundSequenceNode(this);
		if (status == TraverseStatus.CONTINUE) {
			for (Node node : m_nodes) {
				status = node.traverse(nodeTraverser);
				if (status != TraverseStatus.CONTINUE) {
					break;
				}
			}
		}
		if (status == TraverseStatus.ABORT) {
			return status;
		}
		return TraverseStatus.CONTINUE;
	}

	public TimeDistributionBuckets getTimeSpread() {
		TimeDistributionBuckets tdb = new TimeDistributionBuckets();
		List<TimeDistributionBuckets> tsbPath = new LinkedList<TimeDistributionBuckets>();
		for (Node node : m_nodes) {
			tsbPath.add(node.getTimeSpread());
		}
		tdb.add(tsbPath);
		return tdb;
	}

	public List<Node> getPath() {
		return m_nodes;
	}

	public boolean canReplaceChildren() {
		return true;
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

	//below here the methods have been verified useful

	public List<Node> iChildren() {
		return m_nodes;
	}

	public void buildListUp(List<Node> nodeList, Node start) {
		if(m_parent!= null){
			m_parent.buildListUp(nodeList, this);
		}
		for(Node node : m_nodes){
			if(node == start) break;
			node.buildListDown(nodeList);
		}
	}

	public void buildListDown(List<Node> nodeList) {
		for(Node node : m_nodes){
			node.buildListDown(nodeList);
		}

	}



}
