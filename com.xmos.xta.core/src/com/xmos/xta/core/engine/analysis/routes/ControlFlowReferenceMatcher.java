package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.xmos.libs.controlflow.ControlFlow;
import com.xmos.libs.controlflow.FunctionControlFlow;
import com.xmos.libs.controlflow.nodes.CfgFunctionReferenceNode;
import com.xmos.libs.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.memorymonitor.MemoryMonitorException;
import com.xmos.libs.util.Pair;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.analysis.structural.CachedFunctionMap;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.types.ReferenceType;
import com.xmos.xta.core.engine.references.types.ReferenceType.Type;

class ControlFlowReferenceMatcher extends ReferenceMatcher {
	private ControlFlow m_controlFlow;
	private CachedFunctionMap m_cachedFunctionMap;
	
	ControlFlowReferenceMatcher(ControlFlow controlFlow, CachedFunctionMap cachedFunctionMap) {
		m_controlFlow = controlFlow;
		m_cachedFunctionMap = cachedFunctionMap;
	}
	
	List<StackNode> matchReference(Reference ref) 
	    throws AbortedException, MemoryMonitorException {
		
		List<StackNode> stackNodes = new LinkedList<StackNode>();
		for (FunctionControlFlow container : getBaseContianers(ref)) {			
			CfgInstructionNode instruction = getInstructionFromContainer(container, ref);
			List<Stack<CfgFunctionReferenceNode>> nodeStacks = m_controlFlow.getStacks(container);
	
			stackNodes.add(new StackNode(instruction, nodeStacks)); 
		}
		return refineStackNodes(stackNodes, ref);
	}

	private List<FunctionControlFlow> getBaseContianers(Reference ref) {
		List<FunctionControlFlow> fnContainers = new LinkedList<FunctionControlFlow>();
		for (FunctionControlFlow fc : m_controlFlow.getFunctionControlFlows()) {
			
			if (ref.getBase().getType() == Type.FUNCTION) {
				if (ref.getPcs().contains(fc.getStartInstructionNode().getPc())) {
					fnContainers.add(fc);
				}
			} else {
				for (long pc : ref.getPcs()) {
					if (fc.containsPc(pc)) {
						fnContainers.add(fc);
						break;
					}
				}
			}
		}
		return fnContainers;
	}

	private CfgInstructionNode getInstructionFromContainer(FunctionControlFlow fc, Reference ref){
		for (CfgInstructionNode inst : fc.getInstructionNodes()) {
			if (ref.getPcs().contains(inst.getPc()) && ! inst.getInstruction().isFnop()) {
				return inst;
			}
		}
		assert(false);
		return null;
	}
	
	public List<StackNode> refineStackNodes(List<StackNode> stackNodes, Reference reference) 
	    throws AbortedException, MemoryMonitorException {
		
		List<StackNode> refinedStackNodes = new LinkedList<StackNode>();
	
		for(long pc : reference.getPcs()){
			List<ReferenceType> refList = new LinkedList<ReferenceType>();
			
			matchLabel(reference, pc, refList);
			
			List<Stack<CfgFunctionReferenceNode>> stacks = new LinkedList<Stack<CfgFunctionReferenceNode>>();
			List<FunctionControlFlow> containedInFunction = m_controlFlow.getFunctionContaining(pc);

			for (FunctionControlFlow fn : containedInFunction) {
				CfgInstructionNode inst = fn.getInstructionNode(pc);
				
				if (reference.getBase().getType() == Type.FUNCTION && fn.getStartInstructionNode() != inst) {
					continue;
				}

				List<Stack<CfgFunctionReferenceNode>> callStacks = m_controlFlow.getStacks(fn);
				
				for(Stack<CfgFunctionReferenceNode> stack : callStacks){
					Stack<CfgFunctionReferenceNode> copy = new Stack<CfgFunctionReferenceNode>();
					copy.addAll(stack);
					List<ReferenceType> matchingList = new LinkedList<ReferenceType>(refList);
					
					long currentPc = pc;
					CfgFunctionReferenceNode frn = copy.pop();
					while(!matchingList.isEmpty()){
						Pair<Boolean, Long> res = fnRefContainsReference(frn, currentPc, matchingList, stack);
						while(res.first){
							ReferenceType r = matchingList.get(matchingList.size() -1);
							
							currentPc = res.second;
							matchingList.remove(r);
							
							res = fnRefContainsReference(frn, currentPc, matchingList, stack);
						} 
						currentPc = frn.getFunctionCallPc();
						if(copy.isEmpty())
							break;
						frn = copy.pop();
						
					}
					
					if (matchingList.isEmpty()){
						stacks.add(stack);
					}
				}
				if (!stacks.isEmpty()){
					refinedStackNodes.add(new StackNode(inst, stacks));
				}
			}
		}
		return refinedStackNodes;
	}
	
