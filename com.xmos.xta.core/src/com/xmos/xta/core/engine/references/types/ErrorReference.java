package com.xmos.xta.core.engine.references.types;

import com.xmos.libs.util.CoreId;
import com.xmos.xta.core.engine.Engine;

public class ErrorReference extends ReferenceType {
	ErrorReference(String ref, CoreId coreId) {
		super(ref, Type.ERROR, coreId);
	}

	public ReferenceType create(Engine engine, String reference, CoreId coreId) {
		assert(false);
		return null;
	}

	public boolean resolve(Engine engine) {
        return false;
	}

	public String name() {
		return "Error";
	}

	public String description() {
		return "Error";
	}

	public boolean isPortable() {
		// This is portable because:
		//  a) it will always be an error
		//  b) it needs to be for the case of inlined references
		return true;
	}

	public boolean matches(String s) {
		return false;
	}

	public boolean matches(long pc) {
		return false;
	}
}
