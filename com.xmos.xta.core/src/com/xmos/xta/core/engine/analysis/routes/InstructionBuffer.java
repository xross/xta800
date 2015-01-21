package com.xmos.xta.core.engine.analysis.routes;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.xmos.libs.core.xeloader.XeLoader.Architecture;
import com.xmos.xta.core.engine.analysis.structural.nodes.IssueNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class InstructionBuffer {

    private Map<Integer, IBuff> m_ibStash = null;
    private IBuff m_ib = null;
    private Architecture m_arch = null;

    public InstructionBuffer(long startPc, int startNumBytes, Architecture arch) {
        m_ib = new IBuff(startPc, startNumBytes);
        m_ibStash = new TreeMap<Integer, IBuff>();
        m_arch = arch;
    }

    public void createForks(List<Node> nodes) {
        assert(m_ib != null);
        for (Node node : nodes)
            m_ibStash.put(node.getId(), new IBuff(m_ib));
        m_ib = null;
    }

    public void joinForks(List<Node> nodes) {
        assert(m_ib == null);
        List<IBuff> ibs = new LinkedList<IBuff>();
        for (Node node : nodes)
            ibs.add(m_ibStash.remove(node.getId()));
        m_ib = new IBuff(ibs);// picks the shortest, worse case.
        assert(m_ib != null);
    }

    public void startNode(Node node) {
        if (m_ibStash.containsKey(node.getId())) {
            // This is the start of a fork.
            // Grab reference to the stashed value.
            assert(m_ib == null);
            m_ib = m_ibStash.get(node.getId());
        }
        assert(m_ib != null);
    }

    public void endNode(Node node) {
        assert(m_ib != null);
        if (m_ibStash.containsKey(node.getId())) {
            // This is the end of a fork.
            // Stashed value was updated insitu.
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
//        if (fnopCount > 1)
//            System.out.printf("We are expecting multiple fnops (%d)!!!\n", fnopCount);

        // remove the instruction from the ibuff.
        int numFnopsNeeded =  m_ib.retrieveInstruction(issueNode.getNumBytes());
        if (numFnopsNeeded > 0) {
            // Instruction fnop = new NullInstruction("--FNOP--", 0, inst.getPc(), 0, true);
//            if (numFnopsNeeded > 1)
//                System.out.printf("We are adding multiple fnops (%d)\n", numFnopsNeeded);
//            if (numFnopsNeeded == fnopCount)
//                System.out.printf("  fnop\n");
//            else
//                System.out.printf("+ fnops\n");
        }
//        if (fnopCount > 0)
//            System.out.printf("- fnop\n");
        fnopCount = 0;


        // Update the pc.
        if (issueNode.isBranch())
            m_ib.setPc(issueNode.getBranchPc());

        // Can a fetch be done at same time as instruction?
        boolean isLoadStore = issueNode.hasLoadStore() ||
                (m_arch == Architecture.XS2A && issueNode.isBranch());
        if (!isLoadStore)
            m_ib.doFetch();
    }


    class IBuff {
        private static final int IBUFFER_NUM_BYTES = 8;
        private static final int BYTES_PER_WORD = 4;

        private long m_pc;
        private int m_bytes;

        public IBuff(long startPc, int startNumBytes) {
            m_pc = startPc;
            m_bytes = startNumBytes;
            assert(isValid());
        }
        public IBuff(IBuff ib) {
            assert(ib != null);
            m_pc = ib.m_pc;
            m_bytes = ib.m_bytes;
            assert(isValid());
        }
        public IBuff(List<IBuff> ibs) {// picks the shortest, worse case.
            m_bytes = IBUFFER_NUM_BYTES + 1;
            for (IBuff ib : ibs) {
                assert(ib != null);
                if (m_bytes > ib.m_bytes) {
                    m_bytes = ib.m_bytes;
                    m_pc = ib.m_pc;
                }
            }
            assert(isValid());
        }

        public void doFetch() {
            int numBytesInFetch = (isWordAligned(m_pc + m_bytes))? BYTES_PER_WORD : BYTES_PER_WORD / 2;
            if (m_bytes <= (IBUFFER_NUM_BYTES - numBytesInFetch))
                m_bytes += numBytesInFetch;
        }
        public void setPc(long pc) {
            m_pc = pc;
            m_bytes = 0;
        }
        public int retrieveInstruction(int bytes) {
            int numFnops = 0;
            while (bytes >  m_bytes) {
                ++numFnops;
                doFetch();
            }
            m_bytes -= bytes;
            m_pc += bytes;
            return numFnops;
        }
        private boolean isValid() {return m_bytes >= 0 &&  m_bytes <= IBUFFER_NUM_BYTES;}
        private boolean isWordAligned(long address) {return ((address & 0x3) == 0);}
    }
}
