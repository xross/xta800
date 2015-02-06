package com.xmos.xta.core.engine.analysis.routes;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.xmos.libs.core.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.libs.core.xeloader.XeLoader.Architecture;
import com.xmos.xta.core.engine.analysis.structural.nodes.IssueNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class InstructionBuffer {
    private static final boolean traceTree = false;
    private static final boolean traceInstructions = false;

    private Map<Integer, IBuff> m_ibStash = null;
    private IBuff m_ib = null;

    public InstructionBuffer(Architecture arch, long startPc, List<CfgInstructionNode> cfgs) {
        if(traceTree) System.out.printf("\nInstructionBuffer\n=================\n");
        m_ib = new IBuff(arch, startPc, cfgs);
        m_ibStash = new TreeMap<Integer, IBuff>();
    }

    public void createForks(List<Node> nodes) {
        assert(m_ib != null);
        if(traceTree) System.out.printf("Forking:");
        for (Node node : nodes) {
            if(traceTree) System.out.printf("  %d", node.getId());
            m_ibStash.put(node.getId(), new IBuff(m_ib));
        }
        if(traceTree) System.out.printf("\n");
        m_ib = null;
    }

    public void joinForks(List<Node> nodes) {
        assert(m_ib == null);
        List<IBuff> ibs = new LinkedList<IBuff>();
        if(traceTree) System.out.printf("\nJoining:");
        for (Node node : nodes) {
            if(traceTree) System.out.printf("  %d", node.getId());
            ibs.add(m_ibStash.remove(node.getId()));
        }
        if(traceTree) System.out.printf("\n");
        // Pick the worst case (shortest) IBuff
        m_ib = IBuff.shortest(ibs); // if single worst case needing fnop, add an fnop and try again!
        assert(m_ib != null);
    }

    public void startNode(Node node) {
        if (m_ibStash.containsKey(node.getId())) {
            // This is the start of a fork.
            assert(m_ib == null);
            m_ib = m_ibStash.get(node.getId());
            if(traceTree) System.out.printf("\nstart %d\n", node.getId());
        }
        assert(m_ib != null);
    }

    public void endNode(Node node) {
        assert(m_ib != null);
        if (m_ibStash.containsKey(node.getId())) {
            // This is the end of a fork.
            m_ibStash.put(node.getId(), new IBuff(m_ib)); // Write any changes back for 'joinForks' to read.
            if(traceTree) System.out.printf("end %d\n", node.getId());
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
        // remove the instruction from the ibuff.
        int numFnopsNeeded =  m_ib.readInstruction(issueNode.getStartPc(),
                                                   issueNode.getNumBytes(),
                                                   issueNode.isBranch(),
                                                   !issueNode.hasLoadStore());
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
        private long m_nonBranchPc;       // The PC if we have not branched.

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
            m_nonBranchPc = 0;
        }

        public IBuff(IBuff ib) {
            assert(ib != null);
            m_arch = ib.m_arch;
            m_bytesPerEntry = ib.m_bytesPerEntry;
            m_offset = ib.m_offset;
            m_bytes = ib.m_bytes;
            m_pendingFetch = ib.m_pendingFetch;
            m_pendingBranch = ib.m_pendingBranch;
            m_nonBranchPc = ib.m_nonBranchPc;
        }

        public int readInstruction(long pc, int lenBytes, boolean isBranch, boolean loadStoreAvailable) {
            // Run the IBuff fetch using carried forward state.
            if (m_pendingBranch) {
                if(traceIBuff) System.out.print("<m_pendingBranch>\n");
                if (m_nonBranchPc == pc) {
                    // The branch was not taken
                    if(traceIBuff) System.out.print("<branch not taken>\n");
                    if (m_arch == Architecture.XS2A)
                        m_pendingFetch = false; // XS2A fetches the branch PC & throws it away.
                    else if (mimicOriginal) {
                        // A fall-through is also flushed!
                        m_offset = (int) (m_nonBranchPc % m_bytesPerEntry);
                        m_bytes = 0;
                    }
                } else {
                    if(traceIBuff) System.out.print("<Resetting IBuff>\n");
                    m_offset = (int) (pc % m_bytesPerEntry);
                    m_bytes = 0;
                }
            } else {
                //assert(m_nonBranchPc == 0 || m_nonBranchPc == pc);
                if(m_nonBranchPc != 0 && m_nonBranchPc != pc)
                    if(traceIBuff) System.out.print("<ERROR:m_nonBranchPc != pc>\n");
            }
            cycleIBuff(m_pendingFetch, this);

           // Do we need any FNOPs to retrieve the next instruction?
            int numFnops = 0;
            while (m_offset + lenBytes > m_bytes) {
                ++numFnops;
                cycleIBuff(true, this); // We assume the instruction will fit!
            }

            if(traceInstructions) {
                offset = m_offset;
                bytes = m_bytes;
            }

            // Remove the instruction
            m_offset += lenBytes;

            // Carry forward state
            m_pendingFetch = loadStoreAvailable;
            m_pendingBranch = isBranch;
            m_nonBranchPc = pc + lenBytes;

            return numFnops;
        }

        static public IBuff shortest(List<IBuff> ibs) {
            int shortestEstimate = XS2_BYTES_PER_ENTRY + 1; // larger than possible.
            IBuff shortest = null;
            for (IBuff ib : ibs) {
                assert(ib != null);
                // Estimate the effect of pending changes to the IBuff.
                IBuff estimate = new IBuff(ib);
                    if (estimate.m_pendingBranch) {
                        estimate.m_bytes = 0;
                        // The pc will be the same for all, so choose a safe value prior to calling cycleIBuff().
                        estimate.m_offset = 0;
                    }
                    cycleIBuff(estimate.m_pendingFetch, estimate);
                if (shortestEstimate > estimate.m_bytes) {
                    shortestEstimate = estimate.m_bytes;
                    shortest = ib;
                }
            }
            assert(shortest != null);
            if(traceIBuff) System.out.printf("shortest: bytes:%d,  pendingFetch:%s, pendingBranch:%s, nonBranchPc=%x\n",
                    shortest.m_bytes,
                    shortest.m_pendingFetch,
                    shortest.m_pendingBranch,
                    shortest.m_nonBranchPc);
            return shortest;
        }

        static private void cycleIBuff(boolean canLoadStore, IBuff ib) {
            // Do the fetch first.
            if (canLoadStore &&
                    ib.m_bytes <= (IBUFFER_NUM_ENTRIES - 1) * ib.m_bytesPerEntry) {
                ib.m_bytes += ib.m_bytesPerEntry;
                if(traceIBuff) System.out.print("<Fetching IBuff>\n");
            }
            // Then remove instruction and update fifo.
            while (ib.m_offset >= ib.m_bytesPerEntry) {
                ib.m_offset -= ib.m_bytesPerEntry;
                ib.m_bytes -= ib.m_bytesPerEntry;
            }
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
