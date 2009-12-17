package com.xmos.xta.core.engine.defines;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.xmos.libs.util.Pair;
import com.xmos.xta.core.engine.NodeCoreCollection;
import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.references.Reference;

public class Defines {
	private NodeCoreCollection<List<FunctionTimeInfo>> m_functionTimes;
	private NodeCoreCollection<List<PathTimeInfo>> m_pathTimes;
	private NodeCoreCollection<List<LoopInfo>> m_loops;
	private NodeCoreCollection<List<LoopPathInfo>> m_loopPaths;
	private NodeCoreCollection<List<LoopScopeInfo>> m_loopScopes;
	private NodeCoreCollection<List<InstructionTimeInfo>> m_instructionTimes;
    
    public Defines() {
    	m_functionTimes    = new NodeCoreCollection<List<FunctionTimeInfo>>();
    	m_pathTimes        = new NodeCoreCollection<List<PathTimeInfo>>();
    	m_instructionTimes = new NodeCoreCollection<List<InstructionTimeInfo>>();
    	m_loops            = new NodeCoreCollection<List<LoopInfo>>();
    	m_loopPaths        = new NodeCoreCollection<List<LoopPathInfo>>();
    	m_loopScopes       = new NodeCoreCollection<List<LoopScopeInfo>>();
	}

    public Defines(Defines defines) {    	
    	m_functionTimes    = copy(defines.m_functionTimes);
    	m_pathTimes        = copy(defines.m_pathTimes);
    	m_instructionTimes = copy(defines.m_instructionTimes);
    	m_loops            = copy(defines.m_loops);
    	m_loopPaths        = copy(defines.m_loopPaths);
    	m_loopScopes       = copy(defines.m_loopScopes);
    }
    	
