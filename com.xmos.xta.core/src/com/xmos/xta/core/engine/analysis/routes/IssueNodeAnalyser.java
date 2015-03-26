package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import com.xmos.libs.core.controlflow.ControlFlow;
import com.xmos.libs.core.controlflow.FunctionControlFlow;
import com.xmos.libs.core.controlflow.nodes.CfgFunctionReferenceNode;
import com.xmos.libs.core.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.elfhandler.IssueRegions;
import com.xmos.libs.core.elfhandler.IssueRegions.IssueMode;
import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.libs.core.instructiondecoder.Mnemonics;
import com.xmos.libs.core.utils.CoreId;
import com.xmos.libs.core.utils.IteratableSet;
import com.xmos.libs.core.utils.Pair;
import com.xmos.libs.core.xeloader.XeLoader.Architecture;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.IssueNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

class IssueNodeAnalyser {
    private static final boolean traceAll = false;
    private static final boolean traceTree = false || traceAll;
    private static final boolean traceInstructions = false || traceAll;

    private List<IssueNode> m_toDelete;                   // IssueNodes that are not longer needed as combined into another.

    private IBuff m_ib = null;                            // The active IBuff state (null if we need to fetch a m_blockStartState)
    // The following maps are temporal stores. put() & remove() are balanced. They should be empty when the traversal ends!
    private Map<Integer, IBuff> m_blockStartState = null; // Holds starting IBuffs. Key is the first node of a block.
    private Map<Integer, Integer> m_loopheader = null;    // Look up a loop's header-block. Key is the first node of a loop.
    private Map<Integer, IBuff> m_loopsStartState = null; // The state when we first arrive at a loop node. Only used to verify worse case calculations.
    private Map<Node, Integer> m_fnopsNeeded = null;      // IssueNodes inform their parents the number of FNOPs they need. Key is the child..

    private boolean m_ignoreStartingFnops;

    IssueNodeAnalyser(Engine engine, CoreId core, long startPc) {
        if(traceTree) System.out.printf("\nIssueNodeAnalyser\n=================\n");

        m_toDelete = new ArrayList<IssueNode>();

        m_ib = new IBuff(engine, core, startPc);
        m_blockStartState = new TreeMap<Integer, IBuff>();
        m_loopheader = new TreeMap<Integer, Integer>();
        m_loopsStartState = new TreeMap<Integer, IBuff>();
        m_fnopsNeeded = new TreeMap<Node, Integer>();
        m_ignoreStartingFnops = true;
    }

    void createForks(List<Node> nodes) {
        assert(m_ib != null);
        if(traceTree) System.out.printf("Forking:");
        for (Node node : nodes) {
            if(traceTree) System.out.printf("  %d", node.getId());
            m_blockStartState.put(node.getId(), new IBuff(m_ib));
        }
        if(traceTree) m_ib.dump();
        m_ib = null;
    }

    void joinForks(List<Node> nodes) {
        assert(m_ib == null);
        List<Pair<IBuff,Integer>> ibs = new LinkedList<Pair<IBuff,Integer>>();
        if(traceTree) System.out.printf("\nJoining Fork:");
        for (Node node : nodes) {
            if(traceTree) System.out.printf("  %d", node.getId());
            ibs.add(new Pair<IBuff,Integer>(m_blockStartState.remove(node.getId()),node.getId()));
        }
        if(traceTree) System.out.printf("\n");
        // Pick the worst case (shortest) IBuff
        m_ib = IBuff.shortest(ibs);
        assert(m_ib != null);
    }

    void createLoops(List<Node> nodes) {
        assert(m_ib != null);
        if(traceTree) System.out.printf("Looping:");
        m_ib.forcePendingBranch(m_ignoreStartingFnops);  // Make sure we are overly pessimistic when entering loop-header.
        int headerId = 0;
        for (Node node : nodes) {
            if(traceTree) System.out.printf("  %d", node.getId());
            if (headerId == 0) {
                headerId = node.getId();
                m_blockStartState.put(headerId, new IBuff(m_ib));
                // Keep a record of how we entered loop-header so we can compare with loop-body exit states.
                m_loopsStartState.put(headerId, new IBuff(m_ib));
            } else
                m_loopheader.put(node.getId(), headerId);   // So the loop-bodies can find the loop-header exit state.
        }
        if(traceTree)  m_ib.dump();
        m_ib = null;
    }

