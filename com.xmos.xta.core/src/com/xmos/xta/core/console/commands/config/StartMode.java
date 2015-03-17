package com.xmos.xta.core.console.commands.config;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Config.ConfigStartMode;
import com.xmos.xta.core.engine.Engine;

public class StartMode  extends Command {
    private static final String[] COMMAND_NAMES = { "startmode" };

    private ConfigStartMode m_mode;

    public StartMode() {
        super(COMMAND_NAMES);
        m_mode = ConfigStartMode.AUTO;
    }

    public StartMode(ConfigStartMode mode) {
        super(COMMAND_NAMES);
        m_mode = mode;
    }

    @Override
    public int numArgs() {
        return 1;
    }

    @Override
    public String commandLine() {
        switch (m_mode) {
        case AUTO:  return name() + " auto";
        case SINGLE: return name() + " single";
        case DUAL: return name() + " dual";
        }
        assert(false);
        return "";
    }

    @Override
    public String description(Engine engine, boolean detailed) {
        return name() + String.format(" <auto/single/dual>: Sets the issue mode the analysis starts in (currently: %s)",
                engine.getStartMode().toString().toLowerCase());
    }

    @Override
    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length)
            return null;
        if (args[index + 1].equals(ConfigStartMode.AUTO.toString().toLowerCase()))
            return new StartMode(ConfigStartMode.AUTO);
        if (args[index + 1].equals(ConfigStartMode.SINGLE.toString().toLowerCase()))
            return new StartMode(ConfigStartMode.SINGLE);
        if (args[index + 1].equals(ConfigStartMode.DUAL.toString().toLowerCase()))
            return new StartMode(ConfigStartMode.DUAL);
        return null;
    }

    @Override
    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;
        engine.setStartMode(m_mode);
    }

}
