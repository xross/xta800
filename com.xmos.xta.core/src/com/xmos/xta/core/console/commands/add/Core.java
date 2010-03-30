package com.xmos.xta.core.console.commands.add;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.utils.NodeCorePair;

public class Core extends Command {
	private static final String[] COMMAND_NAMES = { "core" };

	private String m_nodeId;
	private String m_coreId;

	public Core() {
		super(COMMAND_NAMES);
		m_nodeId = "";
		m_coreId = "";
	}

	public Core(String nodeId, String coreId) {
		super(COMMAND_NAMES);
		m_nodeId = nodeId;
		m_coreId = coreId;
	}

    public int numArgs() {
    	if (m_nodeId.length() == 0) {
    		return 1;
    	} else {
    		return 2;
    	}
    }

    public String commandLine() {
    	if (m_nodeId.length() == 0) {
    		return name() + String.format(" %s", m_coreId);
    	} else {
    		return name() + String.format(" %s %s", m_nodeId, m_coreId);
    	}
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " [node id] <core id>: Add core to active set";
    }

    public Command clone(String[] args, int index) {
		if ((index + 1) >= args.length)
			return null;

		if (areArgsDone(args, index + 2)) {
	        return new Core("", args[index + 1]);
		} else {
	        return new Core(args[index + 1], args[index + 2]);
		}
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

    	if ((m_nodeId.length() == 0) && m_coreId.equals("*")) {
    		engine.addActiveCores(getSrc());
    		return;
    	}
		
		NodeCorePair ncPair = getNodeCore(engine, m_nodeId, m_coreId);
		if (ncPair != null) {
	        engine.addActiveCore(getSrc(), ncPair);
		}
    }
}
