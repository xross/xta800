package com.xmos.xta.core.engine.references.types;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.engine.Engine;

public class EverythingReference extends ReferenceType {
    EverythingReference(String ref, CoreId coreId) {
        super(ref, Type.EVERYTHING, coreId);
        setResolved(true);
    }

    public ReferenceType create(Engine engine, String reference, CoreId coreId) {
        if (reference.equals("*")) {
            return new EverythingReference(reference, coreId);
        }
        return null;
    }

    public Result resolve(Engine engine) {
        return Result.RESOLVED;
    }

    public String name() {
        return "EverythingReference";
    }

    public String description() {
        return "Matches everything: '*'";
    }

    public boolean isPortable() {
        return true;
    }

    public boolean matches(String s) {
        return true;
    }

    public boolean checkPcsMatch() {
        return false;
    }

    public boolean checkBackTrailMatches() {
        return false;
    }
}
