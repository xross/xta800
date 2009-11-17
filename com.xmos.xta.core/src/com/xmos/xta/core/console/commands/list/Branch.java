package com.xmos.xta.core.console.commands.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.list.List.Operation;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.core.engine.sections.BranchInfo;

public class Branch extends Command {
	private String m_fromRefString;
	private Collection<String> m_toRefStrings;
	private Operation m_operation;

	public Branch(Operation operation) {
		m_operation = operation;
		m_fromRefString = "";
		m_toRefStrings = new HashSet<String>();
	}

	public Branch(String fromRefString, Collection<String> toRefStrings, Operation operation) {
		m_operation = operation;
		m_fromRefString = fromRefString;
		m_toRefStrings = toRefStrings;
	}

	public int numArgs() {
    	switch (m_operation) {
    	case ADD:
    	case REMOVE:
            return 1 + m_toRefStrings.size();         
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
    	case REMOVE:
    		String str = "branch " + m_fromRefString;
    		for (String toRefString : m_toRefStrings) {
    			str += " " + toRefString;
    		}
    		return str; 
    		
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
            return "branch <from LABEL_REF|*> <to LABEL_REF|*> <to LABEL_REF|*>: Removes the given from/to references from the branches list";
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
    	case REMOVE:
    		int i = 2;
    		Collection<String> toRefStrings = new ArrayList<String>();
    		while (!areArgsDone(args, index + i)) {
    			toRefStrings.add(args[index + i]);
    			++i;
    		}		    		
    		return new Branch(args[index + 1], toRefStrings, m_operation);  
    		
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
		
		Collection<Reference> toRefs = new ArrayList<Reference>(); 
		for (String toRefString : m_toRefStrings) {
			Reference ref = ReferenceFactory.createReference(toRefString, ReferenceClass.LABEL_REF);
			toRefs.add(ref);
		}

    	switch (m_operation) {
    	case ADD:
    		engine.addBranch(getSrc(), fromRef, toRefs);
			break;			
    	case REMOVE:
			engine.removeBranch(getSrc(), fromRef, toRefs);
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
		
		Map<Reference, Set<BranchInfo>> branches = engine.getBranches();
		
		for (Reference fromRef : branches.keySet()) {
			String to = getTo(branches, fromRef, false);
			if (to.length() > 0) {
	    	    console.display(String.format("- %s =>%s (binary)\n", fromRef.toString(), to));
			}
			to = getTo(branches, fromRef, true);
			if (to.length() > 0) {
	    	    console.display(String.format("- %s =>%s (user)\n", fromRef.toString(), to));
			}
		}
	}

	private String getTo(Map<Reference, Set<BranchInfo>> branches, Reference fromRef, boolean isUser) {
		String result = "";
		for (BranchInfo info : branches.get(fromRef)) {
			if (isUser == info.isUser()) {
				result += String.format(" %s", info.getRef().toString());
			}
		}
		return result;
	}
}
