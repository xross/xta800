package com.xmos.xta.core.console.commands.config;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;

public class Config extends CommandContainer {
    private static final String[] COMMAND_NAMES = { "config" };

    static private Command[] m_options = {
        new Case(),
        new ErrorAsWarn(),
        new Freq(),
        new From(),
        new LoopPoint(),
        new Scale(),
        new SrcPaths(),
        new Threads(),
        new TimeOut(),
        new TimingErrors(),
        new To(),
        new Verbosity(),
        new WarnAsErrors(),
        new Warn(),
        new NoWarn(),
        new StartMode(),
        new HighPriorityTasks(),
        new TaskPriority(),
    };

    public Config() { super(COMMAND_NAMES); }
    public Config(Command command) { super(COMMAND_NAMES, command); }

    public Command[] getSubCommands() { return m_options; }
    protected String getDescription() { return "Configures the given option"; }
    protected Command clone(Command command) { return new Config(command); }
}
