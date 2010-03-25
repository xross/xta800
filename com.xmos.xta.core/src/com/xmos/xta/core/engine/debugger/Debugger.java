package com.xmos.xta.core.engine.debugger;

import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.controlflow.CallGraph;
import com.xmos.xta.core.engine.analysis.controlflow.FunctionContainer;
import com.xmos.xta.core.engine.utils.NodeCorePair;

public class Debugger {
	private Engine m_engine; 
	
	public Debugger(Engine engine) {
		m_engine = engine;
	}

	public void dumpControlFlow(CommandSource src, String functionName) {
		boolean hit = false;		
		for (NodeCorePair activeCore : m_engine.getActiveCores()) {
			
			CallGraph callGraph = m_engine.getCallGraph(activeCore);
			assert(callGraph != null);
		
		    for (FunctionContainer functionContainer : callGraph.getFunctionContainers()) {
			    if (functionContainer.getFunctionName().equals(functionName)) {
				    functionContainer.dumpGraph();
				    hit = true;
			    }
		    }
		}
		
		if (!hit) { 
		    m_engine.notify(src, Notifier.NOTIFY_ERROR, String.format("Function: %s not found on any active core", functionName));
		}
	}
}
