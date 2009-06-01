package com.xmos.xta.engine.analysis.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.instructions.XtaInstruction;

public class FunctionNode extends Node {

	private String m_functionName;
	private Node m_function;
	private static int functionId;
	private boolean m_knownFunction;
	private Node m_branchTarget;
	
	public FunctionNode (Node branchTarget) {
		super();
		m_branchTarget = branchTarget;
		m_knownFunction = false;
	}
	
	public FunctionNode (Node function, String functionName) {
		super();
		m_function = function;
		m_knownFunction = true;
		if(functionName == null){
			m_functionName = "Unnamed function " + functionId++;
		} else {
			m_functionName = functionName;
		}
	}
	
	public FunctionNode (FunctionNode fn){
		m_functionName = fn.m_functionName;
		m_function = fn.m_function;
		m_knownFunction = fn.m_knownFunction;
	}
	
	public FunctionNode() {
		super();
		m_knownFunction = false;
	}

	public boolean isFunctionKnown(){
		return m_branchTarget != null;
	}
	
	public void setFunction(String functionName, Node fn){
		m_function = fn;
		m_functionName = functionName;
		if(fn!= null){
			m_knownFunction = true;
		}
	}
	
	public String getFucntionName(){
		if(m_functionName == null){
			m_functionName = "Unnamed function " + functionId++;
		}
		return m_functionName;
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
		if(m_knownFunction){
			return m_function.getContaintedNodes();
		} else {
			return new HashSet<Node>();
		}
	}
	
	public String getFunctionName() {
		return m_functionName;
	}
	
	public String getGraphvizName() {
		return "Function" + m_id;
	}

	public List<XtaInstruction> getInstructions() {
		if(m_knownFunction){
			return m_function.getInstructions();
		} else {
			return new LinkedList<XtaInstruction>();
		}
	}
	
	public long getNumberCycles() {
		return m_function.getNumberCycles();
	}

	public long getNumberFNOPS() {
		return m_function.getNumberFNOPS();
	}

	public Set<Integer> getPCsCovered() {
		return m_function.getPCsCovered();
	}
	
	public boolean isFunction() {
		return true;
	}
	
	public int getStartPc() {
		return m_function.getStartPc();
	}

	public void traverse(int depth, NodeTraverser nodeTraverser) {
		nodeTraverser.foundFunctionNode(this, depth);
		if (m_knownFunction) {
		    m_function.traverse(depth, nodeTraverser);
		}
	}	
}
