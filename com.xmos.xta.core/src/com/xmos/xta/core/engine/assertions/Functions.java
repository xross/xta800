package com.xmos.xta.core.engine.assertions;

import java.util.ArrayList;
import java.util.List;

import com.xmos.xta.core.engine.TimeUnit;

public class Functions extends Assertion {
    private List<FunctionInfo> m_assertions;
	
	Functions(int nodeId, int coreId) {
		super(nodeId, coreId);
		m_assertions = new ArrayList<FunctionInfo>();
	}
	
	void add(String function, TimeUnit timeUnit) {
		m_assertions.add(new FunctionInfo(function, timeUnit));
	}
	
	List<FunctionInfo> get() {
		return m_assertions;
	}
}
