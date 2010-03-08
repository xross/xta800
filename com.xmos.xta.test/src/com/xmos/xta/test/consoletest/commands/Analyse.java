package com.xmos.xta.test.consoletest.commands;

import java.util.Random;


public class Analyse implements Command {


	public String getCommand() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("analyse ");
		
		Random r = new Random();
		switch(r.nextInt(3)){
		case 0:{
			sb.append("function ");
			Reference ref = new Reference();
			sb.append(ref.getCommand());
			break;
		}
		case 1:{
			sb.append("path ");
			Reference from = new Reference();
			sb.append(from.getCommand());
			Reference to = new Reference();
			sb.append(to.getCommand());
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
