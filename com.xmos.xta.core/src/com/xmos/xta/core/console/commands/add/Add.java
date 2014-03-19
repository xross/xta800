package com.xmos.xta.core.console.commands.add;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;

public class Add extends CommandContainer {
    private static final String[] COMMAND_NAMES = { "add" };

    static private Command[] m_options = {
        new Branch(),
        new Core(),
        new Exclusion(),
        new FunctionTime(),
        new InstructionTime(),
        new Loop(),
        new LoopPath(),
        new LoopScope(),
        new PathTime(),
    };

    public Add() { super(COMMAND_NAMES); }
    public Add(Command command) { super(COMMAND_NAMES, command); }

    public Command[] getSubCommands() { return m_options; }
    protected String getDescription() { return "Add item to given list"; }
    protected Command clone(Command command) { return new Add(command); }
}
