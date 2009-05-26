package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.PathBasicBlock;

public class PathInfo extends Command {
	private int m_routeId;
	private int m_id;
	private Command.IdType m_idType;
	
	public PathInfo() {
		m_routeId = 0;
		m_id = 0;
		m_idType = null;
	}
	
	public PathInfo(int routeId, int id, IdType idType) {
		m_routeId = routeId;
		m_id = id;
		m_idType = idType;
	}
	
    public int numArgs() {
   		return 2;
    }        

    public String name() {
    	return "path_info";
    }        

    public String commandLine() {
   	    return "path_info " + getRouteId(m_routeId) + " " + getId(m_id, m_idType);
    }        
    
    public String description(Engine engine, boolean detailed) {
    	return "path_info <route id> <path/block id>: Show detailed information for the given path/block";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

        try { 
            return new PathInfo(getRouteId(args[index + 1]), 
            		getId(args[index + 2]), getIdType(args[index + 2]));
        } catch (NumberFormatException e) {        	
          	return null;
        }    		            		
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

	    if (!validateIds(engine, console, m_routeId, m_id, m_idType))
		    return;
    	
    	switch (m_idType) {
        case BLOCK:
        	PathBasicBlock block = engine.getRoute(m_routeId).getBlock(m_id);
        	displayBlockSummary(console, block);
        	break;
        case PATH:
        	PathBasicBlock path = engine.getRoute(m_routeId).getPath(m_id);			
        	displayPathSummary(console, path);
        	break;
        default:
        	assert(false);        
        }    	
    }
    
    private void displayPathSummary(Console console, PathBasicBlock path) {
    	console.display(String.format("Path ID:          %d\n", path.getPathId()));
    	console.display(String.format("Time taken:       %.1f\n", path.getTimeTaken(true)));
        console.display(String.format("Thread cycles:    %d\n", path.getNumCycles(true)));
        console.display(String.format("Num Instructions: %d\n", path.getNumInstructions(true)));
        console.display(String.format("Num FNops:        %d\n", path.getNumFNops(true)));
        console.display(String.format("Num Pauseables:   %d\n", path.getNumPausables(true)));
        console.display(String.format("Num Unknowns:     %d\n", path.getNumUnknowns(true)));

        int numLoops = 0;
        for (PathBasicBlock block : path.getPathBlocks()) {
            numLoops += block.numLoopPaths(path);
        }
        console.display(String.format("Num Loops:        %d\n", numLoops));
        
    	console.display(String.format("Time taken (this):       %.1f\n", path.getTimeTaken(false)));
        console.display(String.format("Thread cycles (this):    %d\n", path.getNumCycles(false)));
        console.display(String.format("Num Instructions (this): %d\n", path.getNumInstructions(false)));
        console.display(String.format("Num FNops (this):        %d\n", path.getNumFNops(false)));
        console.display(String.format("Num Pauseables (this):   %d\n", path.getNumPausables(false)));
        console.display(String.format("Num Unknowns (this):     %d\n", path.getNumUnknowns(false)));

        console.display(String.format("Number of iterations:    %d + %d\n", 
        		path.getHardNumIterations(), path.getSoftNumIterations())); 
    }

    private void displayBlockSummary(Console console, PathBasicBlock block) {
    	console.display(String.format("Timing(ns):       %.1f\n", block.getTimeTakenThis()));
    	console.display(String.format("Thread cycles:    %d\n", block.getNumCyclesThis()));
    	console.display(String.format("Num Instructions: %d\n", block.getNumInstructionsThis()));
    	console.display(String.format("Num FNops:        %d\n", block.getNumFNopsThis()));
    	console.display(String.format("Num Pauseables:   %d\n", block.getNumPausablesThis()));        
    }    
}
