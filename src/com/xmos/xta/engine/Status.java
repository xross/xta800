package com.xmos.xta.engine;

public class Status {
	public enum BlockStatus {
		SEARCHING, PAUSED, SYNCHRONISING, JOINING, UNRESOLVED, 
		RECURSION, ERROR, EXCLUDED, MAX_NUM_PATHS, COMPLETE,
	};
	public enum ExecutionStatus {
		SEARCHING, COMPLETE, UNRESOLVED
	}
}
