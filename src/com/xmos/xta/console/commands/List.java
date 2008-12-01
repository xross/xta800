package com.xmos.xta.console.commands;

import java.io.File;
import java.util.Collection;
import java.util.Vector;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Reference;
import com.xmos.xta.engine.Engine.ReferenceMode;

class List extends Command {
	String m_type;
	
	List() {
		m_type = "";
	}

	List(String type) {
		m_type = type;
	}
	
    public int numArgs() {
    	return 1;
    }        
	
    public String name() {
    	return "list";
    }        
	
    public String commandLine() {
    	return "list " + m_type;
    }
    
    public String description(Engine engine, boolean detailed) {    	
        String descString = "list <type>: Lists all of the given <type> in the currently loaded executable";        
        if (detailed) {
        	descString += "\n";
        	descString += "      required: Lists the required references\n";
        	descString += "      prohibited: Lists the prohibited references\n";
        	descString += "      excluded: Lists the excluded references\n";
        	descString += "      functions: Lists the functions\n";
        	descString += "      sources: Lists the files";
        	
        }
        return descString;
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		

		String type = args[index + 1];
        return new List(type);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
    	if (m_type.equals("functions")) {
    		listFunctions(engine, console);
        } else if (m_type.equals("sources")) {    	
    		listFiles(engine, console);
    	} else if (m_type.equals("required")) {    	
    		listRequirements(engine, console);
    	} else if (m_type.equals("prohibited")) {    	
    		listProhibiteds(engine, console);
    	} else if (m_type.equals("excluded")) {    	
    		listExcludes(engine, console);
    	}
    	else {
    		console.display(String.format("Error: Invalid list command: %s (Try 'help'..)\n", m_type));
    	}
    }    
    
    private void listExcludes(Engine engine, Console console) {
    	for(Reference ref : engine.getReference(ReferenceMode.EXCLUSION)){
    		console.display(String.format("- %s 0x%x\n", ref.getRef(), ref.getPc()));
    	}
	}

	private void listProhibiteds(Engine engine, Console console) {
		for(Reference ref : engine.getReference(ReferenceMode.PROHIBITED)){
    		console.display(String.format("- %s 0x%x\n", ref.getRef(), ref.getPc()));
    	}
	}

	private void listRequirements(Engine engine, Console console) {
    	for(Reference ref : engine.getReference(ReferenceMode.REQUIRED)){
    		console.display(String.format("- %s 0x%x\n", ref.getRef(), ref.getPc()));
    	}
	}

	private void listFunctions(Engine engine, Console console) {    	    
		if (!isExecutableLoaded(engine, console))
			return;			
        
	    int nodeId = engine.getNodeId();
	    int coreId = engine.getCoreId();
        Vector<String> functionNames = engine.getXeLoader().getFunctionNames(nodeId, coreId);

        for (String functionName : functionNames) {
            console.display(String.format("- %s\n", functionName));
        }
    }

    private void listFiles(Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;		
		
        int nodeId = engine.getNodeId();
	    int coreId = engine.getCoreId();
	    DebugInfo debugInfo = engine.getXeLoader().getDebugInfo(nodeId, coreId);
	    assert(debugInfo != null);        
		
    	Collection<File> sourceFiles = debugInfo.getDebugEngine().findFiles("");        
  	    for (File sourceFile : sourceFiles) {
  	    	console.display(String.format("- %s\n", sourceFile.getName()));
    	}    			
    }
}
