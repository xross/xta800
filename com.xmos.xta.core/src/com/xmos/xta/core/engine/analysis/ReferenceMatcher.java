package com.xmos.xta.core.engine.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.NodeDescriptor;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.types.ReferenceType;

public class ReferenceMatcher {

	public Collection<NodeDescriptor> getNodeDescriptors(Aborter aborter,
			Node node, Reference reference) throws AbortedException {
		
		Collection<NodeDescriptor> nodeDescriptors = getNodesDescriptorsContainingReferencePc(aborter, node, reference.getPc());
		return checkBackTrail(nodeDescriptors, reference);
	}

	public Collection<NodeDescriptor> getFunctionNodeDescriptors(Node node, Reference reference) {
		Collection<NodeDescriptor> nodeDescriptors = getNodesDescriptorsContainingFunctionName(node, reference.getBase().getRef());
		return checkBackTrail(nodeDescriptors, reference);
	}
	
	private Collection<NodeDescriptor> getNodesDescriptorsContainingReferencePc(final Aborter aborter,
			Node node, final long pc) throws AbortedException {

		final Collection<NodeDescriptor> nodeDescriptors = new ArrayList<NodeDescriptor>();	
		
		node.traverse(new Stack<Node>(), new DefaultNodeTraverser() {
			public TraverseStatus foundFunctionNode(Node node, Stack<Node> ancestors) {
				if (aborter.getAbort()) {
					return TraverseStatus.ABORT;
				}
				return TraverseStatus.CONTINUE;
			}
			
			public TraverseStatus foundInstructionNode(Node node, Stack<Node> ancestors) {		
				XtaInstruction instruction = ((InstructionNode)node).getXtaInstruction();				
				if (instruction.getPc() == pc && !instruction.isFnop()) {
					Stack<Node> newStack = new Stack<Node>();
					newStack.addAll(ancestors);
					nodeDescriptors.add(new NodeDescriptor(node, newStack));
				}
				return TraverseStatus.CONTINUE;
			}
	    });
		if (aborter.getAbort()) {
			throw new AbortedException();
		}
		return nodeDescriptors;
	}

	private Collection<NodeDescriptor> getNodesDescriptorsContainingFunctionName(Node node, final String name) {		
		final Collection<NodeDescriptor> nodeDescriptors = new ArrayList<NodeDescriptor>();	
		
		node.traverse(new Stack<Node>(), new DefaultNodeTraverser() {		
			public TraverseStatus foundFunctionNode(Node node, Stack<Node> ancestors) {
				String functionName = ((FunctionNode)node).getFunctionName();				
				if (name.equals(functionName)) {
					Stack<Node> newStack = new Stack<Node>();
					newStack.addAll(ancestors);
					nodeDescriptors.add(new NodeDescriptor(node, newStack));					
				}
				return TraverseStatus.CONTINUE;
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
