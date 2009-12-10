package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser.TraverseStatus;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.types.ReferenceType;

public class FunctionNode extends IBufferNode {
	private NodeAttributes m_attributes;	
	private String m_functionName;
	private List<Node> m_functionSequence;
	private static int functionId;
	private Node m_branchTarget;
	private Set<Node> m_functionEndNodes;
	private List<FunctionNode> m_callingFunctions;
	
	public FunctionNode() {
		super();
		m_attributes = new NodeAttributes(this);
		m_functionEndNodes = new TreeSet<Node>();
		m_callingFunctions = new ArrayList<FunctionNode>();
		setUnresolved(true);
	}
	
	public FunctionNode(Node branchTarget) {
		super();
		m_branchTarget = branchTarget;
		m_attributes = new NodeAttributes(this);
		m_callingFunctions = new ArrayList<FunctionNode>();
	}
	
	public FunctionNode(Node function, String functionName) {
		super();
		if (function.isSequenceNode()){
			m_functionSequence = function.getIndirectChildren();
		} else {
			m_functionSequence = new LinkedList<Node>();
			m_functionSequence.add(function);
		}
		
		if (functionName == null){
			m_functionName = "Unnamed function " + functionId++;
		} else {
			m_functionName = functionName;
		}
		
		m_branchTarget = m_functionSequence.get(0);
		m_attributes = new NodeAttributes(this);
		m_callingFunctions = new ArrayList<FunctionNode>();
	}
	
	public FunctionNode(FunctionNode fn){
		m_functionName = fn.m_functionName;
		m_functionSequence = fn.m_functionSequence;
		m_branchTarget = fn.m_branchTarget;
		m_attributes = new NodeAttributes(this);
		setUnresolved(fn.isUnresolved());
		m_callingFunctions = new ArrayList<FunctionNode>();
	}
	
	public Node shallowCopy() {
    	return new FunctionNode(this);
	}

	public Node deepCopy(Aborter aborter) throws AbortedException {
		aborter.getAbort();

		FunctionNode fn = new FunctionNode();
		fn.m_functionName = m_functionName;
		fn.m_branchTarget = m_branchTarget;
		fn.m_attributes = new NodeAttributes(fn);
		fn.setUnresolved(isUnresolved());
		if (m_functionSequence != null) {
			List<Node> nodes = new LinkedList<Node>();
			for (Node node : m_functionSequence){
				nodes.add(node.deepCopy(aborter));
			}
			fn.m_functionSequence = nodes;
		}

		fn.m_callingFunctions = new ArrayList<FunctionNode>();
		return fn;
	}

	public String getTypeName() {
        return "function";
    }
	
	public void setFunctionEndNodes(Set<Node> functionEndNodes){
		m_functionEndNodes = functionEndNodes;
	}
	
