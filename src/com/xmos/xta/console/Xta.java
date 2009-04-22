package com.xmos.xta.console;

import com.xmos.xta.engine.Config;
import com.xmos.xta.engine.Engine;

public class Xta {	
	static public void main(String[] args) {
	    Engine engine = new Engine(new Config());		
		Shell shell = new Shell(ConsoleFactory.buildConsole(engine), engine, args);			
		int numFailures = shell.execute();		
		System.exit(numFailures);
	}
}
