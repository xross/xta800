package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverserWithAncestors;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class FunctionNode extends Node {
	private NodeAttributes m_attributes;
	private String m_functionName;
	private List<Node> m_functionSequence;
	private FunctionReferenceNode m_callingFnRef;
	
	public FunctionNode() {
		super();
		m_attributes = new NodeAttributes(this);
		setUnresolved(true);
	}

	/**
	 * recursive function constructor
	 */
	public FunctionNode(String functionName) {
		super();
		m_attributes = new NodeAttributes(this);
		m_functionName = functionName + "(Recursive)";
		m_functionSequence = new LinkedList<Node>();
		setUnresolved(true);
	}
	
	
	public FunctionNode(Node function, FunctionReferenceNode fnRef) {
		super();
		m_callingFnRef = fnRef;
		if (function.isSequenceNode()){
			SequenceNode sn = (SequenceNode)function;

			m_functionSequence = sn.getPath();
		} else {
			m_functionSequence = new LinkedList<Node>();
			m_functionSequence.add(function);
		}

		if (fnRef == null){
			m_functionName = "(Call to unknown function)";
		} else {
			m_functionName = m_callingFnRef.getFunctionName();
		}
		m_attributes = new NodeAttributes(this);
	}

	public FunctionReferenceNode getCallingFunctionRef(){
		return m_callingFnRef;
	}
	
	public void getBackTrail(StringBuffer refStr) {
		super.getBackTrail(refStr);
		if(m_parent != null){
			refStr.append(String.format("CALL:0x%x,", m_callingFnRef.getFunctionCallPc()));
		}
	}

	public Node copyRouteStructure(Aborter aborter) throws AbortedException {
		//aborter.getAbort();

		FunctionNode fn = new FunctionNode();
		fn.m_functionName = m_functionName;
		fn.m_attributes = new NodeAttributes(fn);
		fn.setUnresolved(isUnresolved());
		if (m_functionSequence != null) {
			List<Node> nodes = new LinkedList<Node>();
			for (Node node : m_functionSequence){
				nodes.add(node.copyRouteStructure(aborter));
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

	public String getDescription() {
		if (m_functionName == null) {
			m_functionName = "(Call to unknown function)";
		}
		return m_functionName;
	}

	public String getFunctionName() {
		if (m_functionName == null) {
			m_functionName = "(Call to unknown function)";
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

	public long getStartPc() {
		if (m_functionSequence != null){
			return m_functionSequence.get(0).getStartPc();
		}
		return 0;
	}

	public TraverseStatus traverse(Stack<Node> ancestors, NodeTraverserWithAncestors nodeTraverser) {
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

	public TraverseStatus traverse(NodeTraverser nodeTraverser) {
		TraverseStatus status = nodeTraverser.foundFunctionNode(this);
		if (status == TraverseStatus.CONTINUE) {
			if (m_functionSequence != null) {
				for (Node node : m_functionSequence) {
					status = node.traverse( nodeTraverser);
					if (status != TraverseStatus.CONTINUE) {
						break;
					}
				}
			}
		}

		if (status == TraverseStatus.ABORT) {
			return status;
		}
		return TraverseStatus.CONTINUE;
	}
	public boolean isFunctionNode() {
		return true;
	}

	public String getBackTrace(String backTrace) {
		return super.getBackTrace(m_functionName + ":" + backTrace);
	}

	public TimeDistributionBuckets getTimeSpread() {
		TimeUnit fixedTime = m_attributes.getFixedTime();
		if (fixedTime != null) {
			return new TimeDistributionBuckets(fixedTime.getCycles());
		}

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

	public boolean canReplaceChildren() {
		return true;
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

	public List<Node> iChildren() {
		if(m_functionSequence!= null){
			return m_functionSequence;
		} else {
			return new LinkedList<Node>();
		}

	}

	public void buildListUp(List<Node> nodeList, Node start) {
		if(m_parent!= null){
			m_parent.buildListUp(nodeList, this);
		}
		nodeList.add(this);
		
		for(Node node : m_functionSequence){
			if(start == node) break;
			node.buildListDown(nodeList);
		}

	}

	public void buildListDown(List<Node> nodeList) {
	}
}
