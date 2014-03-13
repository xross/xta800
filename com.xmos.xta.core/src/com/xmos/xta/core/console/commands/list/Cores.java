package com.xmos.xta.core.console.commands.list;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Cores extends Command {
    private static final String[] COMMAND_NAMES = { "tiles", "cores" };

    public Cores() {
        super(COMMAND_NAMES);
    }

    public int numArgs() {
        return 0;
    }

    public String commandLine() {
           return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + ": Lists the active tiles";
    }

    public Command clone(String[] args, int index) {
           return new Cores();
    }

    public void execute(Shell shell, final Engine engine, final Console console) {
        for (CoreId activeCore : engine.getActiveCores()) {
            console.display(activeCore.getDescription() + "\n");
        }
    }
}