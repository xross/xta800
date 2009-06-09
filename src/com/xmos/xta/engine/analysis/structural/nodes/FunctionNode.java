package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

public class FunctionNode extends Node {
	private String m_functionName;
	private Node m_function;
	private static int functionId;
	private boolean m_knownFunction;
	private Node m_branchTarget;
	
	public FunctionNode() {
		super();
		m_knownFunction = false;
	}
	
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
	
	public Node clone() {
		return new FunctionNode(this);
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

	public double getTimeTakenRelative(Case c) {
		if (m_knownFunction) {
			return m_function.getTimeTakenRelative(c);	
		}
		return 0;
	}

	public double getTimeTakenAbsolute(Case c) {
		if (m_knownFunction) {
			return m_function.getTimeTakenAbsolute(c);	
		}
		return 0;
	}
	
	public long getNumCyclesRelative(Case c) {
		if (m_knownFunction) {		
    		return m_function.getNumCyclesRelative(c);
		}
		return 0;
	}

	public long getNumCyclesAbsolute(Case c) {
		if (m_knownFunction) {		
    		return m_function.getNumCyclesAbsolute(c);
		}
		return 0;
	}
	
	public long getNumInstructionsRelative(Case c) {
		if (m_knownFunction) {		
    		return m_function.getNumInstructionsRelative(c);
		}
		return 0;
	}

	public long getNumInstructionsAbsolute(Case c) {
		if (m_knownFunction) {		
    		return m_function.getNumInstructionsAbsolute(c);
		}
		return 0;
	}
	
	public long getNumFNopsRelative(Case c) {
		if (m_knownFunction) {		
    		return m_function.getNumFNopsRelative(c);
		}
		return 0;
	}

	public long getNumFNopsAbsolute(Case c) {
		if (m_knownFunction) {		
    		return m_function.getNumFNopsAbsolute(c);
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
	
	public Collection<Node> getContainedNodes() {
		if (m_knownFunction) {
			return m_function.getContainedNodes();
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

	public Set<Integer> getPCs() {
		
		if(m_function == null){
			return new HashSet<Integer>();
		} else {
			return m_function.getPCs();
		}
		
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

	public List<Node> getChildren() {
		List<Node> children = new LinkedList<Node>();
		if(m_function!= null){
			children.add(m_function);
		}
		return children;
	}
	
	public Collection<Node> whatsNext(Node node) {
		if(getParent() != null){
			return getParent().whatsNext(this);
		}
		return null;
	}

	public boolean isFunctionNode() {
		return true;
	}	
	
	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, Node node, 
			boolean calledFromAbove, FunctionCallStatus functionCallStatus) {
		
		if (!calledFromAbove) {
			functionCallStatus.setStatus(true);
		    return super.checkBackTrail(referenceTypes, node, calledFromAbove, functionCallStatus);
		}
		return false;
	}	
}
