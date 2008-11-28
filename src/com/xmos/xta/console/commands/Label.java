package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

public class Label extends Command {

	private String m_label;
	private int m_pc;

	Label() {
		m_label = null;
	}

	Label(String label, int pc) {
		m_label = label;
		m_pc = pc;
	}

	public int numArgs() {
		return 2;
	}

	public String name() {
		return "label";
	}

	public String commandLine() {
		return "label 0x" + Integer.toHexString(m_pc) + " " + m_label;
	}

	public String description(Engine engine, boolean detailed) {
		return "label <pc> <label>: adds a label to the given pc";
	}

	public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length)
			return null;
		String pcString = null;
		if (args[index + 1].startsWith("0x")) {
			pcString = args[index + 1].substring(2);
		} else {
			pcString = args[index + 1];
		}
		int pc;
		try {
			pc = Integer.parseInt(pcString, 16);
		} catch (NumberFormatException e) {
			return null;
		}
		return new Label(args[index + 2], pc);
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;
		engine.addLabel(m_pc, m_label);
	}
}
