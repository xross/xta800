package com.xmos.xta.engine;

public class Status {
	public enum BlockStatus {
		SEARCHING, PAUSED, SYNCHRONISING, JOINING, UNRESOLVED, 
		RECURSION, ERROR, EXCLUDED, OUTOFMEMORY, COMPLETE,
	};
	public enum ExecutionStatus {
		SEARCHING, COMPLETE, UNRESOLVED, OUTOFMEMORY
	}
}
