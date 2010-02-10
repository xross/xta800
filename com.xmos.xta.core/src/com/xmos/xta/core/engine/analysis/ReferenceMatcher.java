package com.xmos.xta.core.engine.analysis;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.routes.StackNode;
import com.xmos.xta.core.engine.analysis.structural.StructureGraph;
import com.xmos.xta.core.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.types.ReferenceType;

public class ReferenceMatcher {

	public Set<StackNode> getStackNode(Engine engine, Reference reference) {
		Set<StackNode> stackNodes = new HashSet<StackNode>();
		StructureGraph sg = engine.getStructureGraph();
		
		for(long pc : reference.getPcs()){
		
			InstructionNode inst = engine.getInstructionDecoder().getNode(pc);
			Set<Stack<FunctionReferenceNode>> stacks = new HashSet<Stack<FunctionReferenceNode>>();
			
			Set<FunctionNode> containedInFunction = sg.getFunctionsOfInstruction(inst);
			
			for(FunctionNode fn : containedInFunction){
				Set<Stack<FunctionReferenceNode>> callStacks = sg.getFunctionCallStack(fn);
				BlockNode block = getStartBlock(inst, fn);
				List<InstructionNode> nodeList = new LinkedList<InstructionNode>();
				for(Stack<FunctionReferenceNode> stack : callStacks){
					block.buildListUp(nodeList, stack, inst);
					if(validateList(nodeList, reference, engine)){
						stacks.add(stack);
					}
					
				}
				stackNodes.add(new StackNode(inst, stacks));
			}
		}
		return stackNodes;
	}
	
	private boolean validateList(List<InstructionNode> nodeList, Reference reference, Engine engine) {
		
		for(ReferenceType bt : reference.getBackTrail()){
			boolean matched = false;
			for(long pc : bt.getPcs()){
				InstructionNode inst = engine.getInstructionDecoder().getNode(pc);
				if(nodeList.contains(inst)){
					matched = true;
					int index = nodeList.indexOf(inst);
					nodeList = nodeList.subList(0, index);
				} 
			}
			if(!matched){
				return false;
			}
		}
		return true;
	}

	private BlockNode foundBlock;	
	
	private BlockNode getStartBlock(final InstructionNode inst, FunctionNode fn) {
		foundBlock = null;
		fn.traverse(new Stack<Node>(), new DefaultNodeTraverser() {
			public TraverseStatus foundBlockNode(Node node, Stack<Node> ancestors) {
				if(node.getInstructionNodes().contains(inst)){
					foundBlock = (BlockNode) node;
					return TraverseStatus.ABORT;		
				} else {
					return TraverseStatus.CONTINUE;						
				}
			}
	    });
		assert(foundBlock != null);
		return foundBlock;
	}

}
