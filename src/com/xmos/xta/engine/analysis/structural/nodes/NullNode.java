package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NullNodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class NullNode extends IBufferNode {
	private NullNodeAttributes m_attributes;	
	public NullNode(){
		super();
		m_attributes = new NullNodeAttributes(this);
	}
	
	public String toString(){
		return "NullNode" + m_id;
	}
	
	
	public Node deepCopy(Map<Integer, FunctionNode> startPcs, Engine engine) {
		return new NullNode();
	}

	public NodeAttributes getAttributes() {
		return m_attributes;	
	}

	public List<Node> getChildren() {
		return new LinkedList<Node>();
	}

	public Collection<Node> getContainedNodes() {
		return new LinkedList<Node>();
	}

	public long getError(Case c) {
		return 0;
	}

	public List<Node> getImmediateChildren() {
		return new LinkedList<Node>();
	}

	public void getInstructionStream(Case c, List<InstructionNode> instructions, long iteration) {
		
	}

	public LinkedList<XtaInstruction> getInstructions() {
		return new LinkedList<XtaInstruction>();
	}

	public Set<Integer> getPCs() {
		return new TreeSet<Integer>();
	}

	public int getStartPc() {
		return 0;
	}

	public String getTypeName() {
		return "null";
	}

	public Node shallowCopy() {
		return new NullNode();
	}

	public void traverse(int depth, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundTerminalNode(this, depth))
			return;
	}
	
	public Collection<Node> whatsBefore(Node node) {
		if(getParent() != null){
			return getParent().whatsBefore(this);
		} else {
			return new TreeSet<Node>();
		}
	}

	public Collection<Node> whatsNext(Node node) {
		if (getParent() != null){
			return getParent().whatsNext(this);
		}
		return new TreeSet<Node>();
	}


	public void insertFnops() {
		
	}

	public boolean isNullNode() {
		return true;
	}

}
