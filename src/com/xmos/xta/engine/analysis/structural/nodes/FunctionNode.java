package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.xta.engine.instructions.XtaInstruction;

public class FunctionNode extends Node {

	private String m_functionName;
	private Node m_function;
	private static int functionId;
	private boolean m_knownFunction;
	private Node m_branchTarget;
	
	public FunctionNode(Node branchTarget) {
		super();
		m_branchTarget = branchTarget;
		m_knownFunction = false;
	}
	
	public FunctionNode(Node function, String functionName) {
		super();
		m_function = function;
		m_knownFunction = true;
		if(functionName == null){
			m_functionName = "Unnamed function " + functionId++;
		} else {
			m_functionName = functionName;
		}
		m_branchTarget = m_function;
	}
	
	public FunctionNode(FunctionNode fn){
		m_functionName = fn.m_functionName;
		m_function = fn.m_function;
		m_knownFunction = fn.m_knownFunction;
		m_branchTarget = fn.m_branchTarget;
	}
	
	public FunctionNode() {
		super();
		m_knownFunction = false;
	}

	public Node deepClone(Map<Integer, FunctionNode> startPcs){
		FunctionNode fn = new FunctionNode();
		if(m_knownFunction){
			fn.m_function = m_function.deepClone(startPcs);
		}
		fn.m_functionName = m_functionName;
		fn.m_knownFunction = m_knownFunction;
		fn.m_branchTarget = m_branchTarget;
		
		if(fn.getBranchTarget() != null){
			int pc = fn.getBranchTarget().getStartPc();
			String fnName = fn.getBranchTarget().getInstructions().get(0).getFunctionName();
			fn.setFunction(fnName, startPcs.get(pc).getFunction().deepClone(startPcs));
		} 
		
		return fn;
	}
	
	public String toString(){
		return "Function" + m_id;
	}

	public double getTimeTaken(Case c) {
		if (m_knownFunction) {
			return m_function.getTimeTaken(c);	

		}
		return 0;
	}
	
	public long getNumCycles(Case c) {
		if (m_knownFunction) {		
    		return m_function.getNumCycles(c);
		}
		return 0;
	}

	public long getNumInstructions(Case c) {
		if (m_knownFunction) {		
    		return m_function.getNumInstructions(c);
		}
		return 0;
	}
	
	public long getNumFNops(Case c) {
		if (m_knownFunction) {		
    		return m_function.getNumFNops(c);
		}
		return 0;
	}
	
	public boolean isFunctionKnown() {
		return m_branchTarget != null;
	}
	
	public void setFunction(String functionName, Node fn) {
		m_function = fn;
		m_functionName = functionName;
		if (fn!= null) {
			m_knownFunction = true;
		}
	}

	public Node getFunction() {
		return m_function;
	}
	
	public Node getBranchTarget() {
		return m_branchTarget;
	}

	public Node clone() {
		return new FunctionNode(this);
	}
	
	public Collection<Node> getContaintedNodes() {
		if (m_knownFunction) {
			return m_function.getContaintedNodes();
		} else {
			return new HashSet<Node>();
		}
	}
	
	public String getFunctionName() {
		if(m_functionName == null){
			m_functionName = "Unnamed function " + functionId++;
		}
		return m_functionName;
	}

	public List<XtaInstruction> getInstructions() {
		if (m_knownFunction) {
			return m_function.getInstructions();
		} else {
			return new LinkedList<XtaInstruction>();
		}
	}

	public Set<Integer> getPCsCovered() {
		
		if(m_function == null){
			return new HashSet<Integer>();
		} else {
			return m_function.getPCsCovered();
		}
		
	}
	
	public boolean isFunction() {
		return true;
	}
	
	public int getStartPc() {
		if(m_function != null){
			return m_function.getStartPc();
		}
		return 0;
	}

	public void traverse(int depth, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundFunctionNode(this, depth))
			return;
		
		if (m_knownFunction) {
		    m_function.traverse(depth + 1, nodeTraverser);
		}
	}

}
