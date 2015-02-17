package com.xmos.xta.core.engine.analysis.structural;

import java.util.Map;

import com.xmos.libs.core.controlflow.FunctionControlFlow;
import com.xmos.libs.core.memorymonitor.MemoryMonitorException;
import com.xmos.libs.core.utils.NonIteratableMap;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class CachedFunctionMap {
    private Engine m_engine;
    private Map<FunctionControlFlow, Node> m_cachedFunctions;

    public CachedFunctionMap(Engine engine) {
        m_engine = engine;
        m_cachedFunctions = new NonIteratableMap<FunctionControlFlow, Node>();
    }

    public Node getCachedFunction(FunctionControlFlow functionControlFlow)
        throws AbortedException, MemoryMonitorException {

        Node cachedFunction = m_cachedFunctions.get(functionControlFlow);
        if (cachedFunction == null){
            cachedFunction = StructuralAnalysisEngine.createStructure(m_engine,
                    functionControlFlow.getControlFlow(),
                    functionControlFlow.getStartInstructionNode(),
                    functionControlFlow.getFunctionName());

            m_cachedFunctions.put(functionControlFlow, cachedFunction);
        }
        return cachedFunction;
    }
}
