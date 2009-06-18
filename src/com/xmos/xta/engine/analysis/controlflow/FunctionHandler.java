package com.xmos.xta.engine.analysis.controlflow;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.xmos.xta.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.patterns.CommonMethods;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class FunctionHandler {

	private InstructionDecoder m_instDecoder;
	private Collection<FunctionNode> m_functions;
	private Map<Node, Node> m_instToFunc;
	private Collection<Node> m_pseudoFunctions;
	
	public FunctionHandler(InstructionDecoder instDecoder) {
		m_instDecoder = instDecoder;
	}

	public Collection<FunctionNode> getFunctions(){
		return m_functions;
	}
	
	public Map<Node, Node> getKnownFunctions(){
		return m_instToFunc;
	}
	
	public Collection<Node> getKnownFuncStarts(){
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
		m_instToFunc = new TreeMap<Node, Node>();
		m_functions = new TreeSet<FunctionNode>();
		m_pseudoFunctions = new TreeSet<Node>();
		
		for (Node instruction : m_instDecoder.getInstructions() ){
			assert(instruction.getInstructions().size() == 1);
			XtaInstruction inst = instruction.getInstructions().getFirst();
			if(inst.isLink()){
				if(instruction.getNextNodes().size() > 0){
					Collection<FunctionNode> newFunctions = new HashSet<FunctionNode>();
					for(Node next : instruction.getNextNodes()){
						FunctionNode fn = new FunctionNode(next);
						
						m_instToFunc.put(next, fn);
						newFunctions.add(fn);
						next.getPrevNodes().remove(instruction);
						
					}
					instruction.getNextNodes().clear();
					for(Node f : newFunctions){
						CommonMethods.connect(f, instruction);
						CommonMethods.connect(instruction.getBranchLinkReturn(), f);
						f.getBranchTargets().add(instruction.getBranchLinkReturn());
					}
					m_functions.addAll(newFunctions);
				} else {
					FunctionNode fn = new FunctionNode();
					CommonMethods.connect(fn, instruction);
					CommonMethods.connect(instruction.getBranchLinkReturn(), fn);
					fn.getBranchTargets().add(instruction.getBranchLinkReturn());
					m_functions.add(fn);
					m_pseudoFunctions.add(fn);
				}
			}
		}
		
		for(Node node : m_instToFunc.keySet()){
			if(!canReturn(node, new HashSet<Node>())){
				CommonMethods.disconnect(m_instToFunc.get(node).getNextNodes().iterator().next(), m_instToFunc.get(node));
			}
		}		
		return true;
	}

	private boolean canReturn(Node start, Set<Node> seen){
		seen.add(start);
		if (start.getNextNodes().size() == 0){
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
