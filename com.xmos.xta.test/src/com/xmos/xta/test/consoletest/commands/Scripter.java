package com.xmos.xta.test.consoletest.commands;

import java.util.Random;

import com.xmos.xta.test.consoletest.ConsoleTest;

public class Scripter implements Command{


	public String getCommand() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("scripter ");
		int options = 3;
		if(!ConsoleTest.allowMess){
			options--;
		}
		Random r = new Random();
		switch(r.nextInt(options)){
		case 0:{
			sb.append("disable ");
			break;
		}
		case 1:{
			sb.append("dump ");
			break;
		}
		case 2:{
			sb.append("enable ");
			break;
		}
		case 3:{
			sb.append("listrefs ");
			break;
		}
		case 4:{
			sb.append("rename ");
			break;
		}
		case 5:{
			Mess mess = new Mess();
			sb.append(mess.getCommand());
			break;
		}
		}
		return sb.toString();
	}

}