    void joinLoops(List<Node> nodes) {
        assert(m_ib == null);
        List<Pair<IBuff,Integer>> ibs = new LinkedList<Pair<IBuff,Integer>>();
        if(traceTree) System.out.printf("\nJoining Loops:");
        int headerId = 0;
        IBuff startState = null;
        for (Node node : nodes) {
            if(traceTree) System.out.printf("  %d", node.getId());
            if (headerId == 0) {
                headerId = node.getId();
                m_ib = m_blockStartState.remove(headerId);  // We exit via the header.
                startState = m_loopsStartState.remove(headerId);
            } else {
                m_loopheader.remove(node.getId());
                ibs.add(new Pair<IBuff,Integer>(m_blockStartState.remove(node.getId()),node.getId())); // Was added by endNode().
            }
        }
        if(traceTree) {
            System.out.printf("\nJoined %d :", headerId);
            m_ib.dump();
        }
        // Check that we were pessimistic enough when entering the loop-header.
        if (!ibs.isEmpty() && IBuff.shortest(ibs).isShorterThan(startState))
            if(traceTree) System.out.printf("*** We should have entered loop-header with a worse case IBuff ***\n");
            else assert(false);
        assert(m_ib != null);
    }

    void createSelfLoops(List<Node> nodes) {
        assert(m_ib != null);
        if(traceTree) System.out.printf("SelfLooping:");
        m_ib.forcePendingBranch(m_ignoreStartingFnops);  // Make sure we are overly pessimistic when entering loop-header.
        int refId = 0;  // We nominate the first loop-body to the role of 'loop-header' (as we don't have a loop-header).
        for (Node node : nodes) {
            if(traceTree) System.out.printf("  %d", node.getId());
            m_blockStartState.put(node.getId(), new IBuff(m_ib));
            if (refId == 0) {
                refId = node.getId();
                // Keep a record of how we entered 'loop-header' so we can compare with loop-body exit states.
                m_loopsStartState.put(refId, new IBuff(m_ib));
            }
        }
        if(traceTree)  m_ib.dump();
        m_ib = null;
    }

    void joinSelfLoops(List<Node> nodes) {
        assert(m_ib == null);
        List<Pair<IBuff,Integer>> ibs = new LinkedList<Pair<IBuff,Integer>>();
        if(traceTree) System.out.printf("\nJoining SelfLoops:");
        int refId = 0;
        for (Node node : nodes) {
            if(traceTree) System.out.printf("  %d", node.getId());
            ibs.add(new Pair<IBuff,Integer>(m_blockStartState.remove(node.getId()),node.getId()));
            if (refId == 0)
                refId = node.getId();
        }
        if(traceTree) System.out.printf("\n");
        // Pick the worst case (shortest) IBuff
        m_ib = IBuff.shortest(ibs);
        // Check that we were pessimistic enough when entering the 'loop-header'.
        if (m_ib.isShorterThan(m_loopsStartState.remove(refId)))
            if(traceTree) System.out.printf("*** We should have started SelfLoop with a worse case IBuff ***\n");
            else assert(false);
        assert(m_ib != null);
    }

    void startNode(Node node) {
        int nodeId = node.getId();
        if (m_loopheader.containsKey(nodeId)){
            // This is the start of a loop-block with a loop-header.
            // We need to make a copy of the exit IBuff state of the loop-header.
            assert(m_ib == null);
            int headerId = m_loopheader.get(nodeId);
            m_blockStartState.put(node.getId(), new IBuff( m_blockStartState.get(headerId)));
        }
        if (m_blockStartState.containsKey(nodeId)) {
            // This is the start of a block.
            assert(m_ib == null);
            m_ib = m_blockStartState.get(nodeId);
            if(traceTree) {
                System.out.printf("\nstart %d", nodeId);
                m_ib.dump();
            }
        }
        assert(m_ib != null);
    }

