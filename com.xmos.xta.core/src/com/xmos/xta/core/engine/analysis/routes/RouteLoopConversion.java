package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.xmos.libs.core.controlflow.ControlFlow;
import com.xmos.libs.core.controlflow.FunctionControlFlow;
import com.xmos.libs.core.controlflow.nodes.CfgFunctionReferenceNode;
import com.xmos.libs.core.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.memorymonitor.MemoryMonitorException;
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
    // and exclusion stack nodes to the given lists
    public void updateLoopStackNodes(Reference reference,
            List<StackNode> loopStartStackNodes,
            List<StackNode> exclusionStackNodes)
        throws AbortedException, MemoryMonitorException {

        process(reference, loopStartStackNodes, exclusionStackNodes);
    }

    private boolean process(Reference reference,
            List<StackNode> loopStartStackNodes,
            List<StackNode> exclusionStackNodes)
        throws AbortedException, MemoryMonitorException {

        boolean withinLoop = false;

        List<StackNode> fromStackNodes = RouteConstructor.getPathStackNodes(
                m_controlFlow, m_cachedFunctionMap, reference);

        for (StackNode fromStackNode : fromStackNodes) {

            // First we look for a loop in the function that contained the original reference..
            FunctionControlFlow functionControlFlow =
                m_controlFlow.getFunctionControlFlow(fromStackNode.getInstruction());
            IssueNode issueNode = getCachedIssueNode(
                    functionControlFlow, fromStackNode.getInstruction());

            Node loopNode = findLoopNode(issueNode);
            if (loopNode != null) {
                addLoopStackNodes(loopNode, fromStackNode.getNodeStacks(),
                        functionControlFlow, loopStartStackNodes, exclusionStackNodes);

                withinLoop = true;
                break;
            }

            // Next search up the various function call stacks for all the loops..
            for (Stack<CfgFunctionReferenceNode> nodeStack : fromStackNode.getNodeStacks()) {

                CfgFunctionReferenceNode functionReferenceNode = nodeStack.pop();
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
                        List<Stack<CfgFunctionReferenceNode>> nodeStacks =
                            new ArrayList<Stack<CfgFunctionReferenceNode>>();
                        nodeStacks.add(nodeStack);

                        addLoopStackNodes(loopNode, nodeStacks, functionControlFlow,
                                loopStartStackNodes, exclusionStackNodes);

                        withinLoop = true;
                        break;
                    }
                    functionReferenceNode = nodeStack.pop();
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

    private void addLoopStackNodes(Node loopNode,
            List<Stack<CfgFunctionReferenceNode>> nodeStacks,
            FunctionControlFlow functionControlFlow,
            List<StackNode> loopStartStackNodes,
            List<StackNode> exclusionStackNodes) {

        if (loopStartStackNodes == null)
            return;

        // For the given structural loop node, this finds the
        // first structural instruction node in the loop.
        IssueNode issueNode = getFirstIssueNode(loopNode);

        // Now find the control flow instruction node
        // corresponding to the above structural instruction node.
        CfgInstructionNode cfgInstructionNode = functionControlFlow.getInstructionNode(issueNode.getPC());

        loopStartStackNodes.add(new StackNode(cfgInstructionNode, nodeStacks));

        if (loopNode.isLoopNode() || loopNode.isSelfLoopNode()) {
            Node successor = loopNode.getSuccessor();
            if (successor != null) {
                issueNode = getFirstIssueNode(successor);
                cfgInstructionNode = functionControlFlow.getInstructionNode(issueNode.getPC());

                exclusionStackNodes.add(new StackNode(cfgInstructionNode, nodeStacks));
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
            public TraverseStatus foundIssueNode(Node node) {
                IssueNode issueNode = (IssueNode)node;

                if (issueNode.getPC() == pc) {
                    issueNodes.add((IssueNode)node);
                    return TraverseStatus.ABORT;
                }
                return TraverseStatus.CONTINUE;
            }
        });
        assert(issueNodes.size() == 1);
        return issueNodes.iterator().next();
    }

    private IssueNode getFirstIssueNode(Node node) {
        final Collection<IssueNode> issueNode = new ArrayList<IssueNode>();
        node.traverse(new DefaultNodeTraverser() {
            public TraverseStatus foundIssueNode(Node node) {
                issueNode.add((IssueNode)node);
                return TraverseStatus.ABORT;
            }
        });
        assert(issueNode.size() == 1);
        return issueNode.iterator().next();
    }
}
