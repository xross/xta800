package com.xmos.xta.core.engine.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.NodeDescriptor;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.types.ReferenceType;

public class ReferenceMatcher {
	public Collection<NodeDescriptor> getNodeDescriptors(Node node, Reference reference) {
		Collection<NodeDescriptor> nodeDescriptors = getNodesDescriptorsContainingReferencePc(node, reference.getPc());
		return checkBackTrail(nodeDescriptors, reference);
	}

	private Collection<NodeDescriptor> getNodesDescriptorsContainingReferencePc(Node node, final long pc) {		
		final Collection<NodeDescriptor> nodeDescriptors = new ArrayList<NodeDescriptor>();	
		
		node.traverse(new Stack<Node>(), new DefaultNodeTraverser() {		
			public boolean foundInstructionNode(Node node, Stack<Node> ancestors) {		
				XtaInstruction instruction = ((InstructionNode)node).getXtaInstruction();				
				if (instruction.getPc() == pc && !instruction.isFnop()) {
					Stack<Node> newStack = new Stack<Node>();
					newStack.addAll(ancestors);
					nodeDescriptors.add(new NodeDescriptor((InstructionNode)node, newStack));
				}
				return true;
			}
	    });
		return nodeDescriptors;
	}

	private Collection<NodeDescriptor> checkBackTrail(Collection<NodeDescriptor> nodeDescriptors, Reference reference) {
		Collection<NodeDescriptor> matchingNodeDescriptors = new ArrayList<NodeDescriptor>();
		
		for (NodeDescriptor nodeDescriptor : nodeDescriptors) {
			Stack<ReferenceType> stack = createBackTrailStack(reference);
			if (stack.empty()) {
				matchingNodeDescriptors.add(nodeDescriptor);
				
			} else {
				if (nodeDescriptor.checkBackTrail(stack)) {
					matchingNodeDescriptors.add(nodeDescriptor);
				}
			}
		}
		return matchingNodeDescriptors;
	}
	
	private Stack<ReferenceType> createBackTrailStack(Reference reference) {
		Stack<ReferenceType> stack = new Stack<ReferenceType>();
		
		for (ReferenceType referenceType : reference.getBackTrail()) {
			stack.push(referenceType);
		}							
		return stack;
	}
}
