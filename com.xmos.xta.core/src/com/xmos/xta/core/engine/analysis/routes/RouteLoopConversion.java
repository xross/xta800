package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.xmos.libs.core.controlflow.ControlFlow;
import com.xmos.libs.core.controlflow.FunctionControlFlow;
import com.xmos.libs.core.controlflow.nodes.CfgFunctionReferenceNode;
import com.xmos.libs.core.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.memorymonitor.MemoryMonitorException;
import com.xmos.libs.core.utils.BackTrail;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.analysis.structural.CachedFunctionMap;
import com.xmos.xta.core.engine.analysis.structural.nodes.IssueNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.references.Reference;

public class RouteLoopConversion {
    private ControlFlow m_controlFlow;
    private CachedFunctionMap m_cachedFunctionMap;

    public RouteLoopConversion(ControlFlow controlFlow, CachedFunctionMap cachedFunctionMap) {
        m_controlFlow = controlFlow;
        m_cachedFunctionMap = cachedFunctionMap;
    }

    // Given a reference, this function returns true if the reference
    // points to a location within a loop, and false otherwise.
    public boolean isWithinLoop(Reference reference) {
        try {
            return process(reference, null, null);

        } catch (Exception e) {}
        return false;
    }

    // Given a reference, this function adds the relevant start loop
    // and exclusion BackTrail nodes to the given lists
    public void updateLoopBackTrails(Reference reference,
            List<BackTrails> loopStartBackTrailsList,
            List<BackTrails> exclusionBackTrailsList)
        throws AbortedException, MemoryMonitorException {

        process(reference, loopStartBackTrailsList, exclusionBackTrailsList);
    }

    private boolean process(Reference reference,
            List<BackTrails> loopStartBackTrailsList,
            List<BackTrails> exclusionBackTrailsList)
        throws AbortedException, MemoryMonitorException {

        boolean withinLoop = false;

        List<BackTrails> backTrailsList = RouteConstructor.getPathBackTrails(
                m_controlFlow, m_cachedFunctionMap, reference);

        for (BackTrails backTrails : backTrailsList) {

            // First we look for a loop in the function that contained the original reference..
            FunctionControlFlow functionControlFlow =
                m_controlFlow.getFunctionControlFlow(backTrails.getInstruction());
            IssueNode issueNode = getCachedIssueNode(
                    functionControlFlow, backTrails.getInstruction());

            Node loopNode = findLoopNode(issueNode);
            if (loopNode != null) {
                addLoopBackTrails(loopNode, backTrails.getBackTrails(),
                        functionControlFlow, loopStartBackTrailsList, exclusionBackTrailsList);

                withinLoop = true;
                break;
            }

            // Next search up the various function call BackTrails for all the loops..
            for (BackTrail backTrail : backTrails.getBackTrails()) {

                CfgFunctionReferenceNode functionReferenceNode = backTrail.pop();
                while (functionReferenceNode != null) {

                    Set<CfgNode> callNodes = functionReferenceNode.getPrevNodes();
                    if (callNodes.isEmpty())
                        break;

                    assert(callNodes.size() == 1);
                    CfgNode callNode = callNodes.iterator().next();

                    assert(callNode instanceof CfgInstructionNode);
                    CfgInstructionNode instructionCallNode = (CfgInstructionNode)callNode;

                    functionControlFlow = m_controlFlow.getFunctionControlFlow(instructionCallNode);
                    issueNode = getCachedIssueNode(functionControlFlow, instructionCallNode);

                    loopNode = findLoopNode(issueNode);
                    if (loopNode != null) {
                        List<BackTrail> newBackTrails = new ArrayList<BackTrail>();
                        newBackTrails.add(backTrail);
                        addLoopBackTrails(loopNode, newBackTrails, functionControlFlow, loopStartBackTrailsList, exclusionBackTrailsList);
                        withinLoop = true;
                        break;
                    }
                    functionReferenceNode = backTrail.pop();
                }
            }
        }
        return withinLoop;
    }

    private Node findLoopNode(IssueNode node) {
        Node loopNode = node.getEnclosingLoopNode();
        if (loopNode != null) {
            return loopNode;
        }
        Node infiniteLoopNode = node.getEnclosingInfiniteLoopNode();
        if (infiniteLoopNode != null) {
            return infiniteLoopNode;
        }
        return null;
    }

    private void addLoopBackTrails(Node loopNode,
            List<BackTrail> backTrailsList,
            FunctionControlFlow functionControlFlow,
            List<BackTrails> loopStartBackTrailsList,
            List<BackTrails> exclusionBackTrailsList) {

        if (loopStartBackTrailsList == null)
            return;

        // For the given structural loop node, this finds the
        // first structural instruction node in the loop.
        IssueNode issueNode = getFirstIssueNode(loopNode);

        // Now find the control flow instruction node
        // corresponding to the above structural instruction node.
        CfgInstructionNode cfgInstructionNode = functionControlFlow.getInstructionNode(issueNode.getStartPc());

        loopStartBackTrailsList.add(new BackTrails(cfgInstructionNode, backTrailsList));

        if (loopNode.isLoopNode() || loopNode.isSelfLoopNode()) {
            Node successor = loopNode.getSuccessor();
            if (successor != null) {
                issueNode = getFirstIssueNode(successor);
                cfgInstructionNode = functionControlFlow.getInstructionNode(issueNode.getStartPc());

                exclusionBackTrailsList.add(new BackTrails(cfgInstructionNode, backTrailsList));
            }
        }
    }

    private IssueNode getCachedIssueNode(
            FunctionControlFlow functionControlFlow,
            CfgInstructionNode cfgInstructionNode)
        throws AbortedException, MemoryMonitorException {

        Node functionNode = m_cachedFunctionMap.getCachedFunction(functionControlFlow);
        return getIssueNodeMatchingPc(functionNode, cfgInstructionNode.getPc());
    }

    private IssueNode getIssueNodeMatchingPc(Node node, final long pc) {
        final List<IssueNode> issueNodes = new ArrayList<IssueNode>();
        node.traverse(new DefaultNodeTraverser() {
            public TraverseStatus foundIssueNode(Node node, Object clientState) {
                IssueNode issueNode = (IssueNode)node;

                if (issueNode.getStartPc() == pc) {
                    issueNodes.add((IssueNode)node);
                    return TraverseStatus.ABORT;
                }
                return TraverseStatus.CONTINUE;
            }
        },
        null    // No clientState
        );
        assert(issueNodes.size() == 1);
        return issueNodes.iterator().next();
    }

    private IssueNode getFirstIssueNode(Node node) {
        final Collection<IssueNode> issueNode = new ArrayList<IssueNode>();
        node.traverse(new DefaultNodeTraverser() {
            public TraverseStatus foundIssueNode(Node node, Object clientState) {
                issueNode.add((IssueNode)node);
                return TraverseStatus.ABORT;
            }
        },
        null    // No clientState
        );
        assert(issueNode.size() == 1);
        return issueNode.iterator().next();
    }
}
