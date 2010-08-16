package com.xmos.xta.core.iopasm.instructions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xmos.xta.core.iopasm.Program;

public class Wait extends Instruction {

	private Map<Instruction, String> m_successorResources;
	private Map<Instruction, Integer> m_successorMasks;
    
	public Wait(String label, String labelOffset, List<String> unitStrings, int lineNumber, String comment) {
		super(label, labelOffset, unitStrings, lineNumber, comment);
		m_successorResources = new HashMap<Instruction, String>();
		m_successorMasks = new HashMap<Instruction, Integer>();
	}

	@Override
	public boolean isWait() {
		return true;
	}
	
	@Override
	public void connect(Program program) {
		String control = getControlUnitString().replaceFirst("wait ", "");
		
		int openIndex = control.indexOf('(');
		while (openIndex != -1) {
			int closeIndex = control.indexOf(')');
			if (closeIndex == -1) {
				error("missing ')' in 'wait'");
			}
			String targetStr = control.substring(openIndex + 1, closeIndex);
			addTargets(program, targetStr);

			control = control.substring(closeIndex + 1);
			openIndex = control.indexOf('(');
		}
	}

	private void addTargets(Program program, String control) {
		int barIndex = control.indexOf('|');
		int andIndex = control.indexOf('&');
		
		if (barIndex == -1 || andIndex == -1) {
			error("can't parse control flow target '" + control + "'");
		}

		String label = control.substring(0, barIndex).trim();
		String resource = control.substring(barIndex + 1, andIndex).trim();
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
			addSuccessor(target, resource, mask);
		}
	}
	
	private void addSuccessor(Instruction next, String resource, int mask) {
		m_successors.add(next);
		m_successorResources.put(next, resource);
		m_successorMasks.put(next, mask);
	}
	
	@Override
	public String getConnectorLabel(Instruction next) {
		String label = "";

		if (m_successorResources.containsKey(next)) {
			assert(m_successorMasks.containsKey(next));
			int mask = m_successorMasks.get(next);
			if (mask == 0) {
				label = String.format("[label=\"%s\"]", m_successorResources.get(next));
			} else {
				label = String.format("[label=\"%s&0x%x\"]", m_successorResources.get(next), mask);
			}
		}

		return label;
	}
	
	public String getResourceName(Instruction next) {
		assert(m_successorResources.containsKey(next));
		String resource = m_successorResources.get(next);
		
		// String off any conditional bit (==0, ==1)
		int eqIndex = resource.indexOf('='); 
		if (eqIndex != -1) {
			resource = resource.substring(0, eqIndex);
		}
		return resource;
	}
}
