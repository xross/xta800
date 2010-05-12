package com.xmos.xta.core.engine.analysis.routes;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.xmos.libs.util.Pair;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.controlflow.CallGraph;
import com.xmos.xta.core.engine.analysis.controlflow.FunctionContainer;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.types.ReferenceType;
import com.xmos.xta.core.engine.references.types.ReferenceType.Type;

public class PreanalysisReferenceMatcher extends ReferenceMatcher {

	private static final boolean debug = false;
	
	public List<StackNode> matchReference(CallGraph callGraph, Reference ref, Engine engine) {
		
		if (debug) System.out.println("####### begin reference match " + ref + " ###########");
		List<StackNode> stackNodes = new LinkedList<StackNode>();
		for(FunctionContainer matchingContainer : getBaseContianers(ref, callGraph)){
			
			stackNodes.add(new StackNode(getInstructionFromContainer(matchingContainer,ref), callGraph.getStacks(matchingContainer)));
		}
		
		List<StackNode> s = refineStackNodes(stackNodes, ref, callGraph, engine);
		if (debug) System.out.println("####### end reference match " + ref + " ###########");
		return s;
	}
	
	private InstructionNode getInstructionFromContainer(FunctionContainer fc, Reference ref){
		for(InstructionNode inst : fc.getFunctionInstructionNodes()){
			if(ref.getPcs().contains(inst.getPC()) && ! inst.getXtaInstruction().isFnop()){
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
				for(long pc : ref.getPcs()){
					if(fc.containsPc(pc)){
						fnContainers.add(fc);
						break;
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
			
			//Collections.reverse(refList);
			
			
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
					
					long currentPc = pc;
					FunctionReferenceNode frn = copy.pop();
					while(!matchingList.isEmpty()){
						if (debug) System.out.println("checking function " + frn.getFunctionName() + " from pc 0x" + Long.toHexString(currentPc));
						Pair<Boolean, Long> res = fnRefContainsReference(frn, currentPc, matchingList, engine, stack);
						while(res.first){
							ReferenceType r = matchingList.get(matchingList.size() -1);
							
							if (debug) {
								System.out.print("found ");
								System.out.print( r.getType() + "(" + r.getRef() );
								for(long refPc : r.getPcs()){
									System.out.print(", 0x" + Long.toHexString(refPc));
								}
								System.out.print(") in the function " + frn.getFunctionName() + "\n");
							}
							currentPc = res.second;
							matchingList.remove(r);
							
							res = fnRefContainsReference(frn, currentPc, matchingList, engine, stack);
						} 
						currentPc = frn.getFunctionCallPc();
						if(copy.isEmpty())
							break;
						frn = copy.pop();
						
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
	
	private Pair<Boolean, Long> fnRefContainsReference(FunctionReferenceNode frn, Long currentPc, 
			List<ReferenceType> refList, Engine engine, Stack<FunctionReferenceNode> stack ) {
		
		if(refList.isEmpty()) return new Pair<Boolean, Long>(false, (long)0);
		
		ReferenceType r = refList.get(refList.size() -1);
		
		if(r.getType() == ReferenceType.Type.CALL){
			if(r.getPcs().contains(frn.getFunctionCallPc())){
				return new Pair<Boolean, Long>(true, (long)0);
			}
		} else if(r.getType() == ReferenceType.Type.ENDPOINT){
			return matchRefPc(frn, engine, currentPc, r, stack);
		} else if(r.getType() == ReferenceType.Type.ERROR){
			return new Pair<Boolean, Long>(false, (long)0);
		} else if(r.getType() == ReferenceType.Type.EVERYTHING){
			return new Pair<Boolean, Long>(true, currentPc);
		} else if(r.getType() == ReferenceType.Type.FUNCTION){
			if(r.getPcs().contains(frn.getFunctionStartPc())){
				return new Pair<Boolean, Long>(true, frn.getFunctionStartPc());
			}
		} else if(r.getType() == ReferenceType.Type.LABEL){
			return matchRefPc(frn, engine, currentPc, r, stack);
		} else if(r.getType() == ReferenceType.Type.SRCLABEL){
			return matchRefPc(frn, engine, currentPc, r, stack);
		} else assert(false); 
		
		
		return new Pair<Boolean, Long>(false, (long)0);
	}

	//this matches references that resolve to a set of pcs
	private Pair<Boolean, Long> matchRefPc(FunctionReferenceNode frn, Engine engine, Long latestPc, 
			ReferenceType r, Stack<FunctionReferenceNode> stack) {
		FunctionContainer fc = frn.getFunctionContainer();
		Node node = fc.getCachedFunction(engine);
		setParent(node);
		boolean found = false;
		
		if(latestPc == 0x0){
			for(long pc : r.getPcs()){
				latestPc = pc;
				found = true;
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
		return new Pair<Boolean, Long>(found, latestPc);
	}
	
	private int getIndexOfInst(List<Node> nodeList, long pc) {
		for(int i=nodeList.size()-2; i >= 0;i--){
			if(nodeList.get(i) instanceof InstructionNode){
				InstructionNode inst = (InstructionNode)nodeList.get(i);
				if(inst.getPC() == pc){
					return i;
				}
			}
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
