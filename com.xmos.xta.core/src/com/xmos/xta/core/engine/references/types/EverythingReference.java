package com.xmos.xta.core.engine.references.types;

import com.xmos.xta.core.engine.Engine;

public class EverythingReference extends ReferenceType {
	EverythingReference(String ref, int nodeNumber, int coreNumber) {
		super(ref, Type.EVERYTHING, nodeNumber, coreNumber);
		setResolved(true);
	}

	public ReferenceType create(Engine engine, String reference, int nodeNumber, int coreNumber) {
		if (reference.equals("*")) {
	    	return new EverythingReference(reference, nodeNumber, coreNumber);
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
