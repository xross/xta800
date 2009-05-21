package com.xmos.xta.engine.sections;

public class EndpointTableInfo {
    private int m_pc;
    private String m_compilationDir;
    private String m_fileName;
    private int m_lineNumber;
    
    public EndpointTableInfo(int pc, String compilationDir, String fileName, int lineNumber) {
    	m_pc = pc;
    	m_compilationDir = compilationDir;
    	m_fileName = fileName;
    	m_lineNumber = lineNumber;
    }
 
    public int getPc() {
    	return m_pc;
    }
        
    public String getCompilationDir() {
    	return m_compilationDir;
    }
    
    public String getFileName() {
    	return m_fileName;
    }

    public int getLineNumber() {
    	return m_lineNumber;
    }
}
