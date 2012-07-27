package com.xmos.xta.core.engine.defines;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.engine.references.Reference;

public interface DefineInfo {
    String getDescription();
    String getValue();
    CoreId getCoreId();
    Reference[] getReferences();
}
