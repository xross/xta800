package com.xmos.xta.core.console.commands.set;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class InstructionTime extends Command {
	private static final String[] COMMAND_NAMES = { "instructiontime" };

	private int m_routeId;
	private String m_refString;
	private String m_value;
	private String m_mode;

	public InstructionTime() {
		super(COMMAND_NAMES);
	}

	public InstructionTime(int routeId, String refString, String value, String mode) {
		super(COMMAND_NAMES);
		m_routeId = routeId;
		m_refString = refString;
		m_value = value;
		m_mode = mode;
	}

    public int numArgs() {
   		return 4;
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.IGNORE, ArgType.ENDPOINT};
	}

    public String commandLine() {
       	return name() + String.format(" %s %s %s %s", getRouteId(m_routeId),
       			m_refString, m_value, m_mode);
    }

    public String description(Engine engine, boolean detailed) {
        String descString = name() + " <route id> <ENDPOINT> <value> <MODE>: Sets the time taken for the instruction at the given pc";
       	if (detailed) {
           	descString += "\n";
           	descString += TimeUnit.getHelp();
       	}
       	return descString;
    }

    public Command clone(String[] args, int index) {
    	if ((index + numArgs()) >= args.length)
			return null;

    	int routeId = 0;
   		try {
     		routeId = getRouteId(args[index + 1]);
    	} catch (NumberFormatException e) {
    		return null;
    	}

    	String ref = args[index + 2];
    	String value = args[index + 3];
   		String mode = args[index + 4];

	    TimeUnit timeUnit = new TimeUnit(value, mode);
	    if (timeUnit.isValid()) {
	    	return new InstructionTime(routeId, ref, value, mode);
	    }
	    return null;
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		TimeUnit timeUnit = new TimeUnit(m_value, m_mode);
		assert(timeUnit.isValid());
        if (!engine.setInstructionTime(getSrc(), m_routeId, m_refString, timeUnit)) {
   			engine.notify(getSrc(), Notifier.NOTIFY_ERROR, String.format(
   	       			"failed to set instruction time on reference id: %s", m_refString));
		}
    }
}
