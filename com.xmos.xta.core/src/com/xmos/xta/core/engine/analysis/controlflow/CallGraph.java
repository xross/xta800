package com.xmos.xta.core.engine.analysis.controlflow;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.xmos.libs.util.IteratableMap;
import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.NonIteratableMap;
import com.xmos.libs.util.NonIteratableSet;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;

public class CallGraph {
	
	private Set<FunctionContainer> m_functionContainers;
	private Map<FunctionContainer, Set<FunctionReferenceNode>> m_fnRefToCallees;
	private Set<FunctionReferenceNode> m_uncalledFunctions;
	private Map<FunctionReferenceNode, FunctionContainer> m_containingFunction;
	
	public CallGraph(Set<FunctionContainer> functionContainers){
		m_functionContainers = functionContainers;
		m_fnRefToCallees = new IteratableMap<FunctionContainer, Set<FunctionReferenceNode>>();
		m_uncalledFunctions = new NonIteratableSet<FunctionReferenceNode>();
		m_containingFunction = new NonIteratableMap<FunctionReferenceNode, FunctionContainer>();
		makeCalleeGraph(functionContainers);
	}

	private void dumpCallGraph(){
		System.out.println("digraph {");
		for(FunctionContainer fc : m_functionContainers){
			
			for(FunctionReferenceNode frn : fc.getFunctionReferenceNodes()){
				if(frn.isKnownFunction()){
					System.out.println(fc.getFunctionName() + " -> " + frn.getFunctionName() + ";");
				}
			}
		}
		System.out.println("}");
	}
	
	
	private void makeCalleeGraph(Set<FunctionContainer> functionContainers) {

		for(FunctionContainer fc: functionContainers){
			
			m_fnRefToCallees.put(fc, new IteratableSet<FunctionReferenceNode>());
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
				m_fnRefToCallees.put(fc, new IteratableSet<FunctionReferenceNode>());
				m_fnRefToCallees.get(fc).add(uncalledFunctionRef);
				//m_containingFunction.put(uncalledFunctionRef, fc);
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
		
		if(fc != null && m_fnRefToCallees.containsKey(fc)){
			for(FunctionReferenceNode previous : m_fnRefToCallees.get(fc)){
				
				if(!stack.contains(previous)){
					stack.push(previous);
					FunctionContainer prev = m_containingFunction.get(previous);
					//System.out.println("where is " + previous + "? -> " + prev);
					
					climb(prev, stack, foundStacks);
					stack.pop();
				}
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
			if(fc.containsPc(pc)){
				found.add(fc);
			}
		}
		return found;
	}
	
}
