package com.xmos.xta.core.engine.assertions;

import java.util.ArrayList;
import java.util.List;

import com.xmos.xta.core.engine.NodeCoreCollection;
import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.references.Reference;

public class Assertions {
	private NodeCoreCollection<ArrayList<FunctionTimeInfo>> m_functionTimes;
	private NodeCoreCollection<ArrayList<PathTimeInfo>> m_pathTimes;
	private NodeCoreCollection<ArrayList<LoopInfo>> m_loops;
	private NodeCoreCollection<ArrayList<LoopPathInfo>> m_loopPaths;
	private NodeCoreCollection<ArrayList<LoopScopeInfo>> m_loopScopes;
	private NodeCoreCollection<ArrayList<InstructionTimeInfo>> m_instructionTimes;
    
    public Assertions() {
    	m_functionTimes    = new NodeCoreCollection<ArrayList<FunctionTimeInfo>>();
    	m_pathTimes        = new NodeCoreCollection<ArrayList<PathTimeInfo>>();
    	m_instructionTimes = new NodeCoreCollection<ArrayList<InstructionTimeInfo>>();
    	m_loops            = new NodeCoreCollection<ArrayList<LoopInfo>>();
    	m_loopPaths        = new NodeCoreCollection<ArrayList<LoopPathInfo>>();
    	m_loopScopes       = new NodeCoreCollection<ArrayList<LoopScopeInfo>>();
	}

    public void clear() {
    	m_functionTimes.clear();
    	m_pathTimes.clear();
    	m_instructionTimes.clear();    	
    	m_loops.clear();
    	m_loopPaths.clear();
    	m_loopScopes.clear();
    }
    
    public List<FunctionTimeInfo> getFunctionTimes(int nodeId, int coreId) {
    	return get(m_functionTimes, nodeId, coreId);
    }

    public List<PathTimeInfo> getPathTimes(int nodeId, int coreId) {    
    	return get(m_pathTimes, nodeId, coreId);
    }

    public List<InstructionTimeInfo> getInstructionTimes(int nodeId, int coreId) {    	
    	return get(m_instructionTimes, nodeId, coreId);
    }
    
    public List<LoopInfo> getLoops(int nodeId, int coreId) {    	
    	return get(m_loops, nodeId, coreId);
    }

    public List<LoopPathInfo> getLoopPaths(int nodeId, int coreId) {    	
    	return get(m_loopPaths, nodeId, coreId);
    }

    public List<LoopScopeInfo> getLoopScopes(int nodeId, int coreId) {    	
    	return get(m_loopScopes, nodeId, coreId);
    }
    
	public void addFunctionTime(int nodeId, int coreId, String function, TimeUnit timeUnit) {
		get(m_functionTimes, nodeId, coreId).add(new FunctionTimeInfo(function, timeUnit));
	}

	public void addPathTime(int nodeId, int coreId, String from, String to, TimeUnit timeUnit) {
		get(m_pathTimes, nodeId, coreId).add(new PathTimeInfo(from, to, timeUnit));
	}

	public void addInstructionTime(int nodeId, int coreId, Reference reference, TimeUnit timeUnit) {
		get(m_instructionTimes, nodeId, coreId).add(new InstructionTimeInfo(reference, timeUnit));
	}
	
	public void addLoop(int nodeId, int coreId, Reference reference, long iterations) {
		get(m_loops, nodeId, coreId).add(new LoopInfo(reference, iterations));
	}

	public void addLoopPath(int nodeId, int coreId, Reference reference, long iterations) {
		get(m_loopPaths, nodeId, coreId).add(new LoopPathInfo(reference, iterations));
	}

	public void addLoopScope(int nodeId, int coreId, Reference reference, boolean absolute) {
		get(m_loopScopes, nodeId, coreId).add(new LoopScopeInfo(reference, absolute));
	}
		
	private <T> ArrayList<T> get(NodeCoreCollection<ArrayList<T>> list, int nodeId, int coreId) {
		if (list.get(nodeId, coreId) == null) {
			list.add(nodeId, coreId, new ArrayList<T>());
		}
        return list.get(nodeId, coreId);		
	}		
}

