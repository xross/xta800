package com.xmos.xta.core.engine.analysis.structural.nodes.attributes;

import com.xmos.libs.instructiondecoder.Instruction;
import com.xmos.libs.instructiondecoder.InstructionMnemonics;
import com.xmos.libs.xeloader.XeLoader.Architecture;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Status.S;
import com.xmos.xta.core.engine.utils.CoreConfig;
import com.xmos.xta.core.engine.utils.TimeUnit;
import com.xmos.xta.core.engine.utils.TimeUnit.Units;

public class InstructionNodeAttributes extends NodeAttributes {
	private long m_numCycles;
	private Instruction m_instruction;
	private CoreConfig m_coreConfig;
	private Architecture m_architecture;
	private String m_instructionTimeString;

	public InstructionNodeAttributes(InstructionNode node) {
		super(node);
		m_instruction = node.getInstruction();
		m_numCycles = m_instruction.getNumCycles();
		m_coreConfig = null;
		m_architecture = Architecture.UNKNOWN;
	}

	public void setCoreConfig(CoreConfig coreConfig, Architecture architecture) {
		m_coreConfig = coreConfig;
		m_architecture = architecture;
		m_numCycles = getThreadAdjustedCycles();
	}

	public double getTimeTaken(Case c, Status status, Node ignoreIterationsAboveCond) {
		if (getNode().getUnresolved()) {
			status.mergeStatus(S.UNRESOLVED);
		}
		if (getNode().getRecursion()) {
			status.mergeStatus(S.RECURSION);			
		}
		if (m_instruction.getIsIllegal()) {
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
		if (getNode().getUnresolved()) {
			status.mergeStatus(S.UNRESOLVED);
		}
		if (!m_instruction.isFnop()) {
			return 1;
		}
		return 0;
	}

	public long getNumFNops(Case c, Status status, Node ignoreIterationsAboveCond) {
		if (getNode().getUnresolved()) {
			status.mergeStatus(S.UNRESOLVED);
		}
		if (m_instruction.isFnop()) {
			return 1;
		}
		return 0;
	}

	public long getNumPaths() throws ArithmeticException {
		return 1;
	}

	public void setFixedTime(TimeUnit timeUnit) {
		long cycles = timeUnit.getCycles();
		if (m_instruction.canPause()) {
			long baseNumCycles = getThreadAdjustedCycles();
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

	private long getThreadAdjustedCycles() {
		assert(m_architecture != Architecture.UNKNOWN);
		assert(m_coreConfig != null);

		InstructionMnemonics instructionMnemonics = new InstructionMnemonics(m_architecture);
		if (instructionMnemonics.isDivide(m_instruction) ||
			instructionMnemonics.isRemainder(m_instruction)) {
			return (m_coreConfig.m_numThreads < 4) ?
					m_instruction.getNumCycles() * m_coreConfig.m_numThreads :
					m_instruction.getNumCycles() * 4;
		}
		return m_instruction.getNumCycles();
	}
}
