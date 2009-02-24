package com.xmos.xta.engine.references;

import com.xmos.xta.engine.Engine;

public class EndpointReference extends CodeReference {
	EndpointReference(String ref) {
		super(ref);
	}

	public boolean resolve(Engine engine, int id, int id2) {
		return false;
	}
}
