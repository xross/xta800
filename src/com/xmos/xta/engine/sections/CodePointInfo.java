package com.xmos.xta.engine.sections;

public class CodePointInfo {
    private int m_pc;
    private String m_label;
    private String m_compilationDir;
    private String m_fileName;
    private int m_lineNumber;
    
    public CodePointInfo(int pc, String label, String compilationDir, String fileName, int lineNumber) {
    	m_pc = pc;
    	m_label = label;
    	m_compilationDir = compilationDir;
    	m_fileName = fileName;
    	m_lineNumber = lineNumber;
    }
 
    public int getPc() {
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

    public int getLineNumber() {
    	return m_lineNumber;
    }
}
