package com.xmos.xta.core.engine.defines;

import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.CoreId;

public interface DefineInfo {
    String getDescription();
    String getValue();
    CoreId getCoreId();
    Reference[] getReferences();
}