    void endNode(Node node) {
        assert(m_ib != null);
        int nodeId = node.getId();
        if (m_loopheader.containsKey(nodeId) ||
                m_blockStartState.containsKey(nodeId)) {
            // This is the end of a block.
            // Write any changes back for the join methods to read.
            m_blockStartState.put(nodeId, new IBuff(m_ib));
            if(traceTree) {
                System.out.printf("end %d", nodeId);
                m_ib.dump();
            }
            m_ib = null;
        }

        // Now that we have finished iterating over our children we can add any necessary FNOPs.
        if (node instanceof BlockNode && (!m_fnopsNeeded.isEmpty() || !m_toDelete.isEmpty())) {
            BlockNode blockNode = (BlockNode)node;
            for (Node child : blockNode.iChildren()) {
                if (m_fnopsNeeded.containsKey(child)) {
                    assert(child instanceof IssueNode);
                    blockNode.insertFnopsBefore((IssueNode)child, m_fnopsNeeded.get(child));
                    m_fnopsNeeded.remove(child);
                }
                if (m_toDelete.contains(child)) {
                    assert(child instanceof IssueNode);
                    blockNode.remove(child);
                    m_toDelete.remove(child);
                }
            }
        }
    }

    void handleIssueNode(Node node) {
        assert(m_ib != null);
        IssueNode issueNode = (IssueNode)node;

        if (traceInstructions) System.out.print(m_ib.trace_pendingMessage);

        int numFnopsNeeded =  m_ib.readInstruction(issueNode, m_toDelete);
        if (numFnopsNeeded > 0 && !m_ignoreStartingFnops)
            m_fnopsNeeded.put(issueNode, numFnopsNeeded);
        m_ignoreStartingFnops = false;

        if (traceInstructions) {
            System.out.printf("%d:%d (%d) ", m_ib.trace_offset, m_ib.trace_bytes, issueNode.getNumBytes());
            System.out.print(issueNode);
            System.out.printf("      %s\n", issueNode.isLoadStore()? "load/store" : "ifetch");
        }
    }


    static class IBuff {
        // The IBuff is made up from an array of entries forming a FIFO of entries (a ring buffer).
        // Instructions are read from the tail (m_offset is position of the pc in the tail-entry).
        // Once all bytes are read from the tail entry (m_offset > BYTES_PER_ENTRY), entries are shuffled down leaving the head empty.
        // A fetch will load an empty head with BYTES_PER_ENTRY.
        // N.B. m_offset is only incremented once the instruction has completed (as it may need to be re-run)

        private static final boolean traceIBuff = false || traceAll;

        // Constants dependent upon architecture.
        private static final int IBUFFER_NUM_ENTRIES = 2;
        private static final int XS1_BYTES_PER_ENTRY = 4;
        private static final int XS2_BYTES_PER_ENTRY = 16;

        private static final int DUAL_ISSUE_BYTES = 4;

        private final Architecture m_arch;
        private final int m_bytesPerEntry;
        private int m_offset;           // Position of next instruction in tail-entry.
        private int m_bytes;            // Number of bytes in the IBuff (multiple of m_bytesPerEntry).
        // if (m_offset > m_bytes) the next instruction isn't in the IBuff.

        // Carried forward state.
        private IssueMode m_mode;         // The active IssueMode state.
        private Stack<IssueMode> m_stack; // We stack the issue mode between a BL & RETSP.
        private IssueNode m_prevInstr;    // The first of a dual issue pair.
        private boolean m_pendingFetch;   // The last instruction did not do a load/store, so we can.
        private boolean m_pendingBranch;  // We may have branched.
        private int m_branchNotTakenId;   // The node we will end up at if the pending conditional branch was not taken.

        // traceTree usage: state of IBuff prior to readInstruction.
        private int trace_offset;
        private int trace_bytes;
        private String trace_pendingMessage;

