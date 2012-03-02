package com.xmos.xta.core.engine.sections;

import java.io.File;

public class CodePointTableInfo extends SectionInfo implements Comparable<CodePointTableInfo>, FileLineInfo{
    private long m_pc;
    private String m_compilationDir;
    private String m_fileName;
    private long m_lineNumber;

    public CodePointTableInfo(Section section, long pc, String compilationDir,
    		String fileName, long lineNumber) {
    	super(section);

		if(compilationDir.endsWith("/")){
			compilationDir = compilationDir.substring(0, compilationDir.length()-1);
		}
    	m_pc = pc;
    	m_compilationDir = compilationDir;
    	m_fileName = fileName;
    	m_lineNumber = lineNumber;
    }
    
    public long getPc() {
    	return m_pc;
    }

    public String getCompilationDir() {
    	return m_compilationDir;
    }

    public String getFileName() {
    	return m_fileName;
    }

    public long getLineNumber() {
    	return m_lineNumber;
    }

    public String toString() {
		return m_compilationDir + File.separator + m_fileName + ":" + Long.toString(m_lineNumber);
    }

    public String toReferenceString() {
    	return toString();
    }

	public int compareTo(CodePointTableInfo o) {
		if (!m_fileName.equals(o.m_fileName)) {
			return m_fileName.compareTo(o.m_fileName);
		} else if (m_lineNumber < o.m_lineNumber) {
			return -1;
		} else if (m_lineNumber > o.m_lineNumber) {
			return 1;
		} else if (m_pc < o.m_pc) {
			return -1;
		} else if (m_pc > o.m_pc) {
			return 1;
		} else {
			return 0;
		}
	}
    
    
}
