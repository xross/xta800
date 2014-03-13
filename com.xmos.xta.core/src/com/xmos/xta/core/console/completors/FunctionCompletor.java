package com.xmos.xta.core.console.completors;

import java.util.List;
import java.util.Set;

import jline.Completor;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.engine.Engine;

public class FunctionCompletor implements Completor {
    private Engine m_engine;

    FunctionCompletor(Engine engine) {
        m_engine = engine;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public int complete(String buffer, int cursor, final List candidates) {
        if (m_engine.getExecutable() != null) {
            final String start = (buffer == null) ? "" : buffer;

            for (CoreId activeCore : m_engine.getActiveCores()) {
                Set<String> functions = m_engine.getFunctionNames(activeCore);
                for (String function : functions) {
                    if (function.startsWith(start)) {
                        candidates.add(function);
                    }
                }
            }
        }
        return 0;
    }
}
