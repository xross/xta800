package com.xmos.xta.core.engine.analysis.structural;

import java.util.Map;

import com.xmos.libs.memorymonitor.MemoryMonitorException;
import com.xmos.libs.util.NonIteratableMap;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.controlflows.FunctionContainer;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class CachedFunctionMap {
    private Engine m_engine;
    private Map<FunctionContainer, Node> m_cachedFunctions;
    
    public CachedFunctionMap(Engine engine) {
    	m_engine = engine;
    	m_cachedFunctions = new NonIteratableMap<FunctionContainer, Node>(); 
    }
    
	public Node getCachedFunction(FunctionContainer functionContainer) 
        throws AbortedException, MemoryMonitorException {
	
    	Node cachedFunction = m_cachedFunctions.get(functionContainer);
	    if (cachedFunction == null){
		    StructuralAnalysisEngine sae = new StructuralAnalysisEngine(m_engine);			
		    cachedFunction = sae.createStructure(
		    		functionContainer.getControlFlow(), 
		    		functionContainer.getStartInstruction(), 
		    		functionContainer.getFunctionName());

		    m_cachedFunctions.put(functionContainer, cachedFunction);
	    }
	    return cachedFunction;
    }   
}
