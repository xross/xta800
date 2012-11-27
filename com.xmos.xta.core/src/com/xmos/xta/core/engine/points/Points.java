package com.xmos.xta.core.engine.points;

import java.util.Map;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceType.Result;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;

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
	
	Result update(String refStr, Map<CoreId, Reference> endpoints) {
		endpoints.clear();

		boolean resolved = false;
		boolean removed = false;
		
		for (CoreId activeCore : m_engine.getActiveCores()) {
			Reference reference = ReferenceFactory.createReference(refStr, m_refClass, activeCore);

            Result result = reference.resolve(m_engine);
			if (result == Result.RESOLVED) {
				endpoints.put(activeCore, reference);
				resolved = true;

			} else if (result == Result.REMOVED) {
			    removed = true;
			}
		}
		if (resolved) {
		    return Result.RESOLVED;
		    
		} else if (removed) {
		    return Result.REMOVED;
		    
		} else {
		    return Result.UNRESOLVED;
		}
	}
}
