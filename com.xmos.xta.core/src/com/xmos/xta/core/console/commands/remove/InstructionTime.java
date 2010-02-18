package com.xmos.xta.core.console.commands.remove;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class InstructionTime extends Command {
	private String m_refString;

	public InstructionTime() {
	}

	public InstructionTime(String refString) {
		m_refString = refString;
	}

    public int numArgs() {
   		return 1;
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.ENDPOINT};
	}

    public String name() {
		return "instructiontime";
    }

    public String commandLine() {
       	return "instructiontime " + m_refString;
    }

    public String description(Engine engine, boolean detailed) {
		return "instructiontime <ENDPOINT_REF|*>: Removes the given instruction time from the list of defines";
    }

    public Command clone(String[] args, int index) {
    	if ((index + numArgs()) >= args.length) {
			return null;
    	}

    	return new InstructionTime(args[index + 1]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console)) {
			return;
		}

		engine.removeInstructionTime(getSrc(), m_refString);
    }
}
