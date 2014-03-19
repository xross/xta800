package com.xmos.xta.core.console.commands.debug;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;

public class SetMemThreshold extends Command {
    private static final String[] COMMAND_NAMES = { "setmemthreshold" };

    private double m_threshold;

    public SetMemThreshold() {
        super(COMMAND_NAMES);
    }

    public SetMemThreshold(double threshold) {
        super(COMMAND_NAMES);
        m_threshold = threshold;
    }

    public int numArgs() {
        return 1;
    }

    public String commandLine() {
        return name() + " " + m_threshold;
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <threshold>: Sets the memory threshold to the given value (0.0 - 1.0)";
    }

    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length)
            return null;

        try {
            return new SetMemThreshold(Double.parseDouble(args[index + 1]));

        } catch (NumberFormatException e) {
               return null;
           }
    }

    public void execute(Shell shell, Engine engine, Console console) {
        try {
            engine.getMemoryMonitor().setThreashold(m_threshold);
        } catch (Exception e) {
            engine.notify(getSrc(), Notifier.NOTIFY_ERROR, e.getMessage() + "\n");
        }
    }
}