        IBuff(Engine engine, CoreId core, long startPc) {
            m_arch = engine.getXeLoader().getArchitecture();
            m_bytesPerEntry = (m_arch == Architecture.XS2A)? XS2_BYTES_PER_ENTRY : XS1_BYTES_PER_ENTRY;
            m_offset = (int) (startPc % m_bytesPerEntry);

            ControlFlow controlFlow = engine.getControlFlow(core);
            Map<CfgNode,List<CfgNode>> calleeToCallers = controlFlow.getCalleeToCallersMap();
            List<CfgInstructionNode> instNodes = new LinkedList<CfgInstructionNode>();
            for (FunctionControlFlow fcf : controlFlow.getFunctionContaining(startPc)) {
                CfgInstructionNode instNode = fcf.getInstructionNode(startPc);
                assert(startPc == instNode.getPc());
                instNodes.add(instNode);
            }
            boolean noFetchOnBranchNotTaken = (m_arch == Architecture.XS2A);
            m_bytes = predictIBuffEntries(instNodes, calleeToCallers, m_bytesPerEntry, noFetchOnBranchNotTaken) * m_bytesPerEntry;
            switch (engine.getStartMode()) {
            case SINGLE: m_mode = IssueMode.SINGLE; break;
            case DUAL:   m_mode = IssueMode.DUAL;   break;
            case AUTO:
                IssueRegions issueRegions = new IssueRegions(engine.getXeLoader().getSymbolTable(core));
                IssueMode compiledMode = issueRegions.getIssueMode(startPc);
                m_mode = predictIssueMode(instNodes, calleeToCallers, compiledMode);
                break;
            }
            m_stack = new Stack<IssueMode>();
            m_prevInstr = null;
            m_pendingFetch = false;
            m_pendingBranch = false;
            m_branchNotTakenId = 0;
            trace_pendingMessage= "";
        }

        IBuff(IBuff ib) {
            assert(ib != null);
            m_arch = ib.m_arch;
            m_bytesPerEntry = ib.m_bytesPerEntry;
            m_offset = ib.m_offset;
            m_bytes = ib.m_bytes;
            m_mode = ib.m_mode;
            m_stack = ib.m_stack;
            assert(ib.m_prevInstr == null); // We should never fork etc. part way through an issue.
            m_prevInstr = ib.m_prevInstr;
            m_pendingFetch = ib.m_pendingFetch;
            m_pendingBranch = ib.m_pendingBranch;
            m_branchNotTakenId = ib.m_branchNotTakenId;
            if(traceInstructions)
                trace_pendingMessage = ib.trace_pendingMessage;
        }

        int readInstruction(IssueNode issueNode, List<IssueNode> toDelete) {
            if (m_mode == IssueMode.DUAL)
                if (!packDualIssue(issueNode, toDelete))
                    return 0;  // Skip this node as it will be combined...

            // Update the carried forward state, now we know where we have arrived.
            if (m_pendingBranch)
                handlePendingBranch(issueNode);

            // Only now the last instruction has completed and decisions made can we update the IBuff.
            cycleIBuff(m_pendingFetch);

            int lenBytes = issueNode.getNumBytes();
            assert(m_mode == IssueMode.SINGLE || lenBytes == DUAL_ISSUE_BYTES);

            // Do we need any FNOPs to retrieve the next instruction(s)?
            int numFnops = 0;
            while (m_offset + lenBytes > m_bytes) {
                ++numFnops;
                cycleIBuff(true); // We assume the instruction will fit!
            }

            // Only needed for tracing.
            if(traceInstructions) {
                trace_offset = m_offset;
                trace_bytes = m_bytes;
                trace_pendingMessage = "";
                if (issueNode.isWait())
                    trace_pendingMessage += "<WAITEx>\n";
                if (issueNode.isUnresolved())
                    trace_pendingMessage += "<Unresolved>\n";
            }

            // Update state.
            m_offset += lenBytes;
            updateMode(issueNode);
            m_pendingBranch = issueNode.isBranch();
            m_branchNotTakenId = issueNode.getNextBranchNotTakenId();
            if (issueNode.isUnresolved() || issueNode.isWait()) {
                assert(m_pendingBranch);
                assert(m_branchNotTakenId == 0);
                m_pendingFetch = false; // We don't know how we will get there.
            }
            else
                m_pendingFetch = !issueNode.isLoadStore();
            return numFnops;
        }

        private boolean packDualIssue(IssueNode issueNode, List<IssueNode> toDelete) {
            assert(m_mode == IssueMode.DUAL);
            if (issueNode.getNumBytes() == DUAL_ISSUE_BYTES) {
                assert(issueNode.getStartPc() % DUAL_ISSUE_BYTES == 0);
                assert(m_prevInstr == null);
                // No combining necessary.
            } else {
                if (m_prevInstr == null) {
                    assert(issueNode.getStartPc() % DUAL_ISSUE_BYTES == 0);
                    if (!issueNode.isWait()) {
                        m_prevInstr = issueNode;
                        return false; // Skip this node as it will be combined & deleted.
                    }
                    // We are in the resource lane.
                    // We need to grab our 'lost' memory-lane instruction and combine it here and now!
                    // We ignore any links or attributes this 'lost' instruction may have had.
                    List<Instruction> issue = issueNode.getInstructions();
                    assert(issue.size() == 1);
                    Instruction nextInst = issue.get(0).getNextInst();
                    issue.add(nextInst); // We ignore any edges nextInst may have.
                    assert(issueNode.getNumBytes() == DUAL_ISSUE_BYTES);
                } else {
                    assert(issueNode.getStartPc() % DUAL_ISSUE_BYTES != 0);
                    issueNode.combine(m_prevInstr);
                    assert(issueNode.getNumBytes() == DUAL_ISSUE_BYTES);
                    toDelete.add(m_prevInstr);
                    m_prevInstr = null;
                }
            }
            return true;
        }

