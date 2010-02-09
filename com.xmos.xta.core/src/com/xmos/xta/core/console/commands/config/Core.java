package com.xmos.xta.core.console.commands.config;

import com.xmos.libs.util.Pair;
import com.xmos.libs.xnloader.XnLoader;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;

public class Core extends Command {
	private String m_nodeId;
	private String m_coreId;
	
	public Core() {
		m_nodeId = "";
		m_coreId = "";
	}
	
	public Core(String nodeId, String coreId) {
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

    public String name() {
    	return "core";
    }        

    public String commandLine() {
    	if (m_nodeId.length() == 0) {
    		return String.format("core %s", m_coreId);
    	} else {
    		return String.format("core %s %s", m_nodeId, m_coreId);
    	}
    }
    
    public String description(Engine engine, boolean detailed) {
        return String.format("core [node id] <core id>: Change to core for following analysis (current: %d,%d)",
        		engine.getNodeNumber(), engine.getCoreNumber());      	
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

		XnLoader xnLoader = engine.getXeLoader().getXnLoader();

		int nodeNumber = 0;
		int coreNumber = 0;
		if (m_nodeId.length() == 0) {
			Pair<Integer,Integer> nodeCore = xnLoader.getNodeCore(m_coreId);
			if (nodeCore == null) {
	    		engine.notify(getSrc(), Notifier.NOTIFY_ERROR, String.format("code '%s' not found", m_coreId));
	    		return;
			}
			nodeNumber = nodeCore.first;
			coreNumber = nodeCore.second;
			
		} else {
			nodeNumber = xnLoader.getNodeNumber(m_nodeId);
			if (nodeNumber == -1) {
		        try { nodeNumber = Integer.parseInt(m_nodeId); }
		        catch (NumberFormatException e) { }
			}
			coreNumber = xnLoader.getCoreNumber(nodeNumber, m_coreId);
			if (coreNumber == -1) {
				try { coreNumber = Integer.parseInt(m_coreId); }
				catch (NumberFormatException e) { }
			}
		}

		if (isPresent(engine, nodeNumber, coreNumber, console)) {
	        engine.setNodeNumber(getSrc(), nodeNumber, true);
	        engine.setCoreNumber(getSrc(), coreNumber, true);
		}
    }

	private boolean isPresent(Engine engine, int nodeNumber, int coreNumber, Console console) {
    	if (engine.getXeLoader().getMemoryImage(nodeNumber, coreNumber) == null) {
    		engine.notify(getSrc(), Notifier.NOTIFY_ERROR,
    				String.format("no code loaded on node: %s, core: %s", m_nodeId, m_coreId));
    		return false;
    	}
    	return true;    	
	}
}
