package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.analysis.binary.XtaInstruction;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.LoopNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverserWithAncestors;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;

public class LoopNode extends Node {
	private LoopNodeAttributes m_attributes;
	private Node m_loopHeader;
	private List<Node> m_loops;

	public LoopNode() {
		super();
		m_loopHeader = null;
		m_loops = new ArrayList<Node>();
		m_attributes = new LoopNodeAttributes(this);
	}

	public LoopNode(Set<Node> loops, Node base) {
		super();
		m_loopHeader = base;
		m_loopHeader.getAttributes().setIsLoopHeader(true);
		m_loops = new ArrayList<Node>();
		for (Node loop : loops) {
			m_loops.add(loop);
		}
		m_attributes = new LoopNodeAttributes(this);
	}

	public Node copyRouteStructure(Aborter aborter) throws AbortedException {
		LoopNode ln = new LoopNode();
		for (Node loop : m_loops) {
			Node clone = loop.copyRouteStructure(aborter);
			ln.m_loops.add(clone);
		}
		ln.m_loopHeader = m_loopHeader.copyRouteStructure(aborter);
		ln.m_loopHeader.getAttributes().setIsLoopHeader(true);
		ln.m_attributes = new LoopNodeAttributes(ln);
		return ln;
	}

    public String getTypeName() {
        return "loop";
    }

    public NodeAttributes getAttributes() {
    	return m_attributes;
    }

	public Node getLoopHeader() {
		return m_loopHeader;
	}

	public LinkedList<XtaInstruction> getInstructions() {
		LinkedList<XtaInstruction> insts = new LinkedList<XtaInstruction>(m_loopHeader.getInstructions());
		for (Node al : m_loops) {
			insts.addAll(al.getInstructions());
		}
		return insts;
	}

	public long getStartPc() {
		return m_loopHeader.getStartPc();
	}

	public TraverseStatus traverse(Stack<Node> ancestors, NodeTraverserWithAncestors nodeTraverser) {
		TraverseStatus status = nodeTraverser.foundLoopNode(this, ancestors);
		if (status == TraverseStatus.CONTINUE) {
			ancestors.push(this);
			status = m_loopHeader.traverse(ancestors, nodeTraverser);
			if (status == TraverseStatus.CONTINUE) {
				for (Node node : m_loops) {
					status = node.traverse(ancestors, nodeTraverser);
					if (status != TraverseStatus.CONTINUE) {
						break;
					}
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
		TraverseStatus status = nodeTraverser.foundLoopNode(this);
		if (status == TraverseStatus.CONTINUE) {

			status = m_loopHeader.traverse( nodeTraverser);
			if (status == TraverseStatus.CONTINUE) {
				for (Node node : m_loops) {
					status = node.traverse( nodeTraverser);
					if (status != TraverseStatus.CONTINUE) {
						break;
					}
				}
			}
		}

		if (status == TraverseStatus.ABORT) {
			return status;
		}
		return TraverseStatus.CONTINUE;
	}

	public boolean isLoopNode() {
		return true;
	}

	public TimeDistributionBuckets getTimeSpread() {
		TimeDistributionBuckets tdb = new TimeDistributionBuckets();
		List<TimeDistributionBuckets> loopList = new LinkedList<TimeDistributionBuckets>();
		TimeDistributionBuckets loopHeaderBucket = m_loopHeader.getTimeSpread();
		loopHeaderBucket.repeat(m_attributes.getLoopIterations()+1);
		loopList.add(loopHeaderBucket);
		for(Node loop : m_loops){
			if(m_attributes.getLoopIterations() > 0){
				TimeDistributionBuckets b = loop.getTimeSpread();
				b.repeat(m_attributes.getLoopIterations());
				loopList.add(b);
			}
		}
		tdb.add(loopList);
		return tdb;
	}

	public boolean canReplaceChildren() {
		return true;
	}
	
	public void replaceChild(Node oldNode, Node newNode) {
		if (oldNode == m_loopHeader) {
			m_loopHeader = newNode;

		} else {
		    int index = m_loops.indexOf(oldNode);
		    m_loops.remove(index);
		    m_loops.add(index, newNode);
		}
	}

	public void replaceChildren(List<Node> children) {
		if (!children.isEmpty()) {
			m_loopHeader = children.get(0);
			for (int index = 1; index < children.size(); ++index) {
				m_loops.add(children.get(index));
			}
		}
	}

	//below here the methods have been verified useful

	public List<Node> iChildren() {
		List<Node> children = new LinkedList<Node>();
		children.add(m_loopHeader);
		for (Node node : m_loops) {
			children.add(node);
		}
		return children;
	}

	public void buildListUp(List<Node> nodeList, Node start) {
		if(m_parent!= null){
			m_parent.buildListUp(nodeList, this);
		}
		
		if(m_loops.contains(start)){
			m_loopHeader.buildListDown(nodeList);
		}
	}

	public void buildListDown(List<Node> nodeList) {
		m_loopHeader.buildListDown(nodeList);
		for(Node node : m_loops){
			node.buildListDown(nodeList);
		}
	}
}
