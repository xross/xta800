package com.xmos.xta.core.consoletest.commands.units;

import java.util.Random;

import com.xmos.xta.core.consoletest.commands.Command;
import com.xmos.xta.core.consoletest.commands.Mess;

public class BestWorst implements Command{

	public String getCommand() {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		int i = r.nextInt(3);
		switch(i){
		case 0:
			sb.append("BEST ");
			break;
		case 1:
			sb.append("WORST ");
			break;
		case 2:
			Mess mess= new Mess();
			sb.append(mess.getCommand());
			break;
		}
		return sb.toString();
	}

}
