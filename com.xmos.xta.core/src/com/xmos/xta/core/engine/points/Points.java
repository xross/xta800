package com.xmos.xta.core.engine.points;

import java.util.Map;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.core.engine.utils.CoreId;

class Points {
	private Engine m_engine;
	private ReferenceClass m_refClass;
	
	Points(Engine engine, ReferenceClass refClass) {
		m_engine = engine;
		m_refClass = refClass;
	}
	
	Engine getEngine() {
		return m_engine;
	}
	
	boolean update(String refStr, Map<CoreId, Reference> endpoints) {
		endpoints.clear();

		boolean hit = false;		
		for (CoreId activeCore : m_engine.getActiveCores()) {
			Reference reference = ReferenceFactory.createReference(refStr, m_refClass, activeCore);

			if (reference.resolve(m_engine)) {
				endpoints.put(activeCore, reference);
				hit = true;
			}
		}
		return hit;
	}
}
