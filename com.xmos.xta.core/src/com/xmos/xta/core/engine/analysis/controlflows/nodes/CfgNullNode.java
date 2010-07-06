package com.xmos.xta.core.engine.analysis.controlflows.nodes;

public class CfgNullNode extends CfgNode {
	public CfgNode clone() {
		return new CfgNullNode();
	}

    public String getTypeName() {
        return "CfgNullNode";
    }	
}
