package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;

import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.controlflow.nodes.CfgNullNode;

public class CommonMethods {

    public static CfgNullNode findNullNode(Collection<CfgNode> workingSet){
        for (CfgNode node : workingSet){
            if (node instanceof CfgNullNode) {
                return (CfgNullNode)node;
            }
        }
        // Didn't find one so make a new one!!!!
        CfgNullNode nullNode = new CfgNullNode();
        assert(nullNode != null);
        // And add it to all the ends.
        for (CfgNode node : workingSet)
            if (node.getNextNodes().size() == 0)
                node.connectNext(nullNode);
        //must add all other terminals to it
        workingSet.add(nullNode);
        return nullNode;
    }
}
