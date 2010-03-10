package com.xmos.xta.core.engine.defines;

import com.xmos.xta.core.engine.utils.NodeCorePair;

public interface DefineInfo {
    NodeCorePair getNodeCorePair();
    String getDescription();
    String getValue();
}
