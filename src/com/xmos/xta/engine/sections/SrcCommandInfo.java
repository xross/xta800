package com.xmos.xta.engine.sections;

public class SrcCommandInfo {
    private String m_command;
    private String m_compilationDir;
    private String m_fileName;
    private long m_lineNumber;
    
    public SrcCommandInfo(String command, String compilationDir, String fileName, long lineNumber) {
    	m_command = command;
    	m_compilationDir = compilationDir;
    	m_fileName = fileName;
    	m_lineNumber = lineNumber;
    }
 
    public String getCommand() {
    	return m_command;
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
