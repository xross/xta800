package com.xmos.xta.console.commands.print;

import java.util.Collection;

import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.StructureGraph;
import com.xmos.xta.engine.analysis.nodes.Node;
import com.xmos.xta.engine.analysis.nodes.NodeTraverser;

public class StructureDisplayer {
	private static final boolean DEBUG = true;

	private Engine m_engine;
	private Console m_console;

	public StructureDisplayer(Engine engine, Console console) {
		m_engine = engine;
		m_console = console;
	}
	
	public void display() {
		StructureGraph structureGraph = m_engine.getStructureGraph();

		Collection<Node> nodes = structureGraph.getThreads();	
		for (Node node : nodes) {					
		    node.traverse(0, new NodeTraverser() {
				public void foundBlockNode(Node node, int depth) {
					printIndent(depth);
					m_console.display("block");
					if (DEBUG) m_console.display(String.format("(%d)", node.getId()));
					m_console.display("\n");						
				}

				public void foundFunctionNode(Node node, int depth) {
					printIndent(depth);
					m_console.display(String.format("function"));
					if (DEBUG) m_console.display(String.format("(%d): ", node.getId()));
					m_console.display(String.format("%s\n", node.getFunctionName()));

				}

				public void foundInstructionNode(Node node, int depth) {
					printIndent(depth);
					m_console.display("instruction");
					if (DEBUG) m_console.display(String.format("(%d)", node.getId()));
					m_console.display("\n");						
				}

				public void foundLoopNode(Node node, int depth) {
					printIndent(depth);
					m_console.display("loop");
					if (DEBUG) m_console.display(String.format("(%d)", node.getId()));
					m_console.display("\n");						
				}

				public void foundPathNode(Node node, int depth) {
					printIndent(depth);
					m_console.display("path");
					if (DEBUG) m_console.display(String.format("(%d)", node.getId()));
					m_console.display("\n");						
				}

				public void foundSectionNode(Node node, int depth) {
					printIndent(depth);
					m_console.display("section");
					if (DEBUG) m_console.display(String.format("(%d)", node.getId()));
					m_console.display("\n");						
				}

				public void foundTerminalNode(Node node, int depth) {
					printIndent(depth);
					m_console.display("terminal");
					if (DEBUG) m_console.display(String.format("(%d)", node.getId()));
					m_console.display("\n");						
				}		    	
		    });
		System.out.println();    
		}		
		
    }
	
	private void printIndent(int depth) {
		for (int i = 0; i < depth; ++i) {
			m_console.display(".    ");
		}
	}		
}
