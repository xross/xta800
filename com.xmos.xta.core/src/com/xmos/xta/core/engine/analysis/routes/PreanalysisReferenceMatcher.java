package com.xmos.xta.core.engine.analysis.routes;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.xmos.xta.core.engine.CallGraph;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.FunctionContainer;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.types.ReferenceType;
import com.xmos.xta.core.engine.references.types.ReferenceType.Type;

public class PreanalysisReferenceMatcher {

	private static final boolean debug = false;
	
	public List<StackNode> matchReference(CallGraph callGraph, Reference ref, Engine engine) {
		
		if (debug) System.out.println("####### begin reference match " + ref + " ###########");
		List<FunctionContainer> matchingContainers = new LinkedList<FunctionContainer>();
		matchingContainers = getBaseContianers(ref, callGraph);
		List<StackNode> stackNodes = new LinkedList<StackNode>();
		for(FunctionContainer matchingContainer : matchingContainers){
			
			stackNodes.add(new StackNode(getInstructionFromContainer(matchingContainer,ref), callGraph.getStacks(matchingContainer)));
		}
		//return stackNodes;
		
		List<StackNode> s = refineStackNodes(stackNodes, ref, callGraph, engine);
		if (debug) System.out.println("####### end reference match " + ref + " ###########");
		return s;
	}
	
	private InstructionNode getInstructionFromContainer(FunctionContainer fc, Reference ref){
		for(InstructionNode inst : fc.getFunctionInstructionNodes()){
			if(ref.getPcs().contains(inst.getPC())){
				return inst;
			}
		}
		assert(false);
		return null;
	}
	
	private List<FunctionContainer> getBaseContianers(Reference ref, CallGraph callGraph){
		List<FunctionContainer> fnContainers = new LinkedList<FunctionContainer>();
		
		for(FunctionContainer fc : callGraph.getFunctionContainers()){
			
			if(ref.getBase().getType() == Type.FUNCTION){
				if(ref.getPcs().contains(fc.getStartInstruction().getPC())){
					fnContainers.add(fc);
				}
			} else {
				for(InstructionNode inst : fc.getFunctionInstructionNodes()){
					if(ref.getPcs().contains(inst.getPC())){
						fnContainers.add(fc);
					}
				}
			}
		}
		return fnContainers;
	}

	public List<StackNode> refineStackNodes(List<StackNode> stackNodes, Reference reference, CallGraph callGraph, Engine engine) {
		List<StackNode> refinedStackNodes = new LinkedList<StackNode>();
		if (debug) System.out.println("\nMatching reference: " + reference);

	
		for(long pc : reference.getPcs()){
			List<ReferenceType> refList = new LinkedList<ReferenceType>();
			
			if (debug) System.out.println("Matching PC: 0x" + Long.toHexString(pc));
			
			matchLabel(reference, pc, refList);
			
			Collections.reverse(refList);
			
			
			if (debug) {
				System.out.print("The refList contains: ");
				for(ReferenceType r : refList){
					System.out.print( r.getType() + "(" + r.getRef() );
					for(long refPc : r.getPcs()){
						System.out.print(", 0x" + Long.toHexString(refPc));
					}
					System.out.print(") ");
				}
				System.out.println();
			}

			List<Stack<FunctionReferenceNode>> stacks = new LinkedList<Stack<FunctionReferenceNode>>();

			List<FunctionContainer> containedInFunction = callGraph.getFunctionContaining(pc);

			for(FunctionContainer fn : containedInFunction){
				InstructionNode inst = fn.getInstruction(pc);
				
				if(reference.getBase().getType() == Type.FUNCTION && fn.getStartInstruction() != inst){
					//TODO tidy this up
					continue;
				}
				
				
				
				
				List<Stack<FunctionReferenceNode>> callStacks = callGraph.getStacks(fn);

				
				for(Stack<FunctionReferenceNode> stack : callStacks){
					if (debug) {
						System.out.println("want to find out if stack " + stack +" matches the reference ");
					}
					Stack<FunctionReferenceNode> copy = new Stack<FunctionReferenceNode>();
					copy.addAll(stack);
					List<ReferenceType> matchingList = new LinkedList<ReferenceType>(refList);
					
					
					while(!copy.isEmpty()){
						FunctionReferenceNode frn = copy.pop();
						Long currentPc = new Long(0x0);
						if (debug) System.out.println("checking function " + frn.getFunctionName());
						while(fnRefContainsReference(frn, currentPc, matchingList, engine)){
							ReferenceType r = matchingList.get(matchingList.size() -1);
							if (debug) {
								System.out.print("found ");
								System.out.print( r.getType() + "(" + r.getRef() );
								for(long refPc : r.getPcs()){
									System.out.print(", 0x" + Long.toHexString(refPc));
								}
								System.out.print(") in the function");
							}
							matchingList.remove(r);
						} 
						
					}
					
					if(matchingList.isEmpty()){
						if (debug) System.out.println("success ");
						stacks.add(stack);
					} else {
						if (debug) System.out.println("fail ");
					}
				}
				if(!stacks.isEmpty()){
					refinedStackNodes.add(new StackNode(inst, stacks));
				}
			}
		}

		return refinedStackNodes;
	}
	
	private boolean fnRefContainsReference(FunctionReferenceNode frn, Long currentPc, List<ReferenceType> refList, Engine engine ) {
		
		if(refList.isEmpty()) return false;
		ReferenceType r = refList.get(refList.size() -1);
		
		if(r.getType() == ReferenceType.Type.CALL){
			if(r.getPcs().contains(frn.getFunctionCallPc())){
				return true;
			}
		} else if(r.getType() == ReferenceType.Type.ENDPOINT){
			return matchRefPc(frn, engine, currentPc, r);
		} else if(r.getType() == ReferenceType.Type.ERROR){
			return false;
		} else if(r.getType() == ReferenceType.Type.EVERYTHING){
			return true;
		} else if(r.getType() == ReferenceType.Type.FUNCTION){
			if(r.getPcs().contains(frn.getFunctionStartPc())){
				return true;
			}
		} else if(r.getType() == ReferenceType.Type.LABEL){
			return matchRefPc(frn, engine, currentPc, r);
		} else if(r.getType() == ReferenceType.Type.PC){
			return matchRefPc(frn, engine, currentPc, r);
		} else if(r.getType() == ReferenceType.Type.SRCLABEL){
			return matchRefPc(frn, engine, currentPc, r);
		} else assert(false); 
		
		
		return false;
	}

	//this matches references that resolve to a set of pcs
	private boolean matchRefPc(FunctionReferenceNode frn, Engine engine,
			Long latestPc, ReferenceType r) {
		FunctionContainer fc = frn.getFunctionContainer();
		Node node = fc.getCachedFunction(engine);
		setParent(node);
		boolean found = false;
		
		if(latestPc == 0x0){
			for(long pc : r.getPcs()){
				latestPc = pc;
			}
		} else {
		
			InstructionNode inst = fc.getInstruction(latestPc);
			List<Node> nodeList = new LinkedList<Node>();
			inst.buildListUp(nodeList, null);
			
			for(long pc : r.getPcs()){
				int index = getIndexOfInst(nodeList, pc);
				if(index != -1){
					nodeList = nodeList.subList(0, index);
					latestPc = pc;
					found = true;
				}
			}
		}
		return found;
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

	private void setParent(Node node) {
		for (Node child : node.iChildren()){
			child.setParent(node);
			setParent(child);
		}
	}
}
