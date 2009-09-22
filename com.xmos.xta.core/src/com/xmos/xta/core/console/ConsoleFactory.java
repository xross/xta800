package com.xmos.xta.core.console;

import java.io.IOException;

import com.xmos.xta.core.engine.Engine;

public class ConsoleFactory {
	public static Console buildConsole(Engine engine) {
	    String consoleProperty = System.getProperty("Console");
	    if ((consoleProperty != null) && (consoleProperty.equals("Basic"))) {
	    	return new BasicConsole();	    		
	    }
	    
    	try {
			return new JLineConsole(engine);
		} catch (IOException e) {
			return new BasicConsole();
		}	    
	}
}
