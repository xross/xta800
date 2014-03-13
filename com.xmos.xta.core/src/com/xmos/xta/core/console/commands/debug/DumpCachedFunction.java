package com.xmos.xta.core.console.commands.debug;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.print.StructureDisplayer;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class DumpCachedFunction extends Command {
    private static final String[] COMMAND_NAMES = { "dumpcachedfunction" };

    private String m_functionName;

    public DumpCachedFunction() {
        super(COMMAND_NAMES);
        m_functionName = "";
    }

    public DumpCachedFunction(String functionName) {
        super(COMMAND_NAMES);
        m_functionName = functionName;
    }

    public int numArgs() {
        return 1;
    }

    public ArgType[] getArgTypes() {
        return new Command.ArgType[] {ArgType.FUNCTION};
    }

    public String commandLine() {
        return name() + " " + m_functionName;
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <FUNCTION>: Dump the cached function structure";
    }

    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length)
            return null;

        String functionName = args[index + 1];
        return new DumpCachedFunction(functionName);
    }

    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;

        final StructureDisplayer displayer = new StructureDisplayer(console, engine.getVerbosity());

        for (CoreId coreId : engine.getActiveCores()) {
            console.display(String.format("Core: %s\n", coreId.getCoreReference()));

            Node cachedFunction = engine.getDebugger().getCachedFunction(getSrc(), m_functionName, coreId);
            if (cachedFunction != null) {
                Collection<Node> nodes = new ArrayList<Node>();
                nodes.add(cachedFunction);
                displayer.display(nodes);
            }
        }
    }
}
