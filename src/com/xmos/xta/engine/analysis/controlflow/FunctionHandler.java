package com.xmos.xta.engine.analysis.controlflow;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.xmos.xta.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.NullNode;
import com.xmos.xta.engine.analysis.structural.patterns.CommonMethods;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class FunctionHandler {

	private InstructionDecoder m_instDecoder;
	private Collection<FunctionNode> m_functions;
	private Map<InstructionNode, FunctionNode> m_instToFunc;
	private Collection<Node> m_pseudoFunctions;
	
	public FunctionHandler(InstructionDecoder instDecoder) {
		m_instDecoder = instDecoder;
	}

	public Collection<FunctionNode> getFunctions(){
		return m_functions;
	}
	
	public Map<InstructionNode, FunctionNode> getKnownFunctions(){
		return m_instToFunc;
	}
	
	public Collection<InstructionNode> getKnownFuncStarts(){
		return m_instToFunc.keySet();
	}
	
	public Collection<Node> getPseudoFunctions(){
		return m_pseudoFunctions;
	}
	
	
	/** 
	 * For each function call this places a function node as its successor. The link stage resolves which function it actually is.
	 * All branch and link targets have to be set before this can be reliably run. 
	 */
	public boolean addFunctions(){
		m_instToFunc = new TreeMap<InstructionNode, FunctionNode>();
		m_functions = new TreeSet<FunctionNode>();
		m_pseudoFunctions = new TreeSet<Node>();
		
		for (InstructionNode instruction : m_instDecoder.getInstructions() ){
			
			
			assert(instruction.getInstructions().size() == 1);
			XtaInstruction inst = instruction.getInstructions().getFirst();
			if(inst.isLink()){
				
				if(instruction.getNextNodesSize() > 0){
					Collection<FunctionNode> newFunctions = new TreeSet<FunctionNode>();
					for(Node next : instruction.getNextNodes()){
						InstructionNode instNode  = (InstructionNode)next;
						FunctionNode fn = new FunctionNode(instNode);
						
						m_instToFunc.put(instNode, fn);
						newFunctions.add(fn);
						
						next.removePrevNode(instruction);
						
					}
					instruction.clearNextNodes();
					for(Node f : newFunctions){
						CommonMethods.connect(f, instruction);
						CommonMethods.connect(instruction.getBranchLinkReturn(), f);
						f.addBranchTarget(instruction.getBranchLinkReturn());
					}
					m_functions.addAll(newFunctions);
				} else {
					FunctionNode fn = new FunctionNode();
					CommonMethods.connect(fn, instruction);
					CommonMethods.connect(instruction.getBranchLinkReturn(), fn);
					fn.addBranchTarget(instruction.getBranchLinkReturn());
					m_functions.add(fn);
					m_pseudoFunctions.add(fn);
				}
			} 
		}
		
		for(Node node : m_instToFunc.keySet()){
			if(!canReturn(node, new TreeSet<Node>())){
				CommonMethods.disconnect(m_instToFunc.get(node).getNextNode(), m_instToFunc.get(node));
			}
		}		
		
		for(InstructionNode functionStart : getKnownFuncStarts()){
			NullNode nullNode = new NullNode();
			Set<Node> terminals = new TreeSet<Node>();
			Set<Node> seen = new TreeSet<Node>();
			findTerminals(functionStart, seen, terminals);
			
			for(Node terminal : terminals){
				CommonMethods.connect(nullNode, terminal);
			}
		}
		return true;
	}

	private void findTerminals(Node node, Set<Node> seen,
			Set<Node> terminals) {
		seen.add(node);
		
		if(node.getNextNodesSize() == 0){
			assert(!terminals.contains(node));
			terminals.add(node);
		}
		
		for(Node next : node.getNextNodes()){
			if(!seen.contains(next)){
				findTerminals(next, seen, terminals);
			}
		}
		
	}

	private boolean canReturn(Node start, Set<Node> seen){
		seen.add(start);
		if (start.getNextNodesSize() == 0){
			if (start.isFunctionNode()) return false;
			return start.getInstructions().getFirst().getReturn();
		} else {
			boolean canReturn = false;
			for (Node next : start.getNextNodes()) {
				if (!seen.contains(next)){
					canReturn = canReturn || canReturn(next, seen);
				}
			}
			return canReturn;
		}
	}	
}
