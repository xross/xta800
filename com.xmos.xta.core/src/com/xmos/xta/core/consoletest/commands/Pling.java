package com.xmos.xta.core.consoletest.commands;

import java.util.Random;

public class Pling implements Command {


	public String getCommand() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("!");
		
		Random r = new Random();
		switch(r.nextInt(3)){
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

