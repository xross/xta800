package com.xmos.xta.core.console.commands.add;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Core extends Command {
	private static final String[] COMMAND_NAMES = { "tile", "core" };

	private String m_coreName;

	public Core() {
		super(COMMAND_NAMES);
		m_coreName = "";
	}

	public Core(String coreName) {
		super(COMMAND_NAMES);
		m_coreName = coreName;
	}

    public int numArgs() {
		return 1;
    }

    public String commandLine() {
		return name() + String.format(" %s", m_coreName);
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <core id|*>: Add tile to active set";
    }

    public Command clone(String[] args, int index) {
		if ((index + 1) >= args.length)
			return null;

        return new Core(args[index + 1]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

    	if (m_coreName.equals("*")) {
    		engine.addActiveCores(getSrc());
    		return;
    	}
		
		CoreId coreId = getCoreId(engine, m_coreName);
		if (coreId != null) {
	        engine.addActiveCore(getSrc(), coreId);
		}
    }
}
