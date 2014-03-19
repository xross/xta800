package com.xmos.xta.core.console.commands.clear;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Clear extends Command  {
    private static final String[] COMMAND_NAMES = { "clear" };

    public Clear() {
        super(COMMAND_NAMES);
    }

    public int numArgs() {
        return 0;
    }

    public String commandLine() {
        return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + ": Clears the screen (Gui mode only)";
    }

    public Command clone(String[] args, int index) {
        return new Clear();
    }

    public void execute(Shell shell, Engine engine, Console console) {
        console.clearScreen();
    }
}

