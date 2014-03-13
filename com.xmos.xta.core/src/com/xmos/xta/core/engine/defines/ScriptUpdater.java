package com.xmos.xta.core.engine.defines;

import java.util.Collection;

public interface ScriptUpdater {
    void update(
        Collection<? extends DefineInfo> newList,
        Collection<? extends DefineInfo> oldList,
        String command, String removeStr);
}
