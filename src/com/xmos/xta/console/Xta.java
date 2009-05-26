package com.xmos.xta.console;

import com.xmos.xta.engine.Config;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;

public class Xta {	
	static public void main(String[] args) {
	    Engine engine = new Engine(new Config());	
	    Console console = ConsoleFactory.buildConsole(engine);
	    Notifier notifier = new ConsoleNotifier(console);
		Shell shell = new Shell(console, engine, notifier, args);			
		int numFailures = shell.execute();		
		System.exit(numFailures);
	}
}
