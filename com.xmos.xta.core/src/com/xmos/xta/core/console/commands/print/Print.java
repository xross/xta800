package com.xmos.xta.core.console.commands.print;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;

public class Print extends CommandContainer {
    private static final String[] COMMAND_NAMES = { "print" };

    static private Command[] m_options = {
        new Summary(),
        new Structure(),
        new Asm(),
        new Src(),
        new Trace(),
        new RouteInfo(),
        new NodeInfo(),
        new Warnings(),
        new TimeDistribution(),
    };

    public Print() { super(COMMAND_NAMES); }
    public Print(Command command) { super(COMMAND_NAMES, command); }

    public Command[] getSubCommands() { return m_options; }
    protected String getDescription() { return "Display analysis information"; }
    protected Command clone(Command command) { return new Print(command); }
}
