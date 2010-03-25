package com.xmos.xta.test.consoletest.commands;

import java.util.Random;

import com.xmos.xta.test.consoletest.ConsoleTest;

public class Pling implements Command {


	public String getCommand() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("!");
		int options = 3;
		if(!ConsoleTest.allowMess){
			options--;
		}
		
		Random r = new Random();
		switch(r.nextInt(options)){
		case 0:{
			sb.append(r.nextInt(10) + " ");
			break;
		}
		case 1:{
			sb.append(r.nextInt(1000)+ " ");
			break;
		}
		case 2:{
			Mess mess = new Mess();
			sb.append(mess.getCommand());
			break;
		}
		}
		return sb.toString();
	}

}

