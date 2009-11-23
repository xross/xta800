package com.xmos.xta.core.engine;

public interface CommandRunner {
    void run(String command, CommandSource src, boolean isFinal);
}
