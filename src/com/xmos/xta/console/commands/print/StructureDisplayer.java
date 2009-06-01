package com.xmos.xta.console.commands.print;

import java.util.Collection;

import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.StructureGraph;
import com.xmos.xta.engine.analysis.nodes.Node;
import com.xmos.xta.engine.analysis.nodes.NodeTraverser;

public class StructureDisplayer {
	private static final boolean DEBUG = false;

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
		    node.traverse(0, new NodeTraverser() {
				public void foundBlockNode(Node node, int depth) {
					printIndent(depth);
					m_console.display("block\n");
				}

				public void foundFunctionNode(Node node, int depth) {
					printIndent(depth);
					m_console.display(String.format("function: %s\n", node.getFunctionName()));
				}

				public void foundInstructionNode(Node node, int depth) {
					printIndent(depth);
					m_console.display("instruction\n");
				}

				public void foundLoopNode(Node node, int depth) {
					printIndent(depth);
					m_console.display("loop\n");
				}

				public void foundPathNode(Node node, int depth) {
					printIndent(depth);
					m_console.display("path\n");
				}

				public void foundSectionNode(Node node, int depth) {
					printIndent(depth);
					m_console.display("section\n");
				}

				public void foundTerminalNode(Node node, int depth) {
					printIndent(depth);
					m_console.display("terminal\n");
				}		    	
		    });
		}		
    }
	
	private void printIndent(int depth) {
		for (int i = 0; i < depth; ++i) {
			m_console.display(".    ");
		}
	}		
}
