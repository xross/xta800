package com.xmos.xta.core.console.commands.debug;

import java.util.LinkedList;
import java.util.List;

import com.xmos.libs.controlflow.ControlFlow;
import com.xmos.libs.memorymonitor.MemoryMonitorException;
import com.xmos.libs.util.CoreId;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.routes.RouteConstructor;
import com.xmos.xta.core.engine.analysis.routes.StackNode;
import com.xmos.xta.core.engine.analysis.structural.CachedFunctionMap;
import com.xmos.xta.core.engine.defines.ExclusionInfo;
import com.xmos.xta.core.engine.references.Reference;

public class DumpActiveExclusions extends Command {
	private static final String[] COMMAND_NAMES = { "dumpactiveexclusions" };
	
	public DumpActiveExclusions() {
		super(COMMAND_NAMES);
	}

    public int numArgs() {
    	return 0;
    }

    public String commandLine() {
    	return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + ": Dump a list of pcs that the exclusions have resolved to";
    }

    public Command clone(String[] args, int index) {
    	return new DumpActiveExclusions();
    }

    public void execute(Shell shell, Engine engine, Console console) {   	
    	
    	for(CoreId coreId : engine.getActiveCores()){
    		List<StackNode> exclusionStackNodes = new LinkedList<StackNode>();
			for(ExclusionInfo info : engine.getExclusions()){
				Reference ref = info.getReference();
				if(ref.getCoreId().equals(coreId)){
					List<StackNode> stackNodes = getPathStackNodes(
							engine.getControlFlow(coreId), 
							engine.getCachedFunctionMap(coreId), ref);
					if (stackNodes != null) {
					    exclusionStackNodes.addAll(stackNodes);
					}
				}
			}
			System.out.println(coreId + ":");
			for(StackNode exclusionStack : exclusionStackNodes){
				System.out.println(exclusionStack.getInstruction());
			}
    	}
    }
    
    private List<StackNode> getPathStackNodes(ControlFlow controlFlow, 
    		CachedFunctionMap cachedFunctionMap, Reference reference) {
    	
		try {
			return RouteConstructor.getPathStackNodes(controlFlow, cachedFunctionMap, reference); 

		} catch (AbortedException e) {
			return null;
		} catch (MemoryMonitorException e) {
			return null;
		}
	}
}
