package com.xmos.xta.core.engine.analysis.structural.nodes.attributes;


import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.libs.core.instructiondecoder.InstructionMnemonics;
import com.xmos.libs.core.xeloader.XeLoader.Architecture;
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
    private Architecture m_architecture;
    private String m_instructionTimeString;

    public IssueNodeAttributes(IssueNode node) {
        super(node);
        m_coreConfig = null;
        m_architecture = Architecture.UNKNOWN;
        m_numCycles = calculateCycles(); // accesses m_coreConfig, m_architecture
    }

    public void setCoreConfig(CoreConfig coreConfig, Architecture architecture) {
        m_coreConfig = coreConfig;
        m_architecture = architecture;
        m_numCycles = calculateCycles(); // accesses m_coreConfig, m_architecture
    }

    public double getTimeTaken(Case c, Status status, Node ignoreIterationsAboveCond) {
        if (getNode().getUnresolved()) {
            status.mergeStatus(S.UNRESOLVED);
        }
        if (getNode().getRecursion()) {
            status.mergeStatus(S.RECURSION);
        }
        if (((IssueNode)getNode()).isIllegal()) {
            status.mergeStatus(S.ILLEGAL);
        }
        return new TimeUnit(m_coreConfig, m_numCycles, Units.CYCLES).getNs();
    }

    public long getNumCycles(Case c, Status status, Node ignoreIterationsAboveCond) {
        if (getNode().getUnresolved()) {
            status.mergeStatus(S.UNRESOLVED);
        }
        return m_numCycles;
    }

    public long getNumInstructions(Case c, Status status, Node ignoreIterationsAboveCond) {
        IssueNode issueNode = (IssueNode)getNode();
        if (issueNode.getUnresolved()) {
            status.mergeStatus(S.UNRESOLVED);
        }
        return issueNode.isFnop()? 0 : issueNode.getInstructions().size();
    }

    public long getNumFNops(Case c, Status status, Node ignoreIterationsAboveCond) {
        IssueNode issueNode = (IssueNode)getNode();
        if (issueNode.getUnresolved()) {
            status.mergeStatus(S.UNRESOLVED);
        }
        return issueNode.isFnop() ? 1 : 0;
    }

    public long getNumPaths() throws ArithmeticException {
        return 1;
    }

    public void setFixedTime(TimeUnit timeUnit) {
        long cycles = timeUnit.getCycles();
        if (((IssueNode)getNode()).canPause()) {
            long baseNumCycles = calculateCycles();
            if (cycles > baseNumCycles) {
                m_numCycles = cycles;
            } else {
                m_numCycles = baseNumCycles;
            }
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

    private long calculateCycles() {
        InstructionMnemonics instructionMnemonics = null;
        if (m_coreConfig != null && m_architecture != Architecture.UNKNOWN);
            instructionMnemonics = new InstructionMnemonics(m_architecture);

        long cycles = 0;
        long cyclesDivRem = 0;
        for (Instruction instruction : ((IssueNode)getNode()).getInstructions()) {
            if (instructionMnemonics != null &&
                (instructionMnemonics.isDivide(instruction) ||
                 instructionMnemonics.isRemainder(instruction))) {
                    assert (cyclesDivRem == 0);// We only expect one div/rem per issue.
                    cyclesDivRem = instruction.getNumCycles();
            }
            else if (instruction.getNumCycles() > cycles) {
                cycles = instruction.getNumCycles();
            }
        }

        if (cyclesDivRem > 0) {
            assert (cycles <= 1); // We only expect single cycle instructions issued with a div/rem.
            return cyclesDivRem * (m_coreConfig.m_numThreads < 4 ? m_coreConfig.m_numThreads : 4);
        }
        return cycles;
    }
}
