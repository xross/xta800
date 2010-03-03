package com.xmos.xta.core.engine.defines;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class Defines {
	private Collection<FunctionTimeInfo> m_functionTimes;
	private Collection<PathTimeInfo> m_pathTimes;
	private Collection<LoopInfo> m_loops;
	private Collection<LoopPathInfo> m_loopPaths;
	private Collection<LoopScopeInfo> m_loopScopes;
	private Collection<InstructionTimeInfo> m_instructionTimes;

    public Defines() {
    	m_functionTimes    = new ArrayList<FunctionTimeInfo>();
    	m_pathTimes        = new ArrayList<PathTimeInfo>();
    	m_instructionTimes = new ArrayList<InstructionTimeInfo>();
    	m_loops            = new ArrayList<LoopInfo>();
    	m_loopPaths        = new ArrayList<LoopPathInfo>();
    	m_loopScopes       = new ArrayList<LoopScopeInfo>();
	}

    public Defines(Defines defines) {
    	m_functionTimes    = new ArrayList<FunctionTimeInfo>(defines.m_functionTimes);
    	m_pathTimes        = new ArrayList<PathTimeInfo>(defines.m_pathTimes);
    	m_instructionTimes = new ArrayList<InstructionTimeInfo>(defines.m_instructionTimes);
    	m_loops            = new ArrayList<LoopInfo>(defines.m_loops);
    	m_loopPaths        = new ArrayList<LoopPathInfo>(defines.m_loopPaths);
    	m_loopScopes       = new ArrayList<LoopScopeInfo>(defines.m_loopScopes);
    }

	public void updateScript(Defines defines, StringBuffer script) {
		if (!m_functionTimes.equals(defines.getFunctionTimes())) {
			updateScript(script, m_functionTimes, defines.getFunctionTimes(), "functiontime", "*");
		}
		if (!m_pathTimes.equals(defines.getPathTimes())) {
			updateScript(script, m_pathTimes, defines.getPathTimes(), "pathtime", "* *");
		}
		if (!m_instructionTimes.equals(defines.getInstructionTimes())) {
			updateScript(script, m_instructionTimes, defines.getInstructionTimes(), "instructiontime", "*");
		}
		if (!m_loops.equals(defines.getLoops())) {
			updateScript(script, m_loops, defines.getLoops(), "loop", "*");
		}
		if (!m_loopPaths.equals(defines.getLoopPaths())) {
			updateScript(script, m_loopPaths, defines.getLoopPaths(), "looppath", "*");
		}
		if (!m_loopScopes.equals(defines.getLoopScopes())) {
			updateScript(script, m_loopScopes, defines.getLoopScopes(), "loopscope", "*");
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

    public Collection<FunctionTimeInfo> getFunctionTimes() {
    	return m_functionTimes;
    }

    public Collection<PathTimeInfo> getPathTimes() {
    	return m_pathTimes;
    }

    public Collection<InstructionTimeInfo> getInstructionTimes() {
    	return m_instructionTimes;
    }

    public Collection<LoopInfo> getLoops() {
    	return m_loops;
    }

    public Collection<LoopPathInfo> getLoopPaths() {
    	return m_loopPaths;
    }

    public Collection<LoopScopeInfo> getLoopScopes() {
    	return m_loopScopes;
    }

	public void addFunctionTime(Reference function, TimeUnit timeUnit) {
		FunctionTimeInfo info = new FunctionTimeInfo(function, timeUnit);
		m_functionTimes.remove(info);
		m_functionTimes.add(info);
	}

	public void addPathTime(Reference from, Reference to, TimeUnit timeUnit) {
		PathTimeInfo info = new PathTimeInfo(from, to, timeUnit);
		m_pathTimes.remove(info);
		m_pathTimes.add(info);
	}

	public void addInstructionTime(Reference reference, TimeUnit timeUnit) {
		InstructionTimeInfo info = new InstructionTimeInfo(reference, timeUnit);
		m_instructionTimes.remove(info);
		m_instructionTimes.add(info);
	}

	public void addLoop(Reference reference, long iterations) {
		LoopInfo info = new LoopInfo(reference, iterations);
		m_loops.remove(info);
		m_loops.add(info);
	}

	public void addLoopPath(Reference reference, long iterations) {
		LoopPathInfo info = new LoopPathInfo(reference, iterations);
		m_loopPaths.remove(info);
		m_loopPaths.add(info);
	}

	public void addLoopScope(Reference reference, boolean absolute) {
		LoopScopeInfo info = new LoopScopeInfo(reference, absolute);
		m_loopScopes.remove(info);
		m_loopScopes.add(info);
	}

	public void removeFunctionTime(Reference function) {
		m_functionTimes.remove(new FunctionTimeInfo(function, null));
	}

	public void removePathTime(Reference from, Reference to) {
		m_pathTimes.remove(new PathTimeInfo(from, to, null));
	}

	public void removeInstructionTime(Reference reference) {
		m_instructionTimes.remove(new InstructionTimeInfo(reference, null));
	}

	public void removeLoop(Reference reference) {
		m_loops.remove(new LoopInfo(reference, 0));
	}

	public void removeLoopPath(Reference reference) {
		m_loopPaths.remove(new LoopPathInfo(reference, 0));
	}

	public void removeLoopScope(Reference reference) {
		m_loopScopes.remove(new LoopScopeInfo(reference, false));
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
}

