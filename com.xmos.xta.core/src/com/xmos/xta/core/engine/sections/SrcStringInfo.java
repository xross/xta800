package com.xmos.xta.core.engine.sections;

public class SrcStringInfo {
    private String m_string;
    private String m_compilationDir;
    private String m_fileName;
    private long m_lineNumber;

    public SrcStringInfo(String string, String compilationDir, String fileName, long lineNumber) {
    	m_string = string;
    	m_compilationDir = compilationDir;
    	m_fileName = fileName;
    	m_lineNumber = lineNumber;
    }

    public String getString() {
    	return m_string;
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
}
