package com.xmos.xta.core.console.commands.config;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Freq extends Command {
	private static final String[] COMMAND_NAMES = { "freq" };

	private int m_nodeNumber;
	private double m_frequency;

	public Freq() {
		super(COMMAND_NAMES);
		m_nodeNumber = 0;
		m_frequency = 0.0;
	}

	public Freq(int nodeNumber, double frequency) {
		super(COMMAND_NAMES);
		m_nodeNumber = nodeNumber;
		m_frequency = frequency;
	}

    public int numArgs() {
    	return 2;
    }

    public String commandLine() {
    	return name() + String.format(" %d %f", m_nodeNumber, m_frequency);
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <node number> <core frequency>: Sets the core frequency in Mhz for the given node";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

		int nodeNumber;
		double frequency;		
        try { 
        	nodeNumber = Integer.parseInt(args[index + 1]);
        	frequency = Double.parseDouble(args[index + 2]);

        } catch (NumberFormatException e) {
            return null;
        }
        return new Freq(nodeNumber, frequency);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		engine.setFrequency(getSrc(), m_nodeNumber, m_frequency);
    }
}