	public void updateScript(Defines defines, StringBuffer script, int nodeId, int coreId) {		
		if (!getFunctionTimes(nodeId, coreId).equals(defines.getFunctionTimes(nodeId, coreId))) {			
			updateScript(script, getFunctionTimes(nodeId, coreId), 
					defines.getFunctionTimes(nodeId, coreId), "functiontime", "*");
		}
		if (!getPathTimes(nodeId, coreId).equals(defines.getPathTimes(nodeId, coreId))) {
			updateScript(script, getPathTimes(nodeId, coreId), 
					defines.getPathTimes(nodeId, coreId), "pathtime", "* *");
		}
		if (!getInstructionTimes(nodeId, coreId).equals(defines.getInstructionTimes(nodeId, coreId))) {
			updateScript(script, getInstructionTimes(nodeId, coreId), 
					defines.getInstructionTimes(nodeId, coreId), "instructiontime", "*");
		}
		if (!getLoops(nodeId, coreId).equals(defines.getLoops(nodeId, coreId))) {
			updateScript(script, getLoops(nodeId, coreId), 
					defines.getLoops(nodeId, coreId), "loop", "*");
		}
		if (!getLoopPaths(nodeId, coreId).equals(defines.getLoopPaths(nodeId, coreId))) {
			updateScript(script, getLoopPaths(nodeId, coreId), 
					defines.getLoopPaths(nodeId, coreId), "looppath", "*");
		}
		if (!getLoopScopes(nodeId, coreId).equals(defines.getLoopScopes(nodeId, coreId))) {
			updateScript(script, getLoopScopes(nodeId, coreId), 
					defines.getLoopScopes(nodeId, coreId), "loopscope", "*");
		}
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
    
	public void addFunctionTime(int nodeId, int coreId, Reference function, TimeUnit timeUnit) {
		FunctionTimeInfo info = new FunctionTimeInfo(function, timeUnit);
		List<FunctionTimeInfo> list = get(m_functionTimes, nodeId, coreId);		
		list.remove(info);
		list.add(info);
	}

	public void addPathTime(int nodeId, int coreId, Reference from, Reference to, TimeUnit timeUnit) {
		PathTimeInfo info = new PathTimeInfo(from, to, timeUnit);
		List<PathTimeInfo> list = get(m_pathTimes, nodeId, coreId);		
		list.remove(info);
		list.add(info);
	}

	public void addInstructionTime(int nodeId, int coreId, Reference reference, TimeUnit timeUnit) {
		InstructionTimeInfo info = new InstructionTimeInfo(reference, timeUnit);
		List<InstructionTimeInfo> list = get(m_instructionTimes, nodeId, coreId);		
		list.remove(info);
		list.add(info);
	}
	
	public void addLoop(int nodeId, int coreId, Reference reference, long iterations) {
		LoopInfo info = new LoopInfo(reference, iterations);
		List<LoopInfo> list = get(m_loops, nodeId, coreId);		
		list.remove(info);
		list.add(info);
	}

	public void addLoopPath(int nodeId, int coreId, Reference reference, long iterations) {
		LoopPathInfo info = new LoopPathInfo(reference, iterations);
		List<LoopPathInfo> list = get(m_loopPaths, nodeId, coreId);		
		list.remove(info);
		list.add(info);
	}

	public void addLoopScope(int nodeId, int coreId, Reference reference, boolean absolute) {
		LoopScopeInfo info = new LoopScopeInfo(reference, absolute);
		List<LoopScopeInfo> list = get(m_loopScopes, nodeId, coreId);		
		list.remove(info);
		list.add(info);
	}

	public void removeFunctionTime(int nodeId, int coreId, Reference function) {
		removeAll(get(m_functionTimes, nodeId, coreId),
				new FunctionTimeInfo(function, null));
	}

	public void removePathTime(int nodeId, int coreId, Reference from, Reference to) {
		removeAll(get(m_pathTimes, nodeId, coreId),
				new PathTimeInfo(from, to, null));
	}
	
	public void removeInstructionTime(int nodeId, int coreId, Reference reference) {
		removeAll(get(m_instructionTimes, nodeId, coreId),
				new InstructionTimeInfo(reference, null));
	}
	
	public void removeLoop(int nodeId, int coreId, Reference reference) {
		removeAll(get(m_loops, nodeId, coreId),
				new LoopInfo(reference, 0));
	}

	public void removeLoopPath(int nodeId, int coreId, Reference reference) {
		removeAll(get(m_loopPaths, nodeId, coreId),
				new LoopPathInfo(reference, 0));
	}

	public void removeLoopScope(int nodeId, int coreId, Reference reference) {
		removeAll(get(m_loopScopes, nodeId, coreId),
				new LoopScopeInfo(reference, false));
	}

	private <T> List<T> get(NodeCoreCollection<List<T>> list, int nodeId, int coreId) {
		if (list.get(nodeId, coreId) == null) {
			list.add(nodeId, coreId, new ArrayList<T>());
		}
        return list.get(nodeId, coreId);		
	}

	private <T> void removeAll(List<T> infos, T item) {
		List<T> toRemove = new ArrayList<T>();
		for (T info : infos) {
			if (item.equals(info)) {
				toRemove.add(info);
			}
		}
		infos.removeAll(toRemove);
	}

	private void updateScript(StringBuffer script, 
			Collection<? extends DefineInfo> newList, 
			Collection<? extends DefineInfo> oldList,
			String command, String removeStr) {
		
		if (!oldList.isEmpty()) {
		    script.append("remove " + command + " " + removeStr + "\n");
		}
		for (DefineInfo info : newList) {			
			script.append("add " + command + " " + info.getDescription() + " " + info.getValue() + "\n");
		}		
	}
	
	private <T> NodeCoreCollection<List<T>> copy(NodeCoreCollection<List<T>> oldList) {
    	NodeCoreCollection<List<T>> newList = new NodeCoreCollection<List<T>>();
    	for (Pair<Integer, Integer> pair : oldList.getEntries()) {
    		newList.add(pair.first, pair.second, 
                new ArrayList<T>(oldList.get(pair.first, pair.second)));
    	}
    	return newList;
	}
}

