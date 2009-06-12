package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

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

	public Node deepCopy(Map<Integer, FunctionNode> startPcs) {
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
	}

	public Collection<Node> getContainedNodes() {
		Collection<Node> containedNodes = new HashSet<Node>();
		containedNodes.add(this);
		return containedNodes;
	}

	public LinkedList<XtaInstruction> getInstructions() {
		LinkedList<XtaInstruction> inst = new LinkedList<XtaInstruction>();
		inst.add(m_inst);
		return inst;
	}

	public Set<Integer> getPCs() {
		Set<Integer> pcs = new HashSet<Integer>();
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

	public XtaInstruction getXtaInstruction() {
		return m_inst;
	}
	
	// --------------------------------------------------------------------- //
	// Statistics Gathering Methods
	// --------------------------------------------------------------------- //
	public double getTimeTakenRelative(Case c) {
		if (!m_attributes.getIsAbsolute()) {
		    return m_attributes.getInstructionTimeTaken();
		}
		return 0;
	}

	public double getTimeTakenAbsolute(Case c) {
		if (m_attributes.getIsAbsolute()) {
		    return m_attributes.getInstructionTimeTaken();
		}
		return 0;
	}

	public long getNumCyclesRelative(Case c) {
		return m_attributes.getInstructionNumCycles();
	}

	public long getNumCyclesAbsolute(Case c) {
		return m_attributes.getInstructionNumCycles();
	}

	public void getInstructionStream(Case c, List<XtaInstruction> instructions) {
		assert(false);
	}

	public long getNumInstructionsRelative(Case c) {
		return 1;
	}

	public long getNumInstructionsAbsolute(Case c) {
		return 1;
	}

	public long getNumFNopsRelative(Case c) {
		assert(false);
		return 0;
	}

	public long getNumFNopsAbsolute(Case c) {
		assert(false);
		return 0;
	}

	public void updateFnops(IBufferVector bestVector, IBufferVector worstVector) {
		assert(false);
	}

	public void getInstructionStream(Case c, List<XtaInstruction> instructions, long iteration) {
		assert(false);
	}
}
