package com.xmos.xta.core.engine.references.types;

import com.xmos.libs.controlflow.functionmapper.FunctionMapper;
import com.xmos.libs.util.CoreId;
import com.xmos.xta.core.engine.Engine;

public class LoopLabelPcReference extends ReferenceType {
    LoopLabelPcReference(String ref, CoreId coreId) {
        super(ref, Type.LOOPLABEL, coreId);
        if (ref != null) {
            addPc(getPcString(ref));
        }
    }

    public ReferenceType create(Engine engine, String reference, CoreId coreId) {
        if (checkTypeString(reference, Type.LOOPLABEL, 1) &&
            isPcString(reference)) {
            return new LoopLabelPcReference(reference, coreId);
        }
        return null;
    }

    public Result resolve(Engine engine) {
        FunctionMapper functionMapper = engine.getFunctionMapper(getCoreId());
        if (functionMapper == null)
            return Result.UNRESOLVED;

        if (functionMapper.areProfilablePcs(getPcs())) {
            setResolved(true);
            return Result.RESOLVED;
        }
        return Result.UNRESOLVED;
    }

    public String name() {
        return "LoopLabelPc";
    }

    public String description() {
        return "Raw program counter specified in the format: 0x*";
    }

    public boolean isPortable() {
        return false;
    }
}
