package com.xmos.xta.core.console.commands.debug;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class ListGlobalReferences extends Command {
    private static final String[] COMMAND_NAMES = { "listglobalreferences" };

    private String m_refString;

    public ListGlobalReferences() {
        super(COMMAND_NAMES);
        m_refString = "";
    }

    public ListGlobalReferences(String refString) {
        super(COMMAND_NAMES);
        m_refString = refString;
    }

    public int numArgs() {
        return 1;
    }

    public ArgType[] getArgTypes() {
        return new Command.ArgType[] {ArgType.ALLREFS};
    }

    public String commandLine() {
        return name() + " " + m_refString;
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <ANY>: list all the matching references for the given reference on the global tree";
    }

    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length)
            return null;

        return new ListGlobalReferences(args[index + 1]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;

        for (String reference : engine.getDebugger().getGlobalReferences(getSrc(), m_refString)) {
            console.display(reference + "\n");
        }
    }
}
