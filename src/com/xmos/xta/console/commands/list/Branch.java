package com.xmos.xta.console.commands.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class Branch extends Command {
	public enum Operation {
		ADD, REMOVE, LIST
	};

	private String m_fromRefString;
	private Collection<String> m_toRefStrings;
	private Operation m_operation;

	public Branch(Operation operation) {
		m_operation = operation;
		m_fromRefString = "";
		m_toRefStrings = new HashSet<String>();
	}

	public Branch(String fromRefString, Collection<String> toRefStrings, Operation operation) {
		assert(operation == Operation.ADD);
		m_operation = operation;
		m_fromRefString = fromRefString;
		m_toRefStrings = toRefStrings;
	}

	Branch(String fromRefString, Operation operation) {
		assert(operation == Operation.REMOVE);
		m_operation = operation;
		m_fromRefString = fromRefString;
	}
	
	public int numArgs() {
    	switch (m_operation) {
    	case ADD:
            return 1 + m_toRefStrings.size();         
    	case REMOVE:
            return 1;
    	case LIST:
            return 0;
        default:
        	assert(false);
            return 0;
    	}    		
	}

	public ArgType[] getArgTypes() {
    	switch (m_operation) {
    	case ADD:
    		// Note: From the point of view of the console command 
    		// completion, it wont work after the 10th argument...
    		return new Command.ArgType[] {
    				ArgType.LABEL, ArgType.LABEL, ArgType.LABEL, ArgType.LABEL, ArgType.LABEL,
         			ArgType.LABEL, ArgType.LABEL, ArgType.LABEL, ArgType.LABEL, ArgType.LABEL};
    	case REMOVE:
    	case LIST:
            return super.getArgTypes();
        default:
        	assert(false);
            return null;
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
    		String str = "branch " + m_fromRefString;
    		for (String toRefString : m_toRefStrings) {
    			str += " " + toRefString;
    		}
    		return str; 
    		
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
            return "branch <from LABEL_REF> <to LABEL_REF> <to LABEL_REF> ... etc: Adds the given from/to references to the branches list";         
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
    		int i = 2;
    		Collection<String> toRefStrings = new ArrayList<String>();
    		while (!areArgsDone(args, index + i)) {
    			toRefStrings.add(args[index + i]);
    			++i;
    		}		    		
    		return new Branch(args[index + 1], toRefStrings, m_operation);  
    		
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

		Reference fromRef = ReferenceFactory.createReference(m_fromRefString, ReferenceClass.LABEL_REF);
		
    	switch (m_operation) {
    	case ADD:
    		Collection<Reference> toRefs = new ArrayList<Reference>(); 
    		for (String toRefString : m_toRefStrings) {
    			Reference ref = ReferenceFactory.createReference(toRefString, ReferenceClass.LABEL_REF);
    			toRefs.add(ref);
    		}

    		engine.addBranch(getSrc(), fromRef, toRefs);
			break;			
    	case REMOVE:
			engine.removeBranch(getSrc(), fromRef);
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
		
    	Iterator<Map.Entry<Long, TreeSet<Long>>> iter = engine.getBranches().entrySet().iterator();        
    	while (iter.hasNext()) {
    	    Map.Entry<Long, TreeSet<Long>> pair = iter.next();
    	    Long fromPc = pair.getKey();
    	    TreeSet<Long> toPcs = pair.getValue();
    	    
    	    console.display(String.format("- 0x%x =>", fromPc));
    	    for (Long toPc : toPcs) {
        	    console.display(String.format(" 0x%x", toPc));    	    	
    	    }
    	    console.display("\n");
    	}		
	}
}
