package com.xmos.xta.core.console.commands.load;

import java.io.File;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.ConsoleCommandRunner;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Load extends Command {
    private static final String[] COMMAND_NAMES = { "load" };

    private String m_executableName;

    public Load() {
        super(COMMAND_NAMES);
        m_executableName = "";
    }

    public Load(String executableName) {
        super(COMMAND_NAMES);
        m_executableName = executableName;
    }

    public int numArgs() {
        return 1;
    }

    public ArgType[] getArgTypes() {
        return new Command.ArgType[] {ArgType.FILE};
    }

    public String commandLine() {
        return name() + " \"" + m_executableName + "\"";
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <xe file>: Loads the given xmos executable file";
    }

    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length)
            return null;

        String executableName = expandTilde(args[index + 1]);
        return new Load(executableName);
    }

    public void execute(final Shell shell, final Engine engine, final Console console) {
        engine.loadExecutable(getSrc(), new File(m_executableName),
                new ConsoleCommandRunner(shell, engine, console));
    }
}
