package com.xmos.xta.console.commands.set;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;

public class Loop extends Command {	
	private int m_routeId;
	private String m_refString;
	private int m_iterations;
	
	public Loop() {
		m_routeId = 0;
		m_refString = "";
		m_iterations = 0;
	}
	
	public Loop(int routeId, String refString, int iterations) {
		m_routeId = routeId;
		m_refString = refString;
		m_iterations = iterations;
	}
	
    public int numArgs() {
   		return 3; 
    }        

    public String name() {
    	return "loop";
    }        

    public String commandLine() {
       	return "loop " + getRouteId(m_routeId) + " " + m_refString + " " + m_iterations;        	
    }
    
    public String description(Engine engine, boolean detailed) {
   		return "loop <route id> <ANY_REF> <iterations>: Sets the number of iterations for the loop identified";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

		int routeId;
    	try { routeId = getRouteId(args[index + 1]);
	    } catch (NumberFormatException e) {
	        return null;
	    }    			
   	    int iterations;
       	try { iterations = Integer.parseInt(args[index + 3]); }
   	    catch (NumberFormatException e) {
   	        return null;
   	    }    		   	    
        return new Loop(routeId, args[index + 2], iterations);    		
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

   		if (!engine.setLoopIterations(getSrc(), m_routeId, m_refString, m_iterations)) {
   			engine.notify(getSrc(), Notifier.NOTIFY_ERROR, String.format(
       				"failed to set loop iterations on reference: %s", 
       				m_refString));
   		}
    }    
 }
