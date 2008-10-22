package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.displayers.GraphDisplayer;
import com.xmos.xta.engine.Engine;

class Graph extends Command {
	Graph() {		
	}
	
    public int numArgs() {
    	return 0;
    }        

    public String name() {
    	return "graph";
    }        

    public String commandLine() {
    	return "graph";
    }
    
    public String description(Engine engine, boolean detailed) {
        return "graph: Dump graphviz dot file for previous path/function";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
    	return new Graph();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	
        
        engine.displayRoute(new GraphDisplayer(console));	
    }    
}