	public Set<Node> getFunctionEndNodes(){
		return m_functionEndNodes;
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

	public XtaInstruction getStartInstruction() {
		if (m_functionSequence != null) {
			return m_functionSequence.get(0).getStartInstruction();
		} else {
			return null;
		}
	}

	public long getStartPc() {
		if (m_functionSequence != null){
			return m_functionSequence.get(0).getStartPc();
		}
		return 0;
	}

	public TraverseStatus traverse(Stack<Node> ancestors, NodeTraverser nodeTraverser) {
		TraverseStatus status = nodeTraverser.foundFunctionNode(this, ancestors);
		if (status == TraverseStatus.CONTINUE) {
			if (m_functionSequence != null) {
				ancestors.add(this);
				for (Node node : m_functionSequence) {
					status = node.traverse(ancestors, nodeTraverser);
					if (status != TraverseStatus.CONTINUE) {
						break;
					}
				}
				ancestors.remove(this);
			}
		}

		if (status == TraverseStatus.ABORT) {
			return status;
		}
		return TraverseStatus.CONTINUE;
	}

	public List<Node> getIndirectChildren() {
		List<Node> children = null;
		if (m_functionSequence!= null) {
			children =  m_functionSequence;
		} else {
			children = new LinkedList<Node>();
		}
		return children;
	}
	
	public Collection<NodeDescriptor> whatsNext(NodeDescriptor nodeDescriptor) {
		assert (m_functionSequence.contains(nodeDescriptor.getNode()));
		
		int index = m_functionSequence.indexOf(nodeDescriptor.getNode());
		if (index < m_functionSequence.size() - 1) {
			Collection<NodeDescriptor> next = new TreeSet<NodeDescriptor>();
			next.add(new NodeDescriptor(m_functionSequence.get(index + 1), nodeDescriptor.getAncestors()));
			return next;
			
		} else if (nodeDescriptor.getNode().isInfiniteLoopNode() || 
    			   nodeDescriptor.getNode().isInfiniteSelfLoopNode()) {
			
			Collection<NodeDescriptor> next = new TreeSet<NodeDescriptor>();
			next.add(new NodeDescriptor(nodeDescriptor.getNode(), nodeDescriptor.getAncestors()));
			return next;
			
		} else {
			NodeDescriptor thisNodeDescriptor = new NodeDescriptor(this, nodeDescriptor.getAncestors());
			thisNodeDescriptor.getAncestors().pop();

			if (!thisNodeDescriptor.getAncestors().isEmpty()) {
				return thisNodeDescriptor.getAncestors().peek().whatsNext(thisNodeDescriptor);
			}
			return new TreeSet<NodeDescriptor>();
		}
	}

	public Collection<NodeDescriptor> whatsBefore(NodeDescriptor nodeDescriptor) {
		assert (m_functionSequence.contains(nodeDescriptor.getNode()));
		
		int index = m_functionSequence.indexOf(nodeDescriptor.getNode());
		if (index > 0){
			Collection<NodeDescriptor> before = new TreeSet<NodeDescriptor>();
			before.add(new NodeDescriptor(m_functionSequence.get(index - 1), nodeDescriptor.getAncestors()));
			return before;
			
		} else {
			NodeDescriptor thisNodeDescriptor = new NodeDescriptor(this, nodeDescriptor.getAncestors());
			thisNodeDescriptor.getAncestors().pop();

			if (!thisNodeDescriptor.getAncestors().isEmpty()) {
				return thisNodeDescriptor.getAncestors().peek().whatsBefore(thisNodeDescriptor);
			}
			return new TreeSet<NodeDescriptor>();
		}
	}

	public boolean isFunctionNode() {
		return true;
	}	
	
	public String getBackTrace(String backTrace) {
		return super.getBackTrace(m_functionName + ":" + backTrace); 
	}
	
	public List<NodeDescriptor> getDirectChildren(Stack<Node> ancestors) {
		List<NodeDescriptor> list = new LinkedList<NodeDescriptor>();		
		ancestors.push(this);
	    list.add(new NodeDescriptor(m_functionSequence.get(0), ancestors));
		ancestors.pop();
		return list;
	}
	
	public void insertFnops() {
	}

	public Set<NodeDescriptor> getEndInstructions(Stack<Node> ancestors) {
		if (m_functionSequence != null) {
			ancestors.push(this);
			Set<NodeDescriptor> ends = m_functionSequence.get(m_functionSequence.size() - 1).getEndInstructions(ancestors);
			ancestors.pop();
			return ends;

		}
		return new TreeSet<NodeDescriptor>();
	}
	
	public TimeDistributionBuckets getTimeSpread() {
		TimeDistributionBuckets tdb = new TimeDistributionBuckets();
		List<TimeDistributionBuckets> tsbSequence = new LinkedList<TimeDistributionBuckets>();
		if (m_functionSequence != null && !m_functionSequence.isEmpty()) {
			for (Node node : m_functionSequence) {
				tsbSequence.add(node.getTimeSpread());
			}
		} else {
			TimeDistributionBuckets infinite = new TimeDistributionBuckets(0);
			infinite.setInfinite();
			tsbSequence.add(infinite);
		}
		tdb.add(tsbSequence);
		return tdb;
	}
	
	public void setFunctionName(String string) {
		m_functionName = string;
	}
	
	public void replaceChild(Node oldNode, Node newNode) {
		int index = m_functionSequence.indexOf(oldNode);
		m_functionSequence.remove(index);
		m_functionSequence.add(index, newNode);
	}	
	
	public void replaceChildren(List<Node> children) {
		m_functionSequence.clear();
		m_functionSequence.addAll(children);
	}	
	
	public void getBackTrail(List<Long> backTrail) {
		super.getBackTrail(backTrail);

		Node parent = getParent();
		if (parent != null) {
			Node callNode = findCallNode(parent);
			if (callNode != null) {
				backTrail.add(callNode.getStartPc());
			}
		}				
	}	
	
	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, 
			Stack<Node> ancestors, Node node, boolean calledFromAbove) {
		
		if (!calledFromAbove) {
			ReferenceType referenceType = referenceTypes.peek();
			
			// If the next reference we are trying to match is 
			// a call reference, then we need to find the calling 
			// (instruction) node and check for a match here...
			if (referenceType.getType() == ReferenceType.Type.CALL) {
				
				if (!ancestors.isEmpty()) {					
    				Node parent = ancestors.peek();

    				Node callNode = findCallNode(parent);
					if (callNode != null) {
						if (referenceType.getPcs().contains(callNode.getStartPc())) {
							referenceTypes.pop();
							if (referenceTypes.empty()) {
								return true;
							}								
						}
					}					
				}
			}
		    return super.checkBackTrail(referenceTypes, ancestors, node, calledFromAbove);
		}
		return false;
	}
	
	private Node m_callNode;	
	private Node findCallNode(Node parent) {
		// Finds the node previous to this one...
		Node previousNode = null;
		for (Node child : parent.getIndirectChildren()) {
			if (child == this)
				break;
			
			previousNode = child;
		}
		if (previousNode == null) {
			return null;
		}
		
		// Finds the final instruction in the previous node
		// (which must be the calling instruction..)
		m_callNode = null;
		previousNode.traverse(new Stack<Node>(), new DefaultNodeTraverser() {
			public TraverseStatus foundInstructionNode(Node node, Stack<Node> ancestors) {
				m_callNode = node;
				return TraverseStatus.CONTINUE;
			}
		});			
		return m_callNode;
	}

	public void addCallingFunction(FunctionNode f) {
		m_callingFunctions.add(f);
	}

	public List<FunctionNode> getCallingFunctions() {
		return m_callingFunctions;
	}
}
