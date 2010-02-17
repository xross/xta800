package com.xmos.xta.core.engine.sections;

public class CodePointInfo implements ScriptableInfo {
    private long m_pc;
    private String m_label;
    private String m_compilationDir;
    private String m_fileName;
    private long m_lineNumber;
    
    public CodePointInfo(long pc, String label, String compilationDir, String fileName, long lineNumber) {
    	m_pc = pc;
    	m_label = label;
    	m_compilationDir = compilationDir;
    	m_fileName = fileName;
    	m_lineNumber = lineNumber;
    }
 
    public long getPc() {
    	return m_pc;
    }
    
    public String getLabel() {
    	return m_label;
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
    	return m_label;
    }
}
