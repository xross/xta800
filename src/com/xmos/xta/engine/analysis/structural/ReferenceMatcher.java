package com.xmos.xta.engine.analysis.structural;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import com.xmos.xta.engine.analysis.structural.nodes.DefaultNodeTraverser;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.types.ReferenceType;

public class ReferenceMatcher {
	public Collection<Node> getNodes(Node node, Reference reference) {
		Collection<Node> matchingNodes = getNodesContainingReferencePc(node, reference.getPc());
		return checkBackTrail(matchingNodes, reference);
	}

	private Collection<Node> getNodesContainingReferencePc(Node node, final int pc) {		
		final Collection<Node> matchingNodes = new ArrayList<Node>();		
		node.traverse(0, new DefaultNodeTraverser() {
			public boolean foundInstructionNode(Node node, int depth) {							
				XtaInstruction instruction = node.getInstructions().getFirst();				
				if (instruction.getPc() == pc) {
					matchingNodes.add(node);
				}
				return true;
			}
	    });
		return matchingNodes;
	}

	private Collection<Node> checkBackTrail(Collection<Node> nodes, Reference reference) {
		Collection<Node> matchingNodes = new ArrayList<Node>();
		for (Node node : nodes) {
			Stack<ReferenceType> stack = createBackTrailStack(reference);
			if (stack.empty()) {
				matchingNodes.add(node);
			} else {
				if (node.checkBackTrail(stack)) {
					matchingNodes.add(node);
				}				
			}
		}
		return matchingNodes;
	}
	
	private Stack<ReferenceType> createBackTrailStack(Reference reference) {
		Stack<ReferenceType> stack = new Stack<ReferenceType>();
		for (ReferenceType referenceType : reference.getBackTrail()) {
			stack.push(referenceType);
		}							
		return stack;
	}
}
