package com.xmos.xta.console.commands.print;

import java.util.Collection;

import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.StructureGraph;
import com.xmos.xta.engine.analysis.nodes.Node;

public class StructureDisplayer {
	private Engine m_engine;
	private Console m_console;

	public StructureDisplayer(Engine engine, Console console) {
		m_engine = engine;
		m_console = console;
	}
	
	public void display() {
		StructureGraph structureGraph = m_engine.getStructureGraph();

		//Collection<Node> nodes = structureGraph.getThreads();
		Collection<Node> nodes = structureGraph.getFunctions();
		for (Node node : nodes) {
		    node.display(0);	
		}		
    }
}
