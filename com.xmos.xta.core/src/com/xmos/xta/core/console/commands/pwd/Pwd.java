package com.xmos.xta.core.console.commands.pwd;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Pwd extends Command {
    private static final String[] COMMAND_NAMES = { "pwd" };

    public Pwd() {
        super(COMMAND_NAMES);
    }

    public int numArgs() {
        return 0;
    }

    public String commandLine() {
        return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + ": Displays the current working directory";
    }

    public Command clone(String[] args, int index) {
        return new Pwd();
    }

    public void execute(Shell shell, Engine engine, Console console) {
        String currentDirectory = System.getProperty("user.dir");
        console.display(String.format("%s\n", currentDirectory));
    }
}