	private Pair<Boolean, Long> fnRefContainsReference(CfgFunctionReferenceNode frn, Long currentPc, 
			List<ReferenceType> refList, Stack<CfgFunctionReferenceNode> stack) 
		throws AbortedException, MemoryMonitorException {

		if (refList.isEmpty()) 
			return new Pair<Boolean, Long>(false, (long)0);
		
		ReferenceType r = refList.get(refList.size() -1);
		
		if (r.getType() == ReferenceType.Type.CALL) {
			if (r.getPcs().contains(frn.getFunctionCallPc())) {
				return new Pair<Boolean, Long>(true, (long)0);
			}
		} else if (r.getType() == ReferenceType.Type.ENDPOINT) {
			return matchRefPc(frn, currentPc, r, stack);
			
		} else if (r.getType() == ReferenceType.Type.ERROR) {
			return new Pair<Boolean, Long>(false, (long)0);
			
		} else if (r.getType() == ReferenceType.Type.EVERYTHING) {
			return new Pair<Boolean, Long>(true, currentPc);
			
		} else if (r.getType() == ReferenceType.Type.FUNCTION) {
			if (r.getPcs().contains(frn.getFunctionStartPc())) {
				return new Pair<Boolean, Long>(true, frn.getFunctionStartPc());
			}
		} else if (r.getType() == ReferenceType.Type.LABEL) {
			return matchRefPc(frn, currentPc, r, stack);
			
		} else if (r.getType() == ReferenceType.Type.SRCLABEL) {
			return matchRefPc(frn, currentPc, r, stack);
			
		} else {
			assert(false); 
		}
		return new Pair<Boolean, Long>(false, (long)0);
	}

	private Pair<Boolean, Long> matchRefPc(CfgFunctionReferenceNode frn, Long latestPc, 
			ReferenceType r, Stack<CfgFunctionReferenceNode> stack) 
		throws AbortedException, MemoryMonitorException {

		boolean found = false;

		if (latestPc == 0x0) {
			for (long pc : r.getPcs()) {
				latestPc = pc;
				found = true;
			}
		} else {		
			Node functionNode =  m_cachedFunctionMap.getCachedFunction(frn.getFunctionControlFlow());
			InstructionNode inst = getInstructionNode(functionNode, latestPc);						
			List<Node> nodeList = new LinkedList<Node>();
			inst.buildListUp(nodeList, null);

			for (long pc : r.getPcs()) {
				int index = getIndexOfInst(nodeList, pc);
				if (index != -1) {
					nodeList = nodeList.subList(0, index);
					latestPc = pc;
					found = true;
				}
			}
		}
		return new Pair<Boolean, Long>(found, latestPc);
	}
	
	private int getIndexOfInst(List<Node> nodeList, long pc) {
		for (int i = nodeList.size()-2; i >= 0;i--) {
			if (nodeList.get(i) instanceof InstructionNode){
				InstructionNode inst = (InstructionNode)nodeList.get(i);
				if (inst.getPC() == pc){
					return i;
				}
			}
		}
		return -1;
	}

	private InstructionNode getInstructionNode(Node node, final long pc) {
		final List<InstructionNode> instructionNodes = new ArrayList<InstructionNode>();
		node.traverse(new DefaultNodeTraverser() {
			public TraverseStatus foundInstructionNode(Node node) {
				InstructionNode instructionNode = (InstructionNode)node;
				
				if (instructionNode.getPC() == pc) {
					instructionNodes.add((InstructionNode)node); 
					return TraverseStatus.ABORT;					
				}
				return TraverseStatus.CONTINUE;
			}
		});
		assert(instructionNodes.size() == 1);
		return instructionNodes.iterator().next();
	}
}