        private void handlePendingBranch(IssueNode issueNode) {
            if(traceIBuff) {
                System.out.print("<m_pendingBranch>");
                if (m_branchNotTakenId != 0 || issueNode.getBranchNotTakenId() != 0)
                    System.out.printf("      branchNotTakenId %d %d", m_branchNotTakenId, issueNode.getBranchNotTakenId());
                System.out.print("\n");
            }
            if (m_branchNotTakenId != 0 && m_branchNotTakenId == issueNode.getBranchNotTakenId()) {
                // The branch was not taken
                assert(m_offset % m_bytesPerEntry == (int) (issueNode.getStartPc() % m_bytesPerEntry));
                if(traceIBuff) System.out.print("<branch not taken>\n");
                if (m_arch == Architecture.XS2A)
                    m_pendingFetch = false; // XS2A fetches the branch PC & throws it away.
            } else {
                if(traceIBuff) System.out.print("<Branch clearing IBuff>\n");
                m_offset = (int) (issueNode.getStartPc() % m_bytesPerEntry);
                m_bytes = 0;
            }
        }

        void updateMode(IssueNode issueNode) {
            // The WAITx is in the resource-lane and will assume the memory-lane instruction is not run.
            if (issueNode.isWait()) {
                if (issueNode.isUnresolved() && !m_stack.isEmpty())
                    m_mode = m_stack.pop();  // End of this function's analysis.
                // else the user has given an xtabranch destination.
                return;
            }
            // Instructions that use the memory-lane are mutually exclusive.
            if (issueNode.isEntsp())
                m_mode = IssueMode.SINGLE;
            if (issueNode.isDualsp())
                m_mode = IssueMode.DUAL;
            if (issueNode.isRetsp() && !m_stack.isEmpty())
                m_mode = m_stack.pop();
            if (issueNode.isBranchLink())
                m_stack.push(m_mode);
        }

        boolean isShorterThan(IBuff ib) {
            if (estimateIbuffBytes() == ib.estimateIbuffBytes())
                return ib.m_branchNotTakenId > 0;  // Pick the one that is most likely to branch.
            return estimateIbuffBytes() < ib.estimateIbuffBytes();
        }

        static IBuff shortest(List<Pair<IBuff,Integer>> ibs) {
            Pair<IBuff,Integer> shortest = null;
            for (Pair<IBuff,Integer> ib : ibs) {
                assert(ib != null);
                if (shortest == null || ib.first.isShorterThan(shortest.first))
                    shortest = ib;
            }
            assert(shortest != null);
            if(traceIBuff) {
                System.out.printf("shortest: %d :", shortest.second);
                shortest.first.dump();
            }
            return shortest.first;
        }

        void forcePendingBranch(boolean ignoreStartingFnops) {
            // **** An alternative approach would be to use predictIBuffEntries() if we can access the cfg.

            // This is used by createLoops() and createSelfLoops() to preempt a worse case IBuff when entering loop-head.
            // This code is tuned to make sure the loop-body exits are never worse than the loop-header entry.
            // RL-TODO *** We assume a loop-body branch will always have a pendingFetch. ***
            // These changes will only ever make things more pessimistic.
            // This is validated by joinLoops() and joinSelfLoops() - see isShorterThan().
            if (!m_pendingBranch) {
                // We are not branching into the loop-head, so the loop-body must be branching.
                // Which is worse: our current state or the loop-body's branch with fetch?
                if (m_pendingFetch || m_offset < m_bytes - m_bytesPerEntry) {
                    // A loop-body branch will be no better than this, so mimic the loop-body's branch.
                    m_pendingBranch = true;
                    m_pendingFetch = true;
                }
            }
            //else
            //    We are branching into the loop-head.
            m_branchNotTakenId = 0; // Make sure we always take a branch.
         }

