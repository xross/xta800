package com.xmos.xta.core.console.commands.set;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class FunctionTime extends Command {
    private static final String[] COMMAND_NAMES = { "functiontime" };

    private int m_routeId;
    private String m_functionName;
    private String m_value;
    private String m_mode;

    public FunctionTime() {
        super(COMMAND_NAMES);
        m_routeId = 0;
        m_functionName = "";
    }

    public FunctionTime(int routeId, String functionName, String value, String mode) {
        super(COMMAND_NAMES);
        m_routeId = routeId;
        m_functionName = functionName;
        m_value = value;
        m_mode = mode;
    }

    public int numArgs() {
        return 4;
    }

    public ArgType[] getArgTypes() {
        return new Command.ArgType[] {ArgType.IGNORE, ArgType.FUNCTION};
    }

    public String commandLine() {
        return name() + " " + getRouteId(m_routeId) + " " + m_functionName + " " + m_value + " " + m_mode;
    }

    public String description(Engine engine, boolean detailed) {
        String descString = name() + " <route id> <FUNCTION> <value> <MODE>: Set timing requirement for the given function on the given route";
           if (detailed) {
               descString += "\n" + TimeUnit.getHelp();
           }
        return descString;
    }

    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length)
            return null;

        try {
            int routeId = getRouteId(args[index + 1]);
            String name  = args[index + 2];
            String value = args[index + 3];
               String mode  = args[index + 4];

            TimeUnit timeUnit = new TimeUnit(value, mode);
            if (timeUnit.isValid()) {
                return new FunctionTime(routeId, name, value, mode);
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
        engine.setFunctionTime(getSrc(), m_routeId, m_functionName, timeUnit);
    }
}
