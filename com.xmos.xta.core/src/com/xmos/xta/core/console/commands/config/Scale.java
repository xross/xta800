package com.xmos.xta.core.console.commands.config;

import com.xmos.libs.core.utils.Conversion;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;

public class Scale extends Command {
    private static final String[] COMMAND_NAMES = { "scale" };

    private boolean m_scale;

    public Scale() {
        super(COMMAND_NAMES);
        m_scale = true;
    }

    public Scale(boolean scale) {
        super(COMMAND_NAMES);
        m_scale = scale;
    }

    public int numArgs() {
        return 1;
    }

    public String commandLine() {
        if (m_scale) {
            return name() + " true";
        } else {
            return name() + " false";
        }
    }

    public String description(Engine engine, boolean detailed) {
        return name() + String.format(" <true/false>: Configures whether results are scales (currently: %s)",
                Conversion.getScale());
    }

    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length)
            return null;

        boolean scale = true;
        if (args[index + 1].toLowerCase().equals("true")) {
            scale = true;
        } else if (args[index + 1].toLowerCase().equals("false")) {
            scale = false;
        } else {
            return null;
        }
        return new Scale(scale);
    }

    public void execute(Shell shell, Engine engine, Console console) {
        if (Conversion.getScale() != m_scale) {
            Conversion.setScale(m_scale);
            engine.notify(getSrc(), Notifier.NOTIFY_UPDATE_STATE, "");
        }
    }
}
