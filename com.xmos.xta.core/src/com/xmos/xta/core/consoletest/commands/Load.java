package com.xmos.xta.core.consoletest.commands;

import java.util.Random;

public class Load implements Command {
	private String m_fileName;
	
	public Load(String fileName){
		m_fileName = fileName;
	}
	
	public String getCommand() {
		Random r = new Random();
		if(r.nextBoolean()){
			return "load " + m_fileName + " ";
		} else {
			Mess mess = new Mess();
			return "load " + mess.getCommand();
		}	
	}

}
