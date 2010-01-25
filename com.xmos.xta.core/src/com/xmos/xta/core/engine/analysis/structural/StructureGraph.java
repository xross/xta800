package com.xmos.xta.core.engine.analysis.structural;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;

public class StructureGraph {

	private Node m_searchNode;
	
	//for each function there is a set of end nodes.
	private Map<FunctionNode , Set<InstructionNode>> m_functionToEndNodes;
	private Map<FunctionNode , InstructionNode> m_functionToStartNode;
	
	private Map<FunctionNode, Set<FunctionReferenceNode>> m_functionReferencesWithinFunction;
	private Map<FunctionNode, Set<GraphEdge>> m_functionsToFunctionReferences;
	
	public StructureGraph(Map<FunctionNode , Set<InstructionNode>>functionToEndNodes, 
			Map<FunctionNode , InstructionNode>functionToStartNode, 
			Map<FunctionNode, Set<FunctionReferenceNode>> functionReferencesWithinFunction) {
		super();
		m_functionToStartNode = functionToStartNode;
		
		m_functionReferencesWithinFunction = functionReferencesWithinFunction;
		
		m_functionsToFunctionReferences = new HashMap<FunctionNode, Set<GraphEdge>>();
		for(FunctionNode fn : m_functionReferencesWithinFunction.keySet()){
			m_functionsToFunctionReferences.put(fn, new HashSet<GraphEdge>());
		}
		for(FunctionNode fn : m_functionReferencesWithinFunction.keySet()){
			for(FunctionReferenceNode frn : m_functionReferencesWithinFunction.get(fn)){
				if(frn.isKnownFunction()){
					m_functionsToFunctionReferences.get(frn.getFunctionNode()).add(new GraphEdge(frn, fn));
				}
			}
		}
		
		m_functionToEndNodes = functionToEndNodes;
		assert(m_functionToEndNodes != null);
		assert(!m_functionToEndNodes.isEmpty());
	}
	
	//TODO --look at why this is a public method
	public void removeLinks() {	
		for (Node node : getFunctions()) {
		    node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
				public TraverseStatus foundNode(Node node, Stack<Node> ancestors) {
					node.freeMemory();
					return TraverseStatus.CONTINUE;
				}
		    });
		}	
	}

	public Collection<FunctionNode> getFunctions() {
		return m_functionToEndNodes.keySet();
	}
	
	public Set<InstructionNode> getFunctionsEndNodes(FunctionNode fn){ 
		assert(m_functionToEndNodes.containsKey(fn));
		return m_functionToEndNodes.get(fn);
		
	}
	
	public InstructionNode getFunctionsStartNode(FunctionNode fn){		
		assert(m_functionToStartNode.containsKey(fn));
		return m_functionToStartNode.get(fn);
		
	}
	
    public Node getNode(final int nodeId) {
    	m_searchNode = null;
		for (Node node : getFunctions()) {					
		    node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
				public TraverseStatus foundNode(Node node, Stack<Node> ancestors) {
					if (node.getId() == nodeId) {
						m_searchNode = node;
						return TraverseStatus.NO_CHILDREN;
					}
					return TraverseStatus.CONTINUE;
				}
		    });
		}
        return m_searchNode;   		
    }

    public Set<Stack<FunctionReferenceNode>> getFunctionCallStack(FunctionNode fn){
    	
    	System.out.println("looking for stack for " + fn.getFunctionName());
    	Stack<FunctionReferenceNode> currentStack = new Stack<FunctionReferenceNode>();
    	Set<Stack<FunctionReferenceNode>> completeStacks = new HashSet<Stack<FunctionReferenceNode>>();
    	search(fn, currentStack, completeStacks);
		return completeStacks;
    }
    
    private void search(FunctionNode fn, Stack<FunctionReferenceNode> currentStack, Set<Stack<FunctionReferenceNode>> completeStacks){
    	if(m_functionsToFunctionReferences.get(fn).isEmpty()){
    		completeStacks.add(currentStack);
    	} else {
	    	for(GraphEdge ge : m_functionsToFunctionReferences.get(fn)){
	    		Stack<FunctionReferenceNode> nextStack = new Stack<FunctionReferenceNode>();
	    		nextStack.addAll(currentStack);
	    		nextStack.push(ge.getFunctionReferenceNode());
	    		search(ge.getFunctionNode(), nextStack, completeStacks);
	    	}
    	}
    }
    
    class GraphEdge{
    	private FunctionReferenceNode m_frn;
    	private FunctionNode m_fn;
    	
    	public GraphEdge(FunctionReferenceNode frn, FunctionNode fn){
    		m_frn = frn;
    		m_fn = fn;
    		
    	//	System.out.println(fn.getFunctionName() + " -> " +frn.getFunctionNode().getFunctionName());
    	}
    	public FunctionNode getFunctionNode(){
    		return m_fn;
    	}
    	
    	public FunctionReferenceNode getFunctionReferenceNode(){
    		return m_frn;
    	}
    }
}
