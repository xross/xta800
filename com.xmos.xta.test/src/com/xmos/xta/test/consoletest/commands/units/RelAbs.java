package com.xmos.xta.test.consoletest.commands.units;

import java.util.Random;

import com.xmos.xta.test.consoletest.commands.Command;
import com.xmos.xta.test.consoletest.commands.Mess;

public class RelAbs implements Command{

	
	public String getCommand() {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		int i = r.nextInt(5);
		switch(i){
		case 0:
			sb.append("relative ");
			break;
		case 1:
			sb.append("r ");
			break;
		case 2:
			sb.append("absolute ");
			break;
		case 3:
			sb.append("a ");
			break;
		case 4:
			Mess mess= new Mess();
			sb.append(mess.getCommand());
			break;
		}
		return sb.toString();
	}

}