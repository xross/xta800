package com.xmos.xta.engine;

public class CommandSource {
	public String m_fileName;
	public int m_lineNum;

	public CommandSource(String fileName, int lineNum) {
		m_fileName = fileName;
		m_lineNum = lineNum;
	}
}
