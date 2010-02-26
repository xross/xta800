package com.xmos.xta.core.console;

import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;

public class Xta {
	static public void main(String[] args) {
	    Engine engine = new Engine(new Config());
	    Console console = ConsoleFactory.buildConsole(engine);
	    Notifier notifier = new ConsoleNotifier(console);
		Shell shell = new Shell(console, engine, notifier, args);
		int returnCode = shell.execute();
		System.exit(returnCode);
	}
}
