package com.xmos.xta.core.engine.references.types;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;
import com.xmos.xta.core.engine.utils.FileNameMatcher;

public class EndpointFileLineReference extends ReferenceType {
    EndpointFileLineReference(String ref, CoreId coreId) {
		super(ref, Type.ENDPOINT, coreId);
	}

	public ReferenceType create(Engine engine, String reference, CoreId coreId) {
		if (checkTypeString(reference, Type.ENDPOINT, 2) &&
		    isSourceReference(reference) &&
			inCodePointTable(reference, 
					engine.getEndpointTable(coreId), 
					engine.getWorkspacePath(), 
					engine.getSourceMapper())) {
	    	return new EndpointFileLineReference(reference, coreId);
		}
		return null;
	}

	public Result resolve(Engine engine) {
    	String ref  = getRef();
		String fileName = getFileName(ref);
        long lineNumber = getLineNumber(ref);

        Result result = Result.UNRESOLVED;
		for (CodePointTableInfo info : engine.getEndpointTable(getCoreId())) {
			if ((info.getLineNumber() == lineNumber) && 
				FileNameMatcher.isMatch(info.getCompilationDir(), info.getFileName(), 
						fileName, engine.getWorkspacePath(), engine.getSourceMapper())) {

				addPc(info.getPc());
				setFileName(fileName);
				setLineNum(lineNumber);
				setResolved(true);
				result = Result.RESOLVED;
			}
		}
		return result;
	}

	public String name() {
		return "EndpointFileLine";
	}

	public String description() {
		return "Endpoint specified in the format: 'file name:line number'";
	}

	public boolean isPortable() {
		return false;
	}

	public boolean matches(String s, Engine engine) {
		return fileLineMatches(s, engine);
	}
}
