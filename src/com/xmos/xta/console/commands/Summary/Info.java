package com.xmos.xta.console.commands.Summary;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.PathBasicBlock;

public class Info extends Command {
	private int m_id;
	private Command.IdType m_idType;
	
	public Info() {
		m_id = 0;
		m_idType = null;
	}
	
	public Info(int id, IdType idType) {
		m_id = id;
		m_idType = idType;
	}
	
    public int numArgs() {
   		return 1;
    }        

    public String name() {
    	return "info";
    }        

    public String commandLine() {
   	    return "info " + m_id;
    }        
    
    public String description(Engine engine, boolean detailed) {
    	return "info <path/block id>: Show information for given path/block";
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		

        try { 
            return new Info(getId(args[index + 1]), getIdType(args[index + 1]));
        } catch (NumberFormatException e) {        	
          	return null;
        }    		            		
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (!isRouteAnalysed(engine, console))
			return;
		
	    if (!Command.isIdValid(engine, console, m_id, m_idType)) {	    	
	    	switch (m_idType) {
	        case BLOCK: console.display(String.format("Block: %d not found\n", m_id)); break;
	        case PATH:  console.display(String.format("Path: %d not found\n", m_id)); break;
	    	}
            return;
	    }
    	
    	switch (m_idType) {
        case BLOCK:
        	PathBasicBlock block = engine.getRoute().getBlock(m_id);
        	displayBlockSummary(console, block);
        	break;
        case PATH:
        	PathBasicBlock path = engine.getRoute().getPath(m_id);			
        	displayPathSummary(console, path);
        	break;
        default:
        	assert(false);        
        }    	
    }
    
    private void displayPathSummary(Console console, PathBasicBlock path) {
    	console.display(String.format("Timing(ns):       %.1f\n", path.getTimeTaken(true)));
        console.display(String.format("Thread cycles:    %d\n", path.getNumCycles(true)));
        console.display(String.format("Num Instructions: %d\n", path.getNumInstructions(true)));
        console.display(String.format("Num FNops:        %d\n", path.getNumFNops(true)));
        console.display(String.format("Num Blocks:       %d\n", path.getNumPathBlocks()));
        console.display(String.format("Num Pauseables:   %d\n", path.getNumPausables(true)));
        
        int numLoops = 0;
        for (PathBasicBlock block : path.getPathBlocks()) {
            numLoops += block.numLoopPaths();
        }
        path.doneWithPathBlocks();
        console.display(String.format("Num Loops:        %d\n", numLoops));
    }

    private void displayBlockSummary(Console console, PathBasicBlock block) {
    	console.display(String.format("Timing(ns):       %.1f\n", block.getTimeTakenThis()));
    	console.display(String.format("Thread cycles:    %d\n", block.getNumCyclesThis()));
    	console.display(String.format("Num Instructions: %d\n", block.getNumInstructionsThis()));
    	console.display(String.format("Num FNops:        %d\n", block.getNumFNopsThis()));
    	console.display(String.format("Num Pauseables:   %d\n", block.getNumPausablesThis()));        
    }    
}
