package com.xmos.xta.core.engine.analysis.controlflows.nodes;

import com.xmos.xta.core.engine.analysis.binary.XtaInstruction;

public class CfgInstructionNode extends CfgNode {
	private XtaInstruction m_inst;
	
	public CfgNode clone() {
		return new CfgInstructionNode(this);
	}
	
    public String getTypeName() {
        return "CfgInstructionNode";
    }
	
	public CfgInstructionNode(XtaInstruction inst){
		super();
		m_inst = inst;
	}
	
	public CfgInstructionNode(CfgInstructionNode node) {
		super(node);
		m_inst = node.m_inst;
	}
	
	public String toString() {
		return getTypeName() + getId();
	}
	
	public XtaInstruction getXtaInstruction() {
		return m_inst;
	}

	public long getPc() {
		return m_inst.getPc();
	}	
}
