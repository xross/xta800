package com.xmos.xta.core.engine.sections;

public class CodePointTableInfo implements ScriptableInfo {
    private long m_pc;
    private String m_compilationDir;
    private String m_fileName;
    private long m_lineNumber;

    public CodePointTableInfo(long pc, String compilationDir, String fileName, long lineNumber) {
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
		return m_compilationDir + "\\" + m_fileName + ":" + Long.toString(m_lineNumber);
    }
}
