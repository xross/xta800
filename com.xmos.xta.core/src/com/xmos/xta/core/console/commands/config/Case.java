package com.xmos.xta.core.console.commands.config;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class Case extends Command {
    private static final String[] COMMAND_NAMES = { "case" };

    private Node.Case m_case;

    public Case() {
        super(COMMAND_NAMES);
        m_case = Node.Case.WORST;
    }

    public Case(Node.Case c) {
        super(COMMAND_NAMES);
        m_case = c;
    }

    public int numArgs() {
        return 1;
    }

    public String commandLine() {
        switch (m_case) {
        case BEST:  return name() + " best";
        case WORST: return name() + " worst";
        }
        assert(false);
        return "";
    }

    public String description(Engine engine, boolean detailed) {
        return name() + String.format(" <best/worst>: Sets the case (currently: %s)",
                engine.getCase().toString().toLowerCase());
    }

    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length)
            return null;

        Node.Case c;
        if (args[index + 1].equals(Node.Case.WORST.toString().toLowerCase())) {
            c = Node.Case.WORST;
        } else if (args[index + 1].equals(Node.Case.BEST.toString().toLowerCase())) {
            c = Node.Case.BEST;
        } else {
            return null;
        }
        return new Case(c);
    }

    public void execute(Shell shell, Engine engine, Console console) {
        engine.setCase(getSrc(), m_case);
    }
}
