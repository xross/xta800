package com.xmos.xta.core.engine.analysis;

import java.util.Collections;
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
import com.xmos.xta.core.engine.references.types.ReferenceType.Type;

public class ReferenceMatcher {

	public List<StackNode> getStackNode(Engine engine, Reference reference) {


		List<StackNode> stackNodes = new LinkedList<StackNode>();
		StructureGraph sg = engine.getStructureGraph();

		for(long pc : reference.getPcs()){
			List<ReferenceType> refList = new LinkedList<ReferenceType>();

			matchLabel(reference, pc, refList);

			Collections.reverse(refList);

			InstructionNode inst = engine.getInstructionDecoder().getNode(pc);
			List<Stack<FunctionReferenceNode>> stacks = new LinkedList<Stack<FunctionReferenceNode>>();

			Set<FunctionNode> containedInFunction = sg.getFunctionsOfInstruction(inst);

			for(FunctionNode fn : containedInFunction){
				List<Stack<FunctionReferenceNode>> callStacks = sg.getFunctionCallStack(fn);
				BlockNode block = getStartBlock(inst, fn);
				List<Node> nodeList = new LinkedList<Node>();
				for(Stack<FunctionReferenceNode> stack : callStacks){
					block.buildListUp(nodeList, stack, inst);
					if(validateList(nodeList, refList)){
						stacks.add(stack);
					}
					nodeList.clear();
				}
				if(!stacks.isEmpty()){
					stackNodes.add(new StackNode(inst, stacks));
				}
			}
		}

		return stackNodes;
	}

	public Set<Node> getNodesMatchingReference(Node node, final Reference reference) {
		Set<Node> matches = new TreeSet<Node>();
		assert(reference.getBase().getType() != Type.EVERYTHING);
		for(long pc : reference.getPcs()){
			final Set<Node> insts = new TreeSet<Node>();
			node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
		    	public TraverseStatus foundNode(Node node, Stack<Node> ancestors) {
		    		if(node instanceof InstructionNode){
		    			InstructionNode inst = (InstructionNode)node;
		    			if(!inst.getXtaInstruction().isFnop() && reference.getPcs().contains(inst.getPC())){
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
						stack.push(((FunctionReferenceNode)current).getAlias());
					}
					current = current.getParent();
				}
				List<Node> nodeList = new LinkedList<Node>();
	
				inst.buildListUp(nodeList, stack, null);
	
				List<ReferenceType> refList = new LinkedList<ReferenceType>();
	
				matchLabel(reference, pc, refList);
	
				if(validateList(nodeList, refList)){
					matches.add(inst);
				}
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
			List<Node> nodeList = new LinkedList<Node>();
			function.buildListUp(nodeList, stack, null);
			List<ReferenceType> refList = new LinkedList<ReferenceType>(reference.getBackTrail());

			if(validateList(nodeList, refList)){
				matches.add(function);
			}
		}

		return matches;
	}

	private void matchLabel(Reference reference, long pc, List<ReferenceType> refList){
		List<String> backTrailForPc = new LinkedList<String>(reference.getPcBackTrail(pc));
		for(int i=reference.getBackTrail().size()-1; i>=0; i--){
			ReferenceType refType = reference.getBackTrail().get(i);
			int index = getIndexOf(backTrailForPc, refType.getRef());
			if(index != -1){
				backTrailForPc = backTrailForPc.subList(0, index);
			} else {
				refList.add(0, refType);
			}
		}
	}


	private int getIndexOf(List<String> backTrailForPc, String string){
		int i=0;
		for(String backTrail : backTrailForPc){
			int rightBra = backTrail.indexOf(")");
			int leftBra = backTrail.indexOf("(");

			if(rightBra != -1 && leftBra != -1){
				String bracketedString = backTrail.substring(leftBra+1, rightBra);
				String [] parts = bracketedString.split(",");
				for(String part : parts){
					if(part.equals(string)){
						return i;
					}
				}
			}

			if(backTrail.equals(string)){
				return i;
			}

			i++;
		}


		return -1;
	}

	private boolean validateList(List<Node> nodeList, List<ReferenceType> references) {
		int validEndPoints = 0;

		for(ReferenceType bt : references){

			boolean matched = false;
			boolean isFunctionRef = false;
			if(bt.getType() == Type.CALL){
				isFunctionRef = true;
			}

			if(!bt.getResolved()) return false;

			for(long pc : bt.getPcs()){
				validEndPoints++;
				int index = getIndexOfInst(nodeList, pc);
				if(index != -1){
					if(isFunctionRef){
						if(nodeList.get(index + 1) instanceof FunctionReferenceNode){
							matched = true;
							nodeList = nodeList.subList(0, index+1);
						}
					} else {
						matched = true;
						nodeList = nodeList.subList(0, index);
					}
				}
			}
			if(validEndPoints == 0){
				return true;
			}

			if(!matched){
				return false;
			}
		}
		return true;
	}

	private int getIndexOfInst(List<Node> nodeList, long pc) {
		for(int i=nodeList.size()-2; i > 0;i--){
			if(nodeList.get(i) instanceof InstructionNode){
				InstructionNode inst = (InstructionNode)nodeList.get(i);
				if(inst.getPC() == pc){
					return i;
				}
			}
		}
		return -1;
	}

	private BlockNode foundBlock;

	private BlockNode getStartBlock(final InstructionNode inst, FunctionNode fn) {
		foundBlock = null;
		fn.traverse(new Stack<Node>(), new DefaultNodeTraverser() {
			public TraverseStatus foundBlockNode(Node node, Stack<Node> ancestors) {
				BlockNode block = (BlockNode)node;
				if(block.getInstructionList().contains(inst)){
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
