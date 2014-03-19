package com.xmos.xta.core.console.commands.debug;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class DumpActiveExclusions extends Command {
    private static final String[] COMMAND_NAMES = { "dumpactiveexclusions" };

    public DumpActiveExclusions() {
        super(COMMAND_NAMES);
    }

    public int numArgs() {
        return 0;
    }

    public String commandLine() {
        return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + ": Dump a list of pcs that the exclusions have resolved to";
    }

    public Command clone(String[] args, int index) {
        return new DumpActiveExclusions();
    }

    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;

        engine.getDebugger().dumpActiveExclusions(getSrc(), console);
    }
}
