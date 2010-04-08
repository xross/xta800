package com.xmos.xta.core.console.commands.debug;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;

import com.xmos.xta.core.engine.Engine;

public class VerifyReference extends Command {
	private static final String[] COMMAND_NAMES = { "verifyreference" };

	private String m_refString;

	public VerifyReference() {
		super(COMMAND_NAMES);
	}
	
	public VerifyReference(String refString) {
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
        return name() + " <ANY_REF>: verify the existance of the given reference";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

    	String refString = args[index + 1];
        return new VerifyReference(refString);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

        if (engine.validateReference(m_refString)){
        	console.display("true\n");
        } else {
        	console.display("false\n");
        }
    }
}
