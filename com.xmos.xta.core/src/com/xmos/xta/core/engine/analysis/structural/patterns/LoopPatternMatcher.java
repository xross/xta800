package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.Set;

import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.memorymonitor.MemoryMonitor;
import com.xmos.libs.core.memorymonitor.MemoryMonitorException;
import com.xmos.libs.core.utils.IteratableSet;
import com.xmos.xta.core.engine.analysis.structural.CfgContainerNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InfiniteLoopNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.LoopNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class LoopPatternMatcher extends PatternMatcher {

    public LoopPatternMatcher(MemoryMonitor memoryMonitor) {
        super(memoryMonitor);
    }

    public boolean usesDominatorTree() {
        return false;
    }

    public boolean isMatch(CfgNode headNode, Collection<CfgNode> workingSet, CfgNode treeStart) throws MemoryMonitorException {
        assert(!headNode.getNextNodes().contains(headNode)); // Should have been picked up by SelfLoopPattern.

        m_treeStart = treeStart;

        // A loop pattern is when one or more nodes are doubly attached to a single node (a node is both next and previous).
        Set<CfgNode> intersection = new IteratableSet<CfgNode>(headNode.getNextNodes());
        intersection.retainAll(headNode.getPrevNodes());

        // But we only want nodes that are not attached else where.
        Set<CfgNode> loops = new IteratableSet<CfgNode>();
        for (CfgNode loop : intersection) {
            assert(loop.getNextNodes().contains(headNode));
            if (loop.getNextNodes().size() == 1 && loop.getPrevNodes().size() == 1)
                loops.add(loop);
        }

        if (!loops.isEmpty() && !loops.contains(treeStart)) {

            CfgNode loopNode = createLoopStructuralNode(headNode, loops);
            // We currently explicitly use CfgNullNode as a terminating leaf.
            if (headNode.getNextNodes().size() == loops.size()) // No next node.
                loopNode.connectNext(CommonMethods.findNullNode(workingSet));

            if (headNode == treeStart)
                m_treeStart = loopNode;

            // N.B. Leave the headNode-loops connections intact during the replace.
            CfgNode.replaceNode(headNode, loopNode, loops);

            workingSet.remove(headNode);
            workingSet.removeAll(loops);
            workingSet.add(loopNode);
            return true;
        }
        return false;
    }

    private CfgNode createLoopStructuralNode(CfgNode headNode, Set<CfgNode> loops) {
        Node strHeader = getStructuralNode(headNode);
        Collection<Node> strLoops = getStructuralNodes(loops);
        Node structuralNode = (headNode.getNextNodes().size() == loops.size())?
                new InfiniteLoopNode(strLoops, strHeader) :
                new LoopNode(strLoops, strHeader);
        return new CfgContainerNode(structuralNode);
    }
}
