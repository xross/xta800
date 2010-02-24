package com.xmos.xta.core.consoletest.commands.units;

import java.util.Random;

import com.xmos.xta.core.consoletest.commands.Command;
import com.xmos.xta.core.consoletest.commands.Mess;

public class Mode implements Command {

	public String getCommand() {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		int i = r.nextInt(4);
		switch(i){
		case 0:
			sb.append("ns ");
			break;
		case 1:
			sb.append("mhz ");
			break;
		case 2:
			sb.append("cycles ");
			break;
		case 3:
			Mess mess= new Mess();
			sb.append(mess.getCommand());
			break;
		}
		return sb.toString();
	}

}
