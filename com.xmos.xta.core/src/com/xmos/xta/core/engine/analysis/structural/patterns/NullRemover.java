package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;

import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.controlflow.nodes.CfgNullNode;
import com.xmos.libs.core.memorymonitor.MemoryMonitor;
import com.xmos.libs.core.memorymonitor.MemoryMonitorException;

public class NullRemover extends PatternMatcher {

    public NullRemover(MemoryMonitor memoryMonitor) {
        super(memoryMonitor);
    }

    public boolean isMatch(CfgNode currentNode, Collection<CfgNode> workingSet, CfgNode treeStart) throws MemoryMonitorException {
        m_treeStart = treeStart;
        if (currentNode instanceof CfgNullNode) {
            // Remove multiple CfgNullNodes.
            CfgNullNode next = (CfgNullNode)currentNode;
            while (next.getNextNodes().size() == 1 &&
                    next.getNextNode() instanceof CfgNullNode) {
                next = (CfgNullNode)next.getNextNode();
                workingSet.remove(next);
            }
            if (next != currentNode) {
                CfgNode.replaceRange(currentNode, next, currentNode);
                return true;
            }
        }
        return false;
    }

    public boolean usesDominatorTree() {
        return false;
    }
}