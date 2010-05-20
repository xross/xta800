package com.xmos.xta.core.console.commands.debug;

import java.util.LinkedList;
import java.util.List;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.MemoryMonitorException;
import com.xmos.xta.core.engine.analysis.controlflow.CallGraph;
import com.xmos.xta.core.engine.analysis.routes.PreanalysisReferenceMatcher;
import com.xmos.xta.core.engine.analysis.routes.StackNode;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.CoreId;

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
			for(Reference ref : engine.getExclusions()){
				if(ref.getCoreId().equals(coreId)){
					exclusionStackNodes.addAll(getPathStackNodes(engine.getCallGraph(coreId), ref, engine));
				}
			}
			System.out.println(coreId + ":");
			for(StackNode exclusionStack : exclusionStackNodes){
				System.out.println(exclusionStack.getInstruction());
			}
    	}
    }
    
    private List<StackNode> getPathStackNodes(CallGraph callGraph, Reference reference, Engine engine) {
		PreanalysisReferenceMatcher prm = new PreanalysisReferenceMatcher();
		try {
			return prm.matchReference(callGraph, reference, engine);

		} catch (AbortedException e) {
			return null;
		} catch (MemoryMonitorException e) {
			return null;
		}
	}
}
