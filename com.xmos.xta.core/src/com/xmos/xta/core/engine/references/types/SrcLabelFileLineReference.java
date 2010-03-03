package com.xmos.xta.core.engine.references.types;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.SrcLabelTableInfo;
import com.xmos.xta.core.engine.utils.FileNameMatcher;

public class SrcLabelFileLineReference extends ReferenceType {
	SrcLabelFileLineReference(String ref, int nodeNumber, int coreNumber) {
		super(ref, Type.SRCLABEL, nodeNumber, coreNumber);
	}

	public ReferenceType create(Engine engine, String reference, int nodeNumber, int coreNumber) {
		if (checkTypeString(reference, Type.SRCLABEL, 2) &&
		    isSourceReference(reference) &&
			inSrcLabelTable(reference, engine.getSrcLabelTable(getNodeNumber(), getCoreNumber()), engine.getSourceMapper(getNodeNumber(), getCoreNumber()))) {
	    	return new SrcLabelFileLineReference(reference, nodeNumber, coreNumber);
		}
	    return null;
	}

	public boolean resolve(Engine engine) {
    	String ref  = getRef();
		String fileName = getFileName(ref);
        long lineNumber = getLineNumber(ref);

		for (SrcLabelTableInfo info : engine.getSrcLabelTable(getNodeNumber(), getCoreNumber()).values()) {
            if (FileNameMatcher.isMatch(info.getCompilationDir(), info.getFileName(), fileName, engine.getSourceMapper(getNodeNumber(), getCoreNumber())) &&
            	((lineNumber >= info.getLineNumberStart()) &&
            	 (lineNumber <= info.getLineNumberEnd()))) {

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
