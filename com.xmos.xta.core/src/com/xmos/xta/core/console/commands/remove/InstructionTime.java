package com.xmos.xta.core.console.commands.remove;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class InstructionTime extends Command {
	private static final String[] COMMAND_NAMES = { "instructiontime" };

	private String m_refString;

	public InstructionTime() {
		super(COMMAND_NAMES);
	}

	public InstructionTime(String refString) {
		super(COMMAND_NAMES);
		m_refString = refString;
	}

    public int numArgs() {
   		return 1;
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.ENDPOINT};
	}

    public String commandLine() {
       	return name() + " " + m_refString;
    }

    public String description(Engine engine, boolean detailed) {
		return name() + " <ENDPOINT|*>: Removes the given instruction time from the list of defines";
    }

    public Command clone(String[] args, int index) {
    	if ((index + numArgs()) >= args.length) {
			return null;
    	}

    	return new InstructionTime(args[index + 1]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (isExecutableLoaded(engine, console)) {
			engine.removeInstructionTime(getSrc(), m_refString);
		}
    }
}
