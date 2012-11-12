package com.xmos.xta.core.console.commands.set;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class PathTime extends Command {
	private static final String[] COMMAND_NAMES = { "pathtime" };

	private int m_routeId;
	private String m_fromRef;
	private String m_toRef;
	private String m_value;
	private String m_mode;

	public PathTime() {
		super(COMMAND_NAMES);
	}

	public PathTime(int routeId, String fromRef, String toRef, String value, String mode) {
		super(COMMAND_NAMES);
		m_routeId = routeId;
		m_fromRef = fromRef;
		m_toRef = toRef;
		m_value = value;
		m_mode = mode;
	}

    public int numArgs() {
    	return 5;
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.IGNORE, ArgType.ENDPOINT, ArgType.ENDPOINT};
	}

    public String name() {
    	return "pathtime";
    }

    public String commandLine() {
    	return name() + " " + getRouteId(m_routeId) + " " + m_fromRef + " " + m_toRef + " " + m_value + " " + m_mode;
    }

    public String description(Engine engine, boolean detailed) {
    	String descString = name() + " <route id> <from ENDPOINT> <to ENDPOINT> <value> <MODE>: Set timing requirement for the given path on the given route";
       	if (detailed) {
       		descString += "\n" + TimeUnit.getHelp();
       	}
        return descString;
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

		try {
			int routeId    = getRouteId(args[index + 1]);
    	    String fromRef = args[index + 2];
            String toRef   = args[index + 3];
    	    String value   = args[index + 4];
   		    String mode    = args[index + 5];

	        TimeUnit timeUnit = new TimeUnit(value, mode);
	        if (timeUnit.isValid()) {
	    	    return new PathTime(routeId, fromRef, toRef, value, mode);
	        }
	    } catch (NumberFormatException e) {}
	    return null;
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

        if (!validateRouteId(engine, console, m_routeId))
            return;
		
		TimeUnit timeUnit = new TimeUnit(m_value, m_mode);
		assert(timeUnit.isValid());
        engine.setPathTime(getSrc(), m_routeId, m_fromRef, m_toRef, timeUnit);
    }
 }
