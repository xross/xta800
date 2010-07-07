package com.xmos.xta.core.console.commands.config;

import com.xmos.libs.util.CoreId;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Threads extends Command {
	private static final String[] COMMAND_NAMES = { "threads" };

	private String m_coreReference;
	private int m_numThreads;

	public Threads() {
		super(COMMAND_NAMES);
		m_coreReference = "";
		m_numThreads = 0;
	}

	public Threads(String coreReference, int threads) {
		super(COMMAND_NAMES);
		m_coreReference = coreReference;
		m_numThreads = threads;
	}

    public int numArgs() {
    	return 2;
    }

    public String commandLine() {
    	return name() + String.format(" %s %d", m_coreReference, m_numThreads);
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <core id> <num threads>: Sets number of threads currently executing for the given core";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

    	int numThreads;
        try { 
        	numThreads = Integer.parseInt(args[index + 2]);
        	
        } catch (NumberFormatException e) {
            return null;
        }
        return new Threads(args[index + 1], numThreads);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		CoreId coreId = getCoreId(engine, m_coreReference);
		if (coreId != null) {
			engine.setNumThreads(getSrc(), coreId, m_numThreads);
		}
    }
}
