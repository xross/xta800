package com.xmos.xta.console.commands;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.ReferenceFactory.ReferenceClass;

public class Branch extends Command {
	public enum Operation {
		ADD, REMOVE, LIST
	};

	private String m_fromRefString;
	private String m_toRefString;
	private Operation m_operation;

	public Branch(Operation operation) {
		m_operation = operation;
		m_fromRefString = "";
		m_toRefString = "";
	}

	public Branch(String fromRefString, String toRefString, Operation operation) {
		assert(operation == Operation.ADD);
		m_operation = operation;
		m_fromRefString = fromRefString;
		m_toRefString = toRefString;
	}

	Branch(String fromRefString, Operation operation) {
		assert(operation == Operation.REMOVE);
		m_operation = operation;
		m_fromRefString = fromRefString;
		m_toRefString = "";
	}
	
	public int numArgs() {
    	switch (m_operation) {
    	case ADD:
            return 2;         
    	case REMOVE:
            return 1;
    	case LIST:
            return 0;
        default:
        	assert(false);
            return 0;
    	}    		
	}

	public String name() {
    	switch (m_operation) {
    	case ADD:
    	case REMOVE:
    		return "branch"; 
    	case LIST:
    		return "branches";
    	}    			
    	assert(false);
        return "";
	}

	public String commandLine() {
    	switch (m_operation) {
    	case ADD:
    		return "branch " + m_fromRefString + " " + m_toRefString; 
    	case REMOVE:
    		return "branch " + m_fromRefString;
    	case LIST:
    		return "branches";
        default:
        	assert(false);
            return "";
    	}    			
	}

	public String description(Engine engine, boolean detailed) {
    	switch (m_operation) {
    	case ADD:
            return "branch <from LABEL_REF> <to LABEL_REF>: Adds the given from/to reference to the branches list";         
    	case REMOVE:
            return "branch <from LABEL_REF>: Removes the given from reference from the branches list";
    	case LIST:
            return "branches: Lists the branches";
        default:
        	assert(false);
            return "";
    	}    		
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;
				
    	switch (m_operation) {
    	case ADD:
    		return new Branch(args[index + 1], args[index + 2], m_operation);            
    	case REMOVE:
    		return new Branch(args[index + 1], m_operation);            
    	case LIST:
    		return new Branch(m_operation);            
        default:
        	assert(false);
            return null;
    	}    						
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		Reference fromRef = ReferenceFactory.createReference(
				m_fromRefString, ReferenceClass.LABEL_REF);
		Reference toRef = ReferenceFactory.createReference(
				m_toRefString, ReferenceClass.LABEL_REF);

    	switch (m_operation) {
    	case ADD:
			engine.addBranch(fromRef, toRef);
			break;			
    	case REMOVE:
			engine.removeBranch(fromRef);
			break;			
    	case LIST:
    		listBranches(engine, console);        	
    		break;    		
        default:
           	assert(false);
       		break;
    	}
	}
	
	private void listBranches(Engine engine, Console console) {
		if (engine.getBranches().entrySet().size() == 0){
			console.display("none\n");
			return;
		}
		
    	Iterator<Map.Entry<Integer, TreeSet<Integer>>> iter = engine.getBranches().entrySet().iterator();        
    	while (iter.hasNext()) {
    	    Map.Entry<Integer, TreeSet<Integer>> pair = iter.next();
    	    Integer fromPc = pair.getKey();
    	    TreeSet<Integer> toPcs = pair.getValue();
    	    
    	    console.display(String.format("- 0x%x =>", fromPc));
    	    for (Integer toPc : toPcs) {
        	    console.display(String.format(" 0x%x", toPc));    	    	
    	    }
    	    console.display("\n");
    	}		
	}
}
