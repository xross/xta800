package com.xmos.xta.core.console.commands.list;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.SrcStringInfo;

public class SrcCommands extends Command {
    private static final String[] COMMAND_NAMES = { "srccommands" };

    public SrcCommands() {
        super(COMMAND_NAMES);
    }

    public int numArgs() {
        return 0;
    }

    public String commandLine() {
           return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + ": Displays the command list embedded in the loaded executable";
    }

    public Command clone(String[] args, int index) {
           return new SrcCommands();
    }

    public void execute(Shell shell, final Engine engine, final Console console) {
        if (!isExecutableLoaded(engine, console))
            return;

        for (CoreId activeCore : engine.getActiveCores()) {
            for (SrcStringInfo cmdInfo : engine.getSrcLevelCommands(activeCore)) {
                console.display(String.format("%s, %s\n",
                        activeCore.toString(), cmdInfo.getString()));
            }
        }
    }
}
