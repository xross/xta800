package com.xmos.xta.core.engine.debugger;

import java.util.Map;

import com.xmos.libs.core.controlflow.ControlFlow;
import com.xmos.libs.core.controlflow.FunctionControlFlow;
import com.xmos.libs.core.controlflow.nodes.CfgFunctionReferenceNode;
import com.xmos.libs.core.utils.CoreId;
import com.xmos.libs.core.utils.NonIteratableMap;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;

public class DumpCallGraph {
	private Engine m_engine;
	private int m_numFunctions;	
	
	DumpCallGraph(Engine engine) {
		m_engine = engine;
		m_numFunctions = 0;
	}

	public void dump(CommandSource src, Console console) {			
		for (CoreId coreId : m_engine.getXeLoader().getCoreIds()) {			
			ControlFlow controlFlow = m_engine.getControlFlow(coreId);
						
			Map<String, String> functionIdMap = new NonIteratableMap<String, String>();
			
			System.out.printf("############################################\n");
			System.out.printf("# %s\n", coreId.getCoreReference());
			System.out.printf("############################################\n");
			System.out.printf("digraph g {\n");
		    for (FunctionControlFlow functionControlFlow : controlFlow.getFunctionControlFlows()) {
		    	
		    	String name = functionControlFlow.getFunctionName();
		    	String id = getFunctionId(name, functionIdMap);
		    	
		    	System.out.printf("%s [label=\"%s\", fillcolor=red, style=filled, shape=box];\n", id, name);		    	
		    	dumpCalleesForFunction(functionControlFlow, id, functionIdMap);
		    }
		    System.out.printf("}\n");			
		}		
	}
	
	private void dumpCalleesForFunction(FunctionControlFlow functionControlFlow, 
			String functionId, Map<String, String> functionIdMap) {
		
    	for (CfgFunctionReferenceNode frn : functionControlFlow.getFunctionReferenceNodes()) {
			String name = frn.getFunctionName();
			String id = getFunctionId(name, functionIdMap);				
			System.out.printf("%s -> %s\n", functionId, id);
    	}
	}	
	
	private String getFunctionId(String functionName, Map<String, String> functionIdMap) {
		if (functionIdMap.containsKey(functionName)) {
			return functionIdMap.get(functionName);
		}

		String id = "f" + m_numFunctions;
		functionIdMap.put(functionName, id);
		++m_numFunctions;
		return id;
	}
}
