package com.xmos.xta.core.engine.defines;

import com.xmos.xta.core.engine.utils.NodeCorePair;

public interface DefineInfo {
    String getDescription();
    String getValue();
    NodeCorePair getNodeCore();
}
