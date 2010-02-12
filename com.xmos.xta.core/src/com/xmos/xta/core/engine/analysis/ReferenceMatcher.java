package com.xmos.xta.core.engine.analysis;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.routes.StackNode;
import com.xmos.xta.core.engine.analysis.structural.StructureGraph;
import com.xmos.xta.core.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.types.ReferenceType;

public class ReferenceMatcher {

	public List<StackNode> getStackNode(Engine engine, Reference reference) {
		List<StackNode> stackNodes = new LinkedList<StackNode>();
		StructureGraph sg = engine.getStructureGraph();
		
		for(long pc : reference.getPcs()){
		
			InstructionNode inst = engine.getInstructionDecoder().getNode(pc);
			List<Stack<FunctionReferenceNode>> stacks = new LinkedList<Stack<FunctionReferenceNode>>();
			
			Set<FunctionNode> containedInFunction = sg.getFunctionsOfInstruction(inst);
			
			for(FunctionNode fn : containedInFunction){
				List<Stack<FunctionReferenceNode>> callStacks = sg.getFunctionCallStack(fn);
				BlockNode block = getStartBlock(inst, fn);
				List<InstructionNode> nodeList = new LinkedList<InstructionNode>();
				for(Stack<FunctionReferenceNode> stack : callStacks){
					block.buildListUp(nodeList, stack, inst);
					if(validateList(nodeList, reference)){
						stacks.add(stack);
					}
					
				}
				stackNodes.add(new StackNode(inst, stacks));
			}
		}
		return stackNodes;
	}
	
	public Set<Node> getNodesMatchingReference(Node node, final Reference reference) {
		Set<Node> matches = new TreeSet<Node>();
		final Set<Node> insts = new TreeSet<Node>();
		node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
	    	public TraverseStatus foundNode(Node node, Stack<Node> ancestors) {
	    		if(node instanceof InstructionNode){
	    			InstructionNode inst = (InstructionNode)node;
	    			if(reference.getPcs().contains(inst.getPC())){
	    				insts.add(inst);
	    			}
	    		}
	        	return TraverseStatus.CONTINUE;
	    	}
	    });
		for(Node inst : insts){
			Stack<FunctionReferenceNode> stack = new Stack<FunctionReferenceNode>();
			Node current = inst;
			while(current.getParent() != null){
				if(current instanceof FunctionReferenceNode){
					stack.push((FunctionReferenceNode)current);
				}
				current = current.getParent();
			}
			List<InstructionNode> nodeList = new LinkedList<InstructionNode>();
			inst.buildListUp(nodeList, stack, null);
			if(validateList(nodeList, reference)){
				matches.add(inst);
			}
		}
		
		return matches;
	}
	
	public Set<Node>  getFunctionNodesMatchingReference(Node node, final Reference reference) {
		
		Set<Node> matches = new TreeSet<Node>();
		final Set<Node> functions = new TreeSet<Node>();
		node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
	    	public TraverseStatus foundNode(Node node, Stack<Node> ancestors) {
	    		if(node instanceof FunctionNode){
	    			FunctionNode function = (FunctionNode)node;
	    			if(reference.getRef().equals(function.getFunctionName())){
	    				functions.add(function);
	    			}
	    		}
	        	return TraverseStatus.CONTINUE;
	    	}
	    });
		
		
		for(Node function : functions){
			Stack<FunctionReferenceNode> stack = new Stack<FunctionReferenceNode>();
			Node current = function;
			while(current.getParent() != null){
				if(current instanceof FunctionReferenceNode){
					stack.push((FunctionReferenceNode)current);
				}
				current = current.getParent();
			}
			List<InstructionNode> nodeList = new LinkedList<InstructionNode>();
			function.buildListUp(nodeList, stack, null);
			if(validateList(nodeList, reference)){
				matches.add(function);
			}
		}
		
		return matches;
	}
	
	private boolean validateList(List<InstructionNode> nodeList, Reference reference) {
		
		for(ReferenceType bt : reference.getBackTrail()){
			boolean matched = false;
			for(long pc : bt.getPcs()){
				int index = getIndexOfInst(nodeList, pc);
				if(index != -1){
					matched = true;
					nodeList = nodeList.subList(0, index);
				} 
			}
			if(!matched){
				return false;
			}
		}
		return true;
	}

	private int getIndexOfInst(List<InstructionNode> nodeList, long pc) {
		for(int i=nodeList.size()-1; i > 0;i--){
			if(nodeList.get(i).getPC() == pc){
				return i;
			}
		}
		return -1;
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
