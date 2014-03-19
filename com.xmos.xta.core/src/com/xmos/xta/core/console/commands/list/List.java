package com.xmos.xta.core.console.commands.list;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;

public class List extends CommandContainer {
    private static final String[] COMMAND_NAMES = { "list" };

    static private Command[] m_options = {
        new Labels(),
        new Calls(),
        new AllCalls(),
        new SrcLabels(),
        new AllSrcLabels(),
        new LoopLabels(),
        new AllLoopLabels(),
        new Endpoints(),
        new AllEndpoints(),
        new Branches(),
        new Cores(),
        new Exclusions(),
        new Functions(),
        new FunctionTimes(),
        new InstructionTimes(),
        new Knowns(),
        new Unknowns(),
        new Loops(),
        new LoopPaths(),
        new LoopScopes(),
        new PathTimes(),
        new Sources(),
        new SrcCommands(),
        new SrcLoops(),
        new ThreadStartPoints(),
        new ThreadStopPoints(),
    };

    public List() { super(COMMAND_NAMES); }
    public List(Command command) { super(COMMAND_NAMES, command); }

    public Command[] getSubCommands() { return m_options; }
    protected String getDescription() { return "Lists the given set of items"; }
    protected Command clone(Command command) { return new List(command); }
}
