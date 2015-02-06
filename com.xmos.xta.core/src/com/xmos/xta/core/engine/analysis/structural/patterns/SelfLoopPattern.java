package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;

import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.memorymonitor.MemoryMonitor;
import com.xmos.libs.core.memorymonitor.MemoryMonitorException;
import com.xmos.xta.core.engine.analysis.structural.CfgContainerNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InfiniteSelfLoopNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.SelfLoopNode;

public class SelfLoopPattern extends PatternMatcher {

    public SelfLoopPattern(MemoryMonitor memoryMonitor) {
        super(memoryMonitor);
    }

    public boolean isMatch(CfgNode currentNode,
            Collection<CfgNode> workingSet, CfgNode treeStart) throws MemoryMonitorException {

        m_treeStart = treeStart;

        if (currentNode.getNextNodes().contains(currentNode)) {
            assert(currentNode.getPrevNodes().contains(currentNode));
            CfgNode loop = createStructuralNode(currentNode, workingSet);

            if (m_treeStart == currentNode) {
                m_treeStart = loop;
            }

            currentNode.removeNextNode(currentNode);
            currentNode.removePrevNode(currentNode);

            for (CfgNode next : currentNode.getNextNodes()) {
                    next.removePrevNode(currentNode);
                    next.addPrevNode(loop);
                    loop.addNextNode(next);
            }

            for (CfgNode prev : currentNode.getPrevNodes()) {
                    prev.removeNextNode(currentNode);
                    prev.addNextNode(loop);
                    loop.addPrevNode(prev);
            }

            assert(workingSet.contains(currentNode));
            workingSet.remove(currentNode);
            workingSet.add(loop);

            return true;
        }
        return false;
    }

    public boolean usesDominatorTree() {
        return false;
    }

    private CfgNode createStructuralNode(CfgNode currentNode, Collection<CfgNode> workingSet) {

        CfgNode cfgNode = null;
        Node strNode = getStructuralNode(currentNode);

        if (currentNode.getNextNodes().size() == 1) {
            Node structuralNode = new InfiniteSelfLoopNode(strNode);
            cfgNode = new CfgContainerNode(structuralNode);

            CfgNode nullNode = CommonMethods.findNullNode(workingSet);
            if (nullNode != null) {
                cfgNode.connectNext(nullNode);
            }

        } else {
            Node structuralNode = new SelfLoopNode(strNode);
            cfgNode = new CfgContainerNode(structuralNode);
        }
        return cfgNode;
    }
}
