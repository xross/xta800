package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.Set;

import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.memorymonitor.MemoryMonitor;
import com.xmos.libs.core.memorymonitor.MemoryMonitorException;
import com.xmos.libs.core.utils.NonIteratableSet;

public class PanicTransformer extends PatternMatcher {

    public PanicTransformer(MemoryMonitor memoryMonitor) {
        super(memoryMonitor);
    }

    public boolean isMatch(CfgNode currentNode,
            Collection<CfgNode> workingSet, CfgNode treeStart) throws MemoryMonitorException {

        m_treeStart = treeStart;
        if (currentNode.getNextNodes().size() < 2) {
            return false;
        }
        return explore(currentNode, new NonIteratableSet<CfgNode>(),
                currentNode.getDominators(), currentNode, workingSet);
    }

    private boolean explore(CfgNode node, NonIteratableSet<CfgNode> seen, Set<CfgNode> dominators,
            CfgNode start, Collection<CfgNode> workingSet) throws MemoryMonitorException {

        seen.add(node);
        if (node.getDominators().containsAll(dominators) &&
            node.getDominators().size() == (dominators.size() + 1)) {

            checkMemory();
            if (node.getNextNodes().size() > 1 && node.getPrevNodes().size() > 1) {
                CfgNode.wedgeDown(node, workingSet);
                workingSet.remove(node);
                return true;
            }
        }
        boolean found = false;
        for (CfgNode next : node.getNextNodes()) {
            checkMemory();
            if (!seen.contains(next)) {
                found = explore(next, seen, dominators, start, workingSet);
            }
            if(found)
                return true;
        }
        return false;
    }

    public boolean usesDominatorTree() {
        return true;
    }
}
