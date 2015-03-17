package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.xmos.libs.core.controlflow.ControlFlow;
import com.xmos.libs.core.controlflow.FunctionControlFlow;
import com.xmos.libs.core.controlflow.nodes.CfgFunctionReferenceNode;
import com.xmos.libs.core.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.core.memorymonitor.MemoryMonitorException;
import com.xmos.libs.core.utils.BackTrail;
import com.xmos.libs.core.utils.Pair;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.analysis.structural.CachedFunctionMap;
import com.xmos.xta.core.engine.analysis.structural.nodes.IssueNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.types.ReferenceType;
import com.xmos.xta.core.engine.references.types.ReferenceType.Type;

class ControlFlowReferenceMatcher extends ReferenceMatcher {
    private static final boolean DEBUG = false;

    static List<BackTrails> getPathBackTrails(ControlFlow controlFlow, CachedFunctionMap cachedFunctionMap, Reference reference)
            throws AbortedException, MemoryMonitorException {
        if (DEBUG) System.out.printf("========== Matching Ref: %s =============\n", reference);

        List<BackTrails> refinedBackTrailsList = new LinkedList<BackTrails>();
        for (long pc : reference.getPcs()) {
            if (DEBUG) System.out.printf("Starting on pc: 0x%x\n", pc);

            List<ReferenceType> refList = new LinkedList<ReferenceType>();
            matchLabel(reference, pc, refList);

            List<FunctionControlFlow> containedInFunction = controlFlow.getFunctionContaining(pc);

            for (FunctionControlFlow fn : containedInFunction) {
                if (DEBUG) System.out.printf("Function: %s\n", fn.getFunctionName());

                CfgInstructionNode inst = fn.getInstructionNode(pc);
                if (reference.getBase().getType() == Type.FUNCTION && fn.getStartInstructionNode() != inst)
                    continue;

                BackTrails backTrails = new BackTrails(inst);
                for (BackTrail backTrail : controlFlow.getBackTrails(fn)) {
                    long currentPc = pc;
                    List<ReferenceType> matchingList = new LinkedList<ReferenceType>(refList);
                    // We want to iterate over backTrail BUT in stack order not as stored, so we use pop().
                    BackTrail copy = new BackTrail(backTrail);
                    while (!copy.isEmpty() && !matchingList.isEmpty()) {
                        CfgFunctionReferenceNode frn = copy.pop();
                        while (!matchingList.isEmpty()) {
                            ReferenceType r = matchingList.get(matchingList.size() -1);
                            Pair<Boolean, Long> res = fnRefContainsReference(frn, currentPc, r, cachedFunctionMap);
                            if (!res.first) break;
                            currentPc = res.second;
                            matchingList.remove(r);
                        }
                        currentPc = frn.getFunctionCallerPc();
                    }
                    if (matchingList.isEmpty())
                        backTrails.add(backTrail);
                }
                if (!backTrails.isEmpty())
                    refinedBackTrailsList.add(backTrails);
            }
        }
        if (DEBUG) System.out.printf("Refining backTrail nodes (End: size: %d)...\n", refinedBackTrailsList.size());
        if (DEBUG) System.out.printf("%s\n", refinedBackTrailsList);
        return refinedBackTrailsList;
    }

    private static Pair<Boolean, Long> fnRefContainsReference(CfgFunctionReferenceNode frn, Long currentPc, ReferenceType r, CachedFunctionMap cachedFunctionMap)
            throws AbortedException, MemoryMonitorException {
        switch(r.getType()) {
        case CALL:
            if (r.getPcs().contains(frn.getFunctionCallerPc()))
                return new Pair<Boolean, Long>(true, (long)0);
            break;
        case ENDPOINT:
            return matchRefPc(frn, currentPc, r, cachedFunctionMap);
        case EVERYTHING:
            return new Pair<Boolean, Long>(true, currentPc);
        case FUNCTION:
            if (r.getPcs().contains(frn.getFunctionStartPc()))
                return new Pair<Boolean, Long>(true, frn.getFunctionStartPc());
            break;
        case LABEL:
            return matchRefPc(frn, currentPc, r, cachedFunctionMap);
        case SRCLABEL:
            return matchRefPc(frn, currentPc, r, cachedFunctionMap);
        default:
            break;
        }
        return new Pair<Boolean, Long>(false, (long)0);
    }

    private static Pair<Boolean, Long> matchRefPc(CfgFunctionReferenceNode frn, Long latestPc, ReferenceType r, CachedFunctionMap cachedFunctionMap)
        throws AbortedException, MemoryMonitorException {

        boolean found = false;

        if (latestPc == 0x0) {
            for (long pc : r.getPcs()) {
                latestPc = pc;
                found = true;
            }
        } else {
            Node functionNode =  cachedFunctionMap.getCachedFunction(frn.getFunctionControlFlow());
            IssueNode issueNode = getIssueNode(functionNode, latestPc);
            List<Node> nodeList = new LinkedList<Node>();
            issueNode.buildListUp(nodeList, null);

            for (long pc : r.getPcs()) {
                int index = getIndexOfInst(nodeList, pc);
                if (index != -1) {
                    nodeList = nodeList.subList(0, index);
                    latestPc = pc;
                    found = true;
                }
            }
        }
        return new Pair<Boolean, Long>(found, latestPc);
    }

    private static int getIndexOfInst(List<Node> nodeList, long pc) {
        for (int i = nodeList.size()-2; i >= 0;i--) {
            if (nodeList.get(i) instanceof IssueNode){
                IssueNode issueNode = (IssueNode)nodeList.get(i);
                if (issueNode.getStartPc() == pc){
                    return i;
                }
            }
        }
        return -1;
    }

    private static IssueNode getIssueNode(Node node, final long pc) {
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
        null // No clientState
        );
        assert(issueNodes.size() == 1);
        return issueNodes.iterator().next();
    }
}
