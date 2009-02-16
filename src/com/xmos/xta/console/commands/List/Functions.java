package com.xmos.xta.console.commands.List;

import java.util.Vector;

import com.xmos.xeloader.SymbolTable;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.console.Command;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

public class Functions extends Command {
	public Functions() {
	}

    public int numArgs() {
    	return 0;
    }        
	
    public String name() {
    	return "functions";
    }        

    public String commandLine() {
    	return "functions";
    }
    
    public String description(Engine engine, boolean detailed) {
        return "functions: Lists the functions in the loaded application";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
        return new Functions();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			
        
	    int nodeId = engine.getNodeId();
	    int coreId = engine.getCoreId();
        Vector<String> functionNames = engine.getXeLoader().getFunctionNames(nodeId, coreId);

        XeLoader xeLoader = engine.getXeLoader();
        SymbolTable symbolTable = xeLoader.getSymbolTable(nodeId, coreId);
        
        for (String functionName : functionNames) {
        	int functionAddress = symbolTable.symbolAddress(functionName);
            console.display(String.format("- %s (0x%x)\n", functionName, functionAddress));
        }
    }
}
