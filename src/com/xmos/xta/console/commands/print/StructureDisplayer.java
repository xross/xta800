package com.xmos.xta.console.commands.print;

import java.util.Collection;

import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.StructureGraph;
import com.xmos.xta.engine.analysis.nodes.Node;
import com.xmos.xta.engine.analysis.nodes.NodeTraverser;
import com.xmos.xta.engine.analysis.nodes.Node.Case;

public class StructureDisplayer {
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
				public boolean foundBlockNode(Node node, int depth) {
					printIndent(depth);
					String nodeName = String.format("block(%d)", node.getId());
					m_console.display(String.format("%-14s", nodeName));
					m_console.display(String.format(" : %.1fns\n", 
							node.getTimeTaken(Case.WORST)));
					return true;
				}

				public boolean foundFunctionNode(Node node, int depth) {
					printIndent(depth);
					String nodeName = String.format("function(%d)", node.getId());
					m_console.display(String.format("%-14s (%s)", nodeName, node.getFunctionName()));
					m_console.display(String.format(" : %.1fns\n", 
							node.getTimeTaken(Case.WORST)));						
					return true;
				}

				public boolean foundInstructionNode(Node node, int depth) {
					printIndent(depth);
					String nodeName = String.format("instruction(%d)", node.getId());
					m_console.display(String.format("%-14s", nodeName));
					m_console.display(String.format(" : %.1fns\n", 
							node.getTimeTaken(Case.WORST)));						
					return true;
				}

				public boolean foundLoopNode(Node node, int depth) {
					printIndent(depth);
					String nodeName = String.format("loop(%d)", node.getId());
					m_console.display(String.format("%-14s", nodeName));
					m_console.display(String.format(" : %.1fns\n", 
							node.getTimeTaken(Case.WORST)));						
					return true;
				}

				public boolean foundPathNode(Node node, int depth) {
					printIndent(depth);
					String nodeName = String.format("path(%d)", node.getId());
					m_console.display(String.format("%-14s", nodeName));
					m_console.display(String.format(" : %.1fns\n", 
							node.getTimeTaken(Case.WORST)));						
					return true;
				}

				public boolean foundSectionNode(Node node, int depth) {
					printIndent(depth);
					String nodeName = String.format("section(%d)", node.getId());
					m_console.display(String.format("%-14s", nodeName));
					m_console.display(String.format(" : %.1fns\n", 
							node.getTimeTaken(Case.WORST)));						
					return true;
				}

				public boolean foundTerminalNode(Node node, int depth) {
					printIndent(depth);
					String nodeName = String.format("terminal(%d)", node.getId());
					m_console.display(String.format("%-14s", nodeName));
					m_console.display(String.format(" : %.1fns\n", 
							node.getTimeTaken(Case.WORST)));						
					return true;
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
