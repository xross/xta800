package com.xmos.xta.core.consoletest.commands;

import java.util.Random;
import com.xmos.xta.core.consoletest.commands.units.Mode;
import com.xmos.xta.core.consoletest.commands.units.Number;


public class Mess implements Command {

	public String getCommand() {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		
		switch (r.nextInt(6)) {
		case 0:
			Reference ref = new Reference();
			sb.append(ref.getCommand());
			break;
		case 1:
			Mode unit = new Mode();
			sb.append(unit.getCommand());
			break;
		case 2:
			Number num = new Number();
			sb.append(num.getCommand());
			break;
		case 3:
			makeRealMess(sb);
			break;
		case 4:
			sb.append(" ");
			break;
		case 5:
			makeRealMess(sb);
			Mess mess = new Mess();
			sb.append(mess.getCommand());
			break;
		}
		return sb.toString();
	}

	private void makeRealMess(StringBuffer sb){
		Random r = new Random();
		int len = r.nextInt(14);
		for(int i=0;i<len;i++){
			//r.nextBytes(bytes)
		}
		sb.append("csdfsefsas ");
	}
	
	
}
