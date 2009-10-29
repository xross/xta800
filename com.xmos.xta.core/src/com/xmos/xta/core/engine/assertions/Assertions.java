package com.xmos.xta.core.engine.assertions;

import java.util.ArrayList;
import java.util.List;

import com.xmos.xta.core.engine.TimeUnit;

public class Assertions {
	private List<Assertion> m_functions;
	private List<Assertion> m_paths;
    
    public Assertions() {
    	m_functions = new ArrayList<Assertion>();
    	m_paths     = new ArrayList<Assertion>();
	}

    public List<FunctionInfo> getFunctions(int nodeId, int coreId) {    	
		return ((Functions)getAssertionList(m_functions, nodeId, coreId, new FunctionBuilder())).get();    	
    }

    public List<PathInfo> getPaths(int nodeId, int coreId) {    	
		return ((Paths)getAssertionList(m_paths, nodeId, coreId, new PathBuilder())).get();    	
    }
    
	public void addFunction(int nodeId, int coreId, String function, TimeUnit timeUnit) {
		((Functions)getAssertionList(m_functions, nodeId, coreId, new FunctionBuilder())).add(function, timeUnit);
	}

	public void addPath(int nodeId, int coreId, String from, String to, TimeUnit timeUnit) {
		((Paths)getAssertionList(m_paths, nodeId, coreId, new PathBuilder())).add(from, to, timeUnit);
	}
	
	private Assertion getAssertionList(List<Assertion> assertions, int nodeId, int coreId, AssertionBuilder builder) {
		for (Assertion assertion : assertions) {
			if ((assertion.getNodeId() == nodeId) &&
				(assertion.getCoreId() == coreId)) {
				return assertion;
			}
		}
		Assertion assertion = builder.build(nodeId, coreId);
		assertions.add(assertion);
		return assertion;
	}
	
	private static interface AssertionBuilder {
		Assertion build(int nodeId, int coreId);
	}
	
	private static class FunctionBuilder implements AssertionBuilder {
		public Assertion build(int nodeId, int coreId) {
			return new Functions(nodeId, coreId);
		}
	}
	
	private static class PathBuilder implements AssertionBuilder {
		public Assertion build(int nodeId, int coreId) {
			return new Paths(nodeId, coreId);
		}
	}
}

