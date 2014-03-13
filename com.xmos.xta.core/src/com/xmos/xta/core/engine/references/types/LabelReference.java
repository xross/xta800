package com.xmos.xta.core.engine.references.types;

import java.util.Map;
import java.util.Set;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.engine.Engine;

public class LabelReference extends ReferenceType {
    LabelReference(String ref, CoreId coreId) {
        super(ref, Type.LABEL, coreId);
    }

    public ReferenceType create(Engine engine, String reference, CoreId coreId) {
        if (checkTypeString(reference, Type.LABEL, 1) &&
            engine.getLabels(coreId).containsKey(getLabel(reference))) {
            return new LabelReference(reference, coreId);
        }
        return null;
    }

    public Result resolve(Engine engine) {
        String ref = getRef();
        String label = getLabel(ref);
        Result result = Result.UNRESOLVED;
        Map<String, Set<Long>> labels = engine.getLabels(getCoreId());
        if (labels.containsKey(label)) {
            for (Long pc : labels.get(label)) {
                addPc(pc);
            }
            setResolved(true);
            result = Result.RESOLVED;
        }
        return result;
    }

    public String name() {
        return "Label";
    }

    public String description() {
        return "Any source or assembly level symbol defined with respect to an executable section";
    }

    public boolean isPortable() {
        return true;
    }
}
