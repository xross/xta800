package com.xmos.xta.core.engine.analysis.structural;

import com.xmos.libs.controlflow.nodes.CfgNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class CfgContainerNode extends CfgNode {
	private Node m_node;

	public CfgNode clone() {
		return new CfgContainerNode(m_node.copyRouteStructure());
	}
	
    public String getTypeName() {
        return "CfgContainerNode";
    }

	public CfgContainerNode(Node node) {
		m_node = node;		
	}
	
	public Node getNode() {
		return m_node;
	}
}
