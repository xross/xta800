package com.xmos.xta.core.console.commands.config;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Config.ConfigThreadPriority;
import com.xmos.xta.core.engine.Engine;

public class ThreadPriority  extends Command {
    private static final String[] COMMAND_NAMES = { "taskpriority", "threadpriority" };

    private ConfigThreadPriority m_priority;

    public ThreadPriority() {
        super(COMMAND_NAMES);
        m_priority = ConfigThreadPriority.LOW;
    }

    public ThreadPriority(ConfigThreadPriority priority) {
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
                engine.getThreadPriority().toString().toLowerCase());
    }

    @Override
    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length)
            return null;
        if (args[index + 1].equals(ConfigThreadPriority.LOW.toString().toLowerCase()))
            return new ThreadPriority(ConfigThreadPriority.LOW);
        if (args[index + 1].equals(ConfigThreadPriority.HIGH.toString().toLowerCase()))
            return new ThreadPriority(ConfigThreadPriority.HIGH);
        return null;
    }

    @Override
    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;
        engine.setThreadPriority(m_priority);
    }

}
