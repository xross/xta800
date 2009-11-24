package com.xmos.xta.core.engine;

public interface CommandRunner {
	public enum CommandPosition {
		FIRST,
		MIDDLE,
		LAST
	}

    void run(String command, CommandSource src, CommandPosition position);
}
