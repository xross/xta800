package com.xmos.xta.core.console.commands.history;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class History extends Command {
    private static final String[] COMMAND_NAMES = { "history" };

    public History() {
        super(COMMAND_NAMES);
    }

    public int numArgs() {
        return 0;
    }

    public String commandLine() {
        return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + ": Displays the command history";
    }

    public Command clone(String[] args, int index) {
        return new History();
    }

    public void execute(Shell shell, Engine engine, Console console) {
        int index = 1;
        for (Command command : shell.getCommandHistory()) {
            console.display(String.format("%d: %s", index, command.getFullCommandLine()));
            console.display("\n");
            ++index;
        }
    }
}
