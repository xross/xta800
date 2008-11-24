package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Mode extends Command {
	boolean dynamicMode;

	Mode() {
		dynamicMode = false;
	}
	
	Mode(boolean mode) {
		dynamicMode = mode;
	}
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "mode";
    }        

    public String commandLine() {
    	if(dynamicMode){
    		return "mode: dynamic";
    	} else {
    		return "mode: static";
    	}
    }
    
    public String description(Engine engine, boolean detailed) {
        return "mode <mode type> : sets the engine into STATIC or DYNAMIC mode";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
    	
    	
		if ((index + numArgs()) >= args.length) 
			return null;		
		if(args[index +1].equals("DYNAMIC")){
			return new Mode(true);
		} else if(args[index +1].equals("STATIC")){
			return new Mode(false);
		} 
		return new Mode(dynamicMode);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	
        engine.setDynamicMode(dynamicMode);                
   }    
}
