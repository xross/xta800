package com.xmos.xta.core.console.commands.remove;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class LoopScope extends Command {
    private static final String[] COMMAND_NAMES = { "loopscope" };

    private String m_refString;

    public LoopScope() {
        super(COMMAND_NAMES);
    }

    public LoopScope(String refString) {
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
           return name() + " " + " " + m_refString;
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <ANY|*>: Removes the given loop scope define to the list of defines";
    }

    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length) {
            return null;
        }

        return new LoopScope(args[index + 1]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
        if (isExecutableLoaded(engine, console)) {
            engine.removeLoopScope(getSrc(), m_refString);
        }

    }
 }