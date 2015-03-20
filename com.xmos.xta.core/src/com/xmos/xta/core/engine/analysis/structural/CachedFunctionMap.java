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
        if (m_cachedFunctions.containsKey(functionControlFlow))
            return m_cachedFunctions.get(functionControlFlow);
        Node functionNode = StructuralAnalysisEngine.createStructure(m_engine,
                functionControlFlow.getControlFlow(),
                functionControlFlow.getStartInstructionNode(),
                functionControlFlow.getFunctionName());
        // N.B. functionNode will be null if the startNode is not in the control flow (on the exclusion list).
        m_cachedFunctions.put(functionControlFlow, functionNode);
        return functionNode;
    }
}
