package com.xmos.xta.core.console.commands.config;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class TimeOut extends Command {
    private static final String[] COMMAND_NAMES = { "timeout" };

    private int m_time;

    public TimeOut() {
        super(COMMAND_NAMES);
        m_time = 0;
    }

    public TimeOut(int time) {
        super(COMMAND_NAMES);
        m_time = time;
    }

    public int numArgs() {
        return 1;
    }

    public String commandLine() {
        return name() + String.format(" %d", m_time);
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <seconds>: Sets the tools timeout on load";
    }

    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length)
            return null;

        int time;
        try {
            time = Integer.parseInt(args[index + 1]);
        } catch (NumberFormatException e) {
            return null;
        }
        return new TimeOut(time);
    }

    public void execute(Shell shell, Engine engine, Console console) {
        engine.setAnalysisTimeOut(m_time);
    }
}
