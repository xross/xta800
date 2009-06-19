package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

public class FunctionNode extends IBufferNode {
	private NodeAttributes m_attributes;	
	private String m_functionName;
	private Node m_function;
	private static int functionId;
	private boolean m_knownFunction;
	private Node m_branchTarget;
	
	public FunctionNode() {
		super();
		m_knownFunction = false;
		m_attributes = new NodeAttributes(this);
	}
	
	public FunctionNode(Node branchTarget) {
		super();
		m_branchTarget = branchTarget;
		m_knownFunction = false;
		m_attributes = new NodeAttributes(this);
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
		m_attributes = new NodeAttributes(this);
	}
	
	public FunctionNode(FunctionNode fn){
		//super(fn);
		m_functionName = fn.m_functionName;
		m_function = fn.m_function;
		m_knownFunction = fn.m_knownFunction;
		m_branchTarget = fn.m_branchTarget;
		m_attributes = new NodeAttributes(this);
	}
	
	public Node shallowCopy() {
    	return new FunctionNode(this);
	}
	
	public Node deepCopy(Map<Integer, FunctionNode> startPcs){
		FunctionNode fn = new FunctionNode();
		if(m_knownFunction){
			fn.m_function = m_function.deepCopy(startPcs);			
		}
		fn.m_functionName = m_functionName;
		fn.m_knownFunction = m_knownFunction;
		fn.m_branchTarget = m_branchTarget;
		/*
		if(fn.getBranchTarget() != null){
			int pc = fn.getBranchTarget().getStartPc();
			String fnName = fn.getBranchTarget().getInstructions().getFirst().getFunctionName();
			
			FunctionNode bob = startPcs.get(pc);
			
			if(bob == null){
				System.out.println("bob 0x" + Integer.toHexString(pc) + "\n\n");
			}
			Node bill = bob.getFunction();
			if(bill == null){
				System.out.println("bill");
			}
			
			
			fn.setFunction(fnName, bill.deepCopy(startPcs));
		} 
		*/
		fn.m_attributes = new NodeAttributes(fn);
		return fn;
	}

    public String getTypeName() {
        return "function";
    }
		
    public NodeAttributes getAttributes() {
    	return m_attributes;
    }
    
	public String toString(){
		return "Function" + m_id;
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
			return new TreeSet<Node>();
		}
	}
	
	public String getDescription() {
		if (m_functionName == null) {
			m_functionName = "Unnamed function " + functionId++;
		}
		return m_functionName;
	}
	
	public String getFunctionName() {
		if (m_functionName == null) {
			m_functionName = "Unnamed function " + functionId++;
		}
		return m_functionName;
	}

	public LinkedList<XtaInstruction> getInstructions() {
		if (m_knownFunction) {
			return m_function.getInstructions();
		} else {
			return new LinkedList<XtaInstruction>();
		}
	}

	public Set<Integer> getPCs() {
		
		if(m_function == null){
			return new TreeSet<Integer>();
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
		if (getParent() != null){
			return getParent().whatsNext(this);
		}
		return new TreeSet<Node>();
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

	public void getInstructionStream(Case c, List<InstructionNode> instructions, long iteration) {
		if(m_knownFunction){
			m_function.getInstructionStream(c, instructions, iteration);
		}
	}

	public long getError(Case c) {
		if(m_knownFunction){
			return m_function.getError(c);
		} else {
			return 0;
		}
	
	}

	public Collection<Node> whatsBefore(Node node) {
		if(getParent()!= null){
			return getParent().whatsBefore(this);
		} else {
			return new TreeSet<Node>();
		}
	}


	public List<Node> getImmediateChildren() {
		List<Node> list = new LinkedList<Node>();
		list.add(m_function);
		return list;
	}

	public void insertFnops() {

		
	}
	
}
