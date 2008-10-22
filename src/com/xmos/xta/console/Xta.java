package com.xmos.xta.console;

import com.xmos.xta.engine.Config;
import com.xmos.xta.engine.Engine;

public class Xta {	
	static public void main(String[] args) {
	    Console console = new ConsoleSystem();
	    Engine engine   = new Engine(new Config());		
		Shell shell     = new Shell(console, engine, args);			
		shell.execute();
	}
}
