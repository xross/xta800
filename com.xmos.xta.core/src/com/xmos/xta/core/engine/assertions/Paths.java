package com.xmos.xta.core.engine.assertions;

import java.util.ArrayList;
import java.util.List;

import com.xmos.xta.core.engine.TimeUnit;

public class Paths extends Assertion {	
    private List<PathInfo> m_assertions;
	
	Paths(int nodeId, int coreId) {
		super(nodeId, coreId);
		m_assertions = new ArrayList<PathInfo>();
    }
	
	void add(String from, String to, TimeUnit timeUnit) {
		m_assertions.add(new PathInfo(from, to, timeUnit));
	}
	
	List<PathInfo> get() {
		return m_assertions;
	}
}
