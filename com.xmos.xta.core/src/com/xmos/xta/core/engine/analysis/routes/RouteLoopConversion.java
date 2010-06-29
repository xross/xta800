package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.xmos.libs.memorymonitor.MemoryMonitorException;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.controlflows.ControlFlow;
import com.xmos.xta.core.engine.analysis.controlflows.FunctionContainer;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.references.Reference;

public class RouteLoopConversion {
	private Engine m_engine;
	private ControlFlow m_controlFlow;    

	public RouteLoopConversion(Engine engine, ControlFlow controlFlow) {
		m_engine = engine;
		m_controlFlow = controlFlow;
	}
	
	// Given a reference, this function returns true if the reference
	// points to a location within a loop, and false otherwise.
	public boolean isWithinLoop(Reference reference) {
		try {
			return process(reference, null, null);

		} catch (Exception e) {}
		return false;
	}
	
	// Given a reference, this function adds the relevant start loop 
	// and exclusion stack nodes to the given lists
	public void updateLoopStackNodes(Reference reference,
			List<StackNode> loopStartStackNodes,
			List<StackNode> exclusionStackNodes) 
	    throws AbortedException, MemoryMonitorException {
		
		process(reference, loopStartStackNodes, exclusionStackNodes);
	}
	
	private boolean process(Reference reference,
			List<StackNode> loopStartStackNodes,
			List<StackNode> exclusionStackNodes) 
	    throws AbortedException, MemoryMonitorException {
		
		boolean withinLoop = false;
		
		List<StackNode> fromStackNodes = RouteConstructor.getPathStackNodes(
				m_engine, m_controlFlow, reference);
		
		for (StackNode fromStackNode : fromStackNodes) {			
			
			// This ensures that the cached function is structuralised..
			refreshCachedFunction(fromStackNode.getInstruction().getPC());
			
			// First we look for a loop in the function that contained the original reference..
			Node loopNode = findLoopNode(fromStackNode.getInstruction());
			if (loopNode != null) {
				addLoopStackNodes(loopNode, fromStackNode.getNodeStacks(), 
						loopStartStackNodes, exclusionStackNodes);
				
				withinLoop = true;
				break;
			}			
			
			// Next search up the various function call stacks for all the loops..
			for (Stack<FunctionReferenceNode> nodeStack : fromStackNode.getNodeStacks()) {

				FunctionReferenceNode functionReferenceNode = nodeStack.pop();
				while (functionReferenceNode != null) {

					Set<Node> callNodes = functionReferenceNode.getPrevNodes();
					if (callNodes.isEmpty())
						break;
										
					assert(callNodes.size() == 1);
					Node callNode = callNodes.iterator().next();					
					
					// This ensures that the cached function is structuralised..
					refreshCachedFunction(callNode.getStartPc());
					
					loopNode = findLoopNode(callNode);
					if (loopNode != null) {
						List<Stack<FunctionReferenceNode>> nodeStacks = 
							new ArrayList<Stack<FunctionReferenceNode>>();
						nodeStacks.add(nodeStack);
						
						addLoopStackNodes(loopNode, nodeStacks, 
								loopStartStackNodes, exclusionStackNodes);	
						
						withinLoop = true;
						break;
					}					
					functionReferenceNode = nodeStack.pop();
				}
			}
		}	
		return withinLoop;
	}
	
	private Node findLoopNode(Node node) {		
		Node loopNode = node.getEnclosingLoopNode();
		if (loopNode != null) {
			return loopNode;
		}
		Node infiniteLoopNode = node.getEnclosingInfiniteLoopNode();
		if (infiniteLoopNode != null) {
			return infiniteLoopNode;
		}								
		return null;
	}
	
	private void addLoopStackNodes(Node loopNode,
			List<Stack<FunctionReferenceNode>> nodeStacks,
			List<StackNode> loopStartStackNodes,
			List<StackNode> exclusionStackNodes) {
		
		if (loopStartStackNodes == null)
			return;
				
		InstructionNode instructionNode = findFirstInstructionNode(loopNode);		
		loopStartStackNodes.add(new StackNode(instructionNode, nodeStacks));

		if (loopNode.isLoopNode() || loopNode.isSelfLoopNode()) {
			Node successor = loopNode.getSuccessor();
			if (successor != null) {				
				instructionNode = findFirstInstructionNode(successor);	
				exclusionStackNodes.add(new StackNode(instructionNode, nodeStacks));
			}			
		}
	}

	private InstructionNode findFirstInstructionNode(Node node) {
		final Collection<InstructionNode> instructionNode = new ArrayList<InstructionNode>();
		node.traverse(new DefaultNodeTraverser() {
			public TraverseStatus foundInstructionNode(Node node) {
				instructionNode.add((InstructionNode)node);
				return TraverseStatus.ABORT;
			}
		});
		assert(instructionNode.size() == 1);
		return instructionNode.iterator().next();
	}
	
	private void refreshCachedFunction(long pc) 
	    throws AbortedException, MemoryMonitorException {
		
		for (FunctionContainer functionContainer : m_controlFlow.getFunctionContaining(pc)) {
			functionContainer.getCachedFunction(m_engine);
		}
	}
}
