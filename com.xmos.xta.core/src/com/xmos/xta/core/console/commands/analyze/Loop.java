package com.xmos.xta.core.console.commands.analyze;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.Analyzer.Status;

public class Loop extends Command {
    private static final String[] COMMAND_NAMES = { "loop" };

    private String m_loopRef;

    public Loop() {
        super(COMMAND_NAMES);
        m_loopRef = "";
    }

    public Loop(String loopRef) {
        super(COMMAND_NAMES);
        m_loopRef = loopRef;
    }

    public int numArgs() {
        if (hasLoopPoint()) {
            return 1;
        } else {
            return 0;
        }
    }

    public ArgType[] getArgTypes() {
        return new Command.ArgType[] {ArgType.ALLREFS};
    }

    public String commandLine() {
        if (hasLoopPoint()) {
            return name() + " " + m_loopRef;
        } else {
            return name();
        }
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <ANY>: Analyze the given loop";
    }

    public Command clone(String[] args, int index) {
        if (areArgsDone(args, index + 1)) {
            return new Loop();

        } else {
            if ((index + 1) >= args.length)
                return null;

            String loopRef = args[index + 1];
            return new Loop(loopRef);
        }
    }

    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;

        if (hasLoopPoint()) {
            if (!engine.getLoopPoints().setLoop(getSrc(), m_loopRef)) {
                engine.setLastAnalysisStatus(Status.ERROR);
                return;
            }
        }
        engine.analyzeLoop(getSrc());
    }

    private boolean hasLoopPoint() {
        return (m_loopRef.length() > 0);
    }
}
