package com.xmos.xta.core.console.commands.print;

import java.util.Collection;
import java.util.Stack;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.UntypedNodeTraverserWithAncestors;

public class StructureDisplayer {

	private Console m_console;
	private int m_verbosity;

	public StructureDisplayer(Console console, int verbosity) {
		m_console = console;
		m_verbosity = verbosity;
	}

	public void display(Collection<Node> nodes) {
		final StringBuffer sb = new StringBuffer();
		for (Node node : nodes) {
		    node.traverse(new Stack<Node>(), new UntypedNodeTraverserWithAncestors() {
				public TraverseStatus foundNode(Node node, Stack<Node> ancestors) {
					displayNodeInfo(node, ancestors, sb);
					return TraverseStatus.CONTINUE;
				}
		    });
		    sb.append("\n");
		}
		m_console.display(sb.toString());
    }

	private void displayNodeInfo(Node node, Stack<Node> ancestors, StringBuffer sb) {
		if (node.isInstructionNode() &&
			(m_verbosity < Config.VERBOSITY_DISPLAY_INSTRUCTION_NODES))
			return;

		if (node.isNullNode() &&
			(m_verbosity < Config.VERBOSITY_DISPLAY_NULL_NODE))
			return;

		printIndent(ancestors.size(), sb);
		printModifiers(node, ancestors.size(), sb);
		displayNodeName(node, sb);
		displayTimeTaken(node, sb);
		displayNumPaths(node, sb);
		displayDescription(node, sb);
		displayAttributes(node, sb);
		displayAncestors(node, ancestors, sb);
		sb.append("\n");
	}

	private void printIndent(int depth, StringBuffer sb) {
		for (int i = 0; i < depth - 1; ++i) {
			sb.append(".    ");
		}
	}

	private void printModifiers(Node node, int depth, StringBuffer sb) {
		if (depth > 0) {
			if (node.getAttributes().getFixedTime() != null) {
				sb.append(".  F ");
			} else if (node.getAttributes().getDisplayAsUnknown()) {
				sb.append(".  * ");
			} else {
				sb.append(".    ");
			}
		}
	}

	private void displayNodeName(Node node, StringBuffer sb) {
		String nodeName = String.format("%s(%d)", node.getTypeName(), node.getId());
		sb.append(String.format("%-14s", nodeName));
	}

	private void displayTimeTaken(Node node, StringBuffer sb) {
		sb.append(node.getAttributes().getTimingSummary());
	}

	private void displayNumPaths(Node node, StringBuffer sb) {
		if (m_verbosity >= Config.VERBOSITY_DISPLAY_NUM_PATHS)
			sb.append(String.format(" [%s]", node.getAttributes().getNumPathsStr()));
	}

	private void displayDescription(Node node, StringBuffer sb) {
		sb.append(" " + node.getDescription());
	}

	private void displayAttributes(Node node, StringBuffer sb) {
		if (m_verbosity >= Config.VERBOSITY_DISPLAY_ATTRIBUTES) {
			NodeAttributes nodeAttributes = node.getAttributes();

			if (node.isUnresolved())
				sb.append(" UNRESOLVED");

			if (node.isLoopNode() || node.isSelfLoopNode()) {
				sb.append(String.format(" [%d]",
						nodeAttributes.getLoopIterations()));

				if (nodeAttributes.getIsAbsolute())
					sb.append(" (ABSOLUTE)");

			} else if (node.isCondNode()) {
				Node enclosingLoopNode = node.getEnclosingLoopNode();
				if (enclosingLoopNode != null) {
	                for (Node option : node.iChildren()) {
	                	String optionSet = nodeAttributes.getSetOption(option) ? "T" : "F";
	                	sb.append(String.format("[%s,%d,%d]", optionSet,
	                			nodeAttributes.getLoopPathIterations(
	                					enclosingLoopNode.getAttributes().getLoopIterations(), option, Case.WORST),
	                			nodeAttributes.getLoopPathIterations(
	                					enclosingLoopNode.getAttributes().getLoopIterations(), option, Case.BEST)));
	                }
				}

			} else if (node.getAttributes().getIsLoopHeader())  {
				sb.append(" (HEADER)");
			}
		}
	}

	private void displayAncestors(Node node, Stack<Node> ancestors, StringBuffer sb) {
		if (m_verbosity >= Config.VERBOSITY_DISPLAY_ANCESTORS) {
			sb.append(" [ANCESTORS: ");
			for (Node ancestor : ancestors) {
				sb.append(String.format("%d ", ancestor.getId()));
			}
			sb.append("]");
		}
	}
}
