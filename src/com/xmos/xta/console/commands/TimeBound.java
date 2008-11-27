package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Route;

public class TimeBound extends Command  {
	int m_timeBound;

	TimeBound() {
		m_timeBound = 0;
	}
	
	TimeBound(int timeBound) {
		m_timeBound = timeBound;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "timebound";
    }        

    public String commandLine() {
    	return "timebound " + m_timeBound;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "timebound <int (ns)> : returns PASS of FAIL if the worst case is less than the time bound";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
    	
    	
		if ((index + numArgs()) >= args.length) 
			return null;
		
		int time = Integer.parseInt(args[index +1]);
		
		return new TimeBound(time);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;		
		
		Route route = engine.getRoute();
        for(Block path : route.completePaths()){
        	double time = path.getTimeTaken(true);
        	if(time > m_timeBound){
        		console.display("FAIL\n");
        		return;
        	}
        }
        console.display("PASS\n");
        return;
   }    
}
