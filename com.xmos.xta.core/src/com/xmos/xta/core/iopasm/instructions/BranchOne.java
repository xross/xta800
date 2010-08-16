package com.xmos.xta.core.iopasm.instructions;

import java.util.List;

import com.xmos.xta.core.iopasm.Program;

public class BranchOne extends Instruction {

	public BranchOne(String label, String labelOffset, List<String> unitStrings, int lineNumber, String comment) {
		super(label, labelOffset, unitStrings, lineNumber, comment);
	}

	@Override
	public boolean isWait() {
		return false;
	}

	@Override
	public void connect(Program program) {
		String control = getControlUnitString();
		
		if ((control.length() == 0) || (control.startsWith("ct0") || control.startsWith("ct1"))) {
			// Straight line code
			Instruction next = program.getInstructionFollowing(this);
			addSuccessor(next);
			
		} else if (control.startsWith("bra")) {
			connectBranch(program);
			
		} else {
			error("don't understand control flow '" + control + "'");
		}
	}

	private void connectBranch(Program program) {
		String control = getControlUnitString().replaceFirst("bra ", "");
		
		int barIndex = control.indexOf('|');
		int andIndex = control.indexOf('&');
		
		if (barIndex == -1 && andIndex == -1) {
			String label = control;
			Instruction target = program.getTarget(this, label, "");
			addSuccessor(target);
			
		} else if (barIndex != -1 && andIndex != -1) {
			// This should be a BranchMany instruction
			assert(false);
			
		} else if (barIndex != -1 && andIndex == -1) {
			String label = control.substring(0, barIndex);
			String offset = control.substring(barIndex + 1);
			Instruction target = program.getTarget(this, label, offset);
			addSuccessor(target);

		} else {
			error("don't know how to parse control '" + getControlUnitString() + "'");
		}
	}

	private void addSuccessor(Instruction next) {
		assert(m_successors.isEmpty());
		m_successors.add(next);
	}

	@Override
	public String getConnectorLabel(Instruction next) {
		return "";
	}
}
