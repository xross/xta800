package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.controlflow.nodes.CfgNullNode;
import com.xmos.libs.core.memorymonitor.MemoryMonitor;
import com.xmos.libs.core.memorymonitor.MemoryMonitorException;
import com.xmos.libs.core.utils.NonIteratableSet;
import com.xmos.xta.core.engine.analysis.structural.CfgContainerNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.NullNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.SequenceNode;

public class SequencePatternMatcher extends PatternMatcher {

    public SequencePatternMatcher(MemoryMonitor memoryMonitor) {
        super(memoryMonitor);
    }

    public boolean isMatch(CfgNode currentNode,
            Collection<CfgNode> workingSet, CfgNode treeStart) throws MemoryMonitorException {

        m_treeStart = treeStart;
        LinkedList<CfgNode> pathList = new LinkedList<CfgNode>();
        pathList.add(currentNode);
        if (isStart(currentNode)) {
            while (continueDown(currentNode, workingSet, pathList)) {
                currentNode = currentNode.getNextNode();
                pathList.add(currentNode);
            }
        }

        CfgNode lastNode = pathList.getLast();
        if ((lastNode instanceof CfgNullNode) && pathList.size() > 1) {
            CfgNode.disconnect(lastNode, pathList.get(pathList.size()-2));
            pathList.removeLast();
            workingSet.remove(lastNode);
        }

        if (pathList.size() > 1) {
            assert(pathSanityCheck(pathList));
            updateGraph(pathList, workingSet, treeStart);
            return true;
        } else {
            return false;
        }
    }

    private boolean isStart(CfgNode node){
        if (node == m_treeStart){
            return true;

        } else if (node.getPrevNodes().size() ==  0) {
            return true;

        } else if (node.getPrevNodes().size() ==  1) {
            CfgNode prev = node.getPrevNode();
            if (prev.getNextNodes().size() > 1) {
                return true;
            } else {
                return false;
            }
        } else if (node.getPrevNodes().size() > 1) {
            return true;

        } else {
            return false;
        }
    }

    private boolean isEnd(CfgNode node, Collection<CfgNode> workingSet, LinkedList<CfgNode> pathList) {

        if (node.getNextNodes().size() ==  0){
            return true;
        } else if (node.getNextNodes().size() ==  1) {
            CfgNode next = node.getNextNode();

            if ((next instanceof CfgNullNode) && pathList.size() + 1 < workingSet.size()) {
                return true;
            }

            if(next == m_treeStart){
                return true;
            }

            if (next.getPrevNodes().size() > 1) {
                return true;
            } else {
                return false;
            }
        }

        //FIXME - i dont think this can be hit
        else if (node.getNextNodes().size() > 1) {
            return true;
        } else {
            return false;
        }
    }

    private boolean continueDown(CfgNode node, Collection<CfgNode> workingSet, LinkedList<CfgNode> pathList){
        if (node.getNextNodes().size() == 1 && !isEnd(node, workingSet, pathList) &&
            !node.dominatorsContain(node.getNextNode())) {
            return true;
        }
        return false;
    }

    private boolean pathSanityCheck(LinkedList<CfgNode> pathList) {
        Set<CfgNode> seen = new NonIteratableSet<CfgNode>();

        if (pathList.size() < 2) {
            return false;
        }

        for (int i = 0; i < pathList.size() - 2; i++) {
            CfgNode node = pathList.get(i);

            if (seen.contains(node)) {
                return false;
            }

            if (node.getNextNodes().size() != 1) {
                return false;
            }

            if (node.getNextNode() != pathList.get(i + 1)) {
                return false;
            }

            seen.add(node);
        }
        return true;
    }

    protected void updateGraph(LinkedList<CfgNode> pathList, Collection<CfgNode> workingSet, CfgNode treeStart) {

        CfgNode path = createStructuralNode(pathList);

        if (pathList.contains(treeStart)) {
            m_treeStart = path;
        } else {
            m_treeStart = treeStart;
        }

        CfgNode startNodeInPath = pathList.getFirst();
        CfgNode endNodeInPath = pathList.getLast();

        for (CfgNode prev : startNodeInPath.getPrevNodes()) {
            path.addPrevNode(prev);

            prev.removeNextNode(startNodeInPath);
            //prev.getNextNodes().remove(startNodeInPath);
            prev.addNextNode(path);
        }
        for (CfgNode next : endNodeInPath.getNextNodes()) {
            path.addNextNode(next);

            next.removePrevNode(endNodeInPath);
            next.addPrevNode(path);
        }

        workingSet.removeAll(pathList);
        workingSet.add(path);
    }

    public boolean usesDominatorTree() {
        return true;
    }

    private CfgNode createStructuralNode(LinkedList<CfgNode> pathList) {

        Node path = null;
        boolean allNodesNull = true;
        for (CfgNode node : pathList) {
            if (!(node instanceof CfgNullNode)) {
                allNodesNull = false;
            }
        }
        if (allNodesNull) {
            path = new NullNode();
        } else {
            List<Node> strPathNodes = getStructuralNodes(pathList);
            path = new SequenceNode(strPathNodes);
        }
        return new CfgContainerNode(path);
    }
}
