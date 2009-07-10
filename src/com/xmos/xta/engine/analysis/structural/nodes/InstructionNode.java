package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.InstructionNodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

public class InstructionNode extends Node {
	private InstructionNodeAttributes m_attributes;	
	private XtaInstruction m_inst;
	
	public InstructionNode(XtaInstruction inst){
		super();
		m_inst = inst;
		m_attributes = new InstructionNodeAttributes(this);
	}

	public InstructionNode(InstructionNode node) {
		super();
		m_inst = node.m_inst;
		m_attributes = new InstructionNodeAttributes(this);
	}
	
	public Node shallowCopy() {
		return new InstructionNode(this);
	}

	public Node deepCopy(Map<Integer, FunctionNode> startPcs, Engine engine) {
		InstructionNode in = new InstructionNode(this);
		in.m_attributes = new InstructionNodeAttributes(in);
		return in;
	}
	
	public Node deepCopy() {
		InstructionNode in = new InstructionNode(this);
		in.m_attributes = new InstructionNodeAttributes(in);
		return in;
	}
	
    public String getTypeName() {    	
        return "instruction";
    }
	
    public NodeAttributes getAttributes() {
    	return m_attributes;
    }
    
	public String toString() {
		return "instruction" + m_id;
		//return m_inst.toString();
	}

	public Collection<Node> getContainedNodes() {
		Collection<Node> containedNodes = new TreeSet<Node>();
		containedNodes.add(this);
		return containedNodes;
	}

	public LinkedList<XtaInstruction> getInstructions() {
		LinkedList<XtaInstruction> inst = new LinkedList<XtaInstruction>();
		inst.add(m_inst);
		return inst;
	}

	public Set<Integer> getPCs() {
		Set<Integer> pcs = new TreeSet<Integer>();
		pcs.add(m_inst.getPc());
		return pcs;
	}

	public int getStartPc() {
		return m_inst.getPc();
	}

	public boolean isFunction() {
		return false;
	}

	public void traverse(int depth, NodeTraverser nodeTraverser) {
		nodeTraverser.foundInstructionNode(this, depth);
	}

	public List<Node> getChildren() {
		return new LinkedList<Node>();
	}

	public Collection<Node> whatsNext(Node node) {
		return null;
	}

	public boolean isInstructionNode() {
		return true;
	}

	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, Node node, boolean calledFromAbove,
			FunctionCallStatus functionCallStatus) {

		ReferenceType referenceType = referenceTypes.peek();

		boolean isMatch = false;
		if (referenceType.getPc() == m_inst.getPc()) {
			if (referenceType.getType() == ReferenceType.Type.CALL) {
				if (functionCallStatus.getStatus()) {
					isMatch = true;
				}
			} else {
				isMatch = true;
			}
		}

		if (isMatch) {
			referenceTypes.pop();
			if (referenceTypes.empty()) {
				return true;
			}
		}

		functionCallStatus.setStatus(false);
		return super.checkBackTrail(referenceTypes, node, calledFromAbove, functionCallStatus);
	}

	public String getDescription() {
		return String.format("0x%x: %s %s", 
				m_inst.getPc(),
				m_inst.getNmenonic(),
				m_inst.getOperandString());
	}
	
	public XtaInstruction getXtaInstruction() {
		return m_inst;
	}
	
	public void getInstructionStream(Case c, List<InstructionNode> instructions) {
		assert(false);
	}

	public long getError(Case c) {
		return 0;
	}

	public Collection<Node> whatsBefore(Node node) {
		assert(false);
		return null;
	}

	public List<Node> getImmediateChildren() {
		assert(false);
		return null;
	}

	public TimeDistributionBuckets getTimeSpread() {
		System.out.println("hit an instruction Node");
		assert(false);
		return null;
	}

}
