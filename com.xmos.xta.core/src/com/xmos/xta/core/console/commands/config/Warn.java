package com.xmos.xta.core.console.commands.config;

import java.util.Map;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Warn extends Command {
    private static final String[] COMMAND_NAMES = { "warn" };

    private String m_option;

    public Warn() {
        super(COMMAND_NAMES);
    }

    public Warn(String option) {
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
        return desc + ">: Enable the given warning";
    }

    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length)
            return null;
        return new Warn(args[index + 1]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
        engine.getConfig().setWarningEnabled(m_option, true, engine, getSrc());
    }
}
