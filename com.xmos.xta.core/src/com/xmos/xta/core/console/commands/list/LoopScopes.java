package com.xmos.xta.core.console.commands.list;

import java.util.Collection;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.defines.LoopScopeInfo;

public class LoopScopes extends Command {
    private static final String[] COMMAND_NAMES = { "loopscopes" };

    public LoopScopes() {
        super(COMMAND_NAMES);
    }

    public int numArgs() {
        return 0;
    }

    public ArgType[] getArgTypes() {
        return super.getArgTypes();
    }

    public String commandLine() {
           return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + ": Displays the loop scope defines";
    }

    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length) {
            return null;
        }

        return new LoopScopes();
    }

    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console)) {
            return;
        }

        Collection<LoopScopeInfo> infos = engine.getLoopScopeDefines();
        for (LoopScopeInfo info : infos) {
            console.display(info + "\n");
        }
    }
 }