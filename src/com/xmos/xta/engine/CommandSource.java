package com.xmos.xta.engine;

public class CommandSource {
	public String m_fileName;
	public long m_lineNum;

	public CommandSource(String fileName, long lineNum) {
		m_fileName = fileName;
		m_lineNum = lineNum;
	}
}
