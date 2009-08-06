package com.xmos.xta.engine.sections;

public class SrcLabelTableInfo {
    private long m_pc;
    private String m_compilationDir;
    private String m_fileName;
    private int m_lineNumberStart;
    private int m_lineNumberEnd;
    
    public SrcLabelTableInfo(long pc, String compilationDir, String fileName, 
    		int lineNumberStart, int lineNumberEnd) {
    	m_pc = pc;
    	m_compilationDir = compilationDir;
    	m_fileName = fileName;
    	m_lineNumberStart = lineNumberStart;
    	m_lineNumberEnd = lineNumberEnd;
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

    public int getLineNumberStart() {
    	return m_lineNumberStart;
    }

    public int getLineNumberEnd() {
    	return m_lineNumberEnd;
    }
}
