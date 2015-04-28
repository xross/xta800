package com.xmos.xta.core.console.commands.config;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Config.ConfigTaskPriority;
import com.xmos.xta.core.engine.Engine;

public class TaskPriority  extends Command {
    private static final String[] COMMAND_NAMES = { "taskpriority" };

    private ConfigTaskPriority m_priority;

    public TaskPriority() {
        super(COMMAND_NAMES);
        m_priority = ConfigTaskPriority.LOW;
    }

    public TaskPriority(ConfigTaskPriority priority) {
        super(COMMAND_NAMES);
        m_priority = priority;
    }

    @Override
    public int numArgs() {
        return 1;
    }

    @Override
    public String commandLine() {
        switch (m_priority) {
        case LOW:  return name() + " low";
        case HIGH: return name() + " high";
        }
        assert(false);
        return "";
    }

    @Override
    public String description(Engine engine, boolean detailed) {
        return name() + String.format(" <low/high>: Sets the task priority the analysis is run in (currently: %s)",
                engine.getTaskPriority().toString().toLowerCase());
    }

    @Override
    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length)
            return null;
        if (args[index + 1].equals(ConfigTaskPriority.LOW.toString().toLowerCase()))
            return new TaskPriority(ConfigTaskPriority.LOW);
        if (args[index + 1].equals(ConfigTaskPriority.HIGH.toString().toLowerCase()))
            return new TaskPriority(ConfigTaskPriority.HIGH);
        return null;
    }

    @Override
    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;
        engine.setTaskPriority(m_priority);
    }

}
