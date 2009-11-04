package com.xmos.xta.core.engine.assertions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.xmos.xta.core.engine.NodeCoreCollection;
import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.references.Reference;

public class Assertions {
	private NodeCoreCollection<ArrayList<FunctionInfo>> m_functions;
	private NodeCoreCollection<ArrayList<PathInfo>> m_paths;
	private NodeCoreCollection<ArrayList<LoopInfo>> m_loops;
	private NodeCoreCollection<ArrayList<LoopPathInfo>> m_loopPaths;
	private NodeCoreCollection<ArrayList<LoopScopeInfo>> m_loopScopes;
	private NodeCoreCollection<ArrayList<TimeInfo>> m_times;
    
    public Assertions() {
    	m_functions  = new NodeCoreCollection<ArrayList<FunctionInfo>>();
    	m_paths      = new NodeCoreCollection<ArrayList<PathInfo>>();
    	m_loops      = new NodeCoreCollection<ArrayList<LoopInfo>>();
    	m_loopPaths  = new NodeCoreCollection<ArrayList<LoopPathInfo>>();
    	m_loopScopes = new NodeCoreCollection<ArrayList<LoopScopeInfo>>();
    	m_times      = new NodeCoreCollection<ArrayList<TimeInfo>>();
	}

    public void clear() {
    	m_functions.clear();
    	m_paths.clear();
    	m_loops.clear();
    	m_loopPaths.clear();
    	m_loopScopes.clear();
    	m_times.clear();    	
    }

	public Collection<AssertionInfo> getDescriptions(int nodeId, int coreId) {
	    Collection<AssertionInfo> infos = new ArrayList<AssertionInfo>();   
		for (FunctionInfo info : get(m_functions, nodeId, coreId)) {
			infos.add(new AssertionInfo(
					String.format("Function Time: %s", info.getFunction()),
					String.format("%s", info.getTimeUnit().getDescription(false))));
		}
		for (PathInfo info : get(m_paths, nodeId, coreId)) {
			infos.add(new AssertionInfo(
					String.format("Path Time: %s %s", info.getFrom(), info.getTo()),
					String.format("%s", info.getTimeUnit().getDescription(false))));			
		}
    	for (LoopInfo info : get(m_loops, nodeId, coreId)) {
			infos.add(new AssertionInfo(
					String.format("Loop Iterations: %s", info.getReference().toString()),
					String.format("%d", info.getIterations())));			
    	}
	    for (LoopPathInfo info : get(m_loopPaths, nodeId, coreId)) {
			infos.add(new AssertionInfo(
					String.format("Loop Path Iterations: %s", info.getReference().toString()),
					String.format("%d", info.getIterations())));			
	    }
  	    for (LoopScopeInfo info : get(m_loopScopes, nodeId, coreId)) {
			infos.add(new AssertionInfo(
					String.format("Loop Scope: %s", info.getReference().toString()),
					String.format("%s", info.getAbsolute() ? "a" : "r")));			
	    }
	    for (TimeInfo info : get(m_times, nodeId, coreId)) {
			infos.add(new AssertionInfo(
					String.format("Instruction Time: %s", info.getReference().toString()),
					String.format("%s", info.getTimeUnit().getDescription(false))));
    	}
	    return infos;
    }
    
    public List<FunctionInfo> getFunctions(int nodeId, int coreId) {
    	return get(m_functions, nodeId, coreId);
    }

    public List<PathInfo> getPaths(int nodeId, int coreId) {    
    	return get(m_paths, nodeId, coreId);
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

    public List<TimeInfo> getTimes(int nodeId, int coreId) {    	
    	return get(m_times, nodeId, coreId);
    }
    
	public void addFunction(int nodeId, int coreId, String function, TimeUnit timeUnit) {
		get(m_functions, nodeId, coreId).add(new FunctionInfo(function, timeUnit));
	}

	public void addPath(int nodeId, int coreId, String from, String to, TimeUnit timeUnit) {
		get(m_paths, nodeId, coreId).add(new PathInfo(from, to, timeUnit));
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

	public void addTime(int nodeId, int coreId, Reference reference, TimeUnit timeUnit) {
		get(m_times, nodeId, coreId).add(new TimeInfo(reference, timeUnit));
	}
		
	private <T> ArrayList<T> get(NodeCoreCollection<ArrayList<T>> list, int nodeId, int coreId) {
		if (list.get(nodeId, coreId) == null) {
			list.add(nodeId, coreId, new ArrayList<T>());
		}
        return list.get(nodeId, coreId);		
	}		
}

