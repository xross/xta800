package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.LinkedList;

import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.controlflow.nodes.CfgNullNode;
import com.xmos.libs.core.memorymonitor.MemoryMonitor;
import com.xmos.libs.core.memorymonitor.MemoryMonitorException;

public class NullRemover extends PatternMatcher {

    public NullRemover(MemoryMonitor memoryMonitor) {
        super(memoryMonitor);
    }

    public boolean isMatch(CfgNode currentNode,
            Collection<CfgNode> workingSet, CfgNode treeStart) throws MemoryMonitorException {

        m_treeStart = treeStart;
        if (currentNode instanceof CfgNullNode) {
            // Remove any CfgNullNode linked to it.
            //CfgNullNode next = (CfgNullNode)currentNode;
            LinkedList<CfgNode> pathList = new LinkedList<CfgNode>();
            //while (next.getNextNodes().size() == 1 &&
            //        next.getNextNode() instanceof CfgNullNode) {
            //
            //}

            assert(currentNode.getNextNodes().size() < 2);
            pathList.add(currentNode); // We will keep this one.
            while (!currentNode.getNextNodes().isEmpty() &&
                    currentNode.getNextNode() instanceof CfgNullNode) {
                currentNode = currentNode.getNextNode();
                assert(currentNode.getNextNodes().size() < 2);
                workingSet.remove(currentNode);
                pathList.add(currentNode);
            }
            if (pathList.size() > 1) {
                CfgNode.replaceRange(pathList.getFirst(), pathList.getLast(), pathList.getFirst());
                return true;
            }
        }
        return false;
    }

    public boolean usesDominatorTree() {
        return false;
    }
}