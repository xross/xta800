package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.controlflow.nodes.CfgNullNode;
import com.xmos.libs.core.memorymonitor.MemoryMonitor;
import com.xmos.libs.core.memorymonitor.MemoryMonitorException;
import com.xmos.libs.core.utils.IteratableMap;
import com.xmos.libs.core.utils.IteratableSet;
import com.xmos.xta.core.engine.analysis.structural.CfgContainerNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.ConditionalNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class ConditionalPatternMatcher extends PatternMatcher {

    public ConditionalPatternMatcher(MemoryMonitor memoryMonitor) {
        super(memoryMonitor);
    }

    public boolean isMatch(final CfgNode headNode, Collection<CfgNode> workingSet, final CfgNode treeStart)
            throws MemoryMonitorException {

        m_treeStart = treeStart;

        // We are looking for a headNode that has multiple bodyNodes
        // and at least two bodyNodes have the same tailNode (listed in tailToBodiesMap).
        // Possible head->tail default-conditionals are listed in isOptionalSet.
        // non-CfgContainerNode-bodyNodes with no tailNodes are also matched (listed in nullToBodiesMap) .

        if (headNode.getNextNodesSize() < 2)
            return false;   // Not a 'branch'.

        Set<CfgNode> defaultConditionals = new IteratableSet<CfgNode>();
        Map<CfgNode, Set<CfgNode>> tailToBodiesMap = new IteratableMap<CfgNode, Set<CfgNode>>();
        Set<CfgNode> nullToBodiesMap = new IteratableSet<CfgNode>();
        for (CfgNode bodyNode : headNode.getNextNodes()) {
            if (bodyNode == headNode )
                return false;   // This is a Loop.

            if (bodyNode.getPrevNodes().size() > 1) {
                // We could have a head->tail default-conditional.
                // We will compare this 'tailNode' with other tailNodes later.
                // (otherwise we ignore it as a potential bodyNode)
                CfgNode tailNode = bodyNode;
                defaultConditionals.add(tailNode);
                continue;
            }

            // Look at the body's tails.
            switch (bodyNode.getNextNodesSize()) {
            case 0:
                if (bodyNode instanceof CfgContainerNode)
                    return false; // This is "something else".
                assert(false); // we currently always have CfgNullNode following.
                nullToBodiesMap.add(bodyNode);
                break;
            case 1:
                // The body converges onto a tail.
                CfgNode tailNode = bodyNode.getNextNode();
                if (tailNode == headNode)
                    return false; // This is a Loop.
                if (tailNode.getPrevNodes().size() > 1) {
                    // Add the tailNode & it's bodies to the tailToBodiesMap map.
                    if (!tailToBodiesMap.containsKey(tailNode))
                        tailToBodiesMap.put(tailNode, new IteratableSet<CfgNode>());
                    tailToBodiesMap.get(tailNode).add(bodyNode);
                    break;
                }
                assert(tailNode.getPrevNodes().size() == 1);
                if(tailNode.getNextNodesSize() == 1)
                    return false; // This is another body. Handle this body->body sequence first!
                // else the bodyNode is a leaf, ignore it.
                assert(bodyNode instanceof CfgContainerNode ||
                        tailNode instanceof CfgNullNode);
                break;
            default:
                // bodyNode is diverging, so ignore it.
            }
        }

        if (nullToBodiesMap.size() > 1) {
            assert(false);
            // How do we handle null optional nodes?
            //Set<CfgNode> bodyNodes = nullToBodiesMap;
            //if(!bodyNodes.contains(treeStart) && bodyNodes.size() > 1) {
            //    updateGraph(headNode, bodyNodes, false, workingSet, ???);
            //    return true;
        }
        for (CfgNode tailNode : tailToBodiesMap.keySet()) {
            Set<CfgNode> bodyNodes = tailToBodiesMap.get(tailNode);
            boolean hasDefault = defaultConditionals.contains(tailNode);
            if(!bodyNodes.contains(treeStart) &&
                    bodyNodes.size() + (hasDefault?1:0) > 1) {
                updateGraph(headNode, bodyNodes, hasDefault, workingSet, tailNode);
                return true;
            }
        }
        return false;
    }

    protected void updateGraph(CfgNode headNode, Collection<CfgNode> bodyNodes, boolean hasDefault,
            Collection<CfgNode> workingSet, CfgNode tailNode) {

        // RL-TODO transfer the 'NonBranchingNode' state from Cfg to structure.

        // Create the new structural node.
        CfgNode newBodyNodes = createStructuralNode(bodyNodes, hasDefault);
        newBodyNodes.addNextNode(tailNode);
        newBodyNodes.addPrevNode(headNode);

        // Remove old bodyNodes and add newBodyNodes to head.
        for (CfgNode bodyNode : bodyNodes)
            headNode.removeNextNode(bodyNode);
        headNode.addNextNode(newBodyNodes);

        // Remove old bodyNodes and add newBodyNodes to tail.
        tailNode.getPrevNodes().removeAll(bodyNodes);
        tailNode.addPrevNode(newBodyNodes);

        // Remove the explicit fall through too.
        if (hasDefault) {
            headNode.removeNextNode(tailNode);
            tailNode.getPrevNodes().remove(headNode);
        }

        workingSet.removeAll(bodyNodes);
        workingSet.add(newBodyNodes);
    }

    public boolean usesDominatorTree() {
        return false;
    }

    private CfgNode createStructuralNode(Collection<CfgNode> condNodes, boolean isOptional) {
        Collection<Node> strCondNodes = getStructuralNodes(condNodes);
        Node structuralNode = new ConditionalNode(strCondNodes, isOptional);
        return new CfgContainerNode(structuralNode);
    }
}
