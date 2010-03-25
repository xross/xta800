package com.xmos.xta.test.consoletest.commands;

import java.util.Random;

import com.xmos.xta.test.consoletest.ConsoleTest;
import com.xmos.xta.test.consoletest.commands.units.Number;
import com.xmos.xta.test.consoletest.commands.units.RelAbs;
import com.xmos.xta.test.consoletest.commands.units.RouteId;
import com.xmos.xta.test.consoletest.commands.units.Mode;

public class Set implements Command {

	public String getCommand() {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		sb.append("set ");
		
		int options = 10;
		if(!ConsoleTest.allowMess){
			options--;
		}
		
		
		int i = r.nextInt(options);
		switch(i){
		case 0 :
			addFunctionTime(sb);
			break;
		case 1 :
			addInstructionTime(sb);
			break;
		case 2 :
			addFrom(sb);
			break;
		case 3 :
			addRequired(sb);
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
		case 8 :
			addTo(sb);
			break;
		case 9 :
			addExclude(sb);
			break;
		case 10 :
			Mess mess = new Mess();
			sb.append(mess.getCommand());
			break;
		}
		return sb.toString();
	}
	
	private void addFrom(StringBuffer sb){
		sb.append("from  ");
		Reference ref = new Reference();
		sb.append(ref.getCommand()); // ref
	}
	
	private void addTo(StringBuffer sb){
		sb.append("to  ");
		Reference ref = new Reference();
		sb.append(ref.getCommand()); // ref
	}
	
	private void addExclude(StringBuffer sb){
		sb.append("exclusion  ");
		RouteId ri = new RouteId();
		Reference ref = new Reference();
		sb.append(ri.getCommand());		// routeid
		sb.append(ref.getCommand()); // ref
	}
	
	private void addFunctionTime(StringBuffer sb) {
		sb.append("functiontime ");
		Number num = new Number();
		Mode mode = new Mode();
		Reference ref = new Reference();
		RouteId ri = new RouteId();
		
		
		sb.append(ri.getCommand());		// routeid
		sb.append(ref.getCommand());	// ref
		sb.append(num.getCommand()); 	// value
		sb.append(mode.getCommand());	// mode
	}
	
	private void addInstructionTime(StringBuffer sb) {
		sb.append("instructiontime ");
		Number num = new Number();
		Mode mode = new Mode();
		Reference ref = new Reference();
		RouteId ri = new RouteId();
		
		
		sb.append(ri.getCommand());		// routeid
		sb.append(ref.getCommand());	// ref
		sb.append(num.getCommand()); 	// value
		sb.append(mode.getCommand());	// mode
	}
	
	private void addLoop(StringBuffer sb) {
		sb.append("loop ");
		Number num = new Number();
		Reference from = new Reference();
		RouteId ri = new RouteId();
		
		sb.append(ri.getCommand());		// routeid
		sb.append(from.getCommand());	// ref
		sb.append(num.getCommand());	// int  TODO
	}
	
	private void addLoopPath(StringBuffer sb) {
		sb.append("looppath ");
		Number num = new Number();
		Reference from = new Reference();
		RouteId ri = new RouteId();
		
		sb.append(ri.getCommand());		// routeid
		sb.append(from.getCommand());	// ref
		sb.append(num.getCommand());	// int  TODO
	}
	
	private void addLoopScope(StringBuffer sb) {
		sb.append("loopscope ");
		Reference from = new Reference();
		RelAbs ra = new RelAbs();
		RouteId ri = new RouteId();
		
		sb.append(ri.getCommand());		// routeid
		sb.append(from.getCommand());	// ref
		sb.append(ra.getCommand());		// rel/abs
	}
	
	private void addPathTime(StringBuffer sb) {
		sb.append("pathtime ");
		Number num = new Number();
		Reference from = new Reference();
		Mode mode = new Mode();
		RouteId ri = new RouteId();
		
		sb.append(ri.getCommand());		// routeid
		sb.append(from.getCommand());	// ref
		sb.append(from.getCommand());	// ref
		sb.append(num.getCommand());	// int TODO
		sb.append(mode.getCommand());	// mode
	}
	
	private void addRequired(StringBuffer sb) {
		sb.append("required ");
		RouteId ri = new RouteId();
		Number num = new Number(); 
		Mode mode = new Mode();
		sb.append(ri.getCommand());		// routeid
		sb.append(num.getCommand());	// int TODO
		sb.append(mode.getCommand());	// mode
	}
}
