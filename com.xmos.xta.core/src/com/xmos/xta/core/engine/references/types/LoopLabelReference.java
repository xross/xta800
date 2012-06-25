package com.xmos.xta.core.engine.references.types;

import com.xmos.libs.util.CoreId;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointInfo;

public class LoopLabelReference extends ReferenceType {
    LoopLabelReference(String ref, CoreId coreId) {
        super(ref, Type.LOOPLABEL, coreId);
    }

    public ReferenceType create(Engine engine, String reference, CoreId coreId) {
        if (checkTypeString(reference, Type.LOOPLABEL, 1) &&
            isValid(engine.getSrcLabels(coreId), getLabel(reference))) {
            return new LoopLabelReference(reference, coreId);
        }
        return null;
    }

    public Result resolve(Engine engine) {
        String ref = getRef();
        String label = getLabel(ref);

        Result result = Result.UNRESOLVED;
        for (CodePointInfo info : engine.getLoopLabels(getCoreId())) {
            if (labelsMatch(info.getLabel(), label)) {
                if (info.exists()) {
                    addPc(info.getPc(), info.getLabel());
                    setResolved(true);
                    result = Result.RESOLVED;
                    
                } else {
                    result = Result.REMOVED;
                }
            }
        }
        return result;
    }

    public String name() {
        return "LoopLabel";
    }

    public String description() {
        return "Loop label specified using the source level pragma mechanism";
    }

    public boolean isPortable() {
        return true;
    }
}
