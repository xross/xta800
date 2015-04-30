package com.xmos.xta.core.console.commands.config;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class HighPriorityThreads extends Command {


    private static final String[] COMMAND_NAMES = { "highprioritytasks", "hightasks", "highprioritythreads", "highthreads" };

    private String m_coreReference;
    private int m_numHighThreads;

    public HighPriorityThreads() {
        super(COMMAND_NAMES);
        m_coreReference = "";
        m_numHighThreads = 0;
    }

    public HighPriorityThreads(String coreReference, int numHighThreads) {
        super(COMMAND_NAMES);
        m_coreReference = coreReference;
        m_numHighThreads = numHighThreads;
    }

    public int numArgs() {
        return 2;
    }

    public String commandLine() {
        return name() + String.format(" %s %d", m_coreReference, m_numHighThreads);
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <tile id> <num tasks>: Sets number of high priority tasks currently executing on the given tile";
    }

    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length)
            return null;

        int numHighThreads;
        try {
            numHighThreads = Integer.parseInt(args[index + 2]);

        } catch (NumberFormatException e) {
            return null;
        }
        return new HighPriorityThreads(args[index + 1], numHighThreads);
    }

    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;

        CoreId coreId = getCoreId(engine, m_coreReference);
        if (coreId != null) {
            engine.setNumHighPriorityThreads(getSrc(), coreId, m_numHighThreads);
        }
    }
}
