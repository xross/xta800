package com.xmos.xta.test.consoletest.commands;

import java.util.Random;

import com.xmos.xta.test.consoletest.ConsoleTest;
import com.xmos.xta.test.consoletest.commands.units.RouteId;

public class Remove implements Command {

	public String getCommand() {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		sb.append("remove ");
		
		
		int options = 12;
		if(!ConsoleTest.allowMess){
			options--;
		}
		
		switch(r.nextInt(options)){
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
		case 8 :
			addTo(sb);
			break;
		case 9 :
			addFrom(sb);
			break;
		case 10 :
			addRoute(sb);
			break;
		case 11:
			Mess mess = new Mess();
			sb.append(mess.getCommand());
			break;
		}
		return sb.toString();
	}

	private void addFrom(StringBuffer sb) {
		sb.append("from ");
		Reference from = new Reference();
		sb.append(from.getCommand());
	}

	private void addTo(StringBuffer sb) {
		sb.append("to ");
		Reference to = new Reference();
		sb.append(to.getCommand());
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
	}
	
	private void addInstructionTime(StringBuffer sb) {
		sb.append("instructiontime ");
		Reference from = new Reference();
		sb.append(from.getCommand());
	}
	
	private void addLoop(StringBuffer sb) {
		sb.append("loop ");
		Reference from = new Reference();
		sb.append(from.getCommand());
	}
	
	private void addLoopPath(StringBuffer sb) {
		sb.append("looppath ");
		Reference from = new Reference();
		sb.append(from.getCommand());
	}
	
	private void addLoopScope(StringBuffer sb) {
		sb.append("loopscope ");
		Reference from = new Reference();
		sb.append(from.getCommand());
	}
	
	private void addPathTime(StringBuffer sb) {
		sb.append("pathtime ");
		
		Reference from = new Reference();
		sb.append(from.getCommand());
		
		Reference to = new Reference();
		sb.append(to.getCommand());
		
	}

	private void addRoute(StringBuffer sb) {
		sb.append("route ");
		RouteId ri = new RouteId();
		sb.append(ri.getCommand());
	}
	
}
