package com.xmos.xta.core.engine.analysis.routes;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.core.engine.CallGraph;
import com.xmos.xta.core.engine.FunctionContainer;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.types.ReferenceType;

public class PreanalysisReferenceMatcher {

	public List<StackNode> matchReference(CallGraph callGraph, Reference ref) {
		List<FunctionContainer> matchingContainers = new LinkedList<FunctionContainer>();
		matchingContainers = getBaseContianers(ref, callGraph);
		List<StackNode> stackNodes = new LinkedList<StackNode>();
		for(FunctionContainer matchingContainer : matchingContainers){
			stackNodes.add(new StackNode(matchingContainer.getStartInstruction(), callGraph.getStacks(matchingContainer)));
		}
		return refineStackNodes(stackNodes, ref);
	}
	
	private List<FunctionContainer> getBaseContianers(Reference ref, CallGraph callGraph){
		List<FunctionContainer> fnContainers = new LinkedList<FunctionContainer>();
		for(FunctionContainer fc : callGraph.getFunctionContainers()){
			for(Node node : fc.getFunctionContents()){
				if(node instanceof InstructionNode){
					if(ref.getPcs().contains(((InstructionNode)node).getPC())){
						fnContainers.add(fc);
					}
				}
			}
		}
		return fnContainers;
	}

	public List<StackNode> refineStackNodes(List<StackNode> stackNodes, Reference reference) {



	/*
		for(long pc : reference.getPcs()){
			List<ReferenceType> refList = new LinkedList<ReferenceType>();

			matchLabel(reference, pc, refList);

			Collections.reverse(refList);

			//InstructionNode inst = engine.getInstructionDecoder(nodeNumber, coreNumber).getNode(pc);
			List<Stack<FunctionReferenceNode>> stacks = new LinkedList<Stack<FunctionReferenceNode>>();

			Set<FunctionContainer> containedInFunction = sg.getFunctionsOfInstruction(inst);

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
*/
		return stackNodes;
		
		
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
}
