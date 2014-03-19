package com.xmos.xta.core.console.commands.config;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.Engine;

public class TimingErrors extends Command {
    private static final String[] COMMAND_NAMES = { "Terror" };

    private boolean m_errorOn;

    public TimingErrors() {
        super(COMMAND_NAMES);
        m_errorOn = false;
    }

    public TimingErrors(boolean errorOn) {
        super(COMMAND_NAMES);
        m_errorOn = errorOn;
    }

    public int numArgs() {
        return 1;
    }

    public String commandLine() {
        if (m_errorOn) {
            return name() + " on";
        } else {
            return name() + " off";
        }
    }

    public String description(Engine engine, boolean detailed) {
        Config config = engine.getConfig();
        return name() + String.format(" <on/off>: treat timing failures as errors or not (currently: %s)",
            config.getTimingFailuresAsErrors() ? "on" : "off");
    }

    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length)
            return null;

        if (args[index + 1].equals("on")) {
            return new TimingErrors(true);
        } else if (args[index + 1].equals("off")) {
            return new TimingErrors(false);
        } else {
            return null;
        }
    }

    public void execute(Shell shell, Engine engine, Console console) {
        engine.setTimingFailuresAsErrors(m_errorOn);
    }
}
