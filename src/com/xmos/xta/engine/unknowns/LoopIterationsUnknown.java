package com.xmos.xta.engine.unknowns;

public class LoopIterationsUnknown extends Unknown {
	public LoopIterationsUnknown() {
	}

	public int getPc() {
		return 0;
	}

	public int getBasicBlockId() {
		return 0;
	}

	public String getDescription() {
		return "";
	}

	public boolean isUnknown() {
		return false;
	}

	public int compareTo(Unknown unknown) {
		if (unknown instanceof LoopIterationsUnknown) {
			return 0;
		} else {
			return -1;
		}
	}
}
