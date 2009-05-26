package com.xmos.xta.engine;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.xmos.xta.engine.analysis.nodes.Function;
import com.xmos.xta.engine.analysis.nodes.Node;
import com.xmos.xta.engine.analysis.patterns.CommonMethods;

public class PseudoFunctionHandeler {

	private InstructionDecoder m_id;
	private Collection<Node> m_functions;
	private Map<Node, Node> m_instToFunc;
	
	public PseudoFunctionHandeler(InstructionDecoder id) {
		m_id = id;
	}

	/** 
	 * For each function call this places a function node as its successor. The link stage resolves which function it actually is.
	 * All branch and link targets have to be set before this can be reliably run. 
	 */
	public boolean addPesudoFunctions(){
		m_instToFunc = new HashMap<Node, Node>();
		m_functions = new HashSet<Node>();
		for (Node instruction : m_id.getInstructions() ){
			assert(instruction.getInstructions().size() == 1);
			XtaInstruction inst = instruction.getInstructions().get(0);
			if(inst.isLink()){
				if(instruction.getNextNodes().size() > 0){
					
					Collection<Node> newFunctions = new HashSet<Node>();
					
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
					m_functions.add(fn);
				}
			}
		}
		return true;
	}

	public Collection<Node> getFunctions(){
		return m_functions;
	}
	
	public Map<Node, Node> getKnownFunctions(){
		return m_instToFunc;
	}
	
}
