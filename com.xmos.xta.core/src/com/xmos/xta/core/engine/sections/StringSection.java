package com.xmos.xta.core.engine.sections;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.libs.core.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class StringSection extends Section {
    private Collection<String> m_strings;

    StringSection(CoreId coreId, Notifier notifier) {
        super(coreId, notifier);
        m_strings = new ArrayList<String>();
    }

    Collection<String> get() {
        return m_strings;
    }

    boolean load(CommandSource src, XeLoader xeLoader) {
        return true;
    }
}
