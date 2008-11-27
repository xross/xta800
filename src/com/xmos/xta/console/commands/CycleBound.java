package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Route;

public class CycleBound extends Command  {
	int m_cycleBound;

	CycleBound() {
		m_cycleBound = 0;
	}
	
	CycleBound(int timeBound) {
		m_cycleBound = timeBound;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "cyclebound";
    }        

    public String commandLine() {
    	return "cyclebound " + m_cycleBound;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "cyclebound <int (ns)> : returns PASS of FAIL if the worst case is less than the cycle bound";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
    	
    	
		if ((index + numArgs()) >= args.length) 
			return null;
		
		int cycles = Integer.parseInt(args[index +1]);
		
		return new CycleBound(cycles);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;		
		
		Route route = engine.getRoute();
        for(Block path : route.completePaths()){
        	int cycles = path.getNumCycles(true);
        	if(cycles > m_cycleBound){
        		console.display("FAIL\n");
        		return;
        	}
        }
        console.display("PASS\n");
        return;
   }    
}
