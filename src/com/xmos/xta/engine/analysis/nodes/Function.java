package com.xmos.xta.engine.analysis.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.instructions.XtaInstruction;


public class Function extends Node {

	private String m_functionName;
	private Node m_function;
	private static int functionId;
	private boolean m_knownFunction;
	private Node m_branchTarget;
	
	public Function (Node branchTarget) {
		super();
		m_branchTarget = branchTarget;
		m_knownFunction = false;
	}
	
	public Function (Node function, String functionName) {
		super();
		m_function = function;
		m_knownFunction = true;
		if(functionName == null){
			m_functionName = "Unnamed function " + functionId++;
		} else {
			m_functionName = functionName;
		}
	}
	
	public Function (Function fn){
		m_functionName = fn.m_functionName;
		m_function = fn.m_function;
		m_knownFunction = fn.m_knownFunction;
	}
	
	public Function() {
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
	
	public Node getFunction(){
		return m_function;
	}
	
	public Node getBranchTarget(){
		return m_branchTarget;
	}

	public void display(int indentLevel) {
		printIndent(indentLevel);
		System.out.printf("function(%s)\n", m_functionName);
		if(m_knownFunction){
	//		if(m_branchTarget == null){
				m_function.display(indentLevel + 1);
	//		} 
		} else {
			printIndent(indentLevel+1);
			System.out.printf("function unknown\n");
		}
	}
	
	public Node clone() {
		return new Function(this);
	}
	
	public Collection<Node> getContaintedNodes() {
		if(m_knownFunction){
			return m_function.getContaintedNodes();
		} else {
			return new HashSet<Node>();
		}
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
	
}
