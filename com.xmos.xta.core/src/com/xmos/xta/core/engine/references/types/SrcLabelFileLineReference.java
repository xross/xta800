package com.xmos.xta.core.engine.references.types;

import com.xmos.libs.util.CoreId;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.SrcLabelTableInfo;
import com.xmos.xta.core.engine.utils.FileNameMatcher;

public class SrcLabelFileLineReference extends ReferenceType {
	SrcLabelFileLineReference(String ref, CoreId coreId) {
		super(ref, Type.SRCLABEL, coreId);
	}

	public ReferenceType create(Engine engine, String reference, CoreId coreId) {
		if (checkTypeString(reference, Type.SRCLABEL, 2) &&
		    isSourceReference(reference) &&
			inSrcLabelTable(reference, 
					engine.getSrcLabelTable(coreId), 
					engine.getWorkspacePath(), 
					engine.getSourceMapper())) {
	    	return new SrcLabelFileLineReference(reference, coreId);
		}
	    return null;
	}

	public boolean resolve(Engine engine) {
    	String ref  = getRef();
		String fileName = getFileName(ref);
        long lineNumber = getLineNumber(ref);

		for (SrcLabelTableInfo info : engine.getSrcLabelTable(getCoreId())
				.values()) {
			if (((lineNumber >= info.getLineNumberStart()) && (lineNumber <= info.getLineNumberEnd())) &&
					FileNameMatcher.isMatch(info.getCompilationDir(), info.getFileName(), 
							fileName, engine.getWorkspacePath(), engine.getSourceMapper())) {

				addPc(info.getPc());
				setFileName(fileName);
				setLineNum(lineNumber);
				setResolved(true);
				return true;
			}
		}
		return false;
	}

	public String name() {
		return "SrcLabelFileLine";
	}

	public String description() {
		return "Source label specified in the format: 'file name:line number'";
	}

	public boolean isPortable() {
		return false;
	}

	public boolean matches(String s, Engine engine) {
		return fileLineMatches(s, engine);
	}
}
