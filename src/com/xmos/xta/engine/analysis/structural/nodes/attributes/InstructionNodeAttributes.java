package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.engine.Config;
import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.Status.S;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class InstructionNodeAttributes extends NodeAttributes {
	private long m_numCycles;
	private XtaInstruction m_instruction;
	private Config m_config;

	public InstructionNodeAttributes(InstructionNode node) {
		super(node);
		m_instruction = node.getXtaInstruction();
		m_numCycles = m_instruction.getNumCycles();
		m_config = null;
	}
	
	public void setConfig(Config config) {
		m_config = config;
	}
	
	public double getTimeTaken(Case c, Status status, Node ignoreIterationsAboveSection) {
		if (getNode().isUnresolved()) {
			status.setStatus(S.UNRESOLVED);
		}
		return getInstructionTimeTaken();
	}

	public long getNumCycles(Case c, Status status, Node ignoreIterationsAboveSection) {
		if (getNode().isUnresolved()) {
			status.setStatus(S.UNRESOLVED);
		}
		return m_numCycles;
	}
	
	public long getNumInstructions(Case c, Status status, Node ignoreIterationsAboveSection) {		
		if (getNode().isUnresolved()) {
			status.setStatus(S.UNRESOLVED);
		}
		return 1;
	}
	
	public long getNumFNops(Case c, Status status, Node ignoreIterationsAboveSection) {
		if (getNode().isUnresolved()) {
			status.setStatus(S.UNRESOLVED);
		}
		if (m_instruction.isFnop()) {
			return 1;
		}
		return 0;
	}
	
	public long getNumPaths() {
		return 1;
	}
	
	public void setInstructionNumCycles(long numCycles) {
		if (m_instruction.canPause()) {
			if (numCycles > m_instruction.getNumCycles()) {
				m_numCycles = numCycles;		
			} else {								
			    m_numCycles = m_instruction.getNumCycles();	
			}
		}					
	}

	public void unsetInstructionNumCycles() {
	    m_numCycles = m_instruction.getNumCycles();
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
}
