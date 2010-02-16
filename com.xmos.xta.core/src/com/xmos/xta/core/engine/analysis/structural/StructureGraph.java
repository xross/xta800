package com.xmos.xta.core.engine.analysis.structural;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

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
	private Map<FunctionNode, Set<InstructionNode>> m_instructionWithinFunction;
	
	private Map<FunctionNode, FunctionReferenceNode> m_uncalledFunctionReference;
	
	public StructureGraph(Map<FunctionNode , Set<InstructionNode>>functionToEndNodes, 
			Map<FunctionNode , InstructionNode>functionToStartNode, 
			Map<FunctionNode, Set<FunctionReferenceNode>> functionReferencesWithinFunction,
			Map<FunctionNode, Set<InstructionNode>> instructionWithinFunction) {
		
		m_functionToStartNode = functionToStartNode;
		m_uncalledFunctionReference = new TreeMap<FunctionNode, FunctionReferenceNode>();
		m_functionReferencesWithinFunction = functionReferencesWithinFunction;
		
		Set<FunctionNode> uncalledFunctions = new TreeSet<FunctionNode>(m_functionReferencesWithinFunction.keySet());
		for(Set<FunctionReferenceNode> frns : m_functionReferencesWithinFunction.values()){
			for(FunctionReferenceNode frn : frns){
				if(frn.isKnownFunction()){
					uncalledFunctions.remove(frn.getFunctionNode());
				}
			}
		}
		
		
		m_instructionWithinFunction = instructionWithinFunction;
		
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

		for(FunctionNode uncalledFunction : uncalledFunctions){
			FunctionReferenceNode frn = new FunctionReferenceNode();
			frn.setFunction(uncalledFunction.getFunctionName(), uncalledFunction);
			m_uncalledFunctionReference.put(uncalledFunction, frn);
		}
		
		
		
		m_functionToEndNodes = functionToEndNodes;
		assert(m_functionToEndNodes != null);
		assert(!m_functionToEndNodes.isEmpty());
	}

	public Collection<FunctionNode> getFunctions() {
		return m_functionToEndNodes.keySet();
	}
	
	public Set<InstructionNode> getFunctionsEndNodes(FunctionNode fn){ 
		assert(m_functionToEndNodes.containsKey(fn));
		return m_functionToEndNodes.get(fn);
	}
	
	public Set<InstructionNode> getInstructionWithinFunction(FunctionNode fn){
		assert(m_instructionWithinFunction.containsKey(fn));
		return m_instructionWithinFunction.get(fn);
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
    
    public List<Stack<FunctionReferenceNode>> getFunctionCallStack(FunctionNode fn){
    	Stack<FunctionReferenceNode> currentStack = new Stack<FunctionReferenceNode>();
    	List<Stack<FunctionReferenceNode>> completeStacks = new LinkedList<Stack<FunctionReferenceNode>>();
    	search(fn, currentStack, completeStacks);
		return completeStacks;
    }
    
    private void search(FunctionNode fn, Stack<FunctionReferenceNode> currentStack, List<Stack<FunctionReferenceNode>> completeStacks){
    	if(m_functionsToFunctionReferences.get(fn).isEmpty()){
    		Stack<FunctionReferenceNode> saveStack = new Stack<FunctionReferenceNode>();
    		saveStack.addAll(currentStack);
    		saveStack.add(0, m_uncalledFunctionReference.get(fn));
    		completeStacks.add(saveStack);
    	} else {
	    	for(GraphEdge ge : m_functionsToFunctionReferences.get(fn)){
	    		if(!formsRecursion(currentStack, ge.getFunctionReferenceNode())){
	    			currentStack.add(0, ge.getFunctionReferenceNode());
	    			search(ge.getFunctionNode(), currentStack, completeStacks);
	    			currentStack.remove(0);
	    		} 
	    	}
    	}
    }
    
    private boolean formsRecursion(Stack<FunctionReferenceNode> currentStack, FunctionReferenceNode functionReferenceNode){
    	for(FunctionReferenceNode frn : currentStack){
    		if(functionReferenceNode.isKnownFunction() && functionReferenceNode.getFunctionName().equals(frn.getFunctionName())){
    			return true;
    		}
    	}
    	return false;
    }
    
    
    class GraphEdge{
    	private FunctionReferenceNode m_frn;
    	private FunctionNode m_fn;
    	
    	public GraphEdge(FunctionReferenceNode frn, FunctionNode fn){
    		m_frn = frn;
    		m_fn = fn;
    	}
    	public FunctionNode getFunctionNode(){
    		return m_fn;
    	}
    	
    	public FunctionReferenceNode getFunctionReferenceNode(){
    		return m_frn;
    	}
    }

	public Set<FunctionNode> getFunctionsOfInstruction(InstructionNode inst) {
		Set<FunctionNode> functions = new TreeSet<FunctionNode>();
		for(FunctionNode fn : getFunctions()){
			if(this.getInstructionWithinFunction(fn).contains(inst)){
				functions.add(fn);
			}
		}
		return functions;
	}
}
