package com.xmos.xta.core.console;

import com.xmos.xta.core.engine.CommandSource;

public class ConsoleCommand {
	private String m_str;
    private CommandSource m_src;
    
    public ConsoleCommand(String command, CommandSource src) {
    	m_str = command;
    	m_src = src;
    }
    
    public String getStr() {
    	return m_str;
    }

    public CommandSource getSrc() {
    	return m_src;
    }
}
