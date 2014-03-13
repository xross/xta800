package com.xmos.xta.core.engine.references.types;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.libs.core.utils.IteratableSet;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointInfo;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;

public class EndpointReference extends ReferenceType {
    EndpointReference(String ref, CoreId coreId) {
        super(ref, Type.ENDPOINT, coreId);
    }

    public ReferenceType create(Engine engine, String reference, CoreId coreId) {
        if (checkTypeString(reference, Type.ENDPOINT, 1) &&
            isValid(engine.getEndpoints(coreId), getLabel(reference))) {
            return new EndpointReference(reference, coreId);
        }
        return null;
    }

    public Result resolve(Engine engine) {
        String ref = getRef();
        String label = getLabel(ref);

        Result result = Result.UNRESOLVED;
        for (CodePointInfo info : engine.getEndpoints(getCoreId())) {
            if (labelsMatch(info.getLabel(), label)) {

                if (info.exists()) {
                    long pc = info.getPc();
                    addPc(pc, info.getLabel());

                    IteratableSet<CodePointTableInfo> endpointTable = engine.getEndpointTable(getCoreId());

                    for(CodePointTableInfo cpti : endpointTable){
                        if(cpti.getPc() == info.getPc()){
                            setFileName(cpti.getFileName());
                            setLineNum(cpti.getLineNumber());
                            setResolved(true);
                            result = Result.RESOLVED;
                            break;
                        }
                    }

                } else {
                    result = Result.REMOVED;
                }
            }
        }
        return result;
    }

    public String name() {
        return "Endpoint";
    }

    public String description() {
        return "Endpoint specified using the source level pragma mechanism";
    }

    public boolean isPortable() {
        return true;
    }
}
