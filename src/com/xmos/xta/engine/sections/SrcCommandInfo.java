package com.xmos.xta.engine.sections;

public class SrcCommandInfo {
    private String m_command;
    private String m_compilationDir;
    private String m_fileName;
    private int m_lineNumber;
    
    public SrcCommandInfo(String command, String compilationDir, String fileName, int lineNumber) {
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

    public int getLineNumber() {
    	return m_lineNumber;
    }
}
