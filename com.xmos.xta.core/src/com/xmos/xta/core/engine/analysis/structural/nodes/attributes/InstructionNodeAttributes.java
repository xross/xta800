package com.xmos.xta.core.engine.analysis.structural.nodes.attributes;

import com.xmos.libs.xeloader.XeLoader.Architecture;
import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.TimeUnit.Units;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Status.S;
import com.xmos.xta.core.engine.instructions.InstructionMnemonics;
import com.xmos.xta.core.engine.instructions.XtaInstruction;

public class InstructionNodeAttributes extends NodeAttributes {
	private long m_numCycles;
	private XtaInstruction m_instruction;
	private Config m_config;
	private Architecture m_architecture;
	private String m_instructionTimeString;

	public InstructionNodeAttributes(InstructionNode node) {
		super(node);
		m_instruction = node.getXtaInstruction();
		m_numCycles = m_instruction.getNumCycles();
		m_config = null;
		m_architecture = Architecture.UNKNOWN;
	}
	
	public void setConfig(Config config, Architecture architecture) {
		m_config = config;
		m_architecture = architecture;
		m_numCycles = getThreadAdjustedCycles();		
	}
	
	public double getTimeTaken(Case c, Status status, Node ignoreIterationsAboveCond) {
		if (m_config == null) {
			status.mergeStatus(S.NOT_AVALIABLE);
			return 0.0;
		}
		if (getNode().isUnresolved()) {
			status.mergeStatus(S.UNRESOLVED);
		}
		return new TimeUnit(m_config, m_numCycles, Units.CYCLES).getNs();
	}

	public long getNumCycles(Case c, Status status, Node ignoreIterationsAboveCond) {
		if (getNode().isUnresolved()) {
			status.mergeStatus(S.UNRESOLVED);
		}
		return m_numCycles;
	}
	
	public long getNumInstructions(Case c, Status status, Node ignoreIterationsAboveCond) {		
		if (getNode().isUnresolved()) {
			status.mergeStatus(S.UNRESOLVED);
		}
		if (!m_instruction.isFnop()) {
			return 1;
		}
		return 0;
	}

	public long getNumFNops(Case c, Status status, Node ignoreIterationsAboveCond) {
		if (getNode().isUnresolved()) {
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
		return new TimeUnit(m_config, m_numCycles, Units.CYCLES);
	}

	public String getFixedTimeString() {
		assert(m_instructionTimeString != null);
		return m_instructionTimeString;
	}
	
	private long getThreadAdjustedCycles() {
		assert(m_architecture != Architecture.UNKNOWN);
		
		InstructionMnemonics instructionMnemonics = new InstructionMnemonics(m_architecture);		
		if (instructionMnemonics.isDivide(m_instruction) ||
			instructionMnemonics.isRemainder(m_instruction)) {
			return (m_config.m_numThreads < 4) ? 
					m_instruction.getNumCycles() * m_config.m_numThreads :
					m_instruction.getNumCycles() * 4;
		}
		return m_instruction.getNumCycles();
	}
}
