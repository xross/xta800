package com.xmos.xta.engine;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.xmos.xta.engine.analysis.nodes.Function;
import com.xmos.xta.engine.analysis.nodes.Node;
import com.xmos.xta.engine.analysis.patterns.CommonMethods;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class FunctionHandler {

	private InstructionDecoder m_id;
	private Collection<Function> m_functions;
	private Map<Node, Node> m_instToFunc;
	private Collection<Node> m_pseudoFunctions;
	
	public FunctionHandler(InstructionDecoder id) {
		m_id = id;
	}

	public Collection<Function> getFunctions(){
		return m_functions;
	}
	
	public Map<Node, Node> getKnownFunctions(){
		return m_instToFunc;
	}
	
	public Collection<Node> getPseudoFunctions(){
		return m_pseudoFunctions;
	}
	
	/** 
	 * For each function call this places a function node as its successor. The link stage resolves which function it actually is.
	 * All branch and link targets have to be set before this can be reliably run. 
	 */
	public boolean addFunctions(){
		m_instToFunc = new HashMap<Node, Node>();
		m_functions = new HashSet<Function>();
		m_pseudoFunctions = new HashSet<Node>();
		
		for (Node instruction : m_id.getInstructions() ){
			assert(instruction.getInstructions().size() == 1);
			XtaInstruction inst = instruction.getInstructions().get(0);
			if(inst.isLink()){
				if(instruction.getNextNodes().size() > 0){
					Collection<Function> newFunctions = new HashSet<Function>();
					for(Node next : instruction.getNextNodes()){
						Function fn = new Function(next);
						m_instToFunc.put(next, fn);
						newFunctions.add(fn);
						next.getPrevNodes().remove(instruction);
						
					}
					instruction.getNextNodes().clear();
					for(Node f : newFunctions){
						CommonMethods.connect(f, instruction);
						CommonMethods.connect(instruction.getBranchLinkReturn(), f);
					}
					m_functions.addAll(newFunctions);
				} else {
					Function fn = new Function();
					CommonMethods.connect(fn, instruction);
					CommonMethods.connect(instruction.getBranchLinkReturn(), fn);
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
		if(start.getNextNodes().size() == 0){
			if(start.isFunction()) return false;
			return start.getInstructions().get(0).getReturn();
		} else {
			boolean canReturn = false;
			for(Node next : start.getNextNodes()){
				if(!seen.contains(next)){
					canReturn = canReturn || canReturn(next, seen);
				}
			}
			return canReturn;
		}
	}
	
}
