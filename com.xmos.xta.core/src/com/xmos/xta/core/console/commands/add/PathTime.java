package com.xmos.xta.core.console.commands.add;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class PathTime extends Command {
	private static final String[] COMMAND_NAMES = { "pathtime" };

	private String m_fromRef;
	private String m_toRef;
	private String m_value;
	private String m_mode;

	public PathTime() {
		super(COMMAND_NAMES);
	}

	public PathTime(String fromRef, String toRef, String value, String mode) {
		super(COMMAND_NAMES);
		m_fromRef = fromRef;
		m_toRef = toRef;
		m_value = value;
		m_mode = mode;
	}

    public int numArgs() {
   		return 4;
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.ENDPOINT, ArgType.ENDPOINT};
	}

    public String commandLine() {
       	return name() + " " + m_fromRef + " " + m_toRef + " " + m_value + " " + m_mode;
    }

    public String description(Engine engine, boolean detailed) {
    	String descString = name() + " <from ENDPOINT_REF> <to ENDPOINT_REF> <value> <mode>: Adds the given path time to the list of defines";
       	if (detailed) {
       		descString += "\n" + TimeUnit.getHelp();
       	}
    	return descString;
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) {
			return null;
		}

	    TimeUnit timeUnit = new TimeUnit(args[index + 3], args[index + 4]);
	    if (timeUnit.isValid()) {
	    	return new PathTime(args[index + 1], args[index + 2], args[index + 3], args[index + 4]);
	    }
	    return null;
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console)) {
			return;
		}

		TimeUnit timeUnit = new TimeUnit(m_value, m_mode);
		assert(timeUnit.isValid());
		engine.addPathTime(getSrc(), m_fromRef, m_toRef, timeUnit);
    }
 }
