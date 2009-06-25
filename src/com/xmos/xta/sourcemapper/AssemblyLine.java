package com.xmos.xta.sourcemapper;

import java.util.LinkedList;
import java.util.List;
import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;

public class AssemblyLine implements Line {	
	private InstructionNode m_instructionNode;
	
	public AssemblyLine(InstructionNode instructionNode) {
		m_instructionNode = instructionNode;
	}

	public List<Line> getChildern() {
		return new LinkedList<Line>();
	}

	public int getLineNumber() {
		return m_instructionNode.getXtaInstruction().getPc();
	}

	public String getText() {
		return m_instructionNode.getXtaInstruction().toString();
	}

	public int getPc() {
		return m_instructionNode.getXtaInstruction().getPc();
	}
	
}