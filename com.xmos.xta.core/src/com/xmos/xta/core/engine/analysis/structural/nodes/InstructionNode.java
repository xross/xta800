package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.InstructionNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.types.ReferenceType;

public class InstructionNode extends Node {
	private InstructionNodeAttributes m_attributes;	
	private XtaInstruction m_inst;
	
	public InstructionNode(XtaInstruction inst){
		super();
		m_inst = inst;
		m_attributes = new InstructionNodeAttributes(this);
	}

	public InstructionNode(InstructionNode node) {
		super(node);
		m_inst = node.m_inst;
		m_attributes = new InstructionNodeAttributes(this);
	}
	
	
	public Node shallowCopy() {
		return new InstructionNode(this);
	}

	public Node deepCopy(Map<Long, FunctionNode> startPcs, Engine engine) {
		InstructionNode in = new InstructionNode(this);
		in.m_attributes = new InstructionNodeAttributes(in);
		return in;
	}

	public Node deepCopy() {
		InstructionNode in = new InstructionNode(this);
		in.m_attributes = new InstructionNodeAttributes(in);
		return in;
	}
	
	public String toString() {
		return String.format("%s (0x%x)", super.toString(), m_inst.getPc());
	}
	
    public String getTypeName() {    	
        return "instruction";
    }
	
    public NodeAttributes getAttributes() {
    	return m_attributes;
    }

	public LinkedList<XtaInstruction> getInstructions() {
		LinkedList<XtaInstruction> inst = new LinkedList<XtaInstruction>();
		inst.add(m_inst);
		return inst;
	}

	public Set<Long> getPCs() {
		Set<Long> pcs = new TreeSet<Long>();
		pcs.add(m_inst.getPc());
		return pcs;
	}

	public long getStartPc() {
		return m_inst.getPc();
	}

	public boolean isFunction() {
		return false;
	}

	public void traverse(Stack<Node> ancestors, NodeTraverser nodeTraverser) {
		nodeTraverser.foundInstructionNode(this, ancestors);
	}

	public List<Node> getIndirectChildren() {
		return new LinkedList<Node>();
	}

	public boolean isInstructionNode() {
		return true;
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
	
	public long getError(Case c) {
		return 0;
	}

	public Collection<NodeDescriptor> whatsNext(NodeDescriptor nodeDescriptor) {
		return null;
	}

	public Collection<NodeDescriptor> whatsBefore(NodeDescriptor nodeDescriptor) {
		assert(false);
		return null;
	}

	public List<NodeDescriptor> getDirectChildren(Stack<Node> ancestors) {
		assert(false);
		return null;
	}

	public Set<NodeDescriptor> getEndInstructions(Stack<Node> ancestors) {
		Set<NodeDescriptor> set = new TreeSet<NodeDescriptor>();
		set.add(new NodeDescriptor(this, ancestors));
		return set;
	}

	public TimeDistributionBuckets getTimeSpread() {
		return new TimeDistributionBuckets(m_attributes.getFixedTime().getCycles());
	}

	public void replaceChild(Node oldNode, Node newNode) {
	}

	public void replaceChildren(List<Node> children) {
	}
	
	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, 
			Stack<Node> ancestors, Node node, boolean calledFromAbove) {
		
		if (referenceTypes.peek().getPc() == m_inst.getPc()) {
			referenceTypes.pop();
			if (referenceTypes.empty()) {
				return true;
			}
		}
		return super.checkBackTrail(referenceTypes, ancestors, node, calledFromAbove);
	}
}
