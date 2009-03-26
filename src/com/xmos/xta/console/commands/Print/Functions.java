package com.xmos.xta.console.commands.Print;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.xmos.elfhandler.ElfHandler;
import com.xmos.elfhandler.Symbol;
import com.xmos.isa.Instruction;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.PathBasicBlock;
import com.xmos.xta.engine.Route;

public class Functions extends Command {
	private int m_routeId;
	
	public Functions() {
		m_routeId = 0;
	}
	
	public Functions(int routeId) {
		m_routeId = routeId;		
	}
	
    public int numArgs()        { return 1; }        
    public String name()        { return "functions"; }        
    public String commandLine() { return "functions " + getRouteId(m_routeId); }        
    
    public String description(Engine engine, boolean detailed) {
    	return "functions <route id>: Shows which paths touch which functions";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		
    	
    	try {
     		return new Functions(getRouteId(args[index + 1]));
	    } catch (NumberFormatException e) {
	        return null;
	    }    	
  }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (!validateRouteId(engine, console, m_routeId))
			return;		    	

    	displayFunctions(console, engine.getRoute(m_routeId), engine.getSymbolTable());
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
            for (PathBasicBlock path : route.getCompletedPaths()) {
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
    
    private void buildPathsWithinFunctionSet(PathBasicBlock path, Set<Integer> pathSet, 
    		int functionAddress, int functionSize) {   
    	
        for (PathBasicBlock block : path.getPathBlocks()) {
            if (pathWithinFunction(block, functionAddress, functionSize)) {
                if (!pathSet.contains(path.getPathId())) {
                    pathSet.add(path.getPathId());
                }
            }
        
            for (PathBasicBlock loopPath : block.getLoopPaths(path)) {
   		    	buildPathsWithinFunctionSet(loopPath, pathSet, functionAddress, functionSize);
       		}
        }
    }
    
    private boolean pathWithinFunction(PathBasicBlock path, int functionAddress, int functionSize) {
      	for (Instruction instruction : path.getInstructions(false)) {
        	if ((instruction.getPc() >= functionAddress) &&
        	    (instruction.getPc() < (functionAddress + functionSize))) {
        	    return true;
        	}
        }
        return false;
    }    	                
}
