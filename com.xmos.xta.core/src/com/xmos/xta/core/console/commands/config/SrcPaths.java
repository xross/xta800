package com.xmos.xta.core.console.commands.config;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class SrcPaths extends Command {
    private static final String[] COMMAND_NAMES = { "srcpaths" };

    private String m_paths;

    public SrcPaths() {
        super(COMMAND_NAMES);
        m_paths = "";
    }

    public SrcPaths(String paths) {
        super(COMMAND_NAMES);
        m_paths = paths;
    }

    public int numArgs() {
        return 1;
    }

    public String commandLine() {
           return name() + " " + m_paths;
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <paths>: Sets the (semicolon separated) source search path";
    }

    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length)
            return null;

        return new SrcPaths(args[index + 1]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
        engine.setSrcPaths(m_paths);
    }
}
