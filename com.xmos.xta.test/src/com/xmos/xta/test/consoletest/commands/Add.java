package com.xmos.xta.test.consoletest.commands;
import java.util.Random;
import com.xmos.xta.test.consoletest.commands.units.*;
import com.xmos.xta.test.consoletest.commands.units.Number;

public class Add implements Command {

	public Add(){

	}

	public String getCommand() {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		sb.append("add ");
		int i = r.nextInt(8);
		switch(i){
		case 0 :
			addBranch(sb);
			break;
		case 1 :
			addExclusion(sb);
			break;
		case 2 :
			addFunctionTime(sb);
			break;
		case 3 :
			addInstructionTime(sb);
			break;
		case 4 :
			addLoop(sb);
			break;
		case 5 :
			addLoopPath(sb);
			break;
		case 6 :
			addLoopScope(sb);
			break;
		case 7 :
			addPathTime(sb);
			break;
		}
		return sb.toString();
	}

	private void addBranch(StringBuffer sb) {
		sb.append("branch ");
		Reference from = new Reference();
		sb.append(from.getCommand());
		Random r = new Random();
		int max = r.nextInt(10);
		for(int i=0;i<max;i++){
			Reference to = new Reference();
			sb.append(to.getCommand());
		}
	}
	
	private void addExclusion(StringBuffer sb) {
		sb.append("exclusion ");
		Reference from = new Reference();
		sb.append(from.getCommand());
	}
	
	private void addFunctionTime(StringBuffer sb) {
		sb.append("functiontime ");
		Reference from = new Reference();
		sb.append(from.getCommand());
		
		Number num = new Number();
		sb.append(num.getCommand());
		
		Mode unit = new Mode();
		sb.append(unit.getCommand());
	}
	
	private void addInstructionTime(StringBuffer sb) {
		sb.append("instructiontime ");
		Reference from = new Reference();
		sb.append(from.getCommand());
		
		Number num = new Number();
		sb.append(num.getCommand());
		
		Mode unit = new Mode();
		sb.append(unit.getCommand());
	}
	
	private void addLoop(StringBuffer sb) {
		sb.append("loop ");
		Reference from = new Reference();
		sb.append(from.getCommand());
		
		Number num = new Number();
		sb.append(num.getCommand());
	}
	
	private void addLoopPath(StringBuffer sb) {
		sb.append("looppath ");
		Reference from = new Reference();
		sb.append(from.getCommand());
		
		Number num = new Number();
		sb.append(num.getCommand());
	}
	
	private void addLoopScope(StringBuffer sb) {
		sb.append("loopscope ");
		Reference from = new Reference();
		sb.append(from.getCommand());
		
		RelAbs ra = new RelAbs();
		sb.append(ra.getCommand());
	}
	
	private void addPathTime(StringBuffer sb) {
		sb.append("pathtime ");
		Reference from = new Reference();
		sb.append(from.getCommand());
		
		Reference to = new Reference();
		sb.append(to.getCommand());
		
		Number num = new Number();
		sb.append(num.getCommand());
		
		Mode unit = new Mode();
		sb.append(unit.getCommand());
	}
}
