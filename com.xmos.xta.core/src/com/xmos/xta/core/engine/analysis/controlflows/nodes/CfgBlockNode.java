package com.xmos.xta.core.engine.analysis.controlflows.nodes;

import java.util.List;

public class CfgBlockNode extends CfgNode {
	private List<CfgInstructionNode> m_insts;
	
	public CfgNode clone() {
		return new CfgBlockNode(this);
	}

    public String getTypeName() {
        return "CfgBlock";
    }
	
	public CfgBlockNode(CfgBlockNode node) {
		super(node);
		m_insts = node.m_insts;
	}
	
	public CfgBlockNode(List<CfgInstructionNode> insts) {
		super();
		m_insts = insts;
	}
	
	public List<CfgInstructionNode> getInstructionList(){
		return m_insts;
	}		
}
