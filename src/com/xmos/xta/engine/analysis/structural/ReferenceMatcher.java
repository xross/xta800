package com.xmos.xta.engine.analysis.structural;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.types.ReferenceType;

public class ReferenceMatcher {
	public Collection<InstructionNode> getNodes(Node node, Reference reference) {
		Collection<InstructionNode> matchingNodes = getNodesContainingReferencePc(node, reference.getPc());
		return checkBackTrail(matchingNodes, reference);
	}

	private Collection<InstructionNode> getNodesContainingReferencePc(Node node, final int pc) {		
		final Collection<InstructionNode> matchingNodes = new ArrayList<InstructionNode>();		
		node.traverse(0, new DefaultNodeTraverser() {		
			public boolean foundInstructionNode(Node node, int depth) {		
				XtaInstruction instruction = ((InstructionNode)node).getXtaInstruction();				
				if (instruction.getPc() == pc && !instruction.isFnop()) {
					matchingNodes.add((InstructionNode)node);
				}
				return true;
			}
	    });
		return matchingNodes;
	}

	private Collection<InstructionNode> checkBackTrail(Collection<InstructionNode> nodes, Reference reference) {
		Collection<InstructionNode> matchingNodes = new ArrayList<InstructionNode>();
		for (InstructionNode node : nodes) {
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
