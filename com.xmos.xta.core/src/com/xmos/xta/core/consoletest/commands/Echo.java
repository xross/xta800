package com.xmos.xta.core.consoletest.commands;

import java.util.Random;

public class Echo implements Command {

	public String getCommand() {
		Random r = new Random();
		Mess mess = new Mess();
		if(r.nextBoolean()){
			return "echo \"" + mess.getCommand()+ "\" ";
		} else {
			
			return "echo " + mess.getCommand();
		}
		
		
	}

}
