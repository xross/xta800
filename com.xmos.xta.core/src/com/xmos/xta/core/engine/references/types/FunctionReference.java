package com.xmos.xta.core.engine.references.types;

import java.util.Collection;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.engine.Engine;

public class FunctionReference extends ReferenceType {
    FunctionReference(String ref, CoreId coreId) {
        super(ref, Type.FUNCTION, coreId);
    }

    public ReferenceType create(Engine engine, String reference, CoreId coreId) {
        if (checkTypeString(reference, Type.FUNCTION, 1) &&
            engine.getLabels(coreId).containsKey(getLabel(reference))) {
            return new FunctionReference(reference, coreId);
        }
        return null;
    }

    public Result resolve(Engine engine) {
        String ref = getRef();
        String functionName = getLabel(ref);

        Collection<String> functionNames = engine.getFunctionNames(getCoreId());
        if (functionNames.contains(functionName)) {
            for(long pc : engine.getFunctionStartPcs(functionName, getCoreId())){
                addPc(pc);
            }
            setResolved(true);
            return Result.RESOLVED;
        }
        return Result.UNRESOLVED;
    }

    public String name() {
        return "Function";
    }

    public String description() {
        return "Any function";
    }

    public boolean isPortable() {
        return true;
    }

    public boolean matches(String s) {
        return getRef().equals(s);
    }
}
