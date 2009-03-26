package com.xmos.xta.console.commands.Set;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class LoopMax extends Command {	
	private int m_pc;
	private int m_iterations;
	private int m_routeId;
	
	public LoopMax() {
		m_pc = 0;
		m_iterations = 0;
		m_routeId = 0;
	}
	
	public LoopMax(int routeId, int pc, int iterations) {
		m_pc = pc;
		m_iterations = iterations;
		m_routeId = routeId;
	}
			
    public int numArgs() {
    	return 3;
    }        

    public String name() {
    	return "loopmax";
    }        

    public String commandLine() {
    	return "loop 0x" + 
    	    Integer.toHexString(m_pc) + " " + 
    	    getRouteId(m_routeId) + " " + 
    	    m_iterations;
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "loopmax <pc> <routeId> <iterations>: Sets the number of iterations for the given route";
    	return descString;
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	int pc;
    	String number = args[index + 1];
    	if(number.startsWith("0x")){
    		number = number.substring(2, number.length());
    	}
    	try { pc = Integer.parseInt(number, 16); }
	    catch (NumberFormatException e) {
	        return null;
	    } 
	    int routeId;
    	try { routeId = getRouteId(args[index + 2]); }
	    catch (NumberFormatException e) {
	        return null;
	    }	    	    
    	int iterations;
    	try { iterations = Integer.parseInt(args[index + 3]); }
	    catch (NumberFormatException e) {
	        return null;
	    }    		  
	    
        return new LoopMax(routeId, pc, iterations);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	
		if(engine.getRoute(m_routeId) == null){
			console.display(String.format("Error: Failed to set loop iterations: no such route id: %d\n", m_routeId));   
			return;
		}

		if (!engine.setLoopMax(m_routeId, m_pc, m_iterations)) {
    		console.display(String.format("Error: Failed to set max loop iterations: route id: %d\n", m_routeId));    		
    	}

    }    
 }
