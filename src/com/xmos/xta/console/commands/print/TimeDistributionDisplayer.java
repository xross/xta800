package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.TimeDistributionBuckets;


public class TimeDistributionDisplayer {
	private Console m_console;
	
	public TimeDistributionDisplayer(Engine engine, Console console) {
		m_console = console;
	}
	
	public void display(Node node) {
		m_console.display("time distribution\n");
		TimeDistributionBuckets tdb = node.getTimeSpread();
		
		tdb.print();
		
	}

   
}
