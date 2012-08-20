package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.xmos.libs.core.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.InstructionNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverserWithAncestors;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;

public class InstructionNode extends Node {
	private InstructionNodeAttributes m_attributes;
	private Instruction m_inst;

	public InstructionNode(CfgInstructionNode node) {
		super(node);
		m_inst = node.getInstruction();
		//m_attributes = new InstructionNodeAttributes(this);
	}

	public InstructionNode(InstructionNode node) {
		super(node);
		m_inst = node.m_inst;
		//m_attributes = new InstructionNodeAttributes(this);
	}
	
	public String toString(){
		return this.getInstruction().toString();
	}
	
	public Node copyRouteStructure() {
		InstructionNode in = new InstructionNode(this);
		in.m_attributes = new InstructionNodeAttributes(in);
		return in;
	}

    public String getTypeName() {
        return "instruction";
    }

    public NodeAttributes getAttributes() {
    	if(m_attributes==null)
    		m_attributes = new InstructionNodeAttributes(this);
    	return m_attributes;
    }

	public LinkedList<Instruction> getInstructions() {
		LinkedList<Instruction> inst = new LinkedList<Instruction>();
		inst.add(m_inst);
		return inst;
	}

	public long getStartPc() {
		return m_inst.getPc();
	}

	public TraverseStatus traverse(Stack<Node> ancestors, NodeTraverserWithAncestors nodeTraverser) {
		TraverseStatus status = nodeTraverser.foundInstructionNode(this, ancestors);
		if (status == TraverseStatus.ABORT) {
			return status;
		}
		return TraverseStatus.CONTINUE;
	}
	
	public TraverseStatus traverse(NodeTraverser nodeTraverser) {
		TraverseStatus status = nodeTraverser.foundInstructionNode(this);
		if (status == TraverseStatus.ABORT) {
			return status;
		}
		return TraverseStatus.CONTINUE;
	}
	public boolean isInstructionNode() {
		return true;
	}

	public String getDescription() {
		return String.format("0x%x: %s %s",
				m_inst.getPc(),
				m_inst.getMnemonic(),
				m_inst.getOperandString());
	}

	public Instruction getInstruction() {
		return m_inst;
	}

	public TimeDistributionBuckets getTimeSpread() {
		return new TimeDistributionBuckets(m_attributes.getFixedTime().getCycles());
	}

	public List<Node> iChildren() {
		List<Node> children = new LinkedList<Node>();
		return children;
	}

	public void buildListDown(List<Node> nodeList) {
		nodeList.add(this);
	}

	public void buildListUp(List<Node> nodeList, Node start) {
		assert(start == null);
		if(m_parent!= null){
			m_parent.buildListUp(nodeList, this);
		}
		nodeList.add(this);
	}

	public long getPC() {
		return m_inst.getPc();
	}
}
