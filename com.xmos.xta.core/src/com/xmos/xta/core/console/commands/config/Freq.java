package com.xmos.xta.core.console.commands.config;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Freq extends Command {
	private static final String[] COMMAND_NAMES = { "freq" };

	private String m_nodeId;
	private double m_frequency;

	public Freq() {
		super(COMMAND_NAMES);
		m_nodeId = "";
		m_frequency = 0.0;
	}

	public Freq(String nodeId, double frequency) {
		super(COMMAND_NAMES);
		m_nodeId = nodeId;
		m_frequency = frequency;
	}

    public int numArgs() {
    	return 2;
    }

    public String commandLine() {
    	return name() + String.format(" %s %f", m_nodeId, m_frequency);
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <node id> <core frequency>: Sets the operating frequency in MHz for the given node";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

        try { 
        	double frequency = Double.parseDouble(args[index + 2]);
            return new Freq(args[index + 1], frequency);
        } catch (NumberFormatException e) { }

        return null;
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		engine.setFrequency(getSrc(), m_nodeId, m_frequency);
    }
}
