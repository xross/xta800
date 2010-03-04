package com.xmos.xta.core.console.commands.config;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Threads extends Command {
	private int m_nodeNumber;
	private int m_coreNumber;
	private int m_numThreads;

	public Threads() {
		m_nodeNumber = 0;
		m_coreNumber = 0;
		m_numThreads = 0;
	}

	public Threads(int nodeNumber, int coreNumber, int threads) {
		m_nodeNumber = nodeNumber;
		m_coreNumber = coreNumber;
		m_numThreads = threads;
	}

    public int numArgs() {
    	return 3;
    }

    public String name() {
    	return "threads";
    }

    public String commandLine() {
    	return String.format("threads %d %d %d", m_nodeNumber, m_coreNumber, m_numThreads);
    }

    public String description(Engine engine, boolean detailed) {
        return String.format("threads <node number> <core number> <num threads>: Sets number of threads currently executing for the given node/core");
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

    	int nodeNumber;
    	int coreNumber;
    	int numThreads;
        try { 
        	nodeNumber = Integer.parseInt(args[index + 1]);
        	coreNumber = Integer.parseInt(args[index + 2]);
        	numThreads = Integer.parseInt(args[index + 3]);
        	
        } catch (NumberFormatException e) {
            return null;
        }
        return new Threads(nodeNumber, coreNumber, numThreads);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		engine.setNumThreads(getSrc(), m_nodeNumber, m_coreNumber, m_numThreads);
    }
}
