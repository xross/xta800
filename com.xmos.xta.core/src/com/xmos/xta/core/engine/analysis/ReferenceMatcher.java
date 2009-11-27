package com.xmos.xta.core.engine.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.NodeDescriptor;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.types.ReferenceType;

public class ReferenceMatcher {

	public Collection<NodeDescriptor> getNodeDescriptors(Engine engine, Node node,
			Reference reference) throws AbortedException {
		
		Collection<NodeDescriptor> nodeDescriptors = getNodesDescriptorsContainingReferencePc(
				engine, node, reference);
		return checkBackTrail(nodeDescriptors, reference);
	}

	public Collection<NodeDescriptor> getNodeDescriptors(Engine engine, Reference reference) throws AbortedException {
		Collection<NodeDescriptor> nodeDescriptors = getNodesDescriptorsContainingReferencePc(engine, reference);
		return checkBackTrail(nodeDescriptors, reference);
	}

	public Collection<NodeDescriptor> getFunctionNodeDescriptors(Engine engine, Node node,
			Reference reference) throws AbortedException {
		
		Collection<NodeDescriptor> nodeDescriptors = getNodesDescriptorsContainingFunctionName(
				engine, node, reference.getBase().getRef());
		return checkBackTrail(nodeDescriptors, reference);
	}

	private Collection<NodeDescriptor> getNodesDescriptorsContainingReferencePc(Engine engine, 
			Reference reference) throws AbortedException {
		
		final Collection<NodeDescriptor> nodeDescriptors = new ArrayList<NodeDescriptor>();
		long pc = reference.getPc();
		Set<InstructionNode> nodes = engine.getStructureGraph().getPcToInstructionNodes().get(pc);
		if (nodes != null) {
			for (InstructionNode inst : nodes) {
				LinkedList<Node> ancestors = new LinkedList<Node>();
				getAllNodeDescriptors(inst, inst.getParent(), nodeDescriptors, ancestors);
			}
		}
		return nodeDescriptors;
	}
	
	private void getAllNodeDescriptors(InstructionNode inst, Node node, 
			Collection<NodeDescriptor> nodeDescriptors, LinkedList<Node> ancestors) {

		LinkedList<Node> nextAncestors = new LinkedList<Node>(ancestors);
		
		List<FunctionNode> callingFunctions = getCallingFunctions(node, nextAncestors);
		if (callingFunctions.isEmpty()) {
			Stack<Node> stack = new Stack<Node>();
			stack.addAll(nextAncestors);
			NodeDescriptor descriptor = new NodeDescriptor(inst, stack);
			nodeDescriptors.add(descriptor);
		}
		
		for (FunctionNode g : callingFunctions) {
			getAllNodeDescriptors(inst, g, nodeDescriptors, nextAncestors);
		}
	}

	private List<FunctionNode> getCallingFunctions(Node node, LinkedList<Node> ancestors) {
		if (node != null) {
			do {
				ancestors.add(0, node);
				node = node.getParent();
			} while (node != null && !node.isFunctionNode());
		}
		
		if (node != null) {
			List<FunctionNode> callingFunctions = ((FunctionNode)node).getCallingFunction();
			if (callingFunctions.isEmpty()) {
				ancestors.add(0, node);
			}
			return callingFunctions;
		}
		return new ArrayList<FunctionNode>();
	}

	private Collection<NodeDescriptor> getNodesDescriptorsContainingReferencePc(final Aborter aborter, Node node,
			final Reference reference) throws AbortedException {
		
		final Collection<NodeDescriptor> nodeDescriptors = new ArrayList<NodeDescriptor>();	
		node.traverse(new Stack<Node>(), new DefaultNodeTraverser() {
			public TraverseStatus foundFunctionNode(Node node, Stack<Node> ancestors) {
				try {
					aborter.getAbort();
				} catch (AbortedException e) {
					return TraverseStatus.ABORT;
				}
				return TraverseStatus.CONTINUE;
			}
			
			public TraverseStatus foundInstructionNode(Node node, Stack<Node> ancestors) {		
				XtaInstruction instruction = ((InstructionNode)node).getXtaInstruction();
				if (reference.matches(instruction.getPc()) && !instruction.isFnop()) {
					Stack<Node> newStack = new Stack<Node>();
					newStack.addAll(ancestors);
					nodeDescriptors.add(new NodeDescriptor(node, newStack));
				}
				return TraverseStatus.CONTINUE;
			}
	    });
		
		aborter.getAbort();
		return nodeDescriptors;
	}

	private Collection<NodeDescriptor> getNodesDescriptorsContainingFunctionName(final Aborter aborter,
			Node node, final String name) throws AbortedException {
		
		final Collection<NodeDescriptor> nodeDescriptors = new ArrayList<NodeDescriptor>();	
		node.traverse(new Stack<Node>(), new DefaultNodeTraverser() {		
			public TraverseStatus foundFunctionNode(Node node, Stack<Node> ancestors) {
				try {
					aborter.getAbort();
				} catch (AbortedException e) {
					return TraverseStatus.ABORT;
				}

				String functionName = ((FunctionNode)node).getFunctionName();				
				if (name.equals(functionName)) {
					Stack<Node> newStack = new Stack<Node>();
					newStack.addAll(ancestors);
					nodeDescriptors.add(new NodeDescriptor(node, newStack));					
				}
				return TraverseStatus.CONTINUE;
			}
	    });
		
		aborter.getAbort();
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
