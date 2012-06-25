package com.xmos.xta.core.console.commands.list;

import java.util.Collections;
import java.util.LinkedList;

import com.xmos.libs.util.CoreId;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.SrcLabelTableInfo;

public class AllLoopLabels extends Command {
    private static final String[] COMMAND_NAMES = { "alllooplabels" };

    public AllLoopLabels() {
        super(COMMAND_NAMES);
    }

    public int numArgs() {
        return 0;
    }

    public String commandLine() {
        return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + ": Lists all the possible locations for loop labels";
    }

    public Command clone(String[] args, int index) {
        return new AllSrcLabels();
    }

    public void execute(Shell shell, final Engine engine, final Console console) {
        if (!isExecutableLoaded(engine, console))
            return;

        for (CoreId activeCore : engine.getActiveCores()) {             
            LinkedList<SrcLabelTableInfo> list = new LinkedList<SrcLabelTableInfo>();
            list.addAll(engine.getLoopLabelTable(activeCore));
            Collections.sort(list); 
            for (SrcLabelTableInfo info : list) {
                console.display(String.format("%s, pc: 0x%x, compilation dir: %s, filename: %s, line numbers: %d - %d\n",
                        activeCore.toString(), 
                        info.getPc(), info.getCompilationDir(), info.getFileName(),
                        info.getLineNumberStart(), info.getLineNumberEnd()));
            }           
        }
    }
}
