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
    	
	public void updateScript(Defines defines, StringBuffer script, int nodeNumber, int coreNumber) {		
		if (!getFunctionTimes(nodeNumber, coreNumber).equals(defines.getFunctionTimes(nodeNumber, coreNumber))) {			
			updateScript(script, getFunctionTimes(nodeNumber, coreNumber), 
					defines.getFunctionTimes(nodeNumber, coreNumber), "functiontime", "*");
		}
		if (!getPathTimes(nodeNumber, coreNumber).equals(defines.getPathTimes(nodeNumber, coreNumber))) {
			updateScript(script, getPathTimes(nodeNumber, coreNumber), 
					defines.getPathTimes(nodeNumber, coreNumber), "pathtime", "* *");
		}
		if (!getInstructionTimes(nodeNumber, coreNumber).equals(defines.getInstructionTimes(nodeNumber, coreNumber))) {
			updateScript(script, getInstructionTimes(nodeNumber, coreNumber), 
					defines.getInstructionTimes(nodeNumber, coreNumber), "instructiontime", "*");
		}
		if (!getLoops(nodeNumber, coreNumber).equals(defines.getLoops(nodeNumber, coreNumber))) {
			updateScript(script, getLoops(nodeNumber, coreNumber), 
					defines.getLoops(nodeNumber, coreNumber), "loop", "*");
		}
		if (!getLoopPaths(nodeNumber, coreNumber).equals(defines.getLoopPaths(nodeNumber, coreNumber))) {
			updateScript(script, getLoopPaths(nodeNumber, coreNumber), 
					defines.getLoopPaths(nodeNumber, coreNumber), "looppath", "*");
		}
		if (!getLoopScopes(nodeNumber, coreNumber).equals(defines.getLoopScopes(nodeNumber, coreNumber))) {
			updateScript(script, getLoopScopes(nodeNumber, coreNumber), 
					defines.getLoopScopes(nodeNumber, coreNumber), "loopscope", "*");
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
    
    public Collection<FunctionTimeInfo> getFunctionTimes(int nodeNumber, int coreNumber) {
    	return get(m_functionTimes, nodeNumber, coreNumber);
    }

    public Collection<PathTimeInfo> getPathTimes(int nodeNumber, int coreNumber) {    
    	return get(m_pathTimes, nodeNumber, coreNumber);
    }

    public Collection<InstructionTimeInfo> getInstructionTimes(int nodeNumber, int coreNumber) {    	
    	return get(m_instructionTimes, nodeNumber, coreNumber);
    }
    
    public Collection<LoopInfo> getLoops(int nodeNumber, int coreNumber) {    	
    	return get(m_loops, nodeNumber, coreNumber);
    }

    public Collection<LoopPathInfo> getLoopPaths(int nodeNumber, int coreNumber) {    	
    	return get(m_loopPaths, nodeNumber, coreNumber);
    }

    public Collection<LoopScopeInfo> getLoopScopes(int nodeNumber, int coreNumber) {    	
    	return get(m_loopScopes, nodeNumber, coreNumber);
    }
    
	public void addFunctionTime(int nodeNumber, int coreNumber, Reference function, TimeUnit timeUnit) {
		FunctionTimeInfo info = new FunctionTimeInfo(function, timeUnit);
		List<FunctionTimeInfo> list = get(m_functionTimes, nodeNumber, coreNumber);		
		list.remove(info);
		list.add(info);
	}

	public void addPathTime(int nodeNumber, int coreNumber, Reference from, Reference to, TimeUnit timeUnit) {
		PathTimeInfo info = new PathTimeInfo(from, to, timeUnit);
		List<PathTimeInfo> list = get(m_pathTimes, nodeNumber, coreNumber);		
		list.remove(info);
		list.add(info);
	}

	public void addInstructionTime(int nodeNumber, int coreNumber, Reference reference, TimeUnit timeUnit) {
		InstructionTimeInfo info = new InstructionTimeInfo(reference, timeUnit);
		List<InstructionTimeInfo> list = get(m_instructionTimes, nodeNumber, coreNumber);		
		list.remove(info);
		list.add(info);
	}
	
	public void addLoop(int nodeNumber, int coreNumber, Reference reference, long iterations) {
		LoopInfo info = new LoopInfo(reference, iterations);
		List<LoopInfo> list = get(m_loops, nodeNumber, coreNumber);		
		list.remove(info);
		list.add(info);
	}

	public void addLoopPath(int nodeNumber, int coreNumber, Reference reference, long iterations) {
		LoopPathInfo info = new LoopPathInfo(reference, iterations);
		List<LoopPathInfo> list = get(m_loopPaths, nodeNumber, coreNumber);		
		list.remove(info);
		list.add(info);
	}

	public void addLoopScope(int nodeNumber, int coreNumber, Reference reference, boolean absolute) {
		LoopScopeInfo info = new LoopScopeInfo(reference, absolute);
		List<LoopScopeInfo> list = get(m_loopScopes, nodeNumber, coreNumber);		
		list.remove(info);
		list.add(info);
	}

	public void removeFunctionTime(int nodeNumber, int coreNumber, Reference function) {
		removeAll(get(m_functionTimes, nodeNumber, coreNumber),
				new FunctionTimeInfo(function, null));
	}

	public void removePathTime(int nodeNumber, int coreNumber, Reference from, Reference to) {
		removeAll(get(m_pathTimes, nodeNumber, coreNumber),
				new PathTimeInfo(from, to, null));
	}
	
	public void removeInstructionTime(int nodeNumber, int coreNumber, Reference reference) {
		removeAll(get(m_instructionTimes, nodeNumber, coreNumber),
				new InstructionTimeInfo(reference, null));
	}
	
	public void removeLoop(int nodeNumber, int coreNumber, Reference reference) {
		removeAll(get(m_loops, nodeNumber, coreNumber),
				new LoopInfo(reference, 0));
	}

	public void removeLoopPath(int nodeNumber, int coreNumber, Reference reference) {
		removeAll(get(m_loopPaths, nodeNumber, coreNumber),
				new LoopPathInfo(reference, 0));
	}

	public void removeLoopScope(int nodeNumber, int coreNumber, Reference reference) {
		removeAll(get(m_loopScopes, nodeNumber, coreNumber),
				new LoopScopeInfo(reference, false));
	}

	private <T> List<T> get(NodeCoreCollection<List<T>> list, int nodeNumber, int coreNumber) {
		if (list.get(nodeNumber, coreNumber) == null) {
			list.add(nodeNumber, coreNumber, new ArrayList<T>());
		}
        return list.get(nodeNumber, coreNumber);		
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

