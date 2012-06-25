package com.xmos.xta.core.engine.references.types;

import com.xmos.libs.util.CoreId;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.SrcLabelTableInfo;
import com.xmos.xta.core.engine.utils.FileNameMatcher;

public class LoopLabelFileLineReference extends ReferenceType {
    LoopLabelFileLineReference(String ref, CoreId coreId) {
        super(ref, Type.LOOPLABEL, coreId);
    }

    public ReferenceType create(Engine engine, String reference, CoreId coreId) {
        if (checkTypeString(reference, Type.LOOPLABEL, 2) &&
            isSourceReference(reference) &&
            inSrcLabelTable(reference, 
                    engine.getLoopLabelTable(coreId), 
                    engine.getWorkspacePath(), 
                    engine.getSourceMapper())) {
            return new LoopLabelFileLineReference(reference, coreId);
        }
        return null;
    }

    public Result resolve(Engine engine) {
        String ref  = getRef();
        String fileName = getFileName(ref);
        long lineNumber = getLineNumber(ref);

        for (SrcLabelTableInfo info : engine.getLoopLabelTable(getCoreId())) {
            if (((lineNumber >= info.getLineNumberStart()) && (lineNumber <= info.getLineNumberEnd())) &&
                    FileNameMatcher.isMatch(info.getCompilationDir(), info.getFileName(), 
                            fileName, engine.getWorkspacePath(), engine.getSourceMapper())) {

                addPc(info.getPc());
                setFileName(fileName);
                setLineNum(lineNumber);
                setResolved(true);
                return Result.RESOLVED;
            }
        }
        return Result.UNRESOLVED;
    }

    public String name() {
        return "SrcLabelFileLine";
    }

    public String description() {
        return "Loop label specified in the format: 'file name:line number'";
    }

    public boolean isPortable() {
        return false;
    }

    public boolean matches(String s, Engine engine) {
        return fileLineMatches(s, engine);
    }
}