        private void cycleIBuff(boolean pendingFetch) {
            // Do the fetch first.
            if (pendingFetch &&
                    m_bytes <= (IBUFFER_NUM_ENTRIES - 1) * m_bytesPerEntry) {
                m_bytes += m_bytesPerEntry;
                if(traceIBuff) System.out.print("<Fetching IBuff>\n");
            }
            // Then remove instruction and update fifo.
            while (m_offset >= m_bytesPerEntry) {
                m_offset -= m_bytesPerEntry;
                m_bytes -= m_bytesPerEntry;
            }
        }

        private int estimateIbuffBytes() {
            // Estimate the effect of pending changes to the IBuff.
            // N.B. We do this with a copy.
            IBuff estimate = new IBuff(this);
            if (estimate.m_pendingBranch) {
                // We ignore m_branchNotTakenId, as we don't know our next node.
                estimate.m_bytes = 0;
                // The pc is not important. Choose a safe value prior to calling cycleIBuff().
                estimate.m_offset = 0;
            }
            estimate.cycleIBuff(estimate.m_pendingFetch);
            return estimate.m_bytes;
        }


        static private Set<CfgNode> traceBack(CfgNode node, Map<CfgNode,List<CfgNode>> calleeToCallers) {
            Set<CfgNode> prev = new IteratableSet<CfgNode>();
            prev.addAll(node.getPrevNodes());
            // Find and add the callers to this function too.
            List<CfgNode> callers = calleeToCallers.get(node);
             if (callers != null)
                prev.addAll(callers);
            return prev;
        }

        static private IssueMode predictIssueMode(List<CfgInstructionNode> instNodes, Map<CfgNode,List<CfgNode>> calleeToCallers, IssueMode compiledMode) {
            List<CfgNode> toCheck = new ArrayList<CfgNode>();
            Set<CfgNode> seen = new TreeSet<CfgNode>();
            for(CfgInstructionNode instNode : instNodes) {
                Set<CfgNode> prev = traceBack(instNode, calleeToCallers);
                toCheck.addAll(prev);
                seen.addAll(prev);
            }
            boolean single = false;
            boolean dual = false;
            while (!toCheck.isEmpty()) {
                CfgNode node = toCheck.remove(toCheck.size()-1);
                if (node instanceof CfgInstructionNode) {
                    Instruction instr = ((CfgInstructionNode)node).getInstruction();
                    if (instr.isA(Mnemonics.ENTSP)) {
                        single = true;
                        continue;
                    }
                    if (instr.isA(Mnemonics.DUALENTSP)) {
                        dual = true;
                        continue;
                    }
                }
                for (CfgNode prev : traceBack(node, calleeToCallers))
                    if (!seen.contains(prev)) {
                        toCheck.add(prev);
                        seen.add(prev);
                    }
            }
            if (dual) {
                if (single)
                    return compiledMode;
                return IssueMode.DUAL;
            }
            return IssueMode.SINGLE; // The hardware starts in SINGLE, no need for an ENTSP.
        }

        static private int predictIBuffEntries(List<CfgInstructionNode> instNodes, Map<CfgNode,List<CfgNode>> calleeToCallers,
                                               int bytesPerEntry, boolean noFetchOnBranchNotTaken) {
            final int INVALID = IBUFFER_NUM_ENTRIES + 1;
            int minEntries = INVALID;
            for(CfgInstructionNode instNode : instNodes) {
                int entries = predictIBuffEntries(instNode, calleeToCallers, bytesPerEntry, noFetchOnBranchNotTaken);
                if (minEntries > entries)
                    minEntries = entries;
            }
            return (minEntries == INVALID)? 0 : minEntries;
        }

