package com.xmos.xta.core.engine.sections;

import java.io.File;

public class LLVMSrcLabelTableInfo implements Comparable<LLVMSrcLabelTableInfo>, ScriptableInfo {
    private String m_compilationDir;
    private String m_fileName;
    private int m_lineNumberStart;
    private int m_lineNumberEnd;

    public LLVMSrcLabelTableInfo(String compilationDir, String fileName,
    		int lineNumberStart, int lineNumberEnd) {
    	
    	m_compilationDir = compilationDir;
    	m_fileName = fileName;
    	m_lineNumberStart = lineNumberStart;
    	m_lineNumberEnd = lineNumberEnd;
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

	public void setLineNumberEnd(int lineNumberEnd) {
		m_lineNumberEnd = lineNumberEnd;
	}

	public int compareTo(LLVMSrcLabelTableInfo o) {
		if (!m_fileName.equals(o.m_fileName)) {
			return m_fileName.compareTo(o.m_fileName);
		} else if (m_lineNumberStart < o.m_lineNumberStart) {
			return -1;
		} else if (m_lineNumberStart > o.m_lineNumberStart) {
			return 1;
		} else {
			return 0;
		}
	}

    public String toString() {
		return m_compilationDir + File.separator + m_fileName + ":" +
			Long.toString(m_lineNumberStart) + "-" + Long.toString(m_lineNumberEnd);
    }

    public String toReferenceString() {
		return m_compilationDir + File.separator + m_fileName + ":" + Long.toString(m_lineNumberStart);
    }

	public long getPc() {
		// TODO Auto-generated method stub
		return 0;
	}
}
