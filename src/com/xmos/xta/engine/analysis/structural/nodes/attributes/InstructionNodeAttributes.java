package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import com.xmos.xeloader.XeLoader.Architecture;
import com.xmos.xta.engine.Config;
import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.Status.S;
import com.xmos.xta.engine.instructions.InstructionMnemonics;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class InstructionNodeAttributes extends NodeAttributes {
	private long m_numCycles;
	private XtaInstruction m_instruction;
	private Config m_config;
	private Architecture m_architecture;

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
		m_numCycles = getThreadAdjustedNumCycles();		
	}
	
	public double getTimeTaken(Case c, Status status, Node ignoreIterationsAboveCond) {
		if (getNode().isUnresolved()) {
			status.mergeStatus(S.UNRESOLVED);
		}
		return getInstructionTimeTaken();
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
	
	public void setInstructionNumCycles(long numCycles) {
		if (m_instruction.canPause()) {
			long baseNumCycles = getThreadAdjustedNumCycles();
			if (numCycles > baseNumCycles) {
				m_numCycles = numCycles;		
			} else {								
			    m_numCycles = baseNumCycles;	
			}
		}					
	}

	public void unsetInstructionNumCycles() {
	    m_numCycles = getThreadAdjustedNumCycles();
		setUnknownState(UnknownState.UNKNOWN);				    
	}
	
	public long getInstructionNumCycles() {
		return m_numCycles;
	}
	
	public void setInstructionTimeTaken(double timeTaken) {
		assert(m_config != null);
    	int threadFactor = (m_config.m_numThreads > 4) ? m_config.m_numThreads : 4;		
       	setInstructionNumCycles((int)((timeTaken * m_config.m_frequency) / (threadFactor * 1000)));
	}

	public void unsetInstructionTimeTaken() {
		unsetInstructionNumCycles();
	}
	
	public double getInstructionTimeTaken() {
		assert(m_config != null);		
    	int threadFactor = (m_config.m_numThreads > 4) ? m_config.m_numThreads : 4;
        return ((double)(threadFactor * 1000) / (double)m_config.m_frequency) * m_numCycles;
	}
	
	private long getThreadAdjustedNumCycles() {
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