        static private int predictIBuffEntries(CfgInstructionNode nextInstrNode, Map<CfgNode,List<CfgNode>> calleeToCallers,
                                               int bytesPerEntry, boolean noFetchOnBranchNotTaken) {
            final int INVALID = IBUFFER_NUM_ENTRIES + 1;
            final int offset = (int) (nextInstrNode.getPc() % bytesPerEntry);
            int minEntries = INVALID;

            for (CfgNode prevNode : traceBack(nextInstrNode, calleeToCallers)) {
                // Have we reached minimum?
                if (minEntries == 0)
                    break;

                if (prevNode instanceof CfgFunctionReferenceNode)
                    continue;
                assert(prevNode instanceof CfgInstructionNode);
                CfgInstructionNode prevInstrNode = (CfgInstructionNode)prevNode;
                Instruction prevInstr = prevInstrNode.getInstruction();

                // Was it a taken branch?
                if (prevInstr.isBranch() && prevInstrNode.getNextBranchNotTakenNode() != nextInstrNode) {
                    // The branch will clear the IBuff, but did we do a fetch?
                    assert(minEntries != 0);
                    if (prevInstr.isWait() || prevInstr.isLoadStore())
                        minEntries = 0;
                    else
                        minEntries = 1;
                    continue;
                }
                // Did the previous instruction fit in the same IBuff entry?
                if (prevInstr.getNumBytes() <= offset) {
                    // We need to predict the IBuff for this prevInstrNode.
                    int prevEntries = predictIBuffEntries(prevInstrNode, calleeToCallers, bytesPerEntry, noFetchOnBranchNotTaken);
                    if (!prevInstr.isLoadStore() && !(prevInstr.isBranch() && noFetchOnBranchNotTaken))    // Branch not taken.
                        prevEntries += 1;  // We did a fetch.
                    minEntries = Math.min(minEntries, prevEntries);
                    continue;
                }
                @SuppressWarnings("all") boolean algorithmGood = IBUFFER_NUM_ENTRIES == 2; assert(algorithmGood);  // Algorithm only works for 2 entry IBuffs.
                // Was the previous instruction was straddling the 2nd IBuff entry?
                if (offset > 0) {
                    // Thus both IBuffs were full when it finished and there was no IBuff fetch.
                    minEntries = Math.min(minEntries, 1); // We are left with the 2nd IBuff.
                    continue;
                }
                assert (offset == 0);
                // We need to see if any of the instructions in the previous IBuff were able to fetch the 2nd IBuff.
                // First check our current branch-not-taken previous instruction.
                if (!prevInstr.isLoadStore() && !(prevInstr.isBranch() && noFetchOnBranchNotTaken)) {
                    minEntries = Math.min(minEntries, 1); // We did a fetch.
                    continue;
                }
                // Check the earlier instructions in the IBuff too.
                boolean fetch = iBuffFetchHistory(prevInstrNode, bytesPerEntry - prevInstr.getNumBytes(), noFetchOnBranchNotTaken);
                minEntries = Math.min(minEntries, fetch?1:0);
            }
            if ( minEntries == INVALID)
                minEntries = 0;  // Assume the worse.
            return minEntries;
        }

        static private boolean iBuffFetchHistory(CfgNode nextNode, int offset, boolean noFetchOnBranchNotTaken) {
            // We are not interested if the instruction is not in this function viz getPrevNodes is empty or a CfgFunctionReferenceNode.
            if (offset <= 0 || nextNode.getPrevNodes().isEmpty())
                return false;
            for (CfgNode prevNode : nextNode.getPrevNodes()) {
                if (prevNode instanceof CfgFunctionReferenceNode)
                    continue;
                assert(prevNode instanceof CfgInstructionNode);
                CfgInstructionNode prevInstrNode = (CfgInstructionNode)prevNode;
                Instruction instr = prevInstrNode.getInstruction();
                if (instr.getNumBytes() > offset ||
                   (instr.isBranch() && prevInstrNode.getNextBranchNotTakenNode() != nextNode)) {
                    return false;  // prevNode was not in this IBuff.
                }
                if (instr.isLoadStore() || (instr.isBranch() && noFetchOnBranchNotTaken))
                    if(!iBuffFetchHistory(prevNode, offset - instr.getNumBytes(), noFetchOnBranchNotTaken))
                        // This instruction did not do a fetch nor did one of its predecessors.
                        return false;
                // We had a fetch, continue checking the other prevNodes.
            }
            return true; // All the prevNodes did a fetch into the next IBuff.
        }

        void dump() {
            System.out.printf("        (%d:%d)  pendingFetch:%s  pendingBranch:%s  branchNotTakenId:%s\n",
                    m_offset, m_bytes, m_pendingFetch,  m_pendingBranch, m_branchNotTakenId);
        }
    }
}
