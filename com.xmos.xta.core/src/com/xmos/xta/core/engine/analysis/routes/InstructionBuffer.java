package com.xmos.xta.core.engine.analysis.routes;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.xmos.libs.core.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.libs.core.xeloader.XeLoader.Architecture;
import com.xmos.xta.core.engine.analysis.structural.nodes.IssueNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class InstructionBuffer {
    private static final boolean traceTree = false;
    private static final boolean traceInstructions = false;

    private IBuff m_ib = null;                            // The active IBuff state (null if we need to fetch a m_blockStartState)
    // The following maps are temporal stores. put() & remove() are balanced. They should be empty when the traversal ends!
    private Map<Integer, IBuff> m_blockStartState = null; // Holds starting IBuffs. Key is the first node of a block.
    private Map<Integer, Integer> m_loopheader = null;    // Look up a loop's header-block. Key is the first node of a loop.
    private Map<Integer, IBuff> m_loopsStartState = null; // The state when we first arrive at a loop node. Only used to verify worse case calculations.

    public InstructionBuffer(Architecture arch, long startPc, List<CfgInstructionNode> cfgs) {
        if(traceTree) System.out.printf("\nInstructionBuffer\n=================\n");
        m_ib = new IBuff(arch, startPc, cfgs);
        m_blockStartState = new TreeMap<Integer, IBuff>();
        m_loopheader = new TreeMap<Integer, Integer>();
        m_loopsStartState = new TreeMap<Integer, IBuff>();
    }

    public void createForks(List<Node> nodes) {
        assert(m_ib != null);
        if(traceTree) System.out.printf("Forking:");
        for (Node node : nodes) {
            if(traceTree) System.out.printf("  %d", node.getId());
            m_blockStartState.put(node.getId(), new IBuff(m_ib));
        }
        if(traceTree) System.out.printf("\n");
        m_ib = null;
    }

    public void joinForks(List<Node> nodes) {
        assert(m_ib == null);
        List<IBuff> ibs = new LinkedList<IBuff>();
        if(traceTree) System.out.printf("\nJoining Fork:");
        for (Node node : nodes) {
            if(traceTree) System.out.printf("  %d", node.getId());
            ibs.add(m_blockStartState.remove(node.getId()));
        }
        if(traceTree) System.out.printf("\n");
        // Pick the worst case (shortest) IBuff
        m_ib = IBuff.shortest(ibs);
        assert(m_ib != null);
    }

    public void createLoops(List<Node> nodes) {
        assert(m_ib != null);
        if(traceTree) System.out.printf("Looping:");
        // N.B. We will always use a branch to loop back to the top - RL-TODO is this true?
        m_ib.forcePendingBranch();  // make sure we are overly pessimistic.
        int headerId = 0;
        for (Node node : nodes) {
            if(traceTree) System.out.printf("  %d", node.getId());
            if (headerId == 0) {
                headerId = node.getId();
                m_blockStartState.put(headerId, new IBuff(m_ib));
                m_loopsStartState.put(headerId, new IBuff(m_ib));  // This won't be changed.
            } else
                m_loopheader.put(node.getId(), headerId);
        }
        if(traceTree) System.out.printf("\n");
        m_ib = null;
    }

    public void joinLoops(List<Node> nodes) {
        assert(m_ib == null);
        List<IBuff> ibs = new LinkedList<IBuff>();
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
                ibs.add(m_blockStartState.remove(node.getId())); // Added by endNode().
            }
        }
        if(traceTree) System.out.printf("\n");
        // RL-TODO Is the worst case worse than the IBuff state when we called createLoop()?
        if (!ibs.isEmpty() && IBuff.shortest(ibs).isShorterThan(startState))
            if(traceTree) System.out.printf("*** We should have started Loop with a worse case IBuff ***\n");
        assert(m_ib != null);
    }

    public void createSelfLoops(List<Node> nodes) {
        assert(m_ib != null);
        if(traceTree) System.out.printf("SelfLooping:");
        // N.B. We will always use a branch to loop back to the top - RL-TODO is this true?
        m_ib.forcePendingBranch();  // make sure we are overly pessimistic.
        int refId = 0;  // We will use the first loop's id to save state for the Loops.
        for (Node node : nodes) {
            if(traceTree) System.out.printf("  %d", node.getId());
            if (refId == 0) {
                refId = node.getId();
                m_loopsStartState.put(refId, new IBuff(m_ib));
            }
            m_blockStartState.put(node.getId(), new IBuff(m_ib));
        }
        if(traceTree) System.out.printf("\n");
        m_ib = null;
    }

    public void joinSelfLoops(List<Node> nodes) {
        assert(m_ib == null);
        List<IBuff> ibs = new LinkedList<IBuff>();
        if(traceTree) System.out.printf("\nJoining SelfLoops:");
        int refId = 0;
        for (Node node : nodes) {
            if(traceTree) System.out.printf("  %d", node.getId());
            if (refId == 0)
                refId = node.getId();
            ibs.add(m_blockStartState.remove(node.getId()));
        }
        if(traceTree) System.out.printf("\n");
        // Pick the worst case (shortest) IBuff
        m_ib = IBuff.shortest(ibs);
        // RL-TODO Is the worst case worse than the IBuff state when we called createSelfLoop()?
        if (m_ib.isShorterThan(m_loopsStartState.remove(refId)))
            if(traceTree) System.out.printf("*** We should have started SelfLoop with a worse case IBuff ***\n");
        assert(m_ib != null);
    }

    public void startNode(Node node) {
        int nodeId = node.getId();
        if (m_loopheader.containsKey(nodeId)){
            // This is the start of a loop-block with a header so load the exit IBuff state of the header.
            assert(m_ib == null);
            int headerId = m_loopheader.get(nodeId);
            m_ib = m_blockStartState.get(headerId);
            if(traceTree) System.out.printf("\nstart %d\n", nodeId);
        }
        else if (m_blockStartState.containsKey(nodeId)) {
            // This is the start of a block.
            assert(m_ib == null);
            m_ib = m_blockStartState.get(nodeId);
            if(traceTree) System.out.printf("\nstart %d\n", nodeId);
        }
        assert(m_ib != null);
    }

    public void endNode(Node node) {
        assert(m_ib != null);
        int nodeId = node.getId();
        if (m_loopheader.containsKey(nodeId) ||
                m_blockStartState.containsKey(nodeId)) {
            // This is the end of a block.
            // Write any changes back for the join methods to read.
            m_blockStartState.put(nodeId, new IBuff(m_ib));
            if(traceTree) System.out.printf("end %d\n", nodeId);
            m_ib = null;
        }
    }


    static int fnopCount = 0;
    public void handleIssueNode(Node node) {
        assert(m_ib != null);
        IssueNode issueNode = (IssueNode)node;

        if (issueNode.isFnop()) {
           ++fnopCount;
            return;  // until we stop adding them earlier!
        }
        // remove the instruction from the IBuff.
        int numFnopsNeeded =  m_ib.readInstruction(issueNode);
        //assert(numFnopsNeeded == fnopCount);
        while(numFnopsNeeded > 0 || fnopCount > 0) {
            if (numFnopsNeeded < fnopCount) {
                if(traceInstructions) System.out.printf("--- fnop\n");
                --fnopCount;
            } else if (numFnopsNeeded > fnopCount) {
                if(traceInstructions) System.out.printf("+++ fnop\n");
                --numFnopsNeeded;
            } else {
                if(traceInstructions) System.out.printf("    fnop\n");
                --fnopCount;
                --numFnopsNeeded;
            }
        }

        if(traceInstructions) System.out.printf("%d:%d (%d) ", m_ib.offset, m_ib.bytes, issueNode.getNumBytes());
        if(traceInstructions) System.out.print(issueNode);
        if(traceInstructions) System.out.printf("      %s\n", issueNode.hasLoadStore()? "load/store" : "ifetch");
    }


    static class IBuff {
        // The IBuff is made up from an array of entries forming a FIFO of entries (a ring buffer).
        // Instructions are read from the tail (m_offset is position of the pc in the tail-entry).
        // Once all bytes are read from the tail entry (m_offset > BYTES_PER_ENTRY), entries are shuffled down leaving the head empty.
        // A fetch will load an empty head with BYTES_PER_ENTRY.
        // N.B. m_offset is only incremented once the instruction has completed
        //      hence doFetch() must use the pre-retrieveInstruction() m_offset value!

        private static final boolean mimicOriginal = true;
        private static final boolean traceIBuff = false;

        // Constants dependent upon architecture.
        private static final int IBUFFER_NUM_ENTRIES = 2;
        private static final int XS1_BYTES_PER_ENTRY = 4;
        private static final int XS2_BYTES_PER_ENTRY = 16;

        private final int m_bytesPerEntry;
        private final Architecture m_arch;
        private int m_offset;           // Position of next instruction in tail-entry.
        private int m_bytes;            // Number of bytes in the IBuff (multiple of m_bytesPerEntry).
        // if (m_offset > m_bytes) the next instruction isn't in the IBuff.

        // Carried forward state.
        private boolean m_pendingFetch;   // The last instruction did not do a load/store, so we can.
        private boolean m_pendingBranch;  // We may have branched.
        private boolean m_pendingWaiteX;  // The pending branch is due to a waiteu/waitet/waitef instruction.
        private int m_branchNotTakenId;   // The node we will end up at if the pending conditional branch was not taken.

        // traceTree usage: state of IBuff prior to readInstruction.
        int offset = 0;
        int bytes = 0;

        public IBuff(Architecture arch, long startPc, List<CfgInstructionNode> cfgs) {
            m_arch = arch;
            m_bytesPerEntry = (arch == Architecture.XS2A)? XS2_BYTES_PER_ENTRY : XS1_BYTES_PER_ENTRY;
            m_offset = (int) (startPc % m_bytesPerEntry);
            m_bytes = IBuff.predictIBuffEntries(arch, startPc, cfgs, m_bytesPerEntry) * m_bytesPerEntry;
            m_pendingFetch = false;
            m_pendingBranch = false;
            m_pendingWaiteX = false;
            m_branchNotTakenId = 0;
        }

        public IBuff(IBuff ib) {
            assert(ib != null);
            m_arch = ib.m_arch;
            m_bytesPerEntry = ib.m_bytesPerEntry;
            m_offset = ib.m_offset;
            m_bytes = ib.m_bytes;
            m_pendingFetch = ib.m_pendingFetch;
            m_pendingBranch = ib.m_pendingBranch;
            m_pendingWaiteX = ib.m_pendingWaiteX;
            m_branchNotTakenId = ib.m_branchNotTakenId;
        }

        public int readInstruction(IssueNode issueNode) {
            // Run the IBuff fetch using carried forward state.
            if (m_pendingBranch) {
                if(traceIBuff) {
                    System.out.print("<m_pendingBranch>");
                    if (m_branchNotTakenId != 0 || issueNode.getPrevBranchNotTakenId() != 0)
                        System.out.printf("  branchNotTakenId %d %d\n", m_branchNotTakenId, issueNode.getPrevBranchNotTakenId());
                    System.out.print("\n");
                }
                if (m_branchNotTakenId != 0 && m_branchNotTakenId == issueNode.getPrevBranchNotTakenId()) {
                    // The branch was not taken
                    if(traceIBuff) System.out.print("<branch not taken>\n");
                    if (m_arch == Architecture.XS2A)
                        m_pendingFetch = false; // XS2A fetches the branch PC & throws it away.
                } else {
                    if(traceIBuff) System.out.print("<Resetting IBuff>\n");
                    m_offset = (int) (issueNode.getStartPc() % m_bytesPerEntry);
                    m_bytes = 0;
                    if (m_pendingWaiteX)
                        m_pendingFetch = false;  // We can't do a fetch when arriving via vector.
                }
            }
            cycleIBuff(m_pendingFetch);

           // Do we need any FNOPs to retrieve the next instruction?
            int lenBytes = issueNode.getNumBytes();
            int numFnops = 0;
            while (m_offset + lenBytes > m_bytes) {
                ++numFnops;
                cycleIBuff(true); // We assume the instruction will fit!
            }

            if(traceInstructions) {
                offset = m_offset;
                bytes = m_bytes;
            }

            // Remove the instruction
            m_offset += lenBytes;

            // Carry forward state
            m_pendingFetch = !issueNode.hasLoadStore();
            m_pendingBranch = issueNode.isBranch();
            m_pendingWaiteX = m_pendingBranch &&
                    (issueNode.getMnemonic().contains("waiteu") ||
                     issueNode.getMnemonic().contains("waitet") ||
                     issueNode.getMnemonic().contains("waitef"));
            m_branchNotTakenId = issueNode.getNextBranchNotTakenId();

            return numFnops;
        }

        public boolean isShorterThan(IBuff ib) {
            return estimateIbuffBytes() < ib.estimateIbuffBytes();
        }

        static public IBuff shortest(List<IBuff> ibs) {
            IBuff shortest = null;
            for (IBuff ib : ibs) {
                assert(ib != null);
                if (shortest == null || ib.isShorterThan(shortest))
                    shortest = ib;
            }
            assert(shortest != null);
            if(traceIBuff) System.out.printf("shortest: bytes:%d,  pendingFetch:%s, pendingBranch:%s\n",
                    shortest.m_bytes,
                    shortest.m_pendingFetch,
                    shortest.m_pendingBranch);
            return shortest;
        }

        public void forcePendingBranch() {
            // This is used by looping to preempt worse case IBuff at the start of a loop.
            // We leave m_pendingFetch and m_bytes as they are.
            if (!m_pendingBranch) {
                // These changes will only ever make things more pessimistic.
                m_pendingBranch = true;
                m_branchNotTakenId = 0;
            }
        }

        private void cycleIBuff(boolean canLoadStore) {
            // Do the fetch first.
            if (canLoadStore &&
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

        static private int predictIBuffEntries(Architecture arch, long startPc, List<CfgInstructionNode> cfgs, int bytesPerEntry) {
            final int INVALID = IBUFFER_NUM_ENTRIES + 1;
            int minEntries = INVALID;
            for(CfgInstructionNode cfg : cfgs) {
                assert(startPc == cfg.getPc());
                int entries = predictIBuffEntries(arch, cfg, bytesPerEntry);
                if (minEntries > entries)
                    minEntries = entries;
            }
            return (minEntries == INVALID)? 0 : minEntries;
        }

        static private int predictIBuffEntries(Architecture arch, CfgInstructionNode cfg, int bytesPerEntry) {
            //assert(IBUFFER_NUM_ENTRIES == 2);  // Algorithm only works for 2 entry buffers.
            long pc = cfg.getPc();
            int offset = (int) (pc % bytesPerEntry);

            int len = cfg.getInstruction().getNumBytes();
            if(offset + len > bytesPerEntry)
                return 2;  // An FNOP has preceded this instruction filling the 2nd entry too.

            if(mimicOriginal)
                return 1;

            CfgInstructionNode prevNode = cfg.getPrevBranchNotTakenNode();  // A branch would have flushed the IBuff.
            while (prevNode != null && offset > 0) {
                // We have either 1 or 2 entries.
                // Working backwards through this entry, to see if a previous instruction fetched the 2nd entry.
                if (!(prevNode instanceof CfgInstructionNode)) break; // RL-TODO do we need to keep looking for the previous instruction?
                Instruction instr = ((CfgInstructionNode)prevNode).getInstruction();
                int lenInstr = instr.getNumBytes();
                // Update pc & offset to the previous instruction, assuming it did not branch.
                offset -= lenInstr;
                pc -= lenInstr;
                if (offset < 0) break; // Not (fully) in this entry.
                if (instr.isBranch())
                    if (instr.getPc() != pc) break; // It was a branch, hence not in this entry.
                    else if(arch==Architecture.XS2A) continue; // It was in this entry BUT the fetch was discarded.
                if (!instr.isLoadStore())
                    return 2; // It was in the entry and a fetch filled the next entry.
                prevNode = prevNode.getPrevBranchNotTakenNode();
            }
            // We could have either 0 or 1 entries.
            return 1;  // If we had 0 entries, an FNOP preceded this instruction.
        }
    }
}
