package com.xmos.xta.core.engine.assertions;

import java.util.ArrayList;
import java.util.Collection;

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
    
    public Collection<FunctionTimeInfo> getFunctionTimes(int nodeId, int coreId) {
    	return get(m_functionTimes, nodeId, coreId);
    }

    public Collection<PathTimeInfo> getPathTimes(int nodeId, int coreId) {    
    	return get(m_pathTimes, nodeId, coreId);
    }

    public Collection<InstructionTimeInfo> getInstructionTimes(int nodeId, int coreId) {    	
    	return get(m_instructionTimes, nodeId, coreId);
    }
    
    public Collection<LoopInfo> getLoops(int nodeId, int coreId) {    	
    	return get(m_loops, nodeId, coreId);
    }

    public Collection<LoopPathInfo> getLoopPaths(int nodeId, int coreId) {    	
    	return get(m_loopPaths, nodeId, coreId);
    }

    public Collection<LoopScopeInfo> getLoopScopes(int nodeId, int coreId) {    	
    	return get(m_loopScopes, nodeId, coreId);
    }
    
	public void addFunctionTime(int nodeId, int coreId, String function, TimeUnit timeUnit) {
		FunctionTimeInfo info = new FunctionTimeInfo(function, timeUnit);
		ArrayList<FunctionTimeInfo> list = get(m_functionTimes, nodeId, coreId);		
		list.remove(info);
		list.add(info);
	}

	public void addPathTime(int nodeId, int coreId, String from, String to, TimeUnit timeUnit) {
		PathTimeInfo info = new PathTimeInfo(from, to, timeUnit);
		ArrayList<PathTimeInfo> list = get(m_pathTimes, nodeId, coreId);		
		list.remove(info);
		list.add(info);
	}

	public void addInstructionTime(int nodeId, int coreId, Reference reference, TimeUnit timeUnit) {
		InstructionTimeInfo info = new InstructionTimeInfo(reference, timeUnit);
		ArrayList<InstructionTimeInfo> list = get(m_instructionTimes, nodeId, coreId);		
		list.remove(info);
		list.add(info);
	}
	
	public void addLoop(int nodeId, int coreId, Reference reference, long iterations) {
		LoopInfo info = new LoopInfo(reference, iterations);
		ArrayList<LoopInfo> list = get(m_loops, nodeId, coreId);		
		list.remove(info);
		list.add(info);
	}

	public void addLoopPath(int nodeId, int coreId, Reference reference, long iterations) {
		LoopPathInfo info = new LoopPathInfo(reference, iterations);
		ArrayList<LoopPathInfo> list = get(m_loopPaths, nodeId, coreId);		
		list.remove(info);
		list.add(info);
	}

	public void addLoopScope(int nodeId, int coreId, Reference reference, boolean absolute) {
		LoopScopeInfo info = new LoopScopeInfo(reference, absolute);
		ArrayList<LoopScopeInfo> list = get(m_loopScopes, nodeId, coreId);		
		list.remove(info);
		list.add(info);
	}

	public void removeFunctionTime(int nodeId, int coreId, String function) {
		get(m_functionTimes, nodeId, coreId).remove(new FunctionTimeInfo(function, null));
	}

	public void removePathTime(int nodeId, int coreId, String from, String to) {
		get(m_pathTimes, nodeId, coreId).remove(new PathTimeInfo(from, to, null));
	}
	
	public void removeInstructionTime(int nodeId, int coreId, Reference reference) {
		get(m_instructionTimes, nodeId, coreId).remove(new InstructionTimeInfo(reference, null));
	}
	
	public void removeLoop(int nodeId, int coreId, Reference reference) {
		get(m_loops, nodeId, coreId).remove(new LoopInfo(reference, 0));
	}

	public void removeLoopPath(int nodeId, int coreId, Reference reference) {
		get(m_loopPaths, nodeId, coreId).remove(new LoopPathInfo(reference, 0));
	}

	public void removeLoopScope(int nodeId, int coreId, Reference reference) {
		get(m_loopScopes, nodeId, coreId).remove(new LoopScopeInfo(reference, false));
	}

	public void clearFunctionTimes(int nodeId, int coreId) {
		get(m_functionTimes, nodeId, coreId).clear();
	}

	public void clearPathTimes(int nodeId, int coreId) {
		get(m_pathTimes, nodeId, coreId).clear();
	}
	
	public void clearInstructionTimes(int nodeId, int coreId) {
		get(m_instructionTimes, nodeId, coreId).clear();
	}
	
	public void clearLoops(int nodeId, int coreId) {
		get(m_loops, nodeId, coreId).clear();
	}

	public void clearLoopPaths(int nodeId, int coreId) {
		get(m_loopPaths, nodeId, coreId).clear();
	}

	public void clearLoopScopes(int nodeId, int coreId) {
		get(m_loopScopes, nodeId, coreId).clear();
	}
	
	private <T> ArrayList<T> get(NodeCoreCollection<ArrayList<T>> list, int nodeId, int coreId) {
		if (list.get(nodeId, coreId) == null) {
			list.add(nodeId, coreId, new ArrayList<T>());
		}
        return list.get(nodeId, coreId);		
	}		
}

