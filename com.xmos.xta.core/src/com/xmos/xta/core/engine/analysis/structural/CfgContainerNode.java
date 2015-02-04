package com.xmos.xta.core.engine.analysis.structural;

import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class CfgContainerNode extends CfgNode {

    private Node m_node;

    public CfgContainerNode copy() {
        return new CfgContainerNode(m_node.copyRouteStructure());
    }

    public CfgContainerNode(Node node) {
        m_node = node;
    }

    public String getTypeName() {
        return "CfgContainerNode";
    }

    public Node getNode() {
        return m_node;
    }
}
