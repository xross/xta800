package com.xmos.xta.core.console.commands.remove;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class PathTime extends Command {
    private static final String[] COMMAND_NAMES = { "pathtime" };

    private String m_fromRef;
    private String m_toRef;

    public PathTime() {
        super(COMMAND_NAMES);
    }

    public PathTime(String fromRef, String toRef) {
        super(COMMAND_NAMES);
        m_fromRef = fromRef;
        m_toRef = toRef;
    }

    public int numArgs() {
           return 2;
    }

    public ArgType[] getArgTypes() {
        return new Command.ArgType[] {ArgType.ENDPOINT, ArgType.ENDPOINT};
    }

    public String commandLine() {
           return name() + " " + m_fromRef + " " + m_toRef;
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <from ENDPOINT|*> <to ENDPOINT|*>: Removes the given path time from the list of defines";
    }

    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length) {
            return null;
        }

        return new PathTime(args[index + 1], args[index + 2]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
        if (isExecutableLoaded(engine, console)) {
            engine.removePathTime(getSrc(), m_fromRef, m_toRef);
        }


    }
 }
