package com.xmos.xta.core.console.commands.config;

import java.util.Map;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class NoWarn extends Command {
    private static final String[] COMMAND_NAMES = { "nowarn" };

    private String m_option;

    public NoWarn() {
        super(COMMAND_NAMES);
    }

    public NoWarn(String option) {
        super(COMMAND_NAMES);
        m_option = option;
    }

    public int numArgs() {
        return 1;
    }

    public String commandLine() {
        return name() + " " + m_option;
    }

    public String description(Engine engine, boolean detailed) {
        String desc = name() + " <";
        Map<String, Boolean> warnings = engine.getConfig().getWarnings();
        boolean first = true;
        for (String key : warnings.keySet()) {
            if (!first) {
                desc += "|";
            }
            desc += key;
            first = false;
        }
        return desc + ">: Disable the given warning";
    }

    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length)
            return null;
        return new NoWarn(args[index + 1]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
        engine.getConfig().setWarningEnabled(m_option, false, engine, getSrc());
    }
}
