package com.xmos.xta.test.consoletest.commands;

import java.util.Random;

import com.xmos.xta.test.consoletest.ConsoleTest;
import com.xmos.xta.test.consoletest.commands.units.BestWorst;
import com.xmos.xta.test.consoletest.commands.units.Number;
import com.xmos.xta.test.consoletest.commands.units.OnOff;
import com.xmos.xta.test.consoletest.commands.units.SourcePath;
import com.xmos.xta.test.consoletest.commands.units.TrueFalse;
public class Config implements Command {

	public String getCommand() {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		sb.append("config ");
		
		int options = 11;
		if(!ConsoleTest.allowMess){
			options--;
		}
		
		switch(r.nextInt(options)){
		case 0:
			sb.append("case ");
			BestWorst bw = new BestWorst();
			sb.append(bw.getCommand());
			break;
		case 1:
			sb.append("verbosity ");
			if(r.nextBoolean()){
				Number node = new Number();
				sb.append(node.getCommand());
			}
			Number core = new Number();
			sb.append(core.getCommand());
			break;
		case 2:
			sb.append("freq ");
			Number freqNum = new Number();
			sb.append(freqNum.getCommand());
			break;
		case 3:
			sb.append("scale ");
			TrueFalse tf = new TrueFalse();
			sb.append(tf.getCommand());
			break;
		case 4:
			sb.append("srcpaths ");
			SourcePath sp = new SourcePath();
			sb.append(sp.getCommand());
			break;
		case 5:
			sb.append("threads ");
			Number numThreads = new Number();
			sb.append(numThreads.getCommand());
			break;
		case 6:
			sb.append("timeout ");
			Number numTimeOut = new Number();
			sb.append(numTimeOut.getCommand());
			break;
		case 7:
			sb.append("Terror ");
			OnOff onOffTerror = new OnOff();
			sb.append(onOffTerror.getCommand());
			break;
		case 8:
			sb.append("verbosity ");
			Number verbosityLevel = new Number();
			sb.append(verbosityLevel.getCommand());
			break;
		case 9:
			sb.append("Werror");
			OnOff onOffWerror = new OnOff();
			sb.append(onOffWerror.getCommand());
			break;
		case 10:
			Mess mess= new Mess();
			sb.append(mess.getCommand());
			break;
		}
		return sb.toString();
	}

}
