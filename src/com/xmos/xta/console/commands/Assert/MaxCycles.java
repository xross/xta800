package com.xmos.xta.console.commands.Assert;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.PathBasicBlock;
import com.xmos.xta.engine.Route;

public class MaxCycles extends Command  {
    private int m_maxCycles;

    public MaxCycles() {
        m_maxCycles = 0;
    }
    
    public MaxCycles(int maxCycles) {
        m_maxCycles = maxCycles;
    }
    
    public int numArgs() {
        return 1;
    }        

    public String name() {
        return "maxcycles";
    }        

    public String commandLine() {
        return "maxcycles " + m_maxCycles;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "maxcycles <num cycles> : Analysis fails at the given cycle limit";          
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {                
        if ((index + numArgs()) >= args.length) 
            return null;
        
        int cycles;
        try { cycles = Integer.parseInt(args[index + 1]); }
        catch (NumberFormatException e) {
            return null;
        }        
        return new MaxCycles(cycles);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;        
        
        Route route = engine.getRoute();
        if (route == null || route.getCompletedPaths().size() == 0){
            console.display("FAIL (no paths found)\n");
            return;
        }
        double worstPathCycles = 0;
        for(PathBasicBlock path : route.getCompletedPaths()){
            double cycles = path.getNumCycles(true);
            worstPathCycles = Math.max(worstPathCycles, cycles);
        }
        if (worstPathCycles > m_maxCycles){
            console.display("FAIL (required ");
            displayCycles(console, m_maxCycles);
            console.display(", worst-case ");
            displayCycles(console, (int)worstPathCycles);
            console.display(", violation ");
            displayCycles(console, (int)(worstPathCycles - m_maxCycles));
            console.display(")\n");
        } else {
            console.display("PASS (required ");
            displayCycles(console, m_maxCycles);
            console.display(", worst-case ");
            displayCycles(console, (int)worstPathCycles);
            console.display(", slack ");
            displayCycles(console, (int)(m_maxCycles - worstPathCycles));
            console.display(")\n");
        }
    }

	private void displayCycles(Console console, int numCycles) {
		console.display(numCycles + " cycle");
		if (numCycles != 1)
			console.display("s");
	}    
}
