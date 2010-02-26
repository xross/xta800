package com.xmos.xta.core.engine.references.types;

import com.xmos.xta.core.engine.Engine;

public class EverythingReference extends ReferenceType {
	EverythingReference(String ref) {
		super(ref, Type.EVERYTHING);
		setResolved(true);
	}

	public ReferenceType create(Engine engine, String reference) {
		if (reference.equals("*")) {
	    	return new EverythingReference(reference);
		}
		return null;
	}

	public boolean resolve(Engine engine) {
		return true;
	}

	public String name() {
		return "EverythingReference";
	}

	public String description() {
		return "Matches everything: '*'";
	}

	public boolean isPortable() {
		return true;
	}

	public boolean matches(String s) {
		return true;
	}

	public boolean checkPcsMatch() {
		return false;
	}

	public boolean checkBackTrailMatches() {
		return false;
	}
}
