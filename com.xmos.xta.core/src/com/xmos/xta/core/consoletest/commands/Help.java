package com.xmos.xta.core.consoletest.commands;

import java.util.Random;

public class Help implements Command {

	public String getCommand() {
		Random r = new Random();
		
		switch (r.nextInt(19)){
		case 0:
			return "help add ";
		case 1:
			return "help analyse ";
		case 2:
			return "help config ";
		case 3:
			return "help clear ";
		case 4:
			return "help echo ";
		case 5:
			return "help exit ";
		case 6:
			return "help help ";
		case 7:
			return "help history ";
		case 8:
			return "help load ";
		case 9:
			return "help list ";
		case 10:
			return "help print ";
		case 11:
			return "help pwd ";
		case 12:
			return "help remove ";
		case 13:
			return "help scripter ";
		case 14:
			return "help set ";
		case 15:
			return "help source ";
		case 16:
			return "help status ";
		case 17:
			return "help version ";
		case 18:
			Mess mess = new Mess();
			return "help " + mess.getCommand();
		}
		return "";
	}
	
}
