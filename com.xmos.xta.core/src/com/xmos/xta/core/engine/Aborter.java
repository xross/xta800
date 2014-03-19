package com.xmos.xta.core.engine;

public interface Aborter {
    void getAbort() throws AbortedException;
}
