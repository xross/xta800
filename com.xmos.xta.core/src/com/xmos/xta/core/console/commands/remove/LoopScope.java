package com.xmos.xta.core.console.commands.remove;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class LoopScope extends Command {
	private String m_refString;

	public LoopScope() {
	}

	public LoopScope(String refString) {
		m_refString = refString;
	}

    public int numArgs() {
   		return 1;
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.ALLREFS};
	}

    public String name() {
		return "loopscope";
    }

    public String commandLine() {
       	return "loopscope " + " " + m_refString;
    }

    public String description(Engine engine, boolean detailed) {
    	return "loopscope <ANY_REF|*>: Removes the given loop scope define to the list of defines";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) {
			return null;
		}

    	return new LoopScope(args[index + 1]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console)) {
			return;
		}
		engine.removeLoopScope(getSrc(), m_refString);
    }
 }