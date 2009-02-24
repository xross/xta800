package com.xmos.xta.engine.references;

import com.xmos.xta.engine.Engine;

public class ErrorReference extends CodeReference {
	ErrorReference(String ref) {
		super(ref);
	}
		
	public boolean resolve(Engine engine, int m_nodeId, int m_coreId) {
        return false;
	}	
}
