package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

public class FunctionNode extends IBufferNode {
	private NodeAttributes m_attributes;	
	private String m_functionName;
	private List<Node> m_functionSequence;
	private static int functionId;
	private Node m_branchTarget;
	
	public FunctionNode() {
		super();
		m_attributes = new NodeAttributes(this);
	}
	
	public FunctionNode(Node branchTarget) {
		super();
		m_branchTarget = branchTarget;
		m_attributes = new NodeAttributes(this);
	}
	
	public FunctionNode(Node function, String functionName) {
		super();
		
		if(function.isSequenceNode()){
			m_functionSequence = function.getChildren();
		} else {
			m_functionSequence = new LinkedList<Node>();
			m_functionSequence.add(function);
		}
		
		if(functionName == null){
			m_functionName = "Unnamed function " + functionId++;
		} else {
			m_functionName = functionName;
		}
		m_branchTarget = m_functionSequence.get(0);
		m_attributes = new NodeAttributes(this);
	}
	
	public FunctionNode(FunctionNode fn){
		m_functionName = fn.m_functionName;
		m_functionSequence = fn.m_functionSequence;
		m_branchTarget = fn.m_branchTarget;
		m_attributes = new NodeAttributes(this);
	}
	
	public Node shallowCopy() {
    	return new FunctionNode(this);
	}

	public Node deepCopy(){
		FunctionNode fn = new FunctionNode();
		fn.m_functionName = m_functionName;
		fn.m_branchTarget = m_branchTarget;
		fn.m_attributes = new NodeAttributes(fn);
		if(m_functionSequence!= null){
			List<Node> nodes = new LinkedList<Node>();
			for(Node node : m_functionSequence){
				nodes.add(node.deepCopy());
			}
			fn.m_functionSequence = nodes;
		}
		return fn;
	}

    public String getTypeName() {
        return "function";
    }
		
    public NodeAttributes getAttributes() {
    	return m_attributes;
    }

	public boolean isFunctionKnown() {
		return m_branchTarget != null;
	}
	
	public void setFunction(String functionName, List<Node> fn) {
		m_functionSequence = fn;
		m_functionName = functionName;
	}

	public List<Node> getFunction() {
		return m_functionSequence;
	}
	
	public Node getBranchTarget() {
		return m_branchTarget;
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
		if (m_functionSequence != null) {
			LinkedList<XtaInstruction> list = new LinkedList<XtaInstruction>();
			for (Node node : m_functionSequence) {
				list.addAll(node.getInstructions());
			}
			return list;
		} else {
			return new LinkedList<XtaInstruction>();
		}
	}
		
	public int getStartPc() {
		if(m_functionSequence != null){
			return m_functionSequence.get(0).getStartPc();
		}
		return 0;
	}

	public void traverse(int depth, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundFunctionNode(this, depth))
			return;
		
		if (m_functionSequence != null) {
			for (Node node : m_functionSequence) {
				node.traverse(depth + 1, nodeTraverser);
			}
		}
	}

	public List<Node> getChildren() {
		List<Node> children = null;
		if(m_functionSequence!= null){
			children =  m_functionSequence;
		} else {
			children = new LinkedList<Node>();
		}
		return children;
	}
	
	public Collection<Node> whatsNext(Node node) {
		assert (m_functionSequence.contains(node));
		int index = m_functionSequence.indexOf(node);
		if (index < m_functionSequence.size() - 1) {
			Collection<Node> next = new TreeSet<Node>();
			next.add(m_functionSequence.get(index + 1));
			return next;
		} else if(node.isInfiniteLoopNode() || node.isInfiniteSelfLoopNode()){
			Collection<Node> next = new TreeSet<Node>();
			next.add(node);
			return next;
		} else {
			if (getParent() != null){
				return getParent().whatsNext(this);
			}
			return new TreeSet<Node>();
		}
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

	public String getBackTrace(String backTrace) {
		return super.getBackTrace(m_functionName + ":" + backTrace); 
	}

	public Collection<Node> whatsBefore(Node node) {
		assert(m_functionSequence.contains(node));
		int index = m_functionSequence.indexOf(node);
		if(index > 0){
			Collection<Node> before = new TreeSet<Node>();
			before.add(m_functionSequence.get(index-1));
			return before;
		} else {
			if(getParent()!= null){
				return getParent().whatsBefore(this);
			} else {
				return new TreeSet<Node>();
			}
		}
	}


	public List<Node> getImmediateChildren() {
		List<Node> list = new LinkedList<Node>();
		list.add(m_functionSequence.get(0));
		return list;
	}

	public void insertFnops() {

		
	}

	public TimeDistributionBuckets getTimeSpread() {
		TimeDistributionBuckets tdb = new TimeDistributionBuckets();
		List<TimeDistributionBuckets> tsbSequence = new LinkedList<TimeDistributionBuckets>();
		for(Node node : m_functionSequence){
			tsbSequence.add(node.getTimeSpread());
		}
		tdb.add(tsbSequence);
		return tdb;
	}
	
	public Set<InstructionNode> getEndNodes() {
		return m_functionSequence.get(m_functionSequence.size()-1).getEndNodes();
	}

	public void setFunctionName(String string) {
		m_functionName = string;
		
	}
}
