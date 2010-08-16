package com.xmos.xta.core.iopasm.instructions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xmos.xta.core.iopasm.Program;

public class BranchMany extends Instruction {

	private Map<Instruction, String> m_successorFlags;
	private Map<Instruction, Integer> m_successorMasks;
    
	public BranchMany(String label, String labelOffset, List<String> unitStrings, int lineNumber, String comment) {
		super(label, labelOffset, unitStrings, lineNumber, comment);
		m_successorFlags = new HashMap<Instruction, String>();
		m_successorMasks = new HashMap<Instruction, Integer>();
	}

	@Override
	public boolean isWait() {
		return false;
	}

	@Override
	public void connect(Program program) {
		String control = getControlUnitString().replaceFirst("bra ", "");

		int barIndex = control.indexOf('|');
		int andIndex = control.indexOf('&');

		assert(barIndex != -1 && andIndex != -1);
		addTargets(program, control);
	}

	private void addTargets(Program program, String control) {
		int barIndex = control.indexOf('|');
		int andIndex = control.indexOf('&');
		
		if (barIndex == -1 || andIndex == -1) {
			error("can't parse control flow target '" + control + "'");
		}

		String label = control.substring(0, barIndex).trim();
		String flag = control.substring(barIndex + 1, andIndex).trim();
		String maskString = control.substring(andIndex + 1).trim();

		int mask = 0;
		try {
			if (maskString.startsWith("0x")) {
				mask = Integer.parseInt(maskString.substring(2), 16);
			} else {
				mask = Integer.parseInt(maskString);
			}
		} catch (NumberFormatException e) {
			error("can't parse mask '" + maskString + "' in control");
		}

		// Connect to all targets with the same label (ignore mask for the moment)
		for (Instruction target : program.getTargets(this, label)) {
			addSuccessor(target, flag, mask);
		}
	}
	
	private void addSuccessor(Instruction next, String flag, int mask) {
		m_successors.add(next);
		m_successorFlags.put(next, flag);
		m_successorMasks.put(next, mask);
	}

	@Override
	public String getConnectorLabel(Instruction next) {
		String label = "";

		if (m_successorFlags.containsKey(next)) {
			assert(m_successorMasks.containsKey(next));
			int mask = m_successorMasks.get(next);
			if (mask == 0) {
				label = String.format("[label=\"%s\"]", m_successorFlags.get(next));
			} else {
				label = String.format("[label=\"%s&0x%x\"]", m_successorFlags.get(next), mask);
			}
		}

		return label;
	}
}
