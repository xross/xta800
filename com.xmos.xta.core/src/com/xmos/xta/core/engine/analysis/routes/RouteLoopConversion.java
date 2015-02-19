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
import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.CachedFunctionMap;
import com.xmos.xta.core.engine.analysis.structural.nodes.IssueNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.references.Reference;

public class RouteLoopConversion {
    // Given a reference, this function returns true if the reference
    // points to a location within a loop, and false otherwise.
    static public boolean isWithinLoop(Engine engine, CoreId core, Reference reference) {
        try {
            return process(engine, core, reference, null, null);
        } catch (Exception e) {}
        return false;
    }

    // Given a reference, this function adds the relevant start loop
    // and exclusion BackTrail nodes to the given lists
    static public List<BackTrails> updateLoopBackTrails(Engine engine, CoreId core, Reference reference, List<BackTrails> exclusionBackTrailsList)
        throws AbortedException, MemoryMonitorException {
        List<BackTrails> loopStartBackTrailsList = new ArrayList<BackTrails>();
        process(engine, core, reference, loopStartBackTrailsList, exclusionBackTrailsList);
        return loopStartBackTrailsList;
    }

    static public boolean process(Engine engine, CoreId core, Reference reference,
                List<BackTrails> loopStartBackTrailsList, List<BackTrails> exclusionBackTrailsList) throws AbortedException, MemoryMonitorException {
        ControlFlow controlFlow = engine.getControlFlow(core);
        CachedFunctionMap cachedFunctionMap = engine.getCachedFunctionMap(core);

        List<BackTrails> backTrailsList = RouteConstructor.getPathBackTrails(engine, core, reference);
        boolean found = false;
        for (BackTrails backTrails : backTrailsList) {

            // First we look for a loop in the function that contained the original reference..
            FunctionControlFlow functionControlFlow =
                    controlFlow.getFunctionControlFlow(backTrails.getInstruction());
            IssueNode issueNode = getCachedIssueNode(cachedFunctionMap,
                    functionControlFlow, backTrails.getInstruction());

            Node loopNode = findLoopNode(issueNode);
            if (loopNode != null) {
                if (loopStartBackTrailsList != null)
                    addLoopBackTrails(loopNode, backTrails, functionControlFlow, loopStartBackTrailsList, exclusionBackTrailsList);
                found = true;
                break;
            }

            // Next search up the various function call BackTrails for all the loops.
            for (BackTrail backTrail : backTrails) {

                CfgFunctionReferenceNode functionReferenceNode;
                while ((functionReferenceNode = backTrail.pop()) != null &&
                        !functionReferenceNode.getPrevNodes().isEmpty()) {

                    assert(functionReferenceNode.getPrevNodes().size() == 1);
                    CfgNode callNode = functionReferenceNode.getPrevNode();
                    assert(callNode instanceof CfgInstructionNode);
                    CfgInstructionNode instructionCallNode = (CfgInstructionNode)callNode;

                    functionControlFlow = controlFlow.getFunctionControlFlow(instructionCallNode);
                    issueNode = getCachedIssueNode(cachedFunctionMap, functionControlFlow, instructionCallNode);

                    loopNode = findLoopNode(issueNode);
                    if (loopNode != null) {
                        if (loopStartBackTrailsList != null) {
                            List<BackTrail> backTrailList = new ArrayList<BackTrail>();
                            backTrailList.add(backTrail);
                            addLoopBackTrails(loopNode, new BackTrails(null, backTrailList), functionControlFlow,
                                    loopStartBackTrailsList, exclusionBackTrailsList);
                        }
                        found = true;
                        break;
                    }
                }
            }
        }
        return found;
    }

    private static Node findLoopNode(IssueNode node) {
        Node loopNode = node.getEnclosingLoopNode();
        if (loopNode != null)
            return loopNode;
        return node.getEnclosingInfiniteLoopNode();
    }

    private static void addLoopBackTrails(Node loopNode, BackTrails backTrails, FunctionControlFlow functionControlFlow,
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

        loopStartBackTrailsList.add(new BackTrails(cfgInstructionNode, backTrails));

        if (loopNode.isLoopNode() || loopNode.isSelfLoopNode()) {
            Node successor = loopNode.getSuccessor();
            if (successor != null) {
                issueNode = getFirstIssueNode(successor);
                cfgInstructionNode = functionControlFlow.getInstructionNode(issueNode.getStartPc());

                exclusionBackTrailsList.add(new BackTrails(cfgInstructionNode, backTrails));
            }
        }
    }

    private static IssueNode getCachedIssueNode(CachedFunctionMap cachedFunctionMap,
            FunctionControlFlow functionControlFlow,
            CfgInstructionNode cfgInstructionNode)
        throws AbortedException, MemoryMonitorException {

        Node functionNode = cachedFunctionMap.getCachedFunction(functionControlFlow);
        return getIssueNodeMatchingPc(functionNode, cfgInstructionNode.getPc());
    }

    private static IssueNode getIssueNodeMatchingPc(Node node, final long pc) {
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

    private static IssueNode getFirstIssueNode(Node node) {
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
