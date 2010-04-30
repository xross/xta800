package com.xmos.xta.core.console.commands.add;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class InstructionTime extends Command {
	private static final String[] COMMAND_NAMES = { "instructiontime" };

	private String m_refString;
	private String m_value;
	private String m_mode;

	public InstructionTime() {
		super(COMMAND_NAMES);
	}

	public InstructionTime(String refString, String value, String mode) {
		super(COMMAND_NAMES);
		m_refString = refString;
		m_value = value;
		m_mode = mode;
	}

    public int numArgs() {
   		return 3;
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.ENDPOINT};
	}

    public String commandLine() {
       	return name() + " " + m_refString + " " + m_value + " " + m_mode;
    }

    public String description(Engine engine, boolean detailed) {
    	String descString = name() + " <ENDPOINT> <value> <MODE>: Adds the given instruction time to the list of defines";
       	if (detailed) {
       		descString += "\n" + TimeUnit.getHelp();
       	}
    	return descString;
    }

    public Command clone(String[] args, int index) {
    	if ((index + numArgs()) >= args.length) {
			return null;
    	}

	    TimeUnit timeUnit = new TimeUnit(args[index + 2], args[index + 3]);
	    if (timeUnit.isValid()) {
	    	return new InstructionTime(args[index + 1], args[index + 2], args[index + 3]);
	    }
	    return null;
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console)) {
			return;
		}

	    TimeUnit timeUnit = new TimeUnit(m_value, m_mode);
	    assert(timeUnit.isValid());
		engine.addInstructionTime(getSrc(), m_refString, timeUnit);
    }
}
