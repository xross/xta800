package com.xmos.xta.core.console.commands.print;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.TimeDistributionBuckets;

public class TimeDistributionDisplayer {
	private Console m_console;

	public TimeDistributionDisplayer(Engine engine, Console console) {
		m_console = console;
	}

	public void display(Node node, Console console) {
		m_console.display("time distribution\n");
		TimeDistributionBuckets tdb = node.getTimeSpread();
		tdb.print(console);
	}
}
