package com.xmos.xta.core.console.commands.add;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Loop extends Command {
	private String m_refString;
	private int m_iterations;

	public Loop() {
	}

	public Loop(String refString, int iterations) {
		m_refString = refString;
		m_iterations = iterations;
	}

    public int numArgs() {
   		return 2;
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.ALLREFS};
	}

    public String name() {
		return "loop";
    }

    public String commandLine() {
       	return "loop " + " " + m_refString + " " + m_iterations;
    }

    public String description(Engine engine, boolean detailed) {
    	return "loop <ANY_REF> <iterations>: Adds the given loop count define to the list of defines";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) {
			return null;
		}

		try {
	   	    int iterations = Integer.parseInt(args[index + 2]);
	        return new Loop(args[index + 1], iterations);
		} catch (NumberFormatException e) {
	   	    return null;
	   	}
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console)) {
			return;
		}

		engine.addLoop(getSrc(), m_refString, m_iterations);
    }
 }