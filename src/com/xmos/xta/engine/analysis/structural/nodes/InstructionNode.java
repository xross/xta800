package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.engine.analysis.structural.nodes.NodeAttributes.UnknownState;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

public class InstructionNode extends Node {
	private XtaInstruction m_inst;

	private long m_numCycles;
	
	public InstructionNode(XtaInstruction inst){
		super();
		m_inst = inst;
		m_numCycles = inst.getNumCycles();
	}

	public InstructionNode(InstructionNode node) {
		super();
		m_inst = node.m_inst;
		m_numCycles = node.m_numCycles;
	}
	
	public Node shallowCopy() {
		return new InstructionNode(this);
	}

	public Node deepCopy(Map<Integer, FunctionNode> startPcs) {
		return shallowCopy();
	}

    public String getTypeName() {
        return "instruction";
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

	public void setInstructionNumCycles(long numCycles) {
		if (m_inst.canPause()) {
			if (numCycles > m_inst.getNumCycles()) {
				m_numCycles = numCycles;		
			} else {								
			    m_numCycles = m_inst.getNumCycles();	
			}
		}					
	}

	public void unsetInstructionNumCycles() {
	    m_numCycles = m_inst.getNumCycles();
		getNodeAttributes().setUnknownState(UnknownState.UNKNOWN);				    
	}
	
	public long getInstructionNumCycles() {
		return m_numCycles;
	}
	
	public void setInstructionTimeTaken(double timeTaken) {
    	int threadFactor = (m_inst.getConfig().m_numThreads > 4) ? m_inst.getConfig().m_numThreads : 4;		
       	setInstructionNumCycles((int)((timeTaken * m_inst.getConfig().m_frequency) / (threadFactor * 1000)));
	}

	public void unsetInstructionTimeTaken() {
		unsetInstructionNumCycles();
	}
	
	public double getInstructionTimeTaken() {
    	int threadFactor = (m_inst.getConfig().m_numThreads > 4) ? m_inst.getConfig().m_numThreads : 4;
        return ((double)(threadFactor * 1000) / (double)m_inst.getConfig().m_frequency) * m_numCycles;
	}

	public XtaInstruction getXtaInstruction() {
		return m_inst;
	}
	
	// --------------------------------------------------------------------- //
	// Statistics Gathering Methods
	// --------------------------------------------------------------------- //
	public double getTimeTakenRelative(Case c) {
		if (!m_isAbsolute) {
		    return getInstructionTimeTaken();
		}
		return 0;
	}

	public double getTimeTakenAbsolute(Case c) {
		if (m_isAbsolute) {
		    return getInstructionTimeTaken();
		}
		return 0;
	}

	public long getNumCyclesRelative(Case c) {
		return getInstructionNumCycles();
	}

	public long getNumCyclesAbsolute(Case c) {
		return getInstructionNumCycles();
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
