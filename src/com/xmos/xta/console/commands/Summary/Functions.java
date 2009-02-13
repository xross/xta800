package com.xmos.xta.console.commands.Summary;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.xmos.elfhandler.ElfHandler;
import com.xmos.elfhandler.Symbol;
import com.xmos.isa.Instruction;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.console.Command;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Route;

public class Functions extends Command {
	public Functions() {}
	
    public int numArgs()        { return 0; }        
    public String name()        { return "functions"; }        
    public String commandLine() { return "functions"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "functions: Show path(s) + functions";
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
   		return new Functions();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (!isRouteAnalysed(engine, console))
			return;		    	

    	displayFunctions(console, engine.getRoute(), engine.getSymbolTable());
    }
	
    private void displayFunctions(Console console, Route route, SymbolTable symbolTable) {
		ElfHandler elfHandler = symbolTable.getElfHandler();        		    
    	Iterator<Map.Entry<Integer, Symbol>> iter = 
    		elfHandler.getFunctionsPcKeyed().entrySet().iterator();
    	    	
    	while (iter.hasNext()) {
    	    Map.Entry<Integer, Symbol> pair = iter.next();
    	    Symbol symbol = pair.getValue();       	    	        	    	   
    	    int functionAddress = symbol.getValue();
    	    int functionSize = symbolTable.functionSize(functionAddress);
    	    
        	TreeSet<Integer> pathIds = new TreeSet<Integer>();
            for (Block path : route.completePaths()) {
            	buildPathsWithinFunctionSet(path, pathIds, functionAddress, functionSize);
            }    	
            
            if (pathIds.size() > 0) {
            	console.display(String.format("%s: ", symbol.getName())); 
            	for (Integer pathId : pathIds) {
            		console.display(String.format("%d ", pathId));
            	}
            	console.display("\n");
            }
    	}
    }
    
    private void buildPathsWithinFunctionSet(Block path, Set<Integer> pathSet, 
    		int functionAddress, int functionSize) {   
    	
        for (Block block : path.getPathBlocks()) {
            if (pathWithinFunction(block, functionAddress, functionSize)) {
                if (!pathSet.contains(path.getId())) {
                    pathSet.add(path.getId());
                }
            }
        
            for (Block loopPath : block.getLoopPaths()) {
   		    	buildPathsWithinFunctionSet(loopPath, pathSet, functionAddress, functionSize);
       		}
        }
        path.doneWithPathBlocks();        
    }
    
    private boolean pathWithinFunction(Block path, int functionAddress, int functionSize) {
      	for (Instruction instruction : path.getInstructions(false)) {
        	if ((instruction.getPc() >= functionAddress) &&
        	    (instruction.getPc() < (functionAddress + functionSize))) {
        	    return true;
        	}
        }
        return false;
    }    	                
}
