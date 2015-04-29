package com.xmos.xta.core.engine.analysis.structural.nodes.attributes;

import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.xta.core.engine.analysis.structural.nodes.IssueNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Status.S;
import com.xmos.xta.core.engine.utils.CoreConfig;
import com.xmos.xta.core.engine.utils.TimeUnit;
import com.xmos.xta.core.engine.utils.TimeUnit.Units;

public class IssueNodeAttributes extends NodeAttributes {
    private long m_numCycles;
    private CoreConfig m_coreConfig;
    private String m_instructionTimeString;

    public IssueNodeAttributes(IssueNode node) {
        super(node);
        m_coreConfig = null;
        m_numCycles = calculateCycles(m_coreConfig, (IssueNode)getNode());
    }

    public void setCoreConfig(CoreConfig coreConfig) {
        m_coreConfig = coreConfig;
        m_numCycles = calculateCycles(m_coreConfig, (IssueNode)getNode());
    }

    public void setCoreConfig(IssueNodeAttributes nodeAttributes) {
        m_coreConfig = nodeAttributes.m_coreConfig;
        m_numCycles = calculateCycles(m_coreConfig, (IssueNode)getNode());
    }

    public double getTimeTaken(Case c, Status status, Node ignoreIterationsAboveCond) {
        if (getNode().isUnresolved()) {
            status.mergeStatus(S.UNRESOLVED);
        }
        if (getNode().isRecursion()) {
            status.mergeStatus(S.RECURSION);
        }
        if (((IssueNode)getNode()).isIllegal()) {
            status.mergeStatus(S.ILLEGAL);
        }
        return new TimeUnit(m_coreConfig, m_numCycles, Units.CYCLES).getNs();
    }

    public long getNumCycles(Case c, Status status, Node ignoreIterationsAboveCond) {
        if (getNode().isUnresolved()) {
            status.mergeStatus(S.UNRESOLVED);
        }
        return m_numCycles;
    }

    public long getNumInstructions(Case c, Status status, Node ignoreIterationsAboveCond) {
        IssueNode issueNode = (IssueNode)getNode();
        if (issueNode.isUnresolved()) {
            status.mergeStatus(S.UNRESOLVED);
        }
        return issueNode.isFnop()? 0 : issueNode.getInstructions().size();
    }

    public long getNumFNops(Case c, Status status, Node ignoreIterationsAboveCond) {
        IssueNode issueNode = (IssueNode)getNode();
        if (issueNode.isUnresolved()) {
            status.mergeStatus(S.UNRESOLVED);
        }
        return issueNode.isFnop() ? 1 : 0;
    }

    public long getNumPaths() throws ArithmeticException {
        return 1;
    }

    public void setFixedTime(TimeUnit timeUnit) {
        long cycles = timeUnit.getCycles();
        IssueNode node = (IssueNode)getNode();
        if (node.canPause()) {
            long baseNumCycles = calculateCycles(m_coreConfig, node);
            m_numCycles = cycles > baseNumCycles? cycles : baseNumCycles;
        }
        m_instructionTimeString = timeUnit.getDescription(false);
    }

    public TimeUnit getFixedTime() {
        return new TimeUnit(m_coreConfig, m_numCycles, Units.CYCLES);
    }

    public String getFixedTimeString() {
        assert(m_instructionTimeString != null);
        return m_instructionTimeString;
    }

    static private long calculateCycles(CoreConfig coreConfig, IssueNode node) {
        // We are working out how many scheduling slots this bundle of instruction will be active for.
        // Most bundles will complete before the task is next scheduled viz hwCycles == 1.
        long hwCycles = 0;
        for (Instruction instruction : node.getInstructions()) {
            long cycles = instruction.getNumCycles();
            assert (hwCycles <= 1 || cycles <= 1); // We only expect one multi-cycle per issue.
            if (hwCycles < cycles)
                hwCycles = cycles;
        }
        // If other tasks are using the hardware unit, we will have to wait for them to finish as well as for us to finish.
        // This is a round-robin by task ID - so the worse case unit busy time will be...
        if (hwCycles > 1 && coreConfig != null)
            hwCycles *= coreConfig.m_numThreads;   // N.B. Should be 'cycles * TasksThatCouldHoldResource'.
        // However there are additional delays whilst:
        //      the completing task is scheduled to pick up the result;
        //      the next task is scheduled to run the instruction.
        // The length of these scheduling delays could be several pipeline cycles viz longer than the hardware cycles!
        // **** Currently we do not include the scheduling delays hence our calculations are not the worse case ****

        // Turn the 'hardware cycles' into 'task cycle' units.
        return (long) Math.ceil(hwCycles / TimeUnit.getScheduleLen(coreConfig));
    }
}
