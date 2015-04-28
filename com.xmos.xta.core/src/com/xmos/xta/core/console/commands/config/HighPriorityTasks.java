package com.xmos.xta.core.console.commands.config;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class HighPriorityTasks extends Command {


    private static final String[] COMMAND_NAMES = { "highprioritytasks", "hightasks" };

    private String m_coreReference;
    private int m_numHighTasks;

    public HighPriorityTasks() {
        super(COMMAND_NAMES);
        m_coreReference = "";
        m_numHighTasks = 0;
    }

    public HighPriorityTasks(String coreReference, int numHighTasks) {
        super(COMMAND_NAMES);
        m_coreReference = coreReference;
        m_numHighTasks = numHighTasks;
    }

    public int numArgs() {
        return 2;
    }

    public String commandLine() {
        return name() + String.format(" %s %d", m_coreReference, m_numHighTasks);
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <tile id> <num tasks>: Sets number of high priority tasks currently executing on the given tile";
    }

    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length)
            return null;

        int numHighTasks;
        try {
            numHighTasks = Integer.parseInt(args[index + 2]);

        } catch (NumberFormatException e) {
            return null;
        }
        return new Threads(args[index + 1], numHighTasks);
    }

    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;

        CoreId coreId = getCoreId(engine, m_coreReference);
        if (coreId != null) {
            engine.setNumHighPriorityTasks(getSrc(), coreId, m_numHighTasks);
        }
    }
}
