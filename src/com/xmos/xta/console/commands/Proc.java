package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Proc extends Command {
	int m_nodeId;
	int m_coreId;
	
	Proc() {
		m_nodeId = 0;
		m_coreId = 0;
	}
	
	Proc(int nodeId, int coreId) {
		m_nodeId = nodeId;
		m_coreId = coreId;
	}
	
    public int numArgs() {
    	return 2;
    }        

    public String name() {
    	return "proc";
    }        

    public String commandLine() {
    	return String.format("proc %d %d", m_nodeId, m_coreId);
    }
    
    public String description(Engine engine, boolean detailed) {
        return "proc <node id> <core id>: All following analysis on given node and core (default: 0,0)";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	int nodeId;
        try { nodeId = Integer.parseInt(args[index + 1]); }
        catch (NumberFormatException e) {
            return null;
        }    	        
        int coreId;
        try { coreId = Integer.parseInt(args[index + 2]); }
        catch (NumberFormatException e) {
            return null;
        }    	        
        return new Proc(nodeId, coreId);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (!isPresent(engine, m_nodeId, m_coreId, console))
			return;
		
        engine.setNodeId(m_nodeId);
        engine.setCoreId(m_coreId);
    }    
}
