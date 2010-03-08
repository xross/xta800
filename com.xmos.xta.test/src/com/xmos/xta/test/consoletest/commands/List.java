package com.xmos.xta.test.consoletest.commands;

import java.util.Random;
import com.xmos.xta.test.consoletest.commands.units.Number;
public class List implements Command {

	private static String [] lists = {"branches","calls","allcalls","endpoints","allendpoints","exclusions",
		"functions","functiontimes","instructiontime","labels","loops","looppaths","loopscopes","pathtimes",
		"sources","srccommands","srcloops","srclabels","allsrclabels","threadstartpoints","threadstoppoints"
	};
	
	public String getCommand() {
		Random r = new Random();
		int i = r.nextInt(lists.length + 3);
		switch (i){
		case 0:
			Number knownNumber = new Number();
			return "list knowns " + knownNumber.getCommand();
		case 1:
			Number unknownNumber = new Number();
			return "list unknowns " + unknownNumber.getCommand();
		case 2:
			Mess mess = new Mess();
			return "list " + mess.getCommand();
		default:
			return "list " + lists[i-3] + " ";
		}
	}

}
