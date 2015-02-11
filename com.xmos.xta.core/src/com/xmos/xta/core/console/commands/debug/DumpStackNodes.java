package com.xmos.xta.core.console.commands.debug;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class DumpStackNodes extends Command {
    private static final String[] COMMAND_NAMES = { "dumpstacknodes" };

    private String m_reference;

    public DumpStackNodes() {
        super(COMMAND_NAMES);
        m_reference = "";
    }

    public DumpStackNodes(String reference) {
        super(COMMAND_NAMES);
        m_reference = reference;
    }

    public int numArgs() {
        return 1;
    }

    public ArgType[] getArgTypes() {
        return new Command.ArgType[] {ArgType.FUNCTION};
    }

    public String commandLine() {
        return name() + " " + m_reference;
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <REFERENCE>: Dumps the stack nodes for the given reference";
    }

    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length)
            return null;

        String reference = args[index + 1];
        return new DumpStackNodes(reference);
    }

    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;

        engine.getDebugger().dumpBackTrails(getSrc(), m_reference, console);
    }
}

