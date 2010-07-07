package com.xmos.xta.core.engine.defines;

import com.xmos.libs.util.CoreId;
import com.xmos.xta.core.engine.references.Reference;

public interface DefineInfo {
    String getDescription();
    String getValue();
    CoreId getCoreId();
    Reference[] getReferences();
}
