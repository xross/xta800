package com.xmos.xta.core.console.commands.remove;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Branch extends Command {
	private static final String[] COMMAND_NAMES = { "branch" };

	private String m_fromRefString;
	private Collection<String> m_toRefStrings;

	public Branch() {
		super(COMMAND_NAMES);
		m_toRefStrings = new ArrayList<String>();
	}

	public Branch(String fromRefString, Collection<String> toRefStrings) {
		super(COMMAND_NAMES);
		m_fromRefString = fromRefString;
		m_toRefStrings = toRefStrings;
	}

	public int numArgs() {
        return 1 + m_toRefStrings.size();
	}

	public ArgType[] getArgTypes() {
        return super.getArgTypes();
	}

	public String commandLine() {
		
		StringBuffer str = new StringBuffer();
		str.append(name());
		str.append(" ");
		str.append(m_fromRefString);
		for (String toRefString : m_toRefStrings) {
			str.append(" ");
			str.append(toRefString);
		}
		return str.toString();
	}

	public String description(Engine engine, boolean detailed) {
        return name() + " <from BRANCH|*> [<to INSTRUCTION|*>]+: Removes the given from/to references from the branches list";
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) {
			return null;
		}

		int i = 2;
		Collection<String> toRefStrings = new ArrayList<String>();
		while (!areArgsDone(args, index + i)) {
			toRefStrings.add(args[index + i]);
			++i;
		}
		return new Branch(args[index + 1], toRefStrings);
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console)) {
			return;
		}
		engine.removeBranch(getSrc(), m_fromRefString, m_toRefStrings);
	}
}
