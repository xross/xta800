package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;

import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.memorymonitor.MemoryMonitor;
import com.xmos.libs.core.memorymonitor.MemoryMonitorException;

public class WedgeStartTransformer extends PatternMatcher {

    public WedgeStartTransformer(MemoryMonitor memoryMonitor) {
        super(memoryMonitor);
    }

    public boolean isMatch(CfgNode currentNode,
            Collection<CfgNode> workingSet, CfgNode treeStart) throws MemoryMonitorException {
        m_treeStart = treeStart;
        if (currentNode != treeStart || workingSet.size() < 2)
            return false;

        // Create a new start-node, leaving the original start-node within the graph.
        // The new start-node will have no previous nodes.
        CfgNode newStartNode = currentNode.copy();
        for (CfgNode next : currentNode.getNextNodes()) {
            newStartNode.connectNext(next);
        }
        workingSet.add(newStartNode);
        m_treeStart = newStartNode;
        return true;
    }

    public boolean usesDominatorTree() {
        return true;
    }
}
