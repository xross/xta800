package com.xmos.xta.engine;

public class CommandSource {
	public enum Type {
		CONSOLE,
		GUI,
		CMD,
		FILE
	}
	
	public String m_fileName;
	public long m_lineNum;
	public Type m_type;

	public CommandSource(String fileName, long lineNum) {
		m_fileName = fileName;
		m_lineNum = lineNum;
		m_type = Type.FILE;
	}

	public CommandSource(Type type) {
		m_type = type;
		switch (type) {
		case CONSOLE:
			m_fileName = "console";
			break;
			
		case GUI:
			m_fileName = "gui";
			break;
			
		case CMD:
			m_fileName = "cmd";
			break;
			
		case FILE:
		default:
			assert(false);
		}		    
		m_lineNum = 0;
	}
}
