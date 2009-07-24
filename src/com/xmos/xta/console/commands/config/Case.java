package com.xmos.xta.console.commands.config;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class Case extends Command {
	private Node.Case m_case;

	public Case() {
		m_case = Node.Case.WORST;
	}
	
	public Case(Node.Case c) {
		m_case = c;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "case";
    }        

    public String commandLine() {
    	switch (m_case) {
    	case BEST:  return "case BEST";    	
    	case WORST: return "case WORST";    	
    	}
    	assert(false);
    	return "";
    }
    
    public String description(Engine engine, boolean detailed) {
        return String.format("case <BEST/WORST>: Sets the case (currently: %s)", 
        		engine.getCase().toString());      	
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	Node.Case c;
    	if (args[index + 1].equals(Node.Case.WORST.toString())) {
    		c = Node.Case.WORST;
    	} else if (args[index + 1].equals(Node.Case.BEST.toString())) {
    		c = Node.Case.BEST;
    	} else {    		
            return null;
        }        
        return new Case(c);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
        engine.setCase(m_case);
    }    
}
