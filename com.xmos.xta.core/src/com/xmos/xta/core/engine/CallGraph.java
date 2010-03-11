package com.xmos.xta.core.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;

public class CallGraph {
	
	private Set<FunctionContainer> m_functionContainers;
	private Map<FunctionContainer, Set<FunctionReferenceNode>> m_fnRefToCallees;
	private Set<FunctionReferenceNode> m_uncalledFunctions;
	private Map<FunctionReferenceNode, FunctionContainer> m_containingFunction;
	
	public CallGraph(Set<FunctionContainer> functionContainers){
		m_functionContainers = functionContainers;
		m_fnRefToCallees = new HashMap<FunctionContainer, Set<FunctionReferenceNode>>();
		m_uncalledFunctions = new TreeSet<FunctionReferenceNode>();
		m_containingFunction = new HashMap<FunctionReferenceNode, FunctionContainer>();
		makeCalleeGraph(functionContainers);
	}

	private void makeCalleeGraph(Set<FunctionContainer> functionContainers) {

		for(FunctionContainer fc: functionContainers){
			
			m_fnRefToCallees.put(fc, new TreeSet<FunctionReferenceNode>());
		}
		
		for(FunctionContainer fc: functionContainers){
			for(FunctionReferenceNode containedRef : fc.getFunctionReferenceNodes()){
				m_containingFunction.put(containedRef, fc);
				FunctionContainer containedFnCont = containedRef.getFunctionContainer();
				if(containedFnCont != null){
					m_fnRefToCallees.get(containedFnCont).add(containedRef);
				}
			}
		}
		for(FunctionContainer fc : m_fnRefToCallees.keySet()){
			if(m_fnRefToCallees.get(fc).isEmpty()){
				FunctionReferenceNode uncalledFunctionRef = new FunctionReferenceNode();
				uncalledFunctionRef.setFunction(fc);
				m_uncalledFunctions.add(uncalledFunctionRef);
				m_fnRefToCallees.put(fc, new TreeSet<FunctionReferenceNode>());
				m_fnRefToCallees.get(fc).add(uncalledFunctionRef);
			}
		}
	}

	public Set<FunctionContainer> getFunctionContainers() {
		return m_functionContainers;
	}

	public List<Stack<FunctionReferenceNode>> getStacks(FunctionContainer fc){
		Stack<FunctionReferenceNode> stack = new Stack<FunctionReferenceNode>();
		List<Stack<FunctionReferenceNode>> foundStacks = new LinkedList<Stack<FunctionReferenceNode>>();
		climb(fc, stack, foundStacks);
		return foundStacks;
	}

	private void climb(FunctionContainer fc,
			Stack<FunctionReferenceNode> stack,
			List<Stack<FunctionReferenceNode>> foundStacks) {
		if(m_fnRefToCallees.containsKey(fc)){
			for(FunctionReferenceNode previous : m_fnRefToCallees.get(fc)){
				stack.push(previous);
				climb(m_containingFunction.get(previous), stack, foundStacks);
				stack.pop();
			}
		} else {
			Stack<FunctionReferenceNode> copy = new Stack<FunctionReferenceNode>();
			copy.addAll(stack);
			Collections.reverse(copy);
			foundStacks.add(copy);
		}
		
	}

	public List<FunctionContainer> getFunctionContaining(long pc) {
		List<FunctionContainer> found = new LinkedList<FunctionContainer>();
		for(FunctionContainer fc : m_functionContainers){
			for(InstructionNode inst : fc.getFunctionInstructionNodes()){
				if(inst.getPC() == pc){
					found.add(fc);
					break;
				}
			}
		}
		return found;
	}
	
}
