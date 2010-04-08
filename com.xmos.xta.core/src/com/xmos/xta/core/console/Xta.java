package com.xmos.xta.core.console;

import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;

public class Xta {
	static public void main(String[] args) {
		Config config = new Config();
	    Engine engine = new Engine(config);
	    Console console = ConsoleFactory.buildConsole(engine);
	    Notifier notifier = new ConsoleNotifier(console, config);
		Shell shell = new Shell(console, engine, notifier, args);
		int returnCode = shell.execute();
		System.exit(returnCode);
	}
}
