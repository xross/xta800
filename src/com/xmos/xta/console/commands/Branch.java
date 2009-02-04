package com.xmos.xta.console.commands;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.references.CodeReference;
import com.xmos.xta.engine.references.CodeReferenceFactory;

public class Branch extends Command {
	enum Operation {
		ADD, REMOVE, LIST
	};

	private CodeReference m_fromRef;
	private CodeReference m_toRef;
	private Operation m_operation;

	Branch(Operation operation) {
		m_operation = operation;
		m_fromRef = null;
		m_toRef = null;
	}

	Branch(CodeReference fromRef, CodeReference toRef, Operation operation) {
		assert(operation == Operation.ADD);
		m_operation = operation;
		m_fromRef = fromRef;
		m_toRef = toRef;
	}

	Branch(CodeReference fromRef, Operation operation) {
		assert(operation == Operation.REMOVE);
		m_operation = operation;
		m_fromRef = fromRef;
		m_toRef = null;
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
		return "branch";
	}

	public String commandLine() {
    	switch (m_operation) {
    	case ADD:
    		return "branch " + m_fromRef.getRef() + " " + m_toRef.getRef(); 
    	case REMOVE:
    		return "branch " + m_fromRef.getRef();
    	case LIST:
    		return "branch";
        default:
        	assert(false);
            return "";
    	}    			
	}

	public String description(Engine engine, boolean detailed) {
    	switch (m_operation) {
    	case ADD:
            return "branch <from ref> <to ref>: Adds the given from/to reference to the branches list";         
    	case REMOVE:
            return "branch <from ref>: Removes the given from reference from the branches list";
    	case LIST:
            return "branch: Lists the branches";
        default:
        	assert(false);
            return "";
    	}    		
	}

	public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length)
			return null;
				
    	switch (m_operation) {
    	case ADD:
    		CodeReference fromRef = CodeReferenceFactory.createCodeReference(args[index + 1]);
    		CodeReference toRef   = CodeReferenceFactory.createCodeReference(args[index + 2]);
    		return new Branch(fromRef, toRef, m_operation);            
    	case REMOVE:
    		CodeReference ref = CodeReferenceFactory.createCodeReference(args[index + 1]);
    		return new Branch(ref, m_operation);            
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
		
    	switch (m_operation) {
    	case ADD:
			engine.addBranch(m_fromRef, m_toRef);
			break;			
    	case REMOVE:
			engine.removeBranch(m_fromRef);
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
    	Iterator<Map.Entry<Integer, TreeSet<Integer>>> iter = engine.getBranches().entrySet().iterator();        
    	while (iter.hasNext()) {
    	    Map.Entry<Integer, TreeSet<Integer>> pair = iter.next();
    	    Integer fromPc = pair.getKey();
    	    TreeSet<Integer> toPcs = pair.getValue();
    	    
    	    console.display(String.format("- 0x%08x =>", fromPc));
    	    for (Integer toPc : toPcs) {
        	    console.display(String.format(" 0x%08x", toPc));    	    	
    	    }
    	    console.display("\n");
    	}		
	}
}
