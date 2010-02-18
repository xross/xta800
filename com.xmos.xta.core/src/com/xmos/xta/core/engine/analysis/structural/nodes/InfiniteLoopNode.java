package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.InfiniteLoopNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser.TraverseStatus;
import com.xmos.xta.core.engine.instructions.XtaInstruction;

public class InfiniteLoopNode extends Node {
	private NodeAttributes m_attributes;
	private Node m_loopHeader;
	private List<Node> m_loops;

	public InfiniteLoopNode(InfiniteLoopNode loop) {
		super();
		m_loopHeader = loop.m_loopHeader;
		m_loops = new ArrayList<Node>(loop.m_loops);
		m_attributes = new InfiniteLoopNodeAttributes(this);
	}

	public InfiniteLoopNode() {
		super();
		m_loopHeader = null;
		m_loops = new ArrayList<Node>();
		m_attributes = new InfiniteLoopNodeAttributes(this);
	}

	public InfiniteLoopNode(Node loop, Node base) {
		super();
		m_loopHeader = base;
		m_loops = new ArrayList<Node>();
		m_loops.add(loop);
		m_attributes = new InfiniteLoopNodeAttributes(this);
	}

	public InfiniteLoopNode(Set<Node> loops, Node base) {
		super();
		m_loopHeader = base;
		m_loopHeader.getAttributes().setIsLoopHeader(true);
		m_loops = new ArrayList<Node>();
		for (Node loop : loops) {
			m_loops.add(loop);
		}
		m_attributes = new InfiniteLoopNodeAttributes(this);
	}

	public InfiniteLoopNode copyStructure() {
		return new InfiniteLoopNode(this);
	}

	public Node deepCopy(Aborter aborter) throws AbortedException {
		InfiniteLoopNode ln = new InfiniteLoopNode();
		for (Node loop : m_loops) {
			Node clone = loop.deepCopy(aborter);
			ln.m_loops.add(clone);
		}
		ln.m_loopHeader = m_loopHeader.deepCopy(aborter);
		ln.m_loopHeader.getAttributes().setIsLoopHeader(true);
		ln.m_attributes = new InfiniteLoopNodeAttributes(ln);
		return ln;
	}

    public String getTypeName() {
        return "infiniteloop";
    }

    public NodeAttributes getAttributes() {
    	return m_attributes;
    }

	public void addLoop(Node loop) {
		m_loops.add(loop);
	}

	public Node getLoopHeader() {
		return m_loopHeader;
	}

	public List<Node> getLoopBodies() {
		return m_loops;
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

	public TraverseStatus traverse(Stack<Node> ancestors, NodeTraverser nodeTraverser) {
		TraverseStatus status = nodeTraverser.foundInfiniteLoopNode(this, ancestors);
		if (status == TraverseStatus.CONTINUE) {
			ancestors.push(this);
			m_loopHeader.traverse(ancestors, nodeTraverser);
			for (Node node : m_loops) {
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

	public boolean isInfiniteLoopNode() {
		return true;
	}

	public TimeDistributionBuckets getTimeSpread() {
		TimeDistributionBuckets tdb = new TimeDistributionBuckets();
		tdb.setInfinite();
		return tdb;
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

	public Set<Node> structurGraphNext(Node childNode) {
		Set<Node> next = new TreeSet<Node>();
		if (m_loopHeader == childNode) {
			next.addAll(m_loops);
		} else {
			next.add(m_loopHeader);
		}
		return next;
	}

	public Set<InstructionNode> getInstructionNodes() {
		Set<InstructionNode> insts = new TreeSet<InstructionNode>();
		insts.addAll(m_loopHeader.getInstructionNodes());
		for (Node node : m_loops) {
			insts.addAll(node.getInstructionNodes());
		}
		return insts;
	}

	public Set<FunctionReferenceNode> getFunctionReferencesNodes() {
		Set<FunctionReferenceNode> fnRef = new TreeSet<FunctionReferenceNode>();
		addFunctionRefToRefSet(fnRef, m_loopHeader);
		for(Node node : m_loops){
			addFunctionRefToRefSet(fnRef, node);
		}
		return fnRef;
	}

	public void buildListUp(List<Node> nodeList,
			Stack<FunctionReferenceNode> stack, Node start) {
		if(m_parent!= null){
			m_parent.buildListUp(nodeList, stack, this);
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
