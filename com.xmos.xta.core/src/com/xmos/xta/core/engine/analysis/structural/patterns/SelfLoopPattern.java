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

    public boolean usesDominatorTree() {
        return false;
    }

    public boolean isMatch(CfgNode headNode, Collection<CfgNode> workingSet, CfgNode treeStart) throws MemoryMonitorException {

        m_treeStart = treeStart;

        if (headNode.getNextNodes().contains(headNode)) {
            assert(headNode.getPrevNodes().contains(headNode));

            CfgNode loop = createSelfLoopStructuralNode(headNode);
            // We currently explicitly use CfgNullNode as a terminating leaf.
            if (headNode.getNextNodes().size() == 1) // No next node.
                loop.connectNext(CommonMethods.findNullNode(workingSet));

            if (m_treeStart == headNode)
                m_treeStart = loop;

            CfgNode.replaceNode(headNode, loop);
            workingSet.remove(headNode);
            workingSet.add(loop);
            return true;
        }
        return false;
    }

    private CfgNode createSelfLoopStructuralNode(CfgNode currentNode) {
        Node strNode = getStructuralNode(currentNode);
        Node structuralNode = (currentNode.getNextNodes().size() == 1)?
                new InfiniteSelfLoopNode(strNode) :
                new SelfLoopNode(strNode);
        return new CfgContainerNode(structuralNode);
    }

}
