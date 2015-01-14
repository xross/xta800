package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.xmos.libs.core.controlflow.ControlFlow;
import com.xmos.libs.core.controlflow.FunctionControlFlow;
import com.xmos.libs.core.controlflow.nodes.CfgFunctionReferenceNode;
import com.xmos.libs.core.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.core.memorymonitor.MemoryMonitorException;
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

    private ControlFlow m_controlFlow;
    private CachedFunctionMap m_cachedFunctionMap;

    ControlFlowReferenceMatcher(ControlFlow controlFlow, CachedFunctionMap cachedFunctionMap) {
        m_controlFlow = controlFlow;
        m_cachedFunctionMap = cachedFunctionMap;
    }

    List<StackNode> matchReference(Reference ref)
        throws AbortedException, MemoryMonitorException {

        if (DEBUG) System.out.printf("========== Matching Ref: %s =============\n", ref);
        List<StackNode> stackNodes = createStackNodes(ref);
        if (DEBUG) System.out.printf("%s\n", stackNodes);
        return stackNodes;
    }

    private List<StackNode> createStackNodes(Reference reference)
        throws AbortedException, MemoryMonitorException {

        List<StackNode> refinedStackNodes = new LinkedList<StackNode>();

        for(long pc : reference.getPcs()){
            if (DEBUG) System.out.printf("Starting on pc: 0x%x\n", pc);

            List<ReferenceType> refList = new LinkedList<ReferenceType>();
            matchLabel(reference, pc, refList);

            List<FunctionControlFlow> containedInFunction = m_controlFlow.getFunctionContaining(pc);

            for (FunctionControlFlow fn : containedInFunction) {
                if (DEBUG) System.out.printf("Function: %s\n", fn.getFunctionName());

                List<Stack<CfgFunctionReferenceNode>> stacks = new LinkedList<Stack<CfgFunctionReferenceNode>>();
                CfgInstructionNode inst = fn.getInstructionNode(pc);

                if (reference.getBase().getType() == Type.FUNCTION && fn.getStartInstructionNode() != inst) {
                    continue;
                }

                List<Stack<CfgFunctionReferenceNode>> callStacks = m_controlFlow.getStacks(fn);

                for(Stack<CfgFunctionReferenceNode> stack : callStacks){
                    Stack<CfgFunctionReferenceNode> copy = new Stack<CfgFunctionReferenceNode>();
                    copy.addAll(stack);
                    List<ReferenceType> matchingList = new LinkedList<ReferenceType>(refList);

                    long currentPc = pc;
                    CfgFunctionReferenceNode frn = copy.pop();
                    while(!matchingList.isEmpty()){
                        Pair<Boolean, Long> res = fnRefContainsReference(frn, currentPc, matchingList, stack);
                        while(res.first){
                            ReferenceType r = matchingList.get(matchingList.size() -1);

                            currentPc = res.second;
                            matchingList.remove(r);

                            res = fnRefContainsReference(frn, currentPc, matchingList, stack);
                        }
                        currentPc = frn.getFunctionCallPc();
                        if(copy.isEmpty())
                            break;
                        frn = copy.pop();

                    }

                    if (matchingList.isEmpty()){
                        stacks.add(stack);
                    }
                }
                if (!stacks.isEmpty()){
                    refinedStackNodes.add(new StackNode(inst, stacks));
                }
            }
        }
        if (DEBUG) System.out.printf("Refining stack nodes (End: size: %d)...\n", refinedStackNodes.size());
        return refinedStackNodes;
    }

    private Pair<Boolean, Long> fnRefContainsReference(CfgFunctionReferenceNode frn, Long currentPc,
            List<ReferenceType> refList, Stack<CfgFunctionReferenceNode> stack)
        throws AbortedException, MemoryMonitorException {

        if (refList.isEmpty())
            return new Pair<Boolean, Long>(false, (long)0);

        ReferenceType r = refList.get(refList.size() -1);

        if (r.getType() == ReferenceType.Type.CALL) {
            if (r.getPcs().contains(frn.getFunctionCallPc())) {
                return new Pair<Boolean, Long>(true, (long)0);
            }
        } else if (r.getType() == ReferenceType.Type.ENDPOINT) {
            return matchRefPc(frn, currentPc, r, stack);

        } else if (r.getType() == ReferenceType.Type.ERROR) {
            return new Pair<Boolean, Long>(false, (long)0);

        } else if (r.getType() == ReferenceType.Type.EVERYTHING) {
            return new Pair<Boolean, Long>(true, currentPc);

        } else if (r.getType() == ReferenceType.Type.FUNCTION) {
            if (r.getPcs().contains(frn.getFunctionStartPc())) {
                return new Pair<Boolean, Long>(true, frn.getFunctionStartPc());
            }
        } else if (r.getType() == ReferenceType.Type.LABEL) {
            return matchRefPc(frn, currentPc, r, stack);

        } else if (r.getType() == ReferenceType.Type.SRCLABEL) {
            return matchRefPc(frn, currentPc, r, stack);

        } else {
            assert(false);
        }
        return new Pair<Boolean, Long>(false, (long)0);
    }

    private Pair<Boolean, Long> matchRefPc(CfgFunctionReferenceNode frn, Long latestPc,
            ReferenceType r, Stack<CfgFunctionReferenceNode> stack)
        throws AbortedException, MemoryMonitorException {

        boolean found = false;

        if (latestPc == 0x0) {
            for (long pc : r.getPcs()) {
                latestPc = pc;
                found = true;
            }
        } else {
            Node functionNode =  m_cachedFunctionMap.getCachedFunction(frn.getFunctionControlFlow());
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

    private int getIndexOfInst(List<Node> nodeList, long pc) {
        for (int i = nodeList.size()-2; i >= 0;i--) {
            if (nodeList.get(i) instanceof IssueNode){
                IssueNode issueNode = (IssueNode)nodeList.get(i);
                if (issueNode.getPC() == pc){
                    return i;
                }
            }
        }
        return -1;
    }

    private IssueNode getIssueNode(Node node, final long pc) {
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
}
