package com.xmos.xta.console.commands;

import java.util.ArrayList;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.constraint.ConstraintFactory;

public class Constraint extends Command {	
	public enum Operation {
		SET, LIST
	};

	private ArrayList<String> m_strings;
	private String m_constraintString;
	private Operation m_operation;
	
	public Constraint(Operation operation) {
		m_operation = operation;
		m_strings = new ArrayList<String>();
		m_constraintString = "";
	}
	
    public int numArgs() {
    	switch (m_operation) {
    	case SET:
        	return m_strings.size();
    	case LIST:
            return 0;
    	}    		
    	assert(false);
        return 0;
    }        

    public String name() {
    	return "constraint";
    }        

    public String commandLine() {
    	return "constraint " + m_constraintString;
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "";
    	switch (m_operation) {
    	case SET:
        	descString = "constraint <expression>: Defines the constraint expression";
        	break;
    	case LIST:
        	descString = "constraint <expression>: Displays the current constraint expression";
        	break;
    	}    		    	
    	return descString;
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		
	    
    	switch (m_operation) {
    	case SET:
    		Constraint constraint = new Constraint(m_operation);
    		int i = 1;
    		while (!areArgsDone(args, index + i)) {
    			constraint.m_strings.add(args[index + i]);
    			constraint.m_constraintString = constraint.m_constraintString + " " + args[index + i];
    			i++;
    		}
    		return constraint;
    	case LIST:
    		return new Constraint(m_operation);
    	}
    	assert(false);
		return null;    						
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		com.xmos.xta.engine.constraint.Constraint constraint;
    	switch (m_operation) {
    	case SET:
    		if (m_constraintString.equals("")) {
    			engine.setConstraint(null);
    		} else {
    			constraint = ConstraintFactory.createConstraint(engine, m_constraintString);
    			if (constraint != null)
    				engine.setConstraint(constraint);
    		}
    		return;
    	case LIST:
    		constraint = engine.getConstraint();
    		if (constraint == null)
    			console.display("No constraint set\n");
    		else
    			console.display(constraint.getString() + "\n");
    		return;
    	}
    	assert(false);
    }
 }
